# To run the program, put the code in `websocket-client-connector`
# and use the `ballerina run websocket-client-connector` command.
$ ballerina run websocket-proxy-server.bal
# Now, we can run this program using any WebSocket client with "ws://localhost:9090/proxy/ws"

# To check the sample, you can use Chrome or Firefox JavaScript console and run the following commands <br>
$ var ws = new WebSocket("ws://localhost:9090/proxy/ws");
$ ws.onmessage = function(frame) {console.log(frame.data)};
$ ws.onclose = function(frame) {console.log(frame)};

# To send messages
$ ws.send("hello world");

#To close the connection
$ ws.close(1000, "I want to go");
