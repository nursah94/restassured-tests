package com.lifeboxBackend;

import com.google.gson.Gson;
import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.dto.VerifyOtpDto;
import com.lifeboxBackend.entity.NotificationJob;
import com.lifeboxBackend.repository.NotificationJobRepository;
import com.lifeboxBackend.requestModel.SendVerificationSms;
import com.lifeboxBackend.requestModel.VerifyPhoneNumber;
import com.lifeboxBackend.service.RequestBaseService;
import groovy.util.logging.Slf4j;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SignUp {

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    @Autowired
    private RequestBaseService requestBaseService;

    private static HashMap<String, Object> testContext = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(SignUp.class);

    private static String referenceToken;

    private String msisdn;

    public void signUp(String requestBody) {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.body(requestBody);
        Response response = request.post(EndPoint.SIGN_UP);

        int statusCode = response.getStatusCode();
        String referenceToken = response.getBody().jsonPath().getString("value.referenceToken"); // Signup api'sinin responeseBody'sinden referenceToken params get edildi.

        SendVerificationSms sendVerificationSms = new SendVerificationSms();     // entity' ın altında SendVerificationSms.java içerisinde objeler oluşturuldu. Objeler gson ile json formatına çevrildi. ve referenceToken parametresi dinamik olarak set edildi.
        sendVerificationSms.setReferenceToken(referenceToken);
        Gson gson = new Gson();
        String json = gson.toJson(sendVerificationSms); //SendVerificationSms.java objeleri json formatına çevrildi.
        testContext.put("json", json);
        Assert.assertEquals(200, statusCode);
        this.referenceToken = referenceToken;
    }

    public void sendVerification() {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.body((testContext.get("json")));
        Response response = request.post(EndPoint.SEND_VERIFICATION_SMS);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

    }

    public void verifyPhone(String msisdn) {

        NotificationJob byId = notificationJobRepository.findFirstByRequestBodyContainingOrderByCreatedDateDesc(msisdn);
        String requestBody = byId.getRequestBody();

        VerifyOtpDto verifyOtpDto = new Gson().fromJson(requestBody, VerifyOtpDto.class);
        VerifyPhoneNumber verifyPhoneNumber = new VerifyPhoneNumber();
        verifyOtpDto.getOtp();
        verifyPhoneNumber.setOtp(verifyOtpDto.getOtp());
        verifyPhoneNumber.setReferenceToken(this.referenceToken);
        Gson gson = new Gson();
        String json = gson.toJson(verifyPhoneNumber);

        RequestSpecification request = requestBaseService.requestSpecification();
        request.body(json);

        Response response = request.post(EndPoint.VERIFY_PHONE_NUMBER);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("The user cannot signUp without filling in the e-mail address.")
    public void signupWithMissingEmail() {

        signUp(requestBaseService.generateSignUpRequest());
        sendVerification();
        verifyPhone(requestBaseService.getMsisdn());
        requestBaseService.updateAccount(requestBaseService.getMsisdn());

    }
}

