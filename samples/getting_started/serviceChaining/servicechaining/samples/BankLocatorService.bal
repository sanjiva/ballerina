package servicechaining.samples;

import ballerina.lang.messages;
import ballerina.net.http;

@http:config {basePath:"/branchlocator"}
service<http> Banklocator {
    
    @http:POST{}
    resource product (message m) {
        message response = {};
        json jsonRequest = messages:getJsonPayload(m);
        string zipCode;
        zipCode, _ = (string) jsonRequest.BranchLocator.ZipCode;
        json payload = {};
        if (zipCode == "95999") {
            payload = {"ABCBank": {"BranchCode":"123"}};
            
        }
        else {
            payload = {"ABCBank": {"BranchCode":"-1"}};
            
        }
        messages:setJsonPayload(response, payload);
        reply response;
    }
}
