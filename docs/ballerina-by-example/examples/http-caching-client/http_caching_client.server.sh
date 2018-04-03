# The two services shown above has to be run separately to observe the following output. For clarity, only the relevant parts of the HTTP trace logs has been included here.
$ ballerina run caching_proxy.bal -B[tracelog.http].level=TRACE
ballerina: deploying service(s) in 'caching_proxy.bal'
ballerina: started HTTP/WS server connector 0.0.0.0:9090
# The caching proxy receives a request from a client.
[2018-03-28 21:43:28,309] TRACE {tracelog.http.downstream} - [id: 0x340d752c, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60366] INBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /cache HTTP/1.1
Host: localhost:9090
User-Agent: curl/7.47.0
Accept: */*

# The proxy in turn makes a request to the backend service.
[2018-03-28 21:43:28,434] TRACE {tracelog.http.upstream} - [id: 0xb83303e8, correlatedSource: 0x340d752c, host:/127.0.0.1:47474 - remote:localhost/127.0.0.1:8080] OUTBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /hello HTTP/1.1
Accept: */*
user-agent: curl/7.47.0
host: localhost:8080
accept-encoding: deflate, gzip

# The backend service responds with a 200 OK and it contains ETag and Cache-Control headers. This response can be cached and as such, the caching client caches it. As it can be seen from the max-age directive of the Cache-Control header, this response is valid for 15 seconds.
[2018-03-28 21:43:28,496] TRACE {tracelog.http.upstream} - [id: 0xb83303e8, correlatedSource: 0x340d752c, host:/127.0.0.1:47474 - remote:localhost/127.0.0.1:8080] INBOUND: DefaultHttpResponse(decodeResult: success, version: HTTP/1.1)
HTTP/1.1 200 OK
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
server: ballerina/0.970.0-alpha1-SNAPSHOT
date: Wed, 28 Mar 2018 21:43:28 +0530
transfer-encoding: chunked
[2018-03-28 21:43:28,503] TRACE {tracelog.http.upstream} - [id: 0xb83303e8, correlatedSource: 0x340d752c, host:/127.0.0.1:47474 - remote:localhost/127.0.0.1:8080] INBOUND: DefaultHttpContent(data: PooledUnsafeHeapByteBuf(ridx: 0, widx: 27, cap: 86), decoderResult: success), 27B
{"message":"Hello, World!"}

# The response is sent back to the client.
[2018-03-28 21:43:28,514] TRACE {tracelog.http.downstream} - [id: 0x340d752c, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60366] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 27, cap: 27, components=1))
HTTP/1.1 200 OK
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
date: Wed, 28 Mar 2018 21:43:28 +0530
server: ballerina/0.970.0-alpha1-SNAPSHOT
content-length: 27, 27B
{"message":"Hello, World!"}

# Subsequent requests to the proxy within the next 15 seconds are served from the proxy's cache. As it can be seen here, the backend service is not contacted.
[2018-03-28 21:43:42,323] TRACE {tracelog.http.downstream} - [id: 0xc0dc2067, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60370] INBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /cache HTTP/1.1
Host: localhost:9090
User-Agent: curl/7.47.0
Accept: */*

# Cached response.
[2018-03-28 21:43:42,332] TRACE {tracelog.http.downstream} - [id: 0xc0dc2067, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60370] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 27, cap: 27, components=1))
HTTP/1.1 200 OK
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
date: Wed, 28 Mar 2018 21:43:28 +0530
age: 14
server: ballerina/0.970.0-alpha1-SNAPSHOT
content-length: 27, 27B
{"message":"Hello, World!"}

# Another request which results in a cached response being served.
2018-03-28 21:43:47,496] TRACE {tracelog.http.downstream} - [id: 0x8088f232, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60372] INBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /cache HTTP/1.1
Host: localhost:9090
User-Agent: curl/7.47.0
Accept: */*

[2018-03-28 21:43:47,504] TRACE {tracelog.http.downstream} - [id: 0x8088f232, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60372] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 27, cap: 27, components=1))
HTTP/1.1 200 OK
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
date: Wed, 28 Mar 2018 21:43:28 +0530
age: 19
server: ballerina/0.970.0-alpha1-SNAPSHOT
content-length: 27, 27B
{"message":"Hello, World!"}

# Another request sent is after staying idle for a while.
[2018-03-28 21:43:57,308] TRACE {tracelog.http.downstream} - [id: 0xa960aed2, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60376] INBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /cache HTTP/1.1
Host: localhost:9090
User-Agent: curl/7.47.0
Accept: */*

# As it can be seen, this time the request is not served from the cache. The backend service is contacted. The If-None-Match header sends the entity tag of the now stale response, so that the backend service may determine whether this response is still valid.
[2018-03-28 21:43:57,312] TRACE {tracelog.http.upstream} - [id: 0xb83303e8, correlatedSource: 0xa960aed2, host:/127.0.0.1:47474 - remote:localhost/127.0.0.1:8080] OUTBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /hello HTTP/1.1
if-none-match: 620328e8
host: localhost:8080
user-agent: ballerina/0.970.0-alpha1-SNAPSHOT
accept-encoding: deflate, gzip

# The response has not changed. Therefore the backend services responds with a 304 Not Modified response. Based on this, the proxy will refresh the response, so that it can continue serving the cached response.
[2018-03-28 21:43:57,323] TRACE {tracelog.http.downstream} - [id: 0x5f677afe, correlatedSource: n/a, host:/127.0.0.1:8080 - remote:/127.0.0.1:47474] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 0, cap: 0, components=1))
HTTP/1.1 304 Not Modified
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
content-length: 0
server: ballerina/0.970.0-alpha1-SNAPSHOT
date: Wed, 28 Mar 2018 21:43:57 +0530

# The cached response is served yet again, since the response has not changed.
[2018-03-28 21:43:57,341] TRACE {tracelog.http.downstream} - [id: 0xa960aed2, correlatedSource: n/a, host:/127.0.0.1:9090 - remote:/127.0.0.1:60376] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 27, cap: 27, components=1))
HTTP/1.1 200 OK
age: 19
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
date: Wed, 28 Mar 2018 21:43:57 +0530
server: ballerina/0.970.0-alpha1-SNAPSHOT
content-length: 27, 27B
{"message":"Hello, World!"}

# The output for the mock service.
$ ballerina run hello_service.bal -B[tracelog.http].level=TRACE
ballerina: deploying service(s) in 'hello_service.bal'
ballerina: started HTTP/WS server connector 0.0.0.0:8080
# For the first request the caching proxy receives, it sends a request to the hello service.
[2018-03-28 21:43:28,443] TRACE {tracelog.http.downstream} - [id: 0x5f677afe, correlatedSource: n/a, host:/127.0.0.1:8080 - remote:/127.0.0.1:47474] INBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /hello HTTP/1.1
Accept: */*
user-agent: curl/7.47.0
host: localhost:8080
accept-encoding: deflate, gzip

# The service responds with a 200 OK with relevant caching headers set.
[2018-03-28 21:43:28,470] TRACE {tracelog.http.downstream} - [id: 0x5f677afe, correlatedSource: n/a, host:/127.0.0.1:8080 - remote:/127.0.0.1:47474] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 27, cap: 27, components=1))
HTTP/1.1 200 OK
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
content-length: 27
server: ballerina/0.970.0-alpha1-SNAPSHOT
date: Wed, 28 Mar 2018 21:43:28 +0530, 27B
{"message":"Hello, World!"}

# The backend service only gets another request when the cached response the proxy has expired and it wants to validate it again.
[2018-03-28 21:43:57,314] TRACE {tracelog.http.downstream} - [id: 0x5f677afe, correlatedSource: n/a, host:/127.0.0.1:8080 - remote:/127.0.0.1:47474] INBOUND: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /hello HTTP/1.1
if-none-match: 620328e8
host: localhost:8080
user-agent: ballerina/0.970.0-alpha1-SNAPSHOT
accept-encoding: deflate, gzip

# After taking a look at the If-None-Match header, the service determines that the response is still the same and that the proxy can keep reusing it.
[2018-03-28 21:43:57,323] TRACE {tracelog.http.downstream} - [id: 0x5f677afe, correlatedSource: n/a, host:/127.0.0.1:8080 - remote:/127.0.0.1:47474] OUTBOUND: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 0, cap: 0, components=1))
HTTP/1.1 304 Not Modified
cache-control: must-revalidate, no-transform, public, max-age=15
etag: 620328e8
content-type: application/json
content-length: 0
server: ballerina/0.970.0-alpha1-SNAPSHOT
date: Wed, 28 Mar 2018 21:43:57 +0530, 0B
