package com.lifeboxBackend;

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
import com.lifeboxBackend.common.EndPoint;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j

public class AccountDelete {

    @Autowired
    private RequestBaseService requestBaseService;

    private static String token;

    private static final Logger log = LoggerFactory.getLogger(AccountDelete.class);

    public void accountDelete() {

        this.token = requestBaseService.getXAuthToken(requestBaseService.generateLoginRequest());
        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.delete(EndPoint.ACCOUNT_DELETE);
        int statusCode = response.getStatusCode();

        if (statusCode != 204) {
            log.error("[ account/delete API RESPONSE CODE IS NOT 200 ] [ PLEASE CHECK API RESPONSE ]");
        }
        Assert.assertEquals(204, statusCode);
    }

    public void firstLoginAfterAccountDelete() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.body(requestBaseService.generateLoginRequest());
        requestSpecification.header("X-Captcha-Id","123123");
        requestSpecification.header("X-Captcha-Answer","293487");

        Response response = requestSpecification.post(EndPoint.LOGIN);

        int statusCode = response.getStatusCode();
        String xAccountStatus = response.header("X-Account-Status");

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("DELETION_REQUESTED", xAccountStatus);

    }

    public void accountStatusControl() throws InterruptedException {

        TimeUnit.SECONDS.sleep(2);
        BigDecimal status = requestBaseService.getaccountStatus(requestBaseService.getMsisdn());
        int statuss = status.intValue();

        if (statuss != 10) {

            log.error(" [ Msısdn ] :" + requestBaseService.getMsisdn() + " [ Account status should be 10 after account/delete request ]"
            );
        }
        Assert.assertEquals(10, statuss);

    }

    public void accountStatusControlAfterFirstLogin() throws InterruptedException {

        TimeUnit.SECONDS.sleep(3);
        BigDecimal status = requestBaseService.getaccountStatus(requestBaseService.getMsisdn());
        int statuss = status.intValue();
        if (statuss != 1) {

            log.error(" [ Msısdn ] :" + requestBaseService.getMsisdn() + " [ Account status should be 1 first login after account/delete process ]"
            );
        }
        Assert.assertEquals(1, statuss);

    }

    @Test
    public void accountDeleteCancelCase() throws InterruptedException { /** if the account deletion is canceled, the status of the account is set from 10 to 1. X-Account-Status = DELETION_REQUESTED of response header is displayed as a result of the first login.*/

        requestBaseService.signUp(requestBaseService.generateSignUpRequest());
        requestBaseService.sendVerification();
        requestBaseService.verifyPhone(requestBaseService.getMsisdn());
        requestBaseService.statusCodeLogin(requestBaseService.generateLoginRequest());
        accountDelete();
        accountStatusControl();
        firstLoginAfterAccountDelete();
        accountStatusControlAfterFirstLogin();
        requestBaseService.statusCodeLogin(requestBaseService.generateLoginRequest());
        requestBaseService.updateAccount(requestBaseService.getMsisdn());

    }


}
