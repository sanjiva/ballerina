package ballerina.net.ws;

import ballerina.doc;

@doc:Description { value:"This pushes text from server to the the same client who sent the message."}
@doc:Param { value:"text: Text which should be sent" }
native function pushText (string text);

@doc:Description { value:"This pushes text from server to all the connected clients of the service."}
@doc:Param { value:"text: Text which should be sent" }
native function broadcastText (string text);

@doc:Description { value:"Store the connection globally for the use of other services."}
@doc:Param { value:"connectionName: Name of the connection" }
native function storeConnection (string connectionName);

@doc:Description { value:"Removes connection from a connection store."}
@doc:Param { value:"connectionName: Name of the connection" }
native function removeStoredConnection (string connectionName);

@doc:Description { value:"Push text to the connection chose by the user from the connection store."}
@doc:Param { value:"connectionName: Name of the connection" }
@doc:Param { value:"text: Text which should be sent" }
native function pushTextToConnection (string connectionName, string text);

@doc:Description { value:"This pushes text from server to all the connected clients of the service."}
@doc:Param { value:"connectionGroupName: Name of the connection group" }
native function addConnectionToGroup (string connectionGroupName);

@doc:Description { value:"Removes connection from a connection group."}
@doc:Param { value:"connectionGroupName: Name of the connection group" }
native function removeConnectionFromGroup (string connectionGroupName);

@doc:Description { value:"Removes connection group."}
@doc:Param { value:"connectionGroupName: Name of the connection group" }
native function removeConnectionGroup (string connectionGroupName);

@doc:Description { value:"Push text from server to all the connected clients of the service."}
@doc:Param { value:"connectionGroupName: Name of the connection group" }
@doc:Param { value:"text: Text which should be sent" }
native function pushTextToGroup (string connectionGroupName, string text);



