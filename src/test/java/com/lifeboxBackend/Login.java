package com.lifeboxBackend;


import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.common.RequestBody;
import com.lifeboxBackend.service.RequestBaseService;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Login {

    @Autowired
    private RequestBaseService requestBaseService;

    private static final Logger log = LoggerFactory.getLogger(Login.class);
    @Test
    public void login(){

        RequestSpecification requestSpecification = requestBaseService.requestSpecification(); //obje yarattık
        requestSpecification.body(RequestBody.STANDART_LOGIN_REQUEST_BODY); //parantez içindekini al requestSpecification.body içine ekle demek

        Response response = requestSpecification.post(EndPoint.LOGIN); //post atan request atan bu

        int statusCode = response.getStatusCode();
        log.info("Status Code = {}",statusCode);

    }
}
