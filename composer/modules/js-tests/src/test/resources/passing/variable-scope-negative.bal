import ballerina.net.http;
import ballerina.net.http.response;

function testVariableIfScope () (int) {
    int a = 90;
    int b = 50;
    if (a > 20) {
        int c = 20;
    } else {
        int k = 60;
        if (b < 100) {
            k = b + 30;
        }
    }
    return k;
}

function testVariableElseScope() (int) {
    int a = 10;
    if(a > 20) {
        a = 50;
    } else {
        int b = 30;
    }

    return b;
}

function testVariableWhileScope() {
    int a = 0;
    while( a < 5) {
        a = a + 1;
        int b = a + 20;
    }
    int sum = b;
}

service<http> myService {
    int a = 20;

    resource myResource1(http:Request req, http:Response res) {
        int b = a + 50;
        response:send(res);
    }

    resource myResource2(http:Request req, http:Response res) {
        int c = b + 50;
        response:send(res);
    }
}

