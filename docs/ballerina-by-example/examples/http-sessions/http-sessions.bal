import ballerina/http;
import ballerina/io;

endpoint http:ServiceEndpoint sessionTestEP {
    port:9090
};

@http:ServiceConfig { basePath:"/sessionTest" }
service<http:Service> sessionTest bind sessionTestEP {

    string key = "status";
    @http:ResourceConfig {
        methods:["GET"],
        path:"/sayHello"
    }
    sayHello (endpoint outboundEP, http:Request req) {
        //createSessionIfAbsent() function returns an existing session for a valid session id, otherwise it returns a new session.
        http:Session session = req.createSessionIfAbsent();
        string result;
        //Session status(new or already existing) is informed by isNew() as boolean value.
        if (session.isNew()) {
            result = "Say hello to a new session";
        } else {
            result = "Say hello to an existing session";
        }
        //Binds a string attribute to this session with a key(string).
        session.setAttribute(key, "Session sample");
        http:Response res = {};
        res.setStringPayload(result);
        _ = outboundEP -> respond(res);
    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/doTask"
    }
    doTask (endpoint outboundEP, http:Request req) {
        //getSession() returns an existing session for a valid session id. otherwise null.
        http:Session session = req.getSession();
        string attributeValue;
        if (session != null) {
            //Returns the object bound with the specified key.
            attributeValue = <string>session.getAttribute(key);
        } else {
            attributeValue = "Session unavailable";
        }
        http:Response res = {};
        res.setStringPayload(attributeValue);
        _ = outboundEP -> respond(res);
    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/sayBye"
    }
    sayBye (endpoint outboundEP, http:Request req) {
        http:Session session = req.getSession();
        http:Response res = {};
        if (session != null) {
            //Returns session id.
            string id = session.getId();
            //Invalidates this session.
            session.invalidate();
            res.setStringPayload("Session: " + id + " invalidated");
        } else {
            res.setStringPayload("Session unavailable");
        }
        _ = outboundEP -> respond(res);
    }
}
