package com.lifeboxBackend;

import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.common.RequestBody;
import com.lifeboxBackend.entity.Asistant_Info;
import com.lifeboxBackend.repository.AssistantInfoRepository;
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


@RunWith(SpringRunner.class)  //hepsine ekle
@SpringBootTest
@Slf4j
public class AssistantInfo {

    @Autowired
    private RequestBaseService requestBaseService; //servise bağlıyor. Serviste ortak şeyler var login gibi

    @Autowired
    private AssistantInfoRepository assistantInfoRepository;  //repo ile bağlamak için

    private static final Logger log = LoggerFactory.getLogger(AssistantInfo.class); //loglama için

    private String token = null;


    private void login() {
        this.token = requestBaseService.getXAuthToken(RequestBody.ASSISTANT_INFO_LOGIN_REQUEST_BODY); //login olup token dönüyor
    }

    public void savedCard() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification(); //application/json kısmını koydun
        requestSpecification.header("X-Auth-Token",token);  //yukardakini koyduk

        Response response = requestSpecification.put(EndPoint.ASSISTANT_SAVED_CARD);  //save butonuna tıklamak!!!!

        int statusCode = response.getStatusCode();  //sadece logda kullanmak için değişken olarak tanımladım. response objesinden status codu get etmek log için.

        if (statusCode != 201) {

            log.error("[ERROR] = [NOT 201] STATUS CODE AND API RESPONSE ",statusCode,response.getBody().asString());
        }

        //save tıklayınca 1 olduğunu kontrol etmek için aşadakiler (1 rows updated yani)
        Asistant_Info assistantInfo = assistantInfoRepository.xx(); //select sorgusu atıyor nesne yarattık save butonuna tıklarkenki gibi
        int savedStatus = assistantInfo.getSaved();  //. assistantInfo yerine xx de olurdu savedStatus'a atadık (değişken tanımlayarak)aşada kontrol etmek için
        Assert.assertEquals(201, statusCode); //

    }

    public void saveCardNocontent(){

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token",token);

        Response response = requestSpecification.put(EndPoint.ASSISTANT_CARD_NOCONTENT);

        int statusCode = response.getStatusCode();

        if (statusCode != 404) {
            log.error("[ERROR] [STATUS CODE] :{} [RESPONSE] : {}",statusCode,response.getBody().asString());
        }

        Assert.assertEquals(404, statusCode);
    }

    public void changeCardStatus() {  //api çağırmıyoz bburda zaten öyle 1'i 0 yapma apisi yok dbden değiştiriyoz ( olsa pmden yaparduk:)
        int updatedSavedCard = assistantInfoRepository.updateSavedCard();  //repositorydeki db queryimizi çağırdık methodu çalıştırdık ve onu bir değişkene tanımladık aşada kullanabilmek için
        Assert.assertEquals(1, updatedSavedCard);

    }

    public void listCard() {
        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token",token);

        Response responseList = requestSpecification.get(EndPoint.ASSISTANT_LIST_CARD);

        int statusCodeList = responseList.getStatusCode();

        if (statusCodeList != 200) {

            log.error("[ERROR] [STATUS CODE] :{} [RESPONSE] : {}",statusCodeList,responseList.getBody().asString());
        }
        Assert.assertEquals(200,statusCodeList);
    }

    @Test
    public void successSavedCard() {

        login();
        savedCard();
        changeCardStatus();

    }
    @Test
    public void successListCard() {

        login();
        listCard();

    }

    @Test
    public void failedSavedCard() {

        login();
        saveCardNocontent();

    }

}
