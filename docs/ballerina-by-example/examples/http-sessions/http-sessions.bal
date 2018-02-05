import ballerina.net.http;

service<http> sessionTest {

    string key = "status";
    @http:resourceConfig {
        methods:["GET"]
    }
    resource sayHello (http:Connection conn, http:InRequest req) {
        //createSessionIfAbsent() function returns an existing session for a valid session id, otherwise it returns a new session.
        http:Session session = conn.createSessionIfAbsent();
        string result;
        //Session status(new or already existing) is informed by isNew() as boolean value.
        if (session.isNew()) {
            result = "Say hello to a new session";
        } else {
            result = "Say hello to an existing session";
        }
        //Binds a string attribute to this session with a key(string).
        session.setAttribute(key, "Session sample");
        http:OutResponse res = {};
        res.setStringPayload(result);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"]
    }
    resource doTask (http:Connection conn, http:InRequest req) {
        //getSession() returns an existing session for a valid session id. otherwise null.
        http:Session session = conn.getSession();
        string attributeValue;
        if (session != null) {
            //Returns the object bound with the specified key.
            attributeValue, _ = (string)session.getAttribute(key);
        } else {
            attributeValue = "Session unavailable";
        }
        http:OutResponse res = {};
        res.setStringPayload(attributeValue);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"]
    }
    resource sayBye (http:Connection conn, http:InRequest req) {
        http:Session session = conn.getSession();
        http:OutResponse res = {};
        if (session != null) {
            //Returns session id.
            string id = session.getId();
            //Invalidates this session.
            session.invalidate();
            res.setStringPayload("Session: " + id + " invalidated");
        } else {
            res.setStringPayload("Session unavailable");
        }
        _ = conn.respond(res);
    }
}
