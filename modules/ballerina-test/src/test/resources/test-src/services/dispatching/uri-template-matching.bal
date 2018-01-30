import ballerina.net.http;

@http:configuration {basePath:"/hello"}
service<http> echo11 {

    @http:resourceConfig {
        methods:["GET"],
        path:"echo2"
    }
    resource echo1 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"echo5":"echo5"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/{abc}-{xyz}"
    }
    resource echo2 (http:Connection conn, http:InRequest req, string abc, string xyz) {
        http:OutResponse res = {};
        json responseJson = {"first":abc, "second":xyz};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/{abc}+{xyz}"
    }
    resource echo3 (http:Connection conn, http:InRequest req, string abc, string xyz) {
        http:OutResponse res = {};
        json responseJson = {"first":xyz, "second":abc};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/{abc}"
    }
    resource echo4 (http:Connection conn, http:InRequest req, string abc) {
        http:OutResponse res = {};
        json responseJson = {"echo3":abc};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/{abc}+{xyz}/bar"
    }
    resource echo5 (http:Connection conn, http:InRequest req, string abc, string xyz) {
        http:OutResponse res = {};
        json responseJson = {"first":abc, "second":xyz, "echo4":"echo4"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/{abc}+{xyz}/{bar}"
    }
    resource echo6 (http:Connection conn, http:InRequest req, string abc, string xyz, string bar) {
        http:OutResponse res = {};
        json responseJson = {"first":abc, "second":xyz, "echo4":bar};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/*"
    }
    resource echo7 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"echo5":"any"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/echo2/{abc}+{xyz}/bar"
    }
    resource echo8 (http:Connection conn, http:InRequest req, string abc, string xyz) {
        http:OutResponse res = {};
        json responseJson = {"first":abc, "second":xyz, "echo8":"echo8"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo3/{abc}+{xyz}"
    }
    resource echo9 (http:Connection conn, http:InRequest req, string abc, string xyz) {
        map params = req.getQueryParams();
        string foo;
        foo, _ = (string)params.foo;
        json responseJson = {"first":abc, "second":xyz, "third":foo, "echo9":"echo9"};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/"
    }
    resource echo10 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string foo;
        foo, _ = (string)params.foo;
        json responseJson = {"third":foo, "echo10":"echo10"};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    resource echo11 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string foo;
        foo, _ = (string)params.foo;
        json responseJson = {"third":foo, "echo11":"echo11"};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo12/{abc}/bar"
    }
    resource echo12 (http:Connection conn, http:InRequest req, string abc) {
        http:OutResponse res = {};
        json responseJson = {"echo12":abc};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo125"
    }
    resource echo125 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string bar;
        bar, _ = (string)params.foo;
        json responseJson = {"echo125":bar};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/paramNeg"
    }
    resource paramNeg (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        TypeCastError err;
        string bar;
        bar, err = (string)params.foo;
        json responseJson = {"echo125":bar, "error":err.msg};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo13"
    }
    resource echo13 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string barStr;
        int bar;
        barStr, _ = (string)params.foo;
        bar, _ = <int>barStr;
        json responseJson = {"echo13":bar};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo14"
    }
    resource echo14 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string barStr;
        float bar;
        barStr, _ = (string)params.foo;
        bar, _ = <float>barStr;
        json responseJson = {"echo14":bar};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo15"
    }
    resource echo15 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string barStr;
        boolean bar;
        barStr, _ = (string) params.foo;
        bar, _ = <boolean> barStr;
        json responseJson = {"echo15":bar};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/so2"
    }
    resource echo (http:Connection conn, http:InRequest req) {
    }
}

@http:configuration {basePath:"/hello/world"}
service<http> echo22 {

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2"
    }
    resource echo1 (http:Connection conn, http:InRequest req) {
        json responseJson = {"echo1":"echo1"};
        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/*"
    }
    resource echo2 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"echo2":"echo2"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/echo2/foo/bar"
    }
    resource echo3 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"echo3":"echo3"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }
}

@http:configuration {basePath:"/"}
service<http> echo33 {
    resource echo1 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string foo;
        foo, _ = (string)params.foo;
        json responseJson = {"third":foo, "echo33":"echo1"};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }
}

service<http> echo44 {


    @http:resourceConfig {
        path:"echo2"
    }
    resource echo221 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"first":"zzz"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    resource echo1 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        var foo, _ = (string)params.foo;
        json responseJson = {"first":foo, "echo44":"echo1"};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"echo2"
    }
    resource echo222 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"first":"bar"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }
}

@http:configuration {basePath:"/echo55"}
service<http> echo55 {
    @http:resourceConfig {
        path:"/foo/bar"
    }
    resource echo1 (http:Connection conn, http:InRequest req) {
        map params = req.getQueryParams();
        string foo;
        foo, _ = (string)params.foo;
        json responseJson = {"echo55":"echo55"};

        http:OutResponse res = {};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        path:"/*"
    }
    resource echo2 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"echo55":"default"};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }
}

@http:configuration {basePath:"/echo66"}
service<http> echo66 {
    @http:resourceConfig {
        path:"/a/*"
    }
    resource echo1 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        json responseJson = {"echo66": req.extraPathInfo};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        path:"/a"
    }
    resource echo2 (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        if (req.extraPathInfo == null) {
            req.extraPathInfo = "empty";
        }
        json responseJson = {"echo66": req.extraPathInfo};
        res.setJsonPayload(responseJson);
        _ = conn.respond(res);
    }
}