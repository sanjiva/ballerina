
import ballerina/jms;
import ballerina/log;

public type SimpleQueueReceiver object {

    public {
        SimpleQueueListenerEndpointConfiguration config;
    }

    private {
        jms:SimpleQueueReceiver receiver;
        QueueReceiverActions? consumerActions;
    }

    public function init(SimpleQueueListenerEndpointConfiguration config) {
        self.config = config;
        self.receiver.init({
                initialContextFactory:"wso2mbInitialContextFactory",
                providerUrl:generateBrokerURL(config),
                connectionFactoryName:config.connectionFactoryName,
                acknowledgementMode:config.acknowledgementMode,
                messageSelector:config.messageSelector,
                properties:config.properties,
                queueName:config.queueName
            });

        self.consumerActions = new QueueReceiverActions(self.receiver.getCallerActions());
    }

    public function register(typedesc serviceType) {
        self.receiver.register(serviceType);
    }

    public function start() {
        self.receiver.start();
    }

    public function getCallerActions() returns QueueReceiverActions {
        match (self.consumerActions) {
            QueueReceiverActions c => return c;
            () => {
                error e = {message:"Queue receiver consumerActions cannot be nil."};
                throw e;
            }
        }
    }

    public function stop() {
        receiver.stop();
    }

    public function createTextMessage(string message) returns Message|error {
        var result = self.receiver.createTextMessage(message);
        match (result) {
            jms:Message m => return new Message(m);
            error e => return e;
        }
    }
};

public type SimpleQueueListenerEndpointConfiguration {
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
    string queueName,
};

public type QueueReceiverActions object {

    public {
        jms:QueueReceiverActions helper;
    }

    public new(helper) {}

    public function acknowledge(Message message) returns error? {
        return helper.acknowledge(message.getJMSMessage());
    }

    public function receive(int timeoutInMilliSeconds = 0) returns Message|error|() {
        var result = helper.receive(timeoutInMilliSeconds = timeoutInMilliSeconds);
        match (result) {
            jms:Message m => return new Message(m);
            error e => return e;
            () => return ();
        }
    }
};
