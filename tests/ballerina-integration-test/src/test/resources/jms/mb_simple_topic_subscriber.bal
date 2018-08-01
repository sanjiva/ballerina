import ballerina/mb;
import ballerina/io;
import ballerina/http;

endpoint mb:SimpleTopicSubscriber topicSubscriber {
    host: "localhost",
    port: 5772,
    topicPattern: "testMbSimpleTopicSubscriberPublisher"
};

// Bind the created consumer to the listener service.
service<mb:Consumer> jmsListener bind topicSubscriber {

    // OnMessage resource get invoked when a message is received.
    onMessage(endpoint subscriber, mb:Message message) {
        string messageText = check message.getTextMessageContent();
        io:println("Message : " + messageText);
    }
}

// This is to make sure that the test case can detect the PID using port. Removing following will result in
// intergration testframe work failing to kill the ballerina service.
endpoint http:Listener helloWorldEp {
    port:9090
};

@http:ServiceConfig {
    basePath:"/jmsDummyService"
}
service<http:Service> helloWorld bind helloWorldEp {

    @http:ResourceConfig {
        methods:["GET"],
        path:"/"
    }
    sayHello (endpoint client, http:Request req) {
        // Do nothing
    }
}
