package com.lifeboxBackend;

import com.google.gson.Gson;
import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.dto.VerifyOtpDto;
import com.lifeboxBackend.entity.NotificationJob;
import com.lifeboxBackend.repository.GlobalRepository;
import com.lifeboxBackend.repository.NotificationJobRepository;
import com.lifeboxBackend.requestModel.VerifyEmail;
import com.lifeboxBackend.service.RequestBaseService;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EmailVerification {

    @Autowired
    private RequestBaseService requestBaseService;

    @Autowired
    private GlobalRepository globalRepository;

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    private String token = null;
    private HashMap<String, Object> testContext = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(EmailVerification.class);
    private String msisdn;

    public void accountInfoBeforeVerifyEmail(String msisdn) throws ParseException {

        this.token = requestBaseService.getXAuthToken(requestBaseService.generateLoginRequest());

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        Response response = requestSpecification.get(EndPoint.ACCOUNT_INFO);
        int statusCode = response.getStatusCode();

        String emailVerificationRemainingDays = response.jsonPath().getString("emailVerificationRemainingDays");
        boolean emailVerified = response.jsonPath().getBoolean("emailVerified");
        String email = response.jsonPath().getString("email");

        /** CREATED_DATE OF ACCOUNT_TABLE */
        long createdDate = response.jsonPath().getLong("createdDate");
        Date date = new Date(createdDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        /**EMAIL_VERIFICATION_DURATION_IN_DAYS OF GLOBAL_SETTING TABLE */
        String valueOfDurationDay = requestBaseService.getValueGlobalSettingTable("708");
        int durationDay = Integer.parseInt(valueOfDurationDay);

        /**EMAIL_VERIFICATION_CHECK_DATE_AFTER OF GLOBAL_SETTING */
        String valueOfCheckDateAfter = requestBaseService.getValueGlobalSettingTable("707");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = dateFormat.parse(valueOfCheckDateAfter);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        /**TODAY */
        DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        Date date3 = new Date();
        dateFormat1.format(date3);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date3);

        long between = DAYS.between(date1.toInstant(), date.toInstant());
        if (between > 0) { /**THE DIFFERENCE BETWEEN ACCOUNT.CREATED_DATE AND DATE OF DEPLOYMENT TO LİVE ( GLOBALSETTING.EMAIL_VERIFICATION_CHECK_DATE) IS MORE THAN 0 USER İS NEW USER */


            calendar.add(Calendar.DAY_OF_MONTH, durationDay);
            date = calendar.getTime();
            long newUserRemainingDay = DAYS.between(date3.toInstant(), date.toInstant());
            newUserRemainingDay = newUserRemainingDay + 1;

            String remaningDays = Long.toString(newUserRemainingDay);

            log.info(" [ Msısdn ] :" + msisdn + " [ Remaining Day ] : " + remaningDays);

            Assert.assertFalse(emailVerified);
            Assert.assertEquals(remaningDays, emailVerificationRemainingDays);

        } else {
            calendar1.add(Calendar.DAY_OF_MONTH, durationDay);
            date1 = calendar1.getTime();
            long oldUserRemainingDay = DAYS.between(date3.toInstant(), date1.toInstant());

            log.info(" [ Msısdn ] :" + msisdn + " [ Remaining Day ] : " + oldUserRemainingDay);

            if (oldUserRemainingDay < 0) {

                Assert.assertEquals(null, emailVerificationRemainingDays);
            }

            Assert.assertEquals(oldUserRemainingDay, emailVerificationRemainingDays);

        }

    }

    public void accountInfo() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        Response response = requestSpecification.get(EndPoint.ACCOUNT_INFO);
        int statusCode = response.getStatusCode();

        String emailVerificationRemainingDays = response.jsonPath().getString("emailVerificationRemainingDays");
        boolean emailVerified = response.jsonPath().getBoolean("emailVerified");

        if (statusCode != 200) {

            log.error(" [ Msısdn ] : " + msisdn + " [ accountInfo api statusCode is not 200 ] [ Api statusCode ] :" + statusCode + " [ Api response Body ] :" + response.getBody().asString());
        }
        Assert.assertEquals(200, statusCode);
        Assert.assertEquals(null, emailVerificationRemainingDays);
        Assert.assertTrue(emailVerified);

    }

    public void verifySendVerificationEmail() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        Response response = requestSpecification.post(EndPoint.VERIFY_SEND_VERIFICATION_EMAIL);

        int statusCode = response.getStatusCode();

        String status = response.jsonPath().getString("status");
        int expectedInputLength = response.jsonPath().getInt("value.expectedInputLength");

        if (statusCode != 200) {

            log.error(" [ Msısdn ] : " + msisdn + " [ sendVerificationEmail api statusCode is not 200 ] [ Api statusCode ] :" + statusCode + " [ Api response Body ] :" + response.getBody().asString());
        }

        Assert.assertEquals("sendVerificationEmail api statusCode should be 200", 200, statusCode);
        Assert.assertEquals("sendVerificationEmail api response", "OK", status);
        Assert.assertEquals(6, expectedInputLength);
    }

    public void otpCountCheck() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        for (int i = 0; i < 11; i++) {

            Response response = requestSpecification.post(EndPoint.VERIFY_SEND_VERIFICATION_EMAIL);

        }
        Response response = requestSpecification.post(EndPoint.VERIFY_SEND_VERIFICATION_EMAIL);
        int statusCode = response.getStatusCode();
        String status = response.jsonPath().getString("status");

        Assert.assertEquals("TOO_MANY_REQUESTS", status);
        Assert.assertEquals(429, statusCode);

    }

    public void getVerifyOtp(String msisdn) {

        List<NotificationJob> find = notificationJobRepository.findByMsisdn(msisdn);
        String requestBody = find.get(0).getRequestBody();
        VerifyOtpDto verifyOtpDto = new Gson().fromJson(requestBody
                , VerifyOtpDto.class);

        verifyOtpDto.getEmailVerificationToken();

        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setOtp(verifyOtpDto.getEmailVerificationToken());

        Gson gson = new Gson();
        String json = gson.toJson(verifyEmail);

        this.testContext.put("json", json);
    }

    public void verifyEmailAdress(String msisdn, Object reqBody) {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        requestSpecification.body(reqBody);

        Response response = requestSpecification.post(EndPoint.VERIFY_EMAIL_ADRESS);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ Msısdn ] : " + msisdn + " [ verifyEmailAdress api statusCode is not 200 ] [ Api statusCode ] :" + statusCode + " [ Api response Body ] :" + response.getBody().asString());
        }

        Assert.assertEquals(200, statusCode);

    }

    public void notVerifyEmailAdress(String msisdn, Object reqBody) {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        requestSpecification.body(reqBody);

        Response response = requestSpecification.post(EndPoint.VERIFY_EMAIL_ADRESS);
        int statusCode = response.getStatusCode();
        String status = response.jsonPath().getString("status");

        Assert.assertEquals(412, statusCode);
        Assert.assertEquals("INVALID_OTP", status);

    }

    @Test
    public void newUserVerifyEmail() throws ParseException, InterruptedException {

        requestBaseService.signUp(requestBaseService.generateSignUpRequest());
        requestBaseService.sendVerification();
        requestBaseService.verifyPhone(requestBaseService.getMsisdn());
        accountInfoBeforeVerifyEmail(requestBaseService.getMsisdn());
        verifySendVerificationEmail();
        getVerifyOtp(requestBaseService.getMsisdn());
        verifyEmailAdress(requestBaseService.getMsisdn(), testContext.get("json"));
        accountInfo();
        requestBaseService.updateAccount(requestBaseService.getMsisdn());

    }

    @Test
    public void remainingDayControl() throws ParseException, InterruptedException {

        requestBaseService.signUp(requestBaseService.generateSignUpRequest());
        requestBaseService.sendVerification();
        requestBaseService.verifyPhone(requestBaseService.getMsisdn());
        accountInfoBeforeVerifyEmail(requestBaseService.getMsisdn());
        requestBaseService.updateAccountCreatedDate(requestBaseService.getMsisdn());
        accountInfoBeforeVerifyEmail(requestBaseService.getMsisdn());
        verifySendVerificationEmail();
        getVerifyOtp(requestBaseService.getMsisdn());
        verifyEmailAdress(requestBaseService.getMsisdn(), testContext.get("json"));
        accountInfo();
        requestBaseService.updateAccount(requestBaseService.getMsisdn());

    }


    @Test
    public void secondOtpControl() throws ParseException, InterruptedException {

        requestBaseService.signUp(requestBaseService.generateSignUpRequest());
        requestBaseService.sendVerification();
        requestBaseService.verifyPhone(requestBaseService.getMsisdn());
        accountInfoBeforeVerifyEmail(requestBaseService.getMsisdn());
        verifySendVerificationEmail();
        getVerifyOtp(requestBaseService.getMsisdn());
        verifySendVerificationEmail();
        notVerifyEmailAdress(requestBaseService.getMsisdn(), testContext.get("json"));
        getVerifyOtp(requestBaseService.getMsisdn());
        verifyEmailAdress(requestBaseService.getMsisdn(), testContext.get("json"));
        requestBaseService.updateAccount(requestBaseService.getMsisdn());

    }

    @Test
    public void otpCountControl() throws InterruptedException, ParseException {

        requestBaseService.signUp(requestBaseService.generateSignUpRequest());
        requestBaseService.sendVerification();
        requestBaseService.verifyPhone(requestBaseService.getMsisdn());
        accountInfoBeforeVerifyEmail(requestBaseService.getMsisdn());
        otpCountCheck();
        requestBaseService.updateAccount(requestBaseService.getMsisdn());

    }


}
