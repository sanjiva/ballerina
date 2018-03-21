package ballerina.net.websub;

import ballerina/log;
import ballerina/mime;
import ballerina/net.http;

@Description {value:"HTTP client connector for outbound WebSub Subscription/Unsubscription requests to a Hub"}
public struct HubClientConnector {
    string hubUri;
    http:ClientEndpoint httpClientEndpoint;
}

@Description {value:"Function to send a subscription request to a WebSub Hub"}
@Param {value:"subscriptionRequest: The SubscriptionChangeRequest containing subscription details"}
@Return {value:"SubscriptionChangeResponse indicating subscription details, if the request was successful"}
@Return {value:"WebSubError if an error occurred with the subscription request"}
public function <HubClientConnector client> subscribe (SubscriptionChangeRequest subscriptionRequest)
(SubscriptionChangeResponse, WebSubError) {
    endpoint http:ClientEndpoint httpClientEndpoint = client.httpClientEndpoint;
    http:Request builtSubscriptionRequest = buildSubscriptionChangeOutRequest(MODE_SUBSCRIBE, subscriptionRequest);
    http:Response response;
    http:HttpConnectorError httpConnectorError;
    response, httpConnectorError = httpClientEndpoint -> post("/", builtSubscriptionRequest);
    return processHubResponse(client.hubUri, MODE_SUBSCRIBE, subscriptionRequest.topic, response, httpConnectorError);
}

@Description {value:"Function to send an unsubscription request to a WebSub Hub"}
@Param {value:"unsubscriptionRequest: The SubscriptionChangeRequest containing unsubscription details"}
@Return {value:"SubscriptionChangeResponse indicating unsubscription details, if the request was successful"}
@Return {value:"WebSubError if an error occurred with the unsubscription request"}
public function <HubClientConnector client> unsubscribe (SubscriptionChangeRequest unsubscriptionRequest)
(SubscriptionChangeResponse, WebSubError) {
    endpoint http:ClientEndpoint httpClientEndpoint = client.httpClientEndpoint;
    http:Request builtSubscriptionRequest = buildSubscriptionChangeOutRequest(MODE_UNSUBSCRIBE, unsubscriptionRequest);
    http:Response response;
    http:HttpConnectorError httpConnectorError;
    response, httpConnectorError = httpClientEndpoint -> post("/", builtSubscriptionRequest);
    return processHubResponse(client.hubUri, MODE_UNSUBSCRIBE, unsubscriptionRequest.topic, response,
                              httpConnectorError);
}

@Description {value:"Function to publish an update to a remote Ballerina WebSub Hub"}
@Param {value:"topic: The topic for which the update occurred"}
@Param {value:"payload: The update payload"}
@Return {value:"WebSubError if an error occurred with the update"}
public function <HubClientConnector client> publishUpdateToRemoteHub (string topic, json payload)
(WebSubError webSubError) {
    endpoint http:ClientEndpoint httpClientEndpoint = client.httpClientEndpoint;
    http:Request request = {};
    http:Response response;
    http:HttpConnectorError httpConnectorError;

    string queryParams = HUB_MODE + "=" + MODE_PUBLISH + "&" + HUB_TOPIC + "=" + topic;
    request.setJsonPayload(payload);
    response, httpConnectorError = httpClientEndpoint -> post("?" + queryParams, request);
    if (httpConnectorError != null) {
        webSubError = { errorMessage:"Notification failed for topic [" + topic + "]",
                            connectorError:httpConnectorError };
    }
    return;
}

@Description {value:"Function to build the subscription request to subscribe at the hub"}
@Param {value:"mode: Whether the request is for subscription or unsubscription"}
@Param {value:"subscriptionChangeRequest: The SubscriptionChangeRequest specifying the topic to subscribe to and the
                                        parameters to use"}
@Return {value:"The Request to send to the hub to subscribe/unsubscribe"}
function buildSubscriptionChangeOutRequest(string mode, SubscriptionChangeRequest subscriptionChangeRequest)
(http:Request) {
    http:Request request = {};
    string body = HUB_MODE + "=" + mode
                  + "&" + HUB_TOPIC + "=" + subscriptionChangeRequest.topic
                  + "&" + HUB_CALLBACK + "=" + subscriptionChangeRequest.callback;
    if (mode == MODE_SUBSCRIBE) {
        //TODO: validate secret and lease seconds
        body = body + "&" + HUB_SECRET + "=" + subscriptionChangeRequest.secret + "&" + HUB_LEASE_SECONDS + "="
               + subscriptionChangeRequest.leaseSeconds;
    }
    request.setStringPayload(body);
    request.setHeader(CONTENT_TYPE, mime:APPLICATION_FORM_URLENCODED);
    return request;
}

@Description {value:"Function to process the response from the hub on subscription/unsubscription and extract
                    required information"}
@Param {value:"hub: The hub to which the subscription/unsubscription request was sent"}
@Param {value:"mode: Whether the request was sent for subscription or unsubscription"}
@Param {value:"topic: The topic for which the subscription/unsubscription request was sent"}
@Param {value:"response: The response received from the hub"}
@Param {value:"httpConnectorError: Error, if occurred, with HTTP client connector invocation"}
@Return {value:"SubscriptionChangeResponse including details of subscription/unsubscription,
                if the request was successful"}
@Return { value : "WebSubErrror indicating any errors that occurred, if the request was unsuccessful"}
function processHubResponse(string hub, string mode, string topic, http:Response response,
                            http:HttpConnectorError httpConnectorError) (SubscriptionChangeResponse, WebSubError) {
    SubscriptionChangeResponse subscriptionChangeResponse;
    WebSubError webSubError;
    if (httpConnectorError != null) {
        string errorMessage = "Error occurred for request: Mode[" + mode + "] at Hub[" + hub +"] - "
                              + httpConnectorError.message;
        webSubError = {errorMessage:errorMessage, connectorError:httpConnectorError};
    }
    else if (response.statusCode != 202) {
        string responsePayload;
        mime:EntityError entityError;
        string errorMessage;
        responsePayload, entityError = response.getStringPayload();
        if (entityError != null) {
            errorMessage = "Error in request: Mode[" + mode + "] at Hub[" + hub +"], "
                           + "Error occurred identifying cause: " + entityError.message;
        } else {
            errorMessage = "Error in request: Mode[" + mode + "] at Hub[" + hub +"] - " + responsePayload;
        }
        webSubError = {errorMessage:errorMessage};
    } else {
        subscriptionChangeResponse = {hub:hub, topic:topic, response:response};
    }
    return subscriptionChangeResponse, webSubError;
}
