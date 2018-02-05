package restfulservice.samples;

import ballerina.net.http;
import ballerina.net.http.response;

@http:configuration {basePath:"/ecommerceservice"}
service<http> Ecommerce {
    http:ClientConnector productsService = create http:ClientConnector("http://localhost:9090");

    @http:resourceConfig {
        methods:["GET"],
        path:"/products/{prodId}"
    }
    resource productsInfo(http:Request req, http:Response res, string prodId) {
        string reqPath = "/productsservice/" + prodId;
        res = productsService.get(reqPath, req);
        response:send(res);
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/products"
    }
    resource productMgt (http:Request req, http:Response res) {
        res = productsService.post("/productsservice", req);
        response:send(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/orders"
    }
    resource ordersInfo (http:Request req, http:Response res) {
        http:ClientConnector productsService = create http:ClientConnector("http://localhost:9090");
        res = productsService.get("/orderservice/orders", req);
        response:send(res);
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/orders"
    }
    resource ordersMgt (http:Request req, http:Response res) {
        res = productsService.post("/orderservice/orders", req);
        response:send(res);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/customers"
    }
    resource customersInfo (http:Request req, http:Response res) {
        res = productsService.get("/customerservice/customers", req);
        response:send(res);
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/customers"
    }
    resource customerMgt (http:Request req, http:Response res) {
        res = productsService.post("/customerservice/customers", req);
        response:send(res);
    }
    
}
