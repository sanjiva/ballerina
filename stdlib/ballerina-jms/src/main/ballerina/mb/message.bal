package ballerina.mb;

import ballerina/jms;

public type Message object {
    private {
        jms:Message message;
    }

    public new (message) {
    }

    documentation {Gets the internal JMS message
        returns JMS message}
    function getJMSMessage() returns jms:Message {
        return self.message;
    }

    documentation {Gets text content of the JMS message
        returns message content as string}
    public function getTextMessageContent() returns string|Error {
        return self.message.getTextMessageContent();
    }

    documentation {Sets a JMS transport string property from the message
        P{{key}} The string property name
        P{{value}} The string property value}
    public function setStringProperty(string key, string value) returns Error|() {
        return self.message.setStringProperty(key, value);
    }

    documentation {Gets a JMS transport string property from the message
        P{{key}} The string property name
        returns The string property value}
    public function getStringProperty(string key) returns string|()|Error {
        return self.message.getStringProperty(key);
    }

    documentation {Sets a JMS transport integer property from the message
        P{{key}} The integer property name
        P{{value}} The integer property value}
    public function setIntProperty(string key, int value) returns Error|() {
        return self.message.setIntProperty(key, value);
    }

    documentation {Gets a JMS transport integer property from the message
        P{{key}} The integer property name
        returns The integer property value}
    public function getIntProperty(string key) returns int|Error {
        return self.message.getIntProperty(key);
    }

    documentation {Sets a JMS transport boolean property from the message
        P{{key}} The boolean property name
        P{{value}} The boolean property value}
    public function setBooleanProperty(string key, boolean value) returns Error|() {
        return self.message.setBooleanProperty(key, value);
    }

    documentation {Gets a JMS transport boolean property from the message
        P{{key}} The boolean property name
        returns The boolean property value}
    public function getBooleanProperty(string key) returns boolean|Error {
        return self.message.getBooleanProperty(key);
    }

    documentation {Sets a JMS transport float property from the message
        P{{key}} The float property name
        P{{value}} The float property value}
    public function setFloatProperty (string key, float value) returns Error|() {
        return self.message.setFloatProperty(key, value);
    }

    documentation {Gets a JMS transport float property from the message
        P{{key}} The float property name
        returns The float property value}
    public function getFloatProperty (string key) returns float|Error {
        return self.message.getFloatProperty(key);
    }

    documentation {Get JMS transport header MessageID from the message
        returns The header value}
    public function getMessageID () returns string|Error {
        return self.message.getMessageID();
    }

    documentation {Get JMS transport header Timestamp from the message
        returns The header value}
    public function getTimestamp () returns int|Error {
        return self.message.getTimestamp();
    }

    documentation {Sets DeliveryMode JMS transport header to the message
        P{{mode}} The header value}
    public function setDeliveryMode (int mode) returns Error|() {
        return self.message.setDeliveryMode(mode);
    }

    documentation {Get JMS transport header DeliveryMode from the message
        returns The header value" }
    public function getDeliveryMode () returns int|Error {
        return self.message.getDeliveryMode();
    }

    documentation {Sets Expiration JMS transport header to the message
        P{{value}} The header value}
    public function setExpiration (int value) returns Error|() {
        return self.message.setExpiration(value);
    }

    documentation {Get JMS transport header Expiration from the message
        returns int: The header value}
    public function getExpiration () returns int|Error {
        return self.message.getExpiration();
    }

    documentation {Clear JMS properties of the message
        returns Error if any JMS provider level internal error occur}
    public function clearProperties() {
        self.message.clearProperties();
    }

    documentation {Clears body of the JMS message
        returns Error if any JMS provider level internal error occur}
    public function clearBody() returns Error|() {
        return self.message.clearBody();
    }

    documentation {Sets Priority JMS transport header to the message
        P{{value}} The header value}
    public function setPriority (int value) returns Error|() {
        return self.message.setPriority(value);
    }

    documentation {Get JMS transport header Priority from the message
        returns The header value}
    public function getPriority () returns int|Error {
        return self.message.getPriority();
    }

    documentation {Get JMS transport header Redelivered from the message
        returns The header value}
    public function getRedelivered () returns boolean|Error {
        return self.message.getRedelivered();
    }

    documentation {Sets CorrelationID JMS transport header to the message
        P{{value}} The header value}
    public function setCorrelationID (string value) returns Error|() {
        return self.message.setCorrelationID(value);
    }

    documentation {Get JMS transport header CorrelationID from the message
        returns The header value}
    public function getCorrelationID () returns string|()|Error {
        return self.message.getCorrelationID();
    }
};


