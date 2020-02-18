package com.lifeboxBackend;

import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.common.RequestBody;
import com.lifeboxBackend.repository.InstapickRepository;
import com.lifeboxBackend.service.RequestBaseService;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Instapick {

    @Autowired
    private RequestBaseService requestBaseService;

    @Autowired
    private InstapickRepository instapickRepository;

    private static final Logger log = LoggerFactory.getLogger(Instapick.class);

    private static String token = null;
    private int used = 0;
    private int ttl = 0;
    private int remainingInstapıck = 0;
    private static List<String> uuidList = null;
    private int numberOfPhotos = 0;
    private static double enbScorePick;
    private static String requestIdentifier = null;

    public void standartLogin() {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.body(RequestBody.STANDART_LOGIN_REQUEST_BODY);
        Response response = request.post(EndPoint.LOGIN);
        //int statusCode = response.getStatusCode();
        String xAuthToken = response.header("X-Auth-Token");
        this.token = xAuthToken;
    }

    public void middleLogin() {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.body(RequestBody.MID_LOGIN_REQUEST_BODY);
        Response response = request.post(EndPoint.LOGIN);
        int statusCode = response.getStatusCode();
        String xAuthToken = response.header("X-Auth-Token");
        this.token = xAuthToken;
    }

    public void premiumLogin() {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.body(RequestBody.PREMIUM_LOGIN_REQUEST_BODY);
        Response response = request.post(EndPoint.LOGIN);
        int statusCode = response.getStatusCode();
        String xAuthToken = response.header("X-Auth-Token");
        this.token = xAuthToken;
    }

    private List<String> fileList() throws JSONException, InterruptedException {

        ArrayList<String> arrayList = new ArrayList<String>();
        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);
        TimeUnit.SECONDS.sleep(2);

        Response response = request.get(EndPoint.FILE_LIST);
        int statusCode = response.getStatusCode();

        if (statusCode == 200) {

            int num1 = requestBaseService.getRandomNumberInRange(1, 5);
            int num2 = requestBaseService.getRandomNumberInRange(6, 100);
            int index = num1 + num2;
            int num3 = index + num1;
            this.numberOfPhotos = num1;

            JSONArray jsonArray = new JSONArray(response.body().asString());
            for (int i = index; i < num3; i++) {

                String uuid = jsonArray.getJSONObject(i).getString("uuid");
                arrayList.add(uuid);
                this.uuidList = arrayList;  //uuid listte attı fotoların uuidleri

            }
            return arrayList;
        }
        return null;
    }

    public void checkJsonResponse(ArrayList<String> requestIdentifierList, ArrayList<String> uuidListArray, ArrayList<Double> scoreArray, Response response) throws JSONException {

        JSONObject parentObject = new JSONObject(response.asString());
        JSONArray parentArray = parentObject.getJSONArray("value");

        for (int i = 0; i < parentArray.length(); i++) {

            JSONObject finalObject = parentArray.getJSONObject(i);
            String requestIdentifier = finalObject.getString("requestIdentifier");
            JSONObject fileInfo = finalObject.getJSONObject("fileInfo");

            String uuid = fileInfo.get("uuid").toString();
            String hashTags = finalObject.getString("hashTags");
            String rank = finalObject.get("rank").toString();

            double score = finalObject.getDouble("score");
            requestIdentifierList.add(requestIdentifier);
            uuidListArray.add(uuid);
            scoreArray.add(score);

            Assert.assertNotNull(uuid);
            Assert.assertNotNull(score);
            Assert.assertNotNull(rank);
            Assert.assertNotNull(hashTags);
            Assert.assertNotNull(requestIdentifier);
        }
    }


    public void analyze(String msısdn) throws JSONException, InterruptedException {

        List<String> uuidList = fileList();
        ArrayList<String> requestIdentifierList = new ArrayList<>();
        ArrayList<String> uuidListArray = new ArrayList<>();
        ArrayList<Double> scoreArray = new ArrayList<>();
        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);
        TimeUnit.SECONDS.sleep(2);
        request.body(uuidList);
        Response response = request.post(EndPoint.ANALYZE);
        int statusCode = response.getStatusCode();

        if (msısdn.equals("1122135246") && statusCode == 200) {

            int total = 5;
            int usedParams = this.used;
            usedParams++;
            this.used = usedParams;

            int remain = this.remainingInstapıck;
            remain = total - usedParams;
            this.remainingInstapıck = remain;

            this.ttl = total;

            analyzeCheck(uuidList, requestIdentifierList, uuidListArray, scoreArray, response);
        } else if (msısdn.equals("5413838594") && statusCode == 200) {

            int total = 10;
            int usedParams = this.used;
            usedParams++;
            this.used = usedParams;

            int remain = this.remainingInstapıck;
            remain = total - usedParams;
            this.remainingInstapıck = remain;

            this.ttl = total;

            analyzeCheck(uuidList, requestIdentifierList, uuidListArray, scoreArray, response);

        } else if (msısdn.equals("1122246135") && statusCode == 200) {

            analyzeCheck(uuidList, requestIdentifierList, uuidListArray, scoreArray, response);

        } else if (statusCode == 412) {

            String status = response.getBody().jsonPath().getString("status");
            String value = response.getBody().jsonPath().getString("value");

            if (value.equals("Instapick no available units left")) {

                log.warn("[ Instapick no available units left ] [ Msısdn ] :" + msısdn);
            } else {

                log.error("[ Instapick Connection Problem Occured ] [ Status Code ] :" + statusCode +
                        "[ status param in response body ] :" + status + "[ value param in response body ] :" + value);

            }

        }
    }


    public void analyzeCheck(List<String> uuidList, ArrayList<String> requestIdentifierList, ArrayList<String> uuidListArray, ArrayList<Double> scoreArray, Response response) throws JSONException {
        checkJsonResponse(requestIdentifierList, uuidListArray, scoreArray, response);
        this.requestIdentifier = requestIdentifierList.get(0); //GetAnalyzeDetails api 'si için

        double enbScore = scoreArray.get(0).doubleValue();

        for (int i = 0; i < scoreArray.size(); i++) {   // Analyze api pick edilecek fotografın bilgisi

            if (enbScore < scoreArray.get(i).doubleValue()) {

                enbScore = scoreArray.get(i).doubleValue();
                this.enbScorePick = enbScore;
            }
        }
        log.info(" [ Analyze process succesfully completed ] [ Number of analyzed photo ] :" + uuidList.size()
                + " [ uuid information of analyzed photo ] :" + uuidList +
                " [ RequestIdentifier info of analyze response ] :" + requestIdentifierList.get(0));

        Assert.assertEquals(requestIdentifierList.size(), uuidList.size());
    }


    public void getAnalyzeDetails() throws JSONException, InterruptedException {

        ArrayList<String> requestIdentifierList = new ArrayList<>();
        ArrayList<String> uuidListArray = new ArrayList<>();
        ArrayList<Double> scoreArray = new ArrayList<>();

        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);
        TimeUnit.SECONDS.sleep(2);
        request.body(requestIdentifier);

        Response response = request.post(EndPoint.GET_ANALYZE_DETAILS);
        int statusCode = response.getStatusCode();

        if (statusCode == 200) {

            checkJsonResponse(requestIdentifierList, uuidListArray, scoreArray, response);
            //Assert.assertEquals(requestIdentifierList.size(), uuidList.size());
        }

    }

    public void analyzeHıstory() throws JSONException, InterruptedException {

        ArrayList<String> requestIdentifierList = new ArrayList<>();
        ArrayList<String> uuidListArray = new ArrayList<>();
        ArrayList<Double> scoreArray = new ArrayList<>();

        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);
        TimeUnit.SECONDS.sleep(2);
        Response response = request.get(EndPoint.ANALYZE_HISTORY);

        int statusCode = response.getStatusCode();

        if (statusCode == 200) {

            String reqIdentifier = this.requestIdentifier;
            double x = this.enbScorePick;
            checkJsonResponse(requestIdentifierList, uuidListArray, scoreArray, response);
            //System.out.println("Score Array =" + scoreArray);

            boolean passes = false;

            for (int i = 0; i < scoreArray.size(); i++) {
                if (scoreArray.get(i) == x) {
                    passes = true;
                }
            }
            //     Assert.assertTrue(passes);
            Assert.assertTrue(requestIdentifierList.contains(reqIdentifier));

        }
    }


    public void thumbnail() throws JSONException {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);

        Response response = request.get(EndPoint.THUMBNAIL);
        int statusCode = response.getStatusCode();

        if (statusCode == 200) {

            JSONArray jsonArray = new JSONArray(response.body().asString());
            Assert.assertTrue(jsonArray.length() <= 4);

        }
    }


    public void v1(String msısdn) throws JSONException {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);
        Response response = request.get(EndPoint.V1);

        JSONArray jsn = new JSONArray(response.asString());
        JSONObject ınstapick = jsn.getJSONObject(3);
        JSONObject jsonObject = ınstapick.getJSONObject("details");

        String isFree = jsonObject.get("isFree").toString();  //is free=hakkı var mı?
        String isEverUsed = jsonObject.get("isEverUsed").toString();
        int total = jsonObject.getInt("total");
        int remaining = jsonObject.getInt("remaining");
        int ussed = jsonObject.getInt("used");

        log.info(" [ Msısdn ] :" + msısdn + " [ Instapick total left ] :" + total + " [ Instapick used left ] :" + used + " [ Instapick remaining left ] :" + remainingInstapıck
                + " [ Instapick used params return from response ] :" + ussed + " [ Instapick remaining params return from response ] :" + remaining +
                " [ Instapick total left return from response ] :" + ttl);

        if (msısdn.equals("1122135246") || msısdn.equals("5413838594")) {

            Assert.assertEquals(used, ussed);
            Assert.assertEquals(remainingInstapıck, remaining);
            Assert.assertEquals(ttl, total);
            Assert.assertEquals("true", isEverUsed);
        } else {

            Assert.assertEquals("true", isFree);
        }


    }


    public void getCount(String msisdn) throws JSONException {

        RequestSpecification request = requestBaseService.requestSpecification();
        request.header("X-Auth-Token", token);
        Response response = request.get(EndPoint.GETCOUNT);

        int total = response.getBody().jsonPath().getInt("value.total");
        int remaining = response.getBody().jsonPath().getInt("value.remaining");
        int ussed = response.getBody().jsonPath().getInt("value.used");

        if (msisdn.equals("1122135246") || msisdn.equals("5413838594")) {

            Assert.assertEquals(used, ussed);
            Assert.assertEquals(remainingInstapıck, remaining);
            Assert.assertEquals(ttl, total);
        } else {
            Assert.assertEquals(0, total);
            Assert.assertEquals(10, remaining);
        }

    }

    public void deleteInstapick(String msısdn) {

        int status = instapickRepository.delete(msısdn);

        if (status != 0) {

            log.info(" [ Instapick rows succesfully deleted ] [ Msısdn ] :" + msısdn);
        }

    }

    @Test
    public void test1standartInstapick() throws JSONException, InterruptedException {

        for (int i = 0; i < 6; i++) {

            standartLogin();
            analyze("1122135246");
            getAnalyzeDetails();
            analyzeHıstory();
            thumbnail();
            v1("1122135246");
            getCount("1122135246");
        }

    }

    @Test
    public void test2middleInstapick() throws JSONException, InterruptedException {

        for (int i = 0; i < 11; i++) {

            middleLogin();
            analyze("5413838594");
            getAnalyzeDetails();
            analyzeHıstory();
            thumbnail();
            v1("5413838594");
            getCount("5413838594");

        }

    }

    @Test
    public void test3premiumInstapick() throws JSONException, InterruptedException {

        for (int i = 0; i < 12; i++) {

            premiumLogin();
            analyze("1122246135");
            getAnalyzeDetails();
            analyzeHıstory();
            thumbnail();
            v1("1122246135");
            getCount("1122246135");

        }

    }

    @Test
    public void test4deleteInstapickFromDB() {

        deleteInstapick("1122135246");
        deleteInstapick("1122246135");
        deleteInstapick("5413838594");
    }


}

