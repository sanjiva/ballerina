
import ballerina/jms;
import ballerina/log;

public type SimpleTopicSubscriber object {

    public {
        SimpleTopicSubscriberEndpointConfiguration config;
    }

    private {
        jms:SimpleTopicSubscriber subscriber;
        TopicSubscriberActions? consumerActions;
    }

    public function init(SimpleTopicSubscriberEndpointConfiguration config) {
        self.config = config;
        self.subscriber.init({
                initialContextFactory:"wso2mbInitialContextFactory",
                providerUrl:generateBrokerURL(config),
                connectionFactoryName:config.connectionFactoryName,
                acknowledgementMode:config.acknowledgementMode,
                messageSelector:config.messageSelector,
                properties:config.properties,
                topicPattern:config.topicPattern
            }
        );
        self.consumerActions = new TopicSubscriberActions(self.subscriber.getCallerActions());
    }

    public function register(typedesc serviceType) {
        self.subscriber.register(serviceType);
    }

    public function start() {
        self.subscriber.start();
    }

    public function getCallerActions() returns TopicSubscriberActions {
        match (self.consumerActions) {
            TopicSubscriberActions c => return c;
            () => {
                error e = {message:"Topic subscriber consumerActions cannot be nil."};
                throw e;
            }
        }
    }

    public function stop() {
        self.subscriber.stop();
    }

    public function createTextMessage(string message) returns Message|error {
        var result = self.subscriber.createTextMessage(message);
        match (result) {
            jms:Message m => return new Message(m);
            error e => return e;
        }
    }
};

public type SimpleTopicSubscriberEndpointConfiguration {
    string username = "admin",
    string password = "admin",
    string host = "localhost",
    int port = 5672,
    string clientID = "ballerina",
    string virtualHost = "default",
    string connectionFactoryName = "ConnectionFactory",
    string acknowledgementMode = "AUTO_ACKNOWLEDGE",
    string messageSelector,
    map properties,
    string topicPattern,
};

public type TopicSubscriberActions object {

    public {
        jms:TopicSubscriberActions helper;
    }

    public new(helper) {}

    public function acknowledge(Message message) returns error? {
        return self.helper.acknowledge(message.getJMSMessage());
    }

    public function receive(int timeoutInMilliSeconds = 0) returns Message|error|() {
        var result = self.helper.receive(timeoutInMilliSeconds = timeoutInMilliSeconds);
        match (result) {
            jms:Message m => return new Message(m);
            error e => return e;
            () => return ();
        }
    }
};
