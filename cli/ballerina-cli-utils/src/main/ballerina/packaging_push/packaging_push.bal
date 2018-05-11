
import ballerina/io;
import ballerina/mime;
import ballerina/http;

documentation {
    This functions pulls a package from ballerina central.

    P{{definedEndpoint}} Endpoint defined with the proxy configurations
    P{{accessToken}} Access token
    P{{mdFileContent}} Package.md file content of the package
    P{{summary}} Summary of the package
    P{{homePageURL}} Website URL of the package
    P{{repositoryURL}} Source code URL of the package
    P{{apiDocURL}} API documentation URL of the package
    P{{authors}} Authors of the package
    P{{keywords}} Keywords which describes the package
    P{{license}} License of the package
    P{{url}} URL to be invoked to push the package
    P{{dirPath}} Directory path where the archived package resides
    P{{msg}} Message printed when the package is pushed successfully which includes package info
}
function pushPackage (http:Client definedEndpoint, string accessToken, string mdFileContent, string summary, string homePageURL, string repositoryURL,
                string apiDocURL, string authors, string keywords, string license, string url, string dirPath, string msg) {
    
    endpoint http:Client httpEndpoint = definedEndpoint;
    mime:Entity mdFileContentBodyPart = addStringBodyParts("description", mdFileContent);
    mime:Entity summaryBodyPart = addStringBodyParts("summary", summary);
    mime:Entity homePageURLBodyPart = addStringBodyParts("websiteURL", homePageURL);
    mime:Entity repositoryURLBodyPart = addStringBodyParts("repositoryURL", repositoryURL);
    mime:Entity apiDocURLBodyPart = addStringBodyParts("apiDocURL", apiDocURL);
    mime:Entity authorsBodyPart = addStringBodyParts("authors", authors);
    mime:Entity keywordsBodyPart = addStringBodyParts("keywords", keywords);
    mime:Entity licenseBodyPart = addStringBodyParts("license", license);

    // Artifact
    mime:Entity filePart = new;
    filePart.setContentDisposition(getContentDispositionForFormData("artifact"));
    filePart.setFileAsEntityBody(untaint dirPath);
    filePart.setContentType(mime:APPLICATION_OCTET_STREAM);

    mime:Entity[] bodyParts = [filePart, mdFileContentBodyPart, summaryBodyPart, homePageURLBodyPart, repositoryURLBodyPart,
                                           apiDocURLBodyPart, authorsBodyPart, keywordsBodyPart, licenseBodyPart];
    http:Request req = new;
    req.addHeader("Authorization", "Bearer " + accessToken);
    req.setBodyParts(bodyParts, contentType = mime:MULTIPART_FORM_DATA);

    var result = httpEndpoint -> post("", request=req);
    http:Response httpResponse = new;
    match result {
        http:Response response => httpResponse = response;
        error e => {
            io:println("connection to the remote host failed : " + e.message);
            return;
        }
    }
    string statusCode = <string> httpResponse.statusCode;
    if (statusCode.hasPrefix("5")) {
        io:println("remote registry failed for url :" + url);
    } else if (statusCode != "200") {
        json jsonResponse = check (httpResponse.getJsonPayload());
        string message = jsonResponse.message.toString();
        io:println(message);
    } else {
        io:println(msg);
    }
}

documentation {
    This function will invoke the method to push the package.
}
function main (string... args) {
    http:Client httpEndpoint;
    string host = args[12];
    string port = args[13];
    if (host != "" && port != "") {
        try {
          httpEndpoint = defineEndpointWithProxy(args[9], host, port, args[14], args[15]);
        } catch (error err) {
          io:println("failed to resolve host : " + host + " with port " + port);
          return;
        }
    } else  if (host != "" || port != "") {
        io:println("both host and port should be provided to enable proxy");     
        return;   
    } else {
        httpEndpoint = defineEndpointWithoutProxy(args[9]);
    }        
    pushPackage(httpEndpoint, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);    
}

documentation {
    This function defines an endpoint with proxy configurations.

    P{{url}} URL to be invoked
    P{{hostname}} Host name of the proxy
    P{{port}} Port of the proxy
    P{{username}} Username of the proxy
    P{{password}} Password of the proxy
    R{{}} Endpoint defined
}
function defineEndpointWithProxy (string url, string hostname, string port, string username, string password) returns http:Client{
    endpoint http:Client httpEndpoint {
        url: url,
        secureSocket:{
            trustStore:{
                path: "${ballerina.home}/bre/security/ballerinaTruststore.p12",
                password: "ballerina"
            },
            verifyHostname: false,
            shareSession: true
        },
            proxy : getProxyConfigurations(hostname, port, username, password)
    };
    return httpEndpoint;
}

documentation {
    This function defines an endpoint without proxy configurations.

    P{{url}} URL to be invoked
    R{{}} Endpoint defined
}
function defineEndpointWithoutProxy (string url) returns http:Client{
    endpoint http:Client httpEndpoint {
        url: url,
        secureSocket:{
            trustStore:{
                path: "${ballerina.home}/bre/security/ballerinaTruststore.p12",
                password: "ballerina"
            },
            verifyHostname: false,
            shareSession: true
        }
    };
    return httpEndpoint;
}

documentation {
    This function will get the content disposition of the form data sent.

    P{{partName}} Multipart name
    R{{}} `ContentDisposition` of the multipart
}
function getContentDispositionForFormData(string partName) returns (mime:ContentDisposition){
    mime:ContentDisposition contentDisposition = new;
    contentDisposition.name =  partName;
    contentDisposition.disposition = "form-data";
    return contentDisposition;
}

documentation {
    This function will add string part information in multiparts.

    P{{key}} Name of the multipart
    P{{value}} String value to be included in the multipart
    R{{}} `Entity` with the part information.
}
function addStringBodyParts (string key, string value) returns (mime:Entity) {
    mime:Entity stringBodyPart = new;
    stringBodyPart.setContentDisposition(getContentDispositionForFormData(key));
    stringBodyPart.setText(value);
    stringBodyPart.setContentType(mime:TEXT_PLAIN);
    return stringBodyPart;
}

documentation {
    This function sets the proxy configurations for the endpoint.

    P{{hostName}} Host name of the proxy
    P{{port}} Port of the proxy
    P{{username}} Username of the proxy
    P{{password}} Password of the proxy
    R{{}} Proxy configurations for the endpoint
}
function getProxyConfigurations(string hostName, string port, string username, string password) returns http:ProxyConfig {
    int portInt = check <int> port;
    http:ProxyConfig proxy = { host : hostName, port : portInt , userName: username, password : password };
    return proxy;
}
