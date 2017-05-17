import ballerina.lang.system;
import ballerina.lang.messages;
import ballerina.net.ws;
import ballerina.net.http;

@http:BasePath {value:"/group"}
@ws:WebSocketUpgradePath {value:"/ws"}
service oddEvenWebSocketService {

    string evenConnectionGroupName = "evenGroup";
    string oddConnectionGroupName = "oddGroup";
    int i = 0;

    @ws:OnOpen {}
    resource onOpen(message m) {
        if (i % 2 == 0) {
            ws:addConnectionToGroup(evenConnectionGroupName);
        } else {
            ws:addConnectionToGroup(oddConnectionGroupName);
        }
        system:println("New client connected to the server.");
        i = i + 1;
    }

    @ws:OnTextMessage {}
    resource onTextMessage(message m) {
        if (messages:getStringPayload(m) == "removeMe") {
            ws:removeConnectionFromGroup(oddConnectionGroupName);
            ws:removeConnectionFromGroup(evenConnectionGroupName);
        } else {
            ws:pushTextToGroup(oddConnectionGroupName, oddConnectionGroupName + ": " + messages:getStringPayload(m));
            ws:pushTextToGroup(evenConnectionGroupName, evenConnectionGroupName+ ": " + messages:getStringPayload(m));
        }
    }

    @ws:OnClose {}
    resource onClose(message m) {
        system:println("client left the server.");
        ws:broadcastText("client left the server.");
    }
}
