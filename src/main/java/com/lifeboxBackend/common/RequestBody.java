package com.lifeboxBackend.common;

public interface RequestBody {


    String STANDART_LOGIN_REQUEST_BODY = "{\n" +
            "    \"username\": \"1122135246\",\n" +
            "    \"password\": \"12866173ag\",\n" +
            "    \"deviceInfo\": {\n" +
            "        \"uuid\": \"SMART-HOME-UUID-1233323\",\n" +
            "        \"name\": \"Smart Home Device\",\n" +
            "        \"deviceType\": \"SMART_HOME\"\n" +
            "    }\n" +
            "}";
    String MID_LOGIN_REQUEST_BODY = "{\n" +
            "    \"username\": \"5413838594\",\n" +
            "    \"password\": \"12866173ag\",\n" +
            "    \"deviceInfo\": {\n" +
            "        \"uuid\": \"SMART-HOME-UUID-1233323\",\n" +
            "        \"name\": \"Smart Home Device\",\n" +
            "        \"deviceType\": \"SMART_HOME\"\n" +
            "    }\n" +
            "}";
    String PREMIUM_LOGIN_REQUEST_BODY = "{\n" +
            "    \"username\": \"1122246135\",\n" +
            "    \"password\": \"12866173ag\",\n" +
            "    \"deviceInfo\": {\n" +
            "        \"uuid\": \"SMART-HOME-UUID-1233323\",\n" +
            "        \"name\": \"Smart Home Device\",\n" +
            "        \"deviceType\": \"SMART_HOME\"\n" +
            "    }\n" +
            "}";
    String TWO_FACTOR_LOGIN = "{\n" +
            "    \"username\": \"12349903\",\n" +
            "    \"password\": \"135246\",\n" +
            "    \"deviceInfo\": {\n" +
            "        \"uuid\": \"ALPER\",\n" +
            "        \"name\": \"Smart Home Device\",\n" +
            "        \"deviceType\": \"SMART_HOME\"\n" +
            "    }\n" +
            "}";
    String ASSISTANT_INFO_LOGIN_REQUEST_BODY = "{\n" +
            "    \"username\": \"112267876\",\n" +
            "    \"password\": \"135246\",\n" +
            "    \"deviceInfo\": {\n" +
            "        \"uuid\": \"ALPER\",\n" +
            "        \"name\": \"Smart Home Device\",\n" +
            "        \"deviceType\": \"SMART_HOME\"\n" +
            "    }\n" +
            "}";

    String adminXML ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                  xmlns:asy=\"http://www.accenture.com/assets/sdp/commonDataModel/asynch\">\n" +
            "    <soapenv:Header/>\n" +
            "    <soapenv:Body>\n" +
            "        <asy:TSO_DATA>\n" +
            "            <asy:TSOheader TSOID=\"\" TSOlabel=\"GENERIC_PROVISIONING\"/>\n" +
            "            <!--Optional:-->\n" +
            "            <asy:TSOattributes>\n" +
            "                <asy:attribute name=\"MSISDN\" value=\"905322102947\"/> <!-- msisdn -->\n" +
            "                <asy:attribute name=\"PAYER_MSISDN\" value=\"\"/>\n" +
            "                <asy:attribute name=\"TRANSACTION_ID\" value=\"9214637530008\"/> <!-- unique olmalı -->\n" +
            "                <asy:attribute name=\"WORKFLOW_LABEL\"\n" +
            "                               value=\"SUBSCRIPTION_CREATION\"/> <!-- işlem tipi, subscription creation activation vs-->\n" +
            "                <asy:attribute name=\"CATALOG_SERVICE_VARIANT_ID\"\n" +
            "                               value=\"602960229\"/> <!-- ilgili comet_offer_id'ye denk subscription_plan.slcm_offer_id olmalı -->\n" +
            "                <asy:attribute name=\"CATALOG_OFFER_ID\"\n" +
            "                               value=\"602960229\"/> <!-- ilgili comet_offer_id'ye denk subscription_plan.slcm_offer_id olmalı -->\n" +
            "                <asy:attribute name=\"NCST\" value=\"395278709\"/> <!-- account.crmCustomerId değeri ile aynı olmalı -->\n" +
            "                <asy:attribute name=\"USER_ID\" value=\"MAYA10\"/> <!-- account_subscription.created_by'a set edilir -->\n" +
            "                <asy:attribute name=\"CHANNEL\" value=\"1282\"/>\n" +
            "                <asy:attribute name=\"APPLICATION\" value=\"SOM\"/>\n" +
            "                <asy:attribute name=\"CPCM_OFFERID\" value=\"384676\"/>\n" +
            "                <asy:attribute name=\"CPCM_PRODUCTID\" value=\"26243\"/>\n" +
            "                <asy:list name=\"CAMPAIGN_PROFILE\">\n" +
            "                    <asy:list name=\"CAMPAIGN_PROFILE\" value=\"1\">\n" +
            "                        <asy:attribute name=\"STATUS_ID\" value=\"3\"/>\n" +
            "                        <asy:attribute name=\"CAMPAIGN_INTERVAL_ID\" value=\"1\"/>\n" +
            "                        <asy:attribute name=\"INACTIVATION_REASON\" value=\"4\"/>\n" +
            "                        <asy:attribute name=\"CAMPAIGN_ID\"\n" +
            "                                       value=\"532566.581790.606734\"/> <!-- crm servisindeki campaign_id olduğu gibi yazılır -->\n" +
            "                        <asy:attribute name=\"CAMPAIGN_NAME\" value=\"Akilli Depo 500 GB Ucretsiz Sessiz Abonelik\"/>\n" +
            "                        <asy:attribute name=\"COMMITMENT_DATE\" value=\"20160711144538\"/>\n" +
            "                        <asy:attribute name=\"CAMPAIGN_START_DATE\" value=\"20150321143755\"/>\n" +
            "                        <asy:attribute name=\"CAMPAIGN_END_DATE\" value=\"20160711144538\"/>\n" +
            "                        <asy:attribute name=\"DEFAULT_CAMPAIGN\" value=\"1\"/>\n" +
            "                    </asy:list>\n" +
            "                </asy:list>\n" +
            "                <asy:list name=\"SDP_SUBSCRIPTION_SERVICE_VARIANT\">\n" +
            "                    <asy:list name=\"SDP_SUBSCRIPTION_SERVICE_VARIANT\">\n" +
            "                        <asy:attribute name=\"CATALOG_SERVICE_VARIANT_ID\"\n" +
            "                                       value=\"602960229\"/> <!-- ilgili comet_offer_id'ye denk subscription_plan.slcm_offer_id olmalı -->\n" +
            "                        <asy:list name=\"SDP_PROFILE\">\n" +
            "                            <asy:list name=\"SDP_PROFILE\" value=\"1\">\n" +
            "                                <asy:attribute name=\"PROFILE_TYPE_ID\" value=\"2\"/>\n" +
            "                                <asy:list name=\"ATTRIBUTE_NAME_COLLECTION\"/>\n" +
            "                            </asy:list>\n" +
            "                        </asy:list>\n" +
            "                    </asy:list>\n" +
            "                </asy:list>\n" +
            "            </asy:TSOattributes>\n" +
            "            <asy:TSOresult>\n" +
            "                <asy:statusCode>0</asy:statusCode>\n" +
            "            </asy:TSOresult>\n" +
            "        </asy:TSO_DATA>\n" +
            "    </soapenv:Body>\n" +
            "</soapenv:Envelope>\n";

}

