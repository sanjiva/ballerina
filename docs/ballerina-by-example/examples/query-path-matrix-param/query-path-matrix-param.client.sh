$ curl "http://localhost:9090/sample/path;a=4;b=5/value1;x=10;y=15?bar=value2"
{
    "pathParam": "value1",
    "queryParam": "value2",
    "matrix": {
        "path": "a=4, b=5",
        "foo": "x=10, y=15"
    }
}
