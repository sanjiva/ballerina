import ballerina.net.http;

@http:configuration {basePath:"/hello"}
service<http> testService {

    @http:resourceConfig {
        methods:["GET"],
        path:"/t1/{person}/bar/{year}/foo"
    }
    resource test1 (http:Connection conn, http:InRequest req, string person, string year) {
        http:OutResponse res = {};
        json outJson = {};
        outJson.pathParams = string `{{person}}, {{year}}`;

        map personMParams = req.getMatrixParams(string `/hello/t1/{{person}}`);
        var age, _ = (string) personMParams["age"];
        var color, _ = (string) personMParams["color"];
        outJson.personMatrix = string `age={{age}};color={{color}}`;

        map yearMParams = req.getMatrixParams(string `/hello/t1/{{person}}/bar/{{year}}`);
        var month, _ = (string) yearMParams["month"];
        var day, _ = (string) yearMParams["day"];
        outJson.yearMatrix = string `month={{month}};day={{day}}`;

        map fooMParams = req.getMatrixParams(string `/hello/t1/{{person}}/bar/{{year}}/foo`);
        var a, _ = (string) fooMParams["a"];
        var b, _ = (string) fooMParams["b"];
        outJson.fooMatrix = string `a={{a}};b={{b}}`;

        map queryParams = req.getQueryParams();
        var x, _ = (string) queryParams["x"];
        var y, _ = (string) queryParams["y"];
        outJson.queryParams = string `x={{x}}&y={{y}}`;

        res.setJsonPayload(outJson);
        _ = conn.respond(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/t2/{person}/foo;a=5;b=10"
    }
    resource testEncoded (http:Connection conn, http:InRequest req, string person) {
        http:OutResponse res = {};
        json outJson = {};
        outJson.person = person;

        map personMParams = req.getMatrixParams(string `/hello/t2/{{person}}`);
        outJson.personParamSize = lengthof personMParams;

        map fooMParams = req.getMatrixParams(string `/hello/t2/{{person}}/foo`);
        outJson.fooParamSize = lengthof fooMParams;

        res.setJsonPayload(outJson);
        _ = conn.respond(res);
    }
}
