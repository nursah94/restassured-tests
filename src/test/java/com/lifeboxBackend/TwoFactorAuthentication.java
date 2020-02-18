package com.lifeboxBackend;

import com.google.gson.Gson;
import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.common.RequestBody;
import com.lifeboxBackend.dto.VerifyOtpDto;
import com.lifeboxBackend.entity.DeviceInfo;
import com.lifeboxBackend.entity.GlobalSetting;
import com.lifeboxBackend.entity.NotificationJob;
import com.lifeboxBackend.repository.DeviceInfoRepository;
import com.lifeboxBackend.repository.GlobalRepository;
import com.lifeboxBackend.repository.NotificationJobRepository;
import com.lifeboxBackend.requestModel.AuthenticateChallenge;
import com.lifeboxBackend.requestModel.TwoFactorPostAuth;
import com.lifeboxBackend.requestModel.TwoFactorSendChallenge;
import com.lifeboxBackend.service.RequestBaseService;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)


public class TwoFactorAuthentication {

    @Autowired
    private RequestBaseService requestBaseService;

    @Autowired
    private GlobalRepository globalRepository;

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    @Autowired
    private DeviceInfoRepository deviceInfoRepository;

    private static final Logger log = LoggerFactory.getLogger(TwoFactorAuthentication.class);

    private static String token = null;
    private static String twofactorToken = null;


    public void postAuthSetting(boolean twoFactorStatus) throws InterruptedException {

        String xAuthToken = requestBaseService.getXAuthToken(RequestBody.TWO_FACTOR_LOGIN);
        TimeUnit.SECONDS.sleep(2);
        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", xAuthToken);

        TwoFactorPostAuth twoFactorPostAuth = new TwoFactorPostAuth();
        twoFactorPostAuth.setMobileNetworkAuthEnabled(true);
        twoFactorPostAuth.setTurkcellPasswordAuthEnabled(true);
        twoFactorPostAuth.setTwoFactorAuthEnabled(twoFactorStatus);

        Gson gson = new Gson();
        String json = gson.toJson(twoFactorPostAuth);

        requestSpecification.body(json);

        Response response = requestSpecification.post(EndPoint.POST_AUTH_SETTING);

        boolean turkcellPasswordAuthEnable = response.getBody().jsonPath().getBoolean("turkcellPasswordAuthEnabled");
        boolean mobileNetworkAuthEnabled = response.getBody().jsonPath().getBoolean("mobileNetworkAuthEnabled");
        boolean twoFactorAuthEnabled = response.getBody().jsonPath().getBoolean("twoFactorAuthEnabled");

        Assert.assertTrue(turkcellPasswordAuthEnable);
        Assert.assertTrue(mobileNetworkAuthEnabled);
        Assert.assertTrue(twoFactorAuthEnabled);

    }

    public void login() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.body(RequestBody.TWO_FACTOR_LOGIN);
        Response response = requestSpecification.post(EndPoint.LOGIN);
        int statusCode = response.getStatusCode();

        String reason = response.getBody().jsonPath().getString("reason");
        String error = response.getBody().jsonPath().getString("error");
        String twofactorToken = response.getBody().jsonPath().getString("2faToken");
        this.twofactorToken = twofactorToken;

        GlobalSetting findBy = globalRepository.findBy("606");
        String status = findBy.getValue();
        int twoFactorStatusAccount = requestBaseService.getTwoFactorStatusAccount("12349903");

        if (status.equals("ON") && twoFactorStatusAccount == 0) {

            Assert.assertEquals(403, statusCode);
            Assert.assertEquals("NEW_DEVICE", reason);
            Assert.assertEquals("2FA_REQUIRED", error);

        } else if (status.equals("ON") && twoFactorStatusAccount == 1) {

            Assert.assertEquals(403, statusCode);
            Assert.assertEquals("ACCOUNT_SETTING", reason);
            Assert.assertEquals("2FA_REQUIRED", error);

        }

    }

    public void sendChallenge(String challengeType) throws InterruptedException {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        TimeUnit.SECONDS.sleep(2);
        TwoFactorSendChallenge twoFactorSendChallenge = new TwoFactorSendChallenge();
        twoFactorSendChallenge.setToken(twofactorToken);
        twoFactorSendChallenge.setType(challengeType);
        Gson gson = new Gson();
        String json = gson.toJson(twoFactorSendChallenge);

        requestSpecification.body(json);
        Response response = requestSpecification.post(EndPoint.SENDCHALLENGE);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

        }
        String status = response.getBody().jsonPath().getString("status");
        String remainingTimeInSeconds = response.getBody().jsonPath().getString("remainingTimeInSeconds");
        String expectedInputLength = response.getBody().jsonPath().getString("expectedInputLength");

        Assert.assertEquals("SENT_NEW_CHALLENGE", status);
        Assert.assertNotNull(remainingTimeInSeconds);
        Assert.assertEquals("6", expectedInputLength);


    }

    public void authenticateChallenge(String challengeType) throws InterruptedException {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        TimeUnit.SECONDS.sleep(2);

        Gson gson = new Gson();

        if (challengeType.equals("EMAIL_OTP")) {

            List<NotificationJob> find = notificationJobRepository.findByMsisdn("12349903");
            String requestBody = find.get(0).getRequestBody();
            VerifyOtpDto verifyOtpDto = new Gson().fromJson(requestBody
                    , VerifyOtpDto.class);

            verifyOtpDto.getOtpCode();
            AuthenticateChallenge authenticateChallenge = new AuthenticateChallenge();
            authenticateChallenge.setToken(twofactorToken);
            authenticateChallenge.setChallengeType(challengeType);
            authenticateChallenge.setOtpCode(verifyOtpDto.getOtpCode());
            String json = gson.toJson(authenticateChallenge);
            requestSpecification.body(json);

        } else {

            NotificationJob byAccountId = notificationJobRepository.findFirstByRequestBodyContainingOrderByCreatedDateDesc("12349903");
            String requestBody = byAccountId.getRequestBody();
            VerifyOtpDto verifyOtpDto = new Gson().fromJson(requestBody
                    , VerifyOtpDto.class);
            verifyOtpDto.getOtp();
            AuthenticateChallenge authenticateChallenge = new AuthenticateChallenge();
            authenticateChallenge.setToken(twofactorToken);
            authenticateChallenge.setChallengeType(challengeType);
            authenticateChallenge.setOtpCode(verifyOtpDto.getOtp());
            String json = gson.toJson(authenticateChallenge);
            requestSpecification.body(json);
        }

        Response response = requestSpecification.post(EndPoint.AUTHENTICATECHALLENGE);
        int statusCode = response.getStatusCode();

        Assert.assertEquals(200, statusCode);

    }

    public void deviceInfoCheck() {

        DeviceInfo byAccountId = deviceInfoRepository.foundDevice();
        int accId = requestBaseService.getAccountId("12349903");
        String uuid = byAccountId.getUuid();
        Date lastAuthenticateDate = byAccountId.getLastAuthenticatedDate();
        int twoFactorVerified = byAccountId.getTwoFactorVerified();
        int accountId = byAccountId.getAccountId();

        Assert.assertEquals("ALPER", uuid);
        Assert.assertEquals(accId, accountId);
        Assert.assertNotNull(lastAuthenticateDate);
        Assert.assertEquals(1, twoFactorVerified);

    }

    public void deleteDeviceInfo() {

        int byAccountId = deviceInfoRepository.deleteDevice();
        Assert.assertEquals(1, byAccountId);

        if (byAccountId == 1) {

            log.info(" [ Msısdn ] : 12349903 [ Device table succesfully cleared ] ");
        }
    }

    @Test
    public void globalOffAccountOnSMS() throws InterruptedException {

        postAuthSetting(true);
        login(); // Two Factor required with 403 statusCode for two_factor switch open on the account_table
        sendChallenge("SMS_OTP"); // Next SMS_OTP step after Choosing challenge type and taken two_factor token.
        authenticateChallenge("SMS_OTP"); // Login with two factor token.
        deviceInfoCheck(); //Check two_factor login success or nonsuccess with logined device uuid from device_info table
        login(); // Try again login process. Two factor required errorMessage should be login api response body
        deleteDeviceInfo(); // Device_info table cleared.
        requestBaseService.twoFactorStatusUpdateAccount("0", "12349903");
    }

    @Test
    public void globalOffAccountOnEmail() throws InterruptedException {

        postAuthSetting(true);
        login(); // Account tablosunda twofactor açık olduğu için 403 hata kodu ile twofactor gerekli oluyor.
        sendChallenge("EMAIL_OTP"); // twoFactor token alınarak challenge tipi seçilerek sms adımına geçiliyor.
        authenticateChallenge("EMAIL_OTP"); // Gelen sms ile 2fa token girilerek login olunuyor.
        deviceInfoCheck(); // Device tablosunda login olunan device uuid, twoFactor ile giriş başarılı veya değil kontrolü
        login(); // Tekrar login deneniyor. Account tablosunda twoFactor açık olduğu için device doğrulansa bile tekrar twoFactore hatası almalı kontrolü
        deleteDeviceInfo(); // Device info table cleared
        requestBaseService.twoFactorStatusUpdateAccount("0", "12349903");

    }

    @Test
    public void globalOnAccountOnSMS() throws InterruptedException {

        login(); // login de 403 twoFactorRequired hatası alınır
        sendChallenge("SMS_OTP");
        authenticateChallenge("SMS_OTP"); // login olunur. x uuid'si ile
        deviceInfoCheck(); // device_info tablosu kontrol edilir.
        requestBaseService.statusCodeLogin(RequestBody.TWO_FACTOR_LOGIN); // x uuid'si ile login denenir ve 200 alınır.
        postAuthSetting(true); // Account tablosunda twoFactor true set edilir.
        login(); // Tekrar x uuid'si ile login denenir ve 403 alınır. Çünkü artık account tablosuda açık.
        deleteDeviceInfo();
        requestBaseService.twoFactorStatusUpdateAccount("0", "12349903");

    }

    @Test
    public void globalOnAccountOnEmail() throws InterruptedException {

        login(); // login de 403 twoFactorRequired hatası alınır
        sendChallenge("EMAIL_OTP");
        authenticateChallenge("EMAIL_OTP"); // login olunur. x uuid'si ile
        deviceInfoCheck(); // device_info tablosu kontrol edilir.
        requestBaseService.statusCodeLogin(RequestBody.TWO_FACTOR_LOGIN); // x uuid'si ile login denenir ve 200 alınır.
        postAuthSetting(true); // Account tablosunda twoFactor true set edilir.
        login(); // Tekrar x uuid'si ile login denenir ve 403 alınır. Çünkü artık account tablosuda açık.
        deleteDeviceInfo();
        requestBaseService.twoFactorStatusUpdateAccount("0", "12349903");
        requestBaseService.UpdateGlobalSettingTable("606", "OFF"); // Tekrar global setting kapanır.

    }

    @Test
    public void globalOnAccountOffSMS() throws InterruptedException {

        login(); // login de 403 twoFactorRequired hatası alınır.
        sendChallenge("SMS_OTP");
        authenticateChallenge("SMS_OTP");
        deviceInfoCheck(); // device_info tablosu kontrol edilir.
        requestBaseService.statusCodeLogin(RequestBody.TWO_FACTOR_LOGIN); // x uuid'si ile login denenir ve 200 alınır.
        deleteDeviceInfo();

    }

    @Test
    public void globalOnAccountOffEmail() throws InterruptedException {

        requestBaseService.UpdateGlobalSettingTable("606", "ON"); // TwoFactor all user için açılır.
        TimeUnit.SECONDS.sleep(55);
        login(); // login de 403 twoFactorRequired hatası alınır.
        sendChallenge("EMAIL_OTP");
        authenticateChallenge("EMAIL_OTP");
        deviceInfoCheck(); // device_info tablosu kontrol edilir.
        requestBaseService.statusCodeLogin(RequestBody.TWO_FACTOR_LOGIN); // x uuid'si ile login denenir ve 200 alınır.
        deleteDeviceInfo();
    }

}
