import ballerina/jms;
import ballerina/log;

// Initialize a JMS connection with the provider
jms:Connection jmsConnection = new ({
    initialContextFactory: "wso2mbInitialContextFactory",
    providerUrl: "amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'"
});

// Initialize a JMS session on top of the created connection
jms:Session jmsSession = new (jmsConnection, {
    acknowledgementMode: "AUTO_ACKNOWLEDGE"
});

// Initialize a Queue sender on top of the the created sessions
endpoint jms:TopicProducer topicProducer {
    session: jmsSession,
    topicPattern: "BallerinaTopic"
};

public function main (string[] args) {
    // Create a Text message.
    jms:Message m = jmsSession.createTextMessage("Test Text");
    // Send the Ballerina message to the JMS provider.
    topicProducer -> send(m);
}
