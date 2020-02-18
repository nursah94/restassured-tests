
package com.lifeboxBackend.common;

public interface EndPoint {

    String SIGN_UP = "/signup";
    String SEND_VERIFICATION_SMS = "/verify/sendVerificationSMS";
    String VERIFY_PHONE_NUMBER = "/verify/phoneNumber";
    String LOGIN = "/auth/token?rememberMe=on";
    String ANALYZE = "/instapick/analyze";
    String FILE_LIST = "/search/byField?fieldName=content_type&fieldValue=image&page=0&size=200&sortBy=createdDate&sortOrder=DESC";
    String GET_ANALYZE_DETAILS = "/instapick/getAnalyzeDetails";
    String ANALYZE_HISTORY = "/instapick/getAnalyzeHistory?pageSize=100&pageNumber=0";
    String THUMBNAIL = "/instapick/thumbnails";
    String V1 = "/assistant/v1";
    String GETCOUNT = "/instapick/getCount";
    String SENDCHALLENGE = "/auth/2fa/challenge";
    String AUTHENTICATECHALLENGE = "/auth/2fa/token";
    String GET_AUTH_SETTING = "/auth/settings ";
    String POST_AUTH_SETTING = "/auth/settings ";
    String VERIFY_SEND_VERIFICATION_EMAIL = "/verify/sendVerificationEmail";
    String VERIFY_EMAIL_ADRESS = "/verify/emailAddress";
    String ACCOUNT_INFO = "/account/info";
    String ACCOUNT_DELETE ="/account/delete";
    String ADMIN_XML ="/services/allAccessSubscriptionOperations.wsdl";
    String SPOTIFY_GET_STATUS = "/migration/spotify/status";
    String SPOTIFY_GET_AUTHORIZE_URL = "/migration/spotify/authorizeUrl";
    String SPOTIFY_CONNECT = "/migration/spotify/connect";
    String SPOTIFY_GET_PLAYLIST = "/migration/spotify/playlist?page=0&size=50";
    String SPOTIFY_GET_TRACK_OF_SELECTED_PLAYLIST = "/migration/spotify/playlist/track";
    String SPOTIFY_DISCONNECT = "/migration/spotify/disconnect";
    String SPOTIFY_START = "/migration/spotify/start";
    String SPOTIFY_STOP ="/migration/spotify/stop";
    String SPOTIFY_PROVIDER_PLAYLIST= "/migration/spotify/provider/playlist?page=0&size=50&sortBy=name&sortOrder=ASC";
    String SPOTIFY_PROVIDER_TRACK = "/migration/spotify/provider/playlist/track";
    String SPOTIFY_DELETE_PROVIDER_PLAYLIST ="/migration/spotify/provider/playlist";
    String SPOTIFY_DELETE_PROVIDER_TRACK="/migration/spotify/provider/playlist/track";
    String ASSISTANT_SAVED_CARD="/assistant/v1/9635"; //9635 idli card'in apisi
    String ASSISTANT_LIST_CARD="/assistant/v1";
    String ASSISTANT_CARD_NOCONTENT="/assistant/v1/3";




}
