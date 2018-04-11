package ballerina.jms;

import ballerina/log;

public type SimpleTopicSubscriber object {
    public {
        SimpleTopicSubscriberEndpointConfiguration config;
    }

    private {
        Connection? connection;
        Session? session;
        TopicSubscriber? subscriber;
    }

    public function init(SimpleTopicSubscriberEndpointConfiguration config) {
        self.config = config;
        Connection conn = new ({
                initialContextFactory: config.initialContextFactory,
                providerUrl: config.providerUrl,
                connectionFactoryName: config.connectionFactoryName,
                properties: config.properties
            });
        self.connection = conn;

        Session newSession = new (conn, {
                acknowledgementMode: config.acknowledgementMode
            });
        self.session = newSession;

        TopicSubscriber topicSubscriber = new;
        TopicSubscriberEndpointConfiguration consumerConfig = {
            session: newSession,
            topicPattern: config.topicPattern,
            messageSelector: config.messageSelector
        };
        topicSubscriber.init(consumerConfig);
        self.subscriber = topicSubscriber;
    }

    public function register (typedesc serviceType) {
        match (subscriber) {
            TopicSubscriber c => {
                c.register(serviceType);
            }
            () => {
                error e = {message: "Topic Subscriber cannot be nil"};
                throw e;
            }
        }
    }

    public function start () {
    }

    public function getClient () returns (TopicSubscriberConnector) {
        match (subscriber) {
            TopicSubscriber c => return c.getClient();
            () => {
                error e = {message: "Topic subscriber cannot be nil"};
                throw e;
            }
        }
    }

    public function stop () {
    }

    public function createTextMessage(string message) returns (Message | Error) {
        match (session) {
            Session s => return s.createTextMessage(message);
            () => {
                error e = {message: "Session cannot be nil"};
                throw e;
            }
        }

    }
};

public type SimpleTopicSubscriberEndpointConfiguration {
    string initialContextFactory = "wso2mbInitialContextFactory";
    string providerUrl = "amqp://admin:admin@ballerina/default?brokerlist='tcp://localhost:5672'";
    string connectionFactoryName = "ConnectionFactory";
    string acknowledgementMode = "AUTO_ACKNOWLEDGE";
    string messageSelector;
    map properties;
    string topicPattern;
};

