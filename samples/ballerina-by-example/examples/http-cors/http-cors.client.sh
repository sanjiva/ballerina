# simple request
$ curl -v "http://localhost:9090/crossOriginService/company" -H "Origin:http://www.bbc.com"

< HTTP/1.1 200 OK
< Content-Type: application/json
< Access-Control-Allow-Origin: http://www.bbc.com
< Access-Control-Allow-Credentials: true
< Content-Length: 21
{"type":"middleware"}

# preflight request
$ curl -v "http://localhost:9090/crossOriginService/lang" -X OPTIONS -H "Origin:http://www.m3.com" -H "Access-Control-Request-Method:POST"

< HTTP/1.1 200 OK
< Access-Control-Allow-Origin: http://www.m3.com
< Access-Control-Allow-Methods: POST
< Access-Control-Max-Age: 84900
< Content-Length: 0
