package com.lifeboxBackend.service;

import com.google.gson.Gson;
import com.lifeboxBackend.TwoFactorAuthentication;
import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.dto.VerifyOtpDto;
import com.lifeboxBackend.entity.Account;
import com.lifeboxBackend.entity.GlobalSetting;
import com.lifeboxBackend.entity.NotificationJob;
import com.lifeboxBackend.repository.AccountRepository;
import com.lifeboxBackend.repository.BatchJobRepository;
import com.lifeboxBackend.repository.GlobalRepository;
import com.lifeboxBackend.repository.NotificationJobRepository;
import com.lifeboxBackend.requestModel.LoginRequestBody;
import com.lifeboxBackend.requestModel.SendVerificationSms;
import com.lifeboxBackend.requestModel.SignUpBody;
import com.lifeboxBackend.requestModel.VerifyPhoneNumber;
import groovy.util.logging.Slf4j;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.baseURI;

@Slf4j
@Service
public class RequestBaseService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private GlobalRepository globalRepository;

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    @Autowired
    private BatchJobRepository batchJobRepository;

    private static final Logger log = LoggerFactory.getLogger(TwoFactorAuthentication.class);
    private static HashMap<String, Object> testContext = new HashMap<>();
    private static String referenceToken;

    public String getMsisdn() {
        return msisdn;
    }

    private String msisdn;

    public io.restassured.specification.RequestSpecification requestSpecification() {
        baseURI = "https://adepotest.turkcell.com.tr/api";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Accept", "application/json");
        requestSpecification.header("Content-Type", "application/json");
        return requestSpecification;
    }

    public String getXAuthToken(String body) {

        baseURI = "https://adepotest.turkcell.com.tr/api";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Accept", "application/json");
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.header("X-Captcha-Id","123123");
        requestSpecification.header("X-Captcha-Answer","293487");

        requestSpecification.body(body);

        Response response = requestSpecification.post(EndPoint.LOGIN);

        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ Authentication api response not 200 ] [ Please check api response ] [ Response code ]" + statusCode +
                    " [ Response body ] :" + response.getBody().asString() + " [ Request Body ] :" + body);
        }
        String XAuthToken = response.header("X-Auth-Token");

        return XAuthToken;
    }

    public void statusCodeLogin(String body) {

        RequestSpecification requestSpecification = requestSpecification();
        requestSpecification.body(body);
        requestSpecification.header("X-Captcha-Id","123123");
        requestSpecification.header("X-Captcha-Answer","293487");
        Response response = requestSpecification.post(EndPoint.LOGIN);

        int statusCode = response.getStatusCode();
        String xAccountStatus = response.header("X-Account-Status");

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("ACTIVE", xAccountStatus);
    }

    public int UpdateGlobalSettingTable(String ıd, String val) { // Global_setting tablosunda ıd 'si verilen alanı update eder.

        int byId = globalRepository.update(val, ıd);
        Assert.assertEquals(byId, 1);
        if (byId == 1) {

            log.info(" [ Global Setting Table successfully updated ] [ Updated ID ] :"
                    + ıd + " [ Updated value ] :" + val);
            return byId;
        } else {

            log.info(" [ DB cannot update ]");
            return 0;
        }

    }

    public String generateLoginRequest() {

        String username = this.msisdn;
        LoginRequestBody loginRequestBody = new LoginRequestBody();
        loginRequestBody.setUsername(username);

        Gson gson = new Gson();
        String loginReqBody = gson.toJson(loginRequestBody);
        return loginReqBody;
    }

    public String generateSignUpRequest() {

        SignUpBody signUpBody = new SignUpBody();
        signUpBody.setEmail(generateRandomEmail());
        signUpBody.setPhoneNumber(generateRandomMsısdn());

        Gson gson = new Gson();
        String signUpRequestBody = gson.toJson(signUpBody);  //jsona çevirmek için
        this.msisdn = signUpBody.getPhoneNumber();
        return signUpRequestBody;
    }

    public void deleteAccount(String msisdn) {

        int delete = accountRepository.deleteAccount(msisdn);

        if (delete > 0) {

            log.info(" [ Account Succesfully Deleted ] [ Deleted msisdn ] :" + msisdn);
        }
    }

    public void deleteBatchJobRecordGivenMsisdnOrAccountId(String msisdn, int account_id) {

        int delete = batchJobRepository.delete(msisdn, account_id);

        if (delete > 0) {

            log.info(" [ Batch_Job record succesfully deleted ] [ Account_id ] :" + account_id + " [ Msısdn ] :" + msisdn);
        }
    }

    public int getTwoFactorStatusAccount(String msısdn) { // Account tablosunda bulunan TWO_FACTOR_AUTH bilgisini döner. 1 = on 0 = off

        Account byStatus = accountRepository.findBy(msısdn);
        int status = byStatus.getTwoFactorAuth();
        return status;
    }

    public void twoFactorStatusUpdateAccount(String val, String username) { // TWO_FACTOR_AUTH kolonunu update eder.

        int byStatus = accountRepository.updateTwoFactorAccount(val, username);
        Assert.assertEquals(byStatus, 1);
        if (byStatus == 1) {

            log.info(" [ Acount table twoFactorAuthentication param succesfully updated ] [ Updated msısdn ] :"
                    + username + " [Updated value ] :" + val);
        }
    }

    public void updateAccountCreatedDate(String msisdn) { /** Update account.createdDate given msisdn*/

        Account byId = accountRepository.findBy(msisdn);
        Date createdDate = byId.getCreatedDate();
        Calendar c = Calendar.getInstance();
        c.setTime(createdDate);
        c.add(Calendar.DATE, -2);
        createdDate = c.getTime();

        int updateAccountCreatedDate = accountRepository.updateAccountCreatedDate(msisdn, createdDate);
        Assert.assertEquals(1, updateAccountCreatedDate);
    }

    public int getAccountId(String msısdn) { /**Return account_id info given msisdn */

        Account byId = accountRepository.findBy(msısdn);
        int ıd = byId.getId();
        return ıd;
    }

    public int getAccountIdAfterAccountDeletion(String msısdn) { /**Return account_id info given msisdn */

        Account byId = accountRepository.findAccountIdAfterDeletion(msısdn);
        int ıd = byId.getId();
        return ıd;
    }

    public BigDecimal getStatusByUsername(String msisdn) {

        Account byId = accountRepository.findBy(msisdn);
        if (byId != null) {
            log.info("Status Found,User msisdn:{}", byId.getMsisdn());
            BigDecimal status = byId.getStatus();
            BigDecimal type = byId.getType();
            return status;
        }
        log.warn("Status Not Found,User msisdn:{}", msisdn);
        return null;
    }

    public BigDecimal getaccountStatus(String msisdn) {

        Account byId = accountRepository.getaccountStatus(msisdn);
        if (byId != null) {
            log.info("Status Found,User msisdn:{}", byId.getMsisdn());
            BigDecimal status = byId.getStatus();
            return status;
        }
        log.warn("Status Not Found,User msisdn:{}", msisdn);
        return null;
    }

    public void updateAccount(String msisdn) {

        BigDecimal status = getStatusByUsername(msisdn);
        String accountStatus = status.toString();

        if (accountStatus.equals("1")) {

            int byStatus = accountRepository.update(msisdn);
            Assert.assertEquals(1, byStatus);

        }
    }

    public String getValueGlobalSettingTable(String id) {

        GlobalSetting findValue = globalRepository.findBy(id);
        String value = findValue.getValue();
        return value;
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();

        for (T element : list) { //İlk liste boyunca geziyor.

            if (!newList.contains(element)) { // eğer eleman yeni dizide yoksa ekliyor.

                newList.add(element);
            }
        }
        return newList;
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public void signUp(String requestBody) {

        RequestSpecification request = requestSpecification();
        request.body(requestBody);
        Response response = request.post(EndPoint.SIGN_UP);

        int statusCode = response.getStatusCode();
        String referenceToken = response.getBody().jsonPath().getString("value.referenceToken"); // Signup api'sinin responeseBody'sinden referenceToken params get edildi.

        SendVerificationSms sendVerificationSms = new SendVerificationSms();     // entity' ın altında SendVerificationSms.java içerisinde objeler oluşturuldu. Objeler gson ile json formatına çevrildi. ve referenceToken parametresi dinamik olarak set edildi.
        sendVerificationSms.setReferenceToken(referenceToken);
        Gson gson = new Gson();
        String json = gson.toJson(sendVerificationSms); //SendVerificationSms.java objeleri json formatına çevrildi.
        testContext.put("json", json);

        if (statusCode != 200) {

            log.error(" [ signUp api status code not 200 ] [ Please check api response ] [ Response code ] :" + statusCode +
                    " [ Response body ] :" + response.getBody().asString());

        }
        Assert.assertEquals(200, statusCode);

        log.info(" [ signUp successfully working ] [ signUp api response body ] :" + response.getBody().asString());

        this.referenceToken = referenceToken;
    }

    public void sendVerification() {

        RequestSpecification request = requestSpecification();
        request.body((testContext.get("json")));
        Response response = request.post(EndPoint.SEND_VERIFICATION_SMS);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [sendVerification api status code not 200 ] [ Please check api response ] [ Response code] :" + statusCode +
                    " [ Response body ] :" + response.getBody().asString());
        }
        log.info(" [ SendVerification api succesfully working ][ SendVerification api response ] : " + response.getBody().asString());
        Assert.assertEquals(200, statusCode);

    }

    public String generateRandomMsısdn() {

        int aNumber = 0;
        aNumber = (int) ((Math.random() * 900000000) + 100000000);
        String msısdn = Integer.toString(aNumber);
        return msısdn;

    }

    public String generateRandomEmail() {

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(1000);
        int randomInt1 = randomGenerator.nextInt(10000);
        String email = ("lifebox" + randomInt + "test" + randomInt1 + "@lifebox.net");
        return email;
    }


    public String getMsısdnFromRequestBody() {

        SignUpBody signUpBody = new SignUpBody();
        return signUpBody.getPhoneNumber();
    }


    public void verifyPhone(String msisdn) throws InterruptedException {

        TimeUnit.SECONDS.sleep(2);

        VerifyPhoneNumber verifyPhoneNumber = new VerifyPhoneNumber();
        verifyPhoneNumber.setOtp("293487");
        verifyPhoneNumber.setReferenceToken(this.referenceToken);
        Gson gson = new Gson();
        String json = gson.toJson(verifyPhoneNumber);

        RequestSpecification request = requestSpecification();
        request.body(json);

        Response response = request.post(EndPoint.VERIFY_PHONE_NUMBER);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ verifyPhone api status code not 200 ] [ Please check api response ] [ Response code ] :" + statusCode +
                    " [ Response body ] :" + response.getBody().asString());
        }
        log.info(" [ verifyPhone api succesfully working ] [ verifyPhone api response ]" + response.getBody().asString());

        Assert.assertEquals(200, statusCode);

    }

}