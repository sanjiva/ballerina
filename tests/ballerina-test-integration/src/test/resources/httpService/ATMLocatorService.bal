import ballerina/io;
import ballerina/mime;
import ballerina/http;

endpoint http:ServiceEndpoint serviceEnpoint {
    port:9090
};

endpoint http:ClientEndpoint bankInfoService {
    targets:[{url: "http://localhost:9090/bankinfo/product"}]

};

endpoint http:ClientEndpoint branchLocatorService {
    targets:[{url: "http://localhost:9090/branchlocator/product"}]
};

@http:ServiceConfig {
    basePath:"/ABCBank",
    endpoints:[serviceEnpoint]
}
service<http:Service> ATMLocator bind serviceEnpoint {
    @http:ResourceConfig {
        methods:["POST"]
    }
    locator (endpoint outboundEP, http:Request req) {

        http:Request backendServiceReq = {};
        var jsonLocatorReq = req.getJsonPayload();
        match jsonLocatorReq {
            json zip => {
                string zipCode;
                zipCode = extractFieldValue(zip["ATMLocator"]["ZipCode"]);
                io:println("Zip Code " + zipCode);
                json branchLocatorReq = {"BranchLocator":{"ZipCode":""}};
                branchLocatorReq.BranchLocator.ZipCode = zipCode;
                backendServiceReq.setJsonPayload(branchLocatorReq);
            }
            http:PayloadError err => {
                io:println("Error occurred while reading ATM locator request");
            }
        }

        http:Response locatorResponse = {};
        var locatorRes = branchLocatorService -> post("", backendServiceReq);
        match locatorRes {
            http:Response locRes => {
                locatorResponse = locRes;
            }
            http:HttpConnectorError err => {
                io:println("Error occurred while reading locator response");
            }
        }

        var branchLocatorRes = locatorResponse.getJsonPayload();
        match branchLocatorRes {
            json branch => {
                string branchCode;
                branchCode = extractFieldValue(branch.ABCBank.BranchCode);
                io:println("Branch Code " + branchCode);
                json bankInfoReq = {"BranchInfo":{"BranchCode":""}};
                bankInfoReq.BranchInfo.BranchCode = branchCode;
                backendServiceReq.setJsonPayload(bankInfoReq);
            }
            http:PayloadError err => {
                io:println("Error occurred while reading branch locator response");
            }
        }

        http:Response infomationResponse = {};
        var infoRes = bankInfoService -> post("", backendServiceReq);
        match infoRes {
            http:Response res => {
                infomationResponse = res;
            }
            http:HttpConnectorError err => {
                io:println("Error occurred while writing info response");
            }
        }
        _ = outboundEP -> forward(infomationResponse);
    }
}

@http:ServiceConfig {
    basePath:"/bankinfo",
    endpoints:[serviceEnpoint]
}
service<http:Service> Bankinfo bind serviceEnpoint {

    @http:ResourceConfig {
        methods:["POST"]
    }
    product (endpoint outboundEP, http:Request req) {
        http:Response res = {};
        var jsonRequest = req.getJsonPayload();
        match jsonRequest {
            json bankInfo => {
                string branchCode;
                branchCode = extractFieldValue(bankInfo.BranchInfo.BranchCode);
                json payload = {};
                if (branchCode == "123") {
                    payload = {"ABC Bank":{"Address":"111 River Oaks Pkwy, San Jose, CA 95999"}};
                } else {
                    payload = {"ABC Bank":{"error":"No branches found."}};
                }

                res.setJsonPayload(payload);
            }
            http:PayloadError err => {
                io:println("Error occurred while reading bank info request");
            }
        }

        _ = outboundEP -> respond(res);
    }
}

@http:ServiceConfig {
    basePath:"/branchlocator",
    endpoints:[serviceEnpoint]
}
service<http:Service> Banklocator bind serviceEnpoint {

    @http:ResourceConfig {
        methods:["POST"]
    }
    product (endpoint outboundEP, http:Request req) {
        http:Response res = {};
        var jsonRequest = req.getJsonPayload();
        match jsonRequest {
            json bankLocator => {
                string zipCode;
                zipCode = extractFieldValue(bankLocator.BranchLocator.ZipCode);
                json payload = {};
                if (zipCode == "95999") {
                    payload = {"ABCBank":{"BranchCode":"123"}};
                } else {
                    payload = {"ABCBank":{"BranchCode":"-1"}};
                }
                res.setJsonPayload(payload);
            }
            http:PayloadError err => {
                io:println("Error occurred while reading bank locator request");
            }
        }

        _ = outboundEP -> respond(res);
    }
}

//Keep this until there's a simpler way to get a string value out of a json
function extractFieldValue(json fieldValue) returns string {
    match fieldValue {
        int i => return "error";
        string s => return s;
        boolean b => return "error";
        null  => return "error";
        json j => return "error";
    }
}