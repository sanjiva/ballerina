import ballerina/http;
import testorg/foo version v1;

endpoint http:NonListener echoEP {
    port:9090
};

@http:ServiceConfig {basePath:"/globalvar"}
service<http:Service> GlobalVar bind echoEP {

    string serviceVarFloat = <string> foo:glbVarFloat;

    @http:ResourceConfig {
        methods:["GET"],
        path:"/defined"
    }
    defineGlobalVar (endpoint conn, http:Request request) {
        http:Response res = new;
        json responseJson = {"glbVarInt":foo:glbVarInt, "glbVarString":foo:glbVarString, "glbVarFloat":foo:glbVarFloat};
        res.setJsonPayload(responseJson);
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/access-service-level"
    }
    accessGlobalVarAtServiceLevel (endpoint conn, http:Request request) {
        http:Response res = new;
        json responseJson = {"serviceVarFloat":serviceVarFloat};
        res.setJsonPayload(responseJson);
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/change-resource-level"
    }
    changeGlobalVarAtResourceLevel (endpoint conn, http:Request request) {
        http:Response res = new;
        foo:glbVarFloatChange = 77.87;
        json responseJson = {"glbVarFloatChange":foo:glbVarFloatChange};
        res.setJsonPayload(responseJson);
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/get-changed-resource-level"
    }
    getChangedGlobalVarAtResourceLevel (endpoint conn, http:Request request) {
        http:Response res = new;
        json responseJson = {"glbVarFloatChange":foo:glbVarFloatChange};
        res.setJsonPayload(responseJson);
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/arrays"
    }
    getGlobalArraysAtResourceLevel (endpoint conn, http:Request request) {
        http:Response res = new;
        json responseJson = {
            "glbArrayElement":foo:glbArray[0],
            "glbSealedArrayElement":foo:glbSealedArray[1],
            "glbSealedArray2Element":foo:glbSealedArray2[2],
            "glbSealed2DArrayElement":foo:glbSealed2DArray[0][0],
            "glbSealed2DArray2Element":foo:glbSealed2DArray2[0][1]
        };
        res.setJsonPayload(responseJson);
        _ = conn -> respond(res);
    }
}

@http:ServiceConfig {basePath:"/globalvar-second"}
service<http:Service> GlobalVarSecond bind echoEP {

    @http:ResourceConfig {
        methods:["GET"],
        path:"/get-changed-resource-level"
    }
    getChangedGlobalVarAtResourceLevel (endpoint conn, http:Request request) {
        http:Response res = new;
        json responseJson = {"glbVarFloatChange":foo:glbVarFloatChange};
        res.setJsonPayload(responseJson);
        _ = conn -> respond(res);
    }

}

