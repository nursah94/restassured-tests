package com.lifeboxBackend;

import com.lifeboxBackend.common.EndPoint;
import com.lifeboxBackend.common.RequestBody;
import com.lifeboxBackend.service.RequestBaseService;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
public class Spotify {

    @Autowired
    private RequestBaseService requestBaseService;

    private String token = null;
    private String spotifyAuthorizeUrl = null;
    private String accessCode = null; // connection code after spotify connect
    private String playlistId = null;
    private String id = null;
    private List<String> playlistIdList;
    private List<String> idForDeleteProvider;
    private static final Logger log = LoggerFactory.getLogger(Spotify.class);

    public void spotifyGetStatus(String expectedConnectStatus) {

        this.token = requestBaseService.getXAuthToken(RequestBody.PREMIUM_LOGIN_REQUEST_BODY);
        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.get(EndPoint.SPOTIFY_GET_STATUS);
        int statusCode = response.getStatusCode();

        boolean connected = response.jsonPath().getBoolean("value.connected");

        if (expectedConnectStatus.equals("false") && statusCode == 200) {

            Assert.assertFalse(connected);
            Assert.assertEquals(200, statusCode);

        } else if (expectedConnectStatus.equals("true") && statusCode == 200) {

            Assert.assertTrue(connected);
            Assert.assertEquals(200, statusCode);
        } else {
            log.error(" [ Spotify /migration/spotify/status api response code is not 200 ] [ Please check response code ] :" + statusCode +
                    " [ Api response Body ] : " + response.getBody().asString());
        }

    }

    public void spotifyyGetAuthorizeUrl() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.get(EndPoint.SPOTIFY_GET_AUTHORIZE_URL);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ ERROR ][ MSISDN ] : 1122246135 [ spotifyGetAuthorizeUrl API RESPONSE CODE NOT 200 ] [ RESPONSE BODY ] :" + response.getBody().asString());
        }
        Assert.assertEquals(200, statusCode);
        this.spotifyAuthorizeUrl = response.getBody().asString();

    }

    public void spotifySuccessConnection(String expectedCase) throws InterruptedException {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        if (expectedCase.equals("success")) {

            System.setProperty("webdriver.chrome.driver", "C:/Users/ext02d15231/Desktop/xxxx/chromedriver.exe");
            ChromeOptions chromeOptions = new ChromeOptions();
            // chromeOptions.addArguments("--headless");
            WebDriver chromeDriver = new ChromeDriver(chromeOptions);
            chromeDriver.get(this.spotifyAuthorizeUrl);

            TimeUnit.SECONDS.sleep(2);

            WebElement emailAdress = chromeDriver.findElement(By.id("login-username"));
            WebElement password = chromeDriver.findElement(By.id("login-password"));
            WebElement button = chromeDriver.findElement(By.id("login-button"));

            emailAdress.sendKeys("lifespotify12@gmail.com");
            password.sendKeys("Lifebox12");
            button.click();
            TimeUnit.SECONDS.sleep(3);

            String currentUrl = chromeDriver.getCurrentUrl(); // Get opened new url
            String[] split = currentUrl.split("code=");
            this.accessCode = split[1];// Get access token from new opened url
            System.out.println(this.accessCode);

            chromeDriver.quit();

            requestSpecification.queryParam("code", this.accessCode);

            Response response = requestSpecification.post(EndPoint.SPOTIFY_CONNECT);
            int statusCode = response.getStatusCode();
            String status = response.jsonPath().getString("status");
            Boolean value = response.jsonPath().getBoolean("value");

            Assert.assertEquals(200, statusCode);
            Assert.assertTrue(value);
            Assert.assertEquals("OK", status);

        } else if (expectedCase.equals("fail")) {

            requestSpecification.queryParam("code", "test");
            Response response = requestSpecification.post(EndPoint.SPOTIFY_CONNECT);
            int statusCode = response.getStatusCode();
            String status = response.jsonPath().getString("status");
            String value = response.jsonPath().getString("value");

            Assert.assertEquals(412, statusCode);
            Assert.assertEquals("3108", status);
            Assert.assertEquals("Spotify Code Invalid", value);
        }

    }

    public void spotifyGetPlaylist(String expectedCase) throws JSONException {

        ArrayList<String> arrayList = new ArrayList<String>();

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.get(EndPoint.SPOTIFY_GET_PLAYLIST);
        int statusCode = response.getStatusCode();

        if (expectedCase.equals("ConnectionNeeded")) {

            String status = response.getBody().jsonPath().getString("status");
            String value = response.getBody().jsonPath().getString("value");

            Assert.assertEquals("3107", status);
            Assert.assertEquals("Connection Needed", value);
            Assert.assertEquals(412, statusCode);

        } else if (expectedCase.equals("success")) {

            Assert.assertEquals(200, statusCode);
            JSONObject parentObject = new JSONObject(response.asString());
            JSONArray parentArray = parentObject.getJSONArray("value");

            for (int i = 1; i < 3; i++) { // get two playlistID

                JSONObject finalObject = parentArray.getJSONObject(i);
                String playlistId = finalObject.getString("playlistId");
                System.out.println(playlistId);

                arrayList.add(playlistId);
                this.playlistIdList = arrayList;

                Assert.assertNotNull(playlistId);
            }
            this.playlistId = playlistIdList.get(0);

        }
    }

    public void spotifyGetTrackOfSelectedPlaylist(String expectedCase) {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        if (expectedCase.equals("success")) {

            requestSpecification.queryParam("playlistId", this.playlistId);
            Response response = requestSpecification.get(EndPoint.SPOTIFY_GET_TRACK_OF_SELECTED_PLAYLIST);
            int statusCode = response.getStatusCode();

            Assert.assertEquals(200, statusCode);
            log.info(" [ Spotify track list of selected playlist Id ] [ PlaylistId ] : " + this.playlistId +
                    " [ Track list info ] :" + response.getBody().asString());

        } else if (expectedCase.equals("ConnectionNeeded")) {

            requestSpecification.queryParam("playlistId", this.playlistId);
            Response response = requestSpecification.get(EndPoint.SPOTIFY_GET_TRACK_OF_SELECTED_PLAYLIST);

            int statusCode = response.getStatusCode();
            String status = response.getBody().jsonPath().getString("status");
            String value = response.getBody().jsonPath().getString("value");

            Assert.assertEquals(412, statusCode);
            Assert.assertEquals("Connection Needed", value);
            Assert.assertEquals("3107", status);

        } else if (expectedCase.equals("NotFound")) {

            requestSpecification.queryParam("playlistId", 1);
            Response response = requestSpecification.get(EndPoint.SPOTIFY_GET_TRACK_OF_SELECTED_PLAYLIST);

            int statusCode = response.getStatusCode();
            String status = response.getBody().jsonPath().getString("status");
            String value = response.getBody().jsonPath().getString("value");

            Assert.assertEquals(412, statusCode);
            Assert.assertEquals("Not found", value);
            Assert.assertEquals("3117", status);
        }

    }

    public void spotifyStart() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        requestSpecification.body(playlistIdList);

        Response response = requestSpecification.post(EndPoint.SPOTIFY_START);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ Spotify start api status code not 200 ] [ Please check api response ] [ Api response code ] :" + statusCode +
                    " [ Response Body ] :" + response.getBody().asString());
        }
        String status = response.getBody().jsonPath().getString("status");
        boolean value = response.getBody().jsonPath().getBoolean("value");

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("OK", status);
        Assert.assertTrue(value);

    }

    public void spotifyProviderPlaylist() throws JSONException {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.get(EndPoint.SPOTIFY_PROVIDER_PLAYLIST);

        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ Spotify get provider playlist api status code not 200 ] [ Please check api response ]" +
                    "[ Status Code ] :" + statusCode + " [ Response Body ] :" + response.getBody().asString());
        } else {

            JSONObject parentObject = new JSONObject(response.asString());
            JSONArray parentArray = parentObject.getJSONArray("value");

            for (int i = 1; i < parentArray.length(); i++) {

                JSONObject finalObject = parentArray.getJSONObject(i);
                String playlistId = finalObject.getString("playlistId");
                String id = finalObject.getString("id");
                this.id = id;

                Assert.assertNotNull(playlistId);
            }


            Assert.assertEquals(200, statusCode);
        }


    }

    public void spotifyProviderTrack() throws InterruptedException {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        TimeUnit.SECONDS.sleep(2);
        requestSpecification.queryParam("playlistId", this.id);
        requestSpecification.queryParam("size", "50");
        requestSpecification.queryParam("page", "0");
        requestSpecification.queryParam("sortBy", "name");
        requestSpecification.queryParam("sortOrder", "ASC");
        TimeUnit.SECONDS.sleep(2);

        Response response = requestSpecification.get(EndPoint.SPOTIFY_PROVIDER_TRACK);

        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ Spotify get provider track api status code not 200 ] [ Please check api response ]" +
                    "[ Status Code ] :" + statusCode + " [ Response Body ] :" + response.getBody().asString());
        }

        Assert.assertEquals(200, statusCode);
    }

    public void spotifyStop() {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.post(EndPoint.SPOTIFY_STOP);

        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ Spotify stop api status code not 200 ] [ Please check api response ] [ Api response code ] :" + statusCode +
                    " [ Response Body ] :" + response.getBody().asString());
        }
        String status = response.getBody().jsonPath().getString("status");
        boolean value = response.getBody().jsonPath().getBoolean("value");

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("OK", status);
        Assert.assertTrue(value);
    }

    public void spotifDisconnect(String expectedCase) {

        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);

        Response response = requestSpecification.post(EndPoint.SPOTIFY_DISCONNECT);
        int statusCode = response.getStatusCode();
        String status = response.jsonPath().getString("status");
        Boolean value = response.jsonPath().getBoolean("value");

        if (expectedCase.equals("success")) {

            Assert.assertEquals("OK", status);
            Assert.assertTrue(value);
            Assert.assertEquals(200, statusCode);

        } else if (expectedCase.equals("ConnectionNeeded")) {

            String values = response.jsonPath().getString("value");

            Assert.assertEquals("Connection Needed", values);
            Assert.assertEquals(412, statusCode);
            Assert.assertEquals("3107", status);
        }

    }

    public void deleteProviderPlayList() {

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(this.id);
        this.idForDeleteProvider = arrayList;
        RequestSpecification requestSpecification = requestBaseService.requestSpecification();
        requestSpecification.header("X-Auth-Token", token);
        requestSpecification.body(this.idForDeleteProvider);

        Response response = requestSpecification.delete(EndPoint.SPOTIFY_DELETE_PROVIDER_PLAYLIST);

        int statusCode = response.getStatusCode();

        if (statusCode != 200) {

            log.error(" [ spotify deleteProviderPlaylist api status code not 200 ] [ Please check api response ] [ Api statusCode ] : " + statusCode + " [ Response Body ] :  " + response.getBody().asString());
        } else {

            String status = response.getBody().jsonPath().getString("status");
            boolean value = response.getBody().jsonPath().getBoolean("value");

            Assert.assertEquals(200, statusCode);
            Assert.assertEquals("OK", status);
            Assert.assertTrue(value);
        }

    }

    @Test
    public void spotifyConnectSuccess() throws JSONException, InterruptedException { /** spotify successful connection case*/

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifDisconnect("success");
    }

    @Test
    public void spotifyInvalidConnect() throws InterruptedException { /** Invalıd connection without spotify token */

        spotifyGetStatus("false");
        spotifySuccessConnection("fail");
    }

    @Test
    public void spotifyGetPlaylistSuccess() throws JSONException, InterruptedException { /**  spotify access to successful playlists*/

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifDisconnect("success");
    }

    @Test
    public void spotifyGetPlaylistConnectionNeeded() throws JSONException { /** access playlists without a spotify connection*/

        spotifyGetStatus("false");
        spotifyGetPlaylist("ConnectionNeeded");
    }

    @Test
    public void spotifyGetPlaylistTrackSuccess() throws InterruptedException, JSONException { /** Successful listing of track in the spotify playlist */

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifyGetTrackOfSelectedPlaylist("success");
        spotifDisconnect("success");
    }

    @Test
    public void spotifyGetPlaylistTrackConnectionNeeded() { /** access track within a playlist without a spotify connection  */

        spotifyGetStatus("false");
        spotifyGetTrackOfSelectedPlaylist("ConnectionNeeded");
    }

    @Test
    public void spotifyGetPlaylistTrackNotFound() throws InterruptedException { /** access track list with a non-playlist */

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetTrackOfSelectedPlaylist("NotFound");
        spotifDisconnect("success");

    }

    @Test
    public void spotifyDisconnectSuccess() throws InterruptedException { /** Success Disconnect from spotify*/

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifDisconnect("success");
    }

    @Test
    public void spotifyDisconnectConnectionNeeded() { /** disconnection without connection */

        spotifyGetStatus("false");
        spotifDisconnect("ConnectionNeeded");
    }

    @Test
    public void spotifyStartSuccess() throws InterruptedException, JSONException {

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifyStart();
        spotifDisconnect("success");

    }

    @Test
    public void spotifyStopSuccess() throws InterruptedException, JSONException {

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifyStart();
        spotifyStop();
        spotifDisconnect("success");

    }

    @Test
    public void spotifyProviderPlayListSuccess() throws InterruptedException, JSONException {

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifyStart();
        spotifyProviderPlaylist();
        spotifDisconnect("success");

    }

    @Test
    public void spotifyProviderTrackSuccess() throws InterruptedException, JSONException {

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifyStart();
        spotifyProviderPlaylist();
        spotifyProviderTrack();
        spotifDisconnect("success");

    }

    @Test
    public void spotifyDeleteProviderPlaylistSuccess() throws InterruptedException, JSONException {

        spotifyGetStatus("false");
        spotifyyGetAuthorizeUrl();
        spotifySuccessConnection("success");
        spotifyGetStatus("true");
        spotifyGetPlaylist("success");
        spotifyStart();
        spotifyProviderPlaylist();
        deleteProviderPlayList();
        spotifDisconnect("success");

    }
}
