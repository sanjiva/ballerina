import ballerina/sql;
import ballerina/jdbc;
import ballerina/io;
import ballerina/time;

type ResultPrimitive record {
    int INT_TYPE;
    int LONG_TYPE;
    float FLOAT_TYPE;
    float DOUBLE_TYPE;
    boolean BOOLEAN_TYPE;
    string STRING_TYPE;
};

type ResultSetTestAlias record {
    int INT_TYPE;
    int LONG_TYPE;
    float FLOAT_TYPE;
    float DOUBLE_TYPE;
    boolean BOOLEAN_TYPE;
    string STRING_TYPE;
    int DT2INT_TYPE;
};

type ResultObject record {
    byte[] BLOB_TYPE;
    string CLOB_TYPE;
    byte[] BINARY_TYPE;
};

type ResultMap record {
    int[] INT_ARRAY;
    int[] LONG_ARRAY;
    float[] FLOAT_ARRAY;
    boolean[] BOOLEAN_ARRAY;
    string[] STRING_ARRAY;
};

type ResultBlob record {
    byte[] BLOB_TYPE;
};

type ResultDates record {
    string DATE_TYPE;
    string TIME_TYPE;
    string TIMESTAMP_TYPE;
    string DATETIME_TYPE;
};

type ResultDatesStruct record {
    time:Time DATE_TYPE;
    time:Time TIME_TYPE;
    time:Time TIMESTAMP_TYPE;
    time:Time DATETIME_TYPE;
};

type ResultDatesInt record {
    int DATE_TYPE;
    int TIME_TYPE;
    int TIMESTAMP_TYPE;
    int DATETIME_TYPE;
};

type ResultSetFloat record {
    float FLOAT_TYPE;
    float DOUBLE_TYPE;
    float NUMERIC_TYPE;
    float DECIMAL_TYPE;
};

type ResultPrimitiveInt record {
    int INT_TYPE;
};

type ResultCount record {
    int COUNTVAL;
};

type ResultTest record {
    int t1Row;
    int t1Int;
    int t2Row;
    int t2Int;
};

type ResultSignedInt record {
    int ID;
    int? TINYINTDATA;
    int? SMALLINTDATA;
    int? INTDATA;
    int? BIGINTDATA;
};

type ResultComplexTypes record {
    int ROW_ID;
    byte[]|() BLOB_TYPE;
    string? CLOB_TYPE;
    byte[]|() BINARY_TYPE;
};

type TestTypeData record {
    int i;
    int[] iA;
    int l;
    int[] lA;
    float f;
    float[] fA;
    float d;
    boolean b;
    string s;
    float[] dA;
    boolean[] bA;
    string[] sA;
};

function testToJson(string jdbcUrl, string userName, string password) returns (json) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
                  boolean_type, string_type from DataTable WHERE row_id = 1", ());
        return check <json>dt;
    } finally {
        testDB.stop();
    }
}

function testToXml(string jdbcUrl, string userName, string password) returns (xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
                   boolean_type, string_type from DataTable WHERE row_id = 1", ());

        return check <xml>dt;
    } finally {
        testDB.stop();
    }
}

function testToXmlMultipleConsume(string jdbcUrl, string userName, string password) returns (xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
        boolean_type, string_type from DataTable WHERE row_id = 1", ());

        xml result = check <xml>dt;
        io:println(result);
        return result;
    } finally {
        testDB.stop();
    }
}

function testToXmlWithAdd(string jdbcUrl, string userName, string password) returns (xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 2 }
    };

    try {
        table dt1 = check testDB->select("SELECT int_type from DataTable WHERE row_id = 1", ());
        xml result1 = check <xml>dt1;

        table dt2 = check testDB->select("SELECT int_type from DataTable WHERE row_id = 1", ());
        xml result2 = check <xml>dt2;

        xml result = result1 + result2;

        table dt3 = check testDB->select("SELECT int_type from DataTable WHERE row_id = 1", ());
        return result;
    } finally {
        testDB.stop();
    }
}

function testToJsonMultipleConsume(string jdbcUrl, string userName, string password) returns (json) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
        boolean_type, string_type from DataTable WHERE row_id = 1", ());

        json result = check <json>dt;
        io:println(result);
        return result;
    } finally {
        testDB.stop();
    }
}


function toXmlComplex(string jdbcUrl, string userName, string password) returns (xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, int_array, long_type, long_array, float_type,
                    float_array, double_type, boolean_type, string_type, double_array, boolean_array, string_array
                    from MixTypes where row_id =1", ());

        return check <xml>dt;
    } finally {
        testDB.stop();
    }
}

function testToXmlComplexWithStructDef(string jdbcUrl, string userName, string password) returns (xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, int_array, long_type, long_array, float_type,
                    float_array, double_type, boolean_type, string_type, double_array, boolean_array, string_array
                    from MixTypes where row_id =1", TestTypeData);

        return check <xml>dt;
    } finally {
        testDB.stop();
    }
}


function testToJsonComplex(string jdbcUrl, string userName, string password) returns (json) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, int_array, long_type, long_array, float_type,
                    float_array, double_type, boolean_type, string_type, double_array, boolean_array, string_array
                    from MixTypes where row_id =1", ());

        return check <json>dt;
    } finally {
        testDB.stop();
    }
}


function testToJsonComplexWithStructDef(string jdbcUrl, string userName, string password) returns (json) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, int_array, long_type, long_array, float_type,
                    float_array, double_type, boolean_type, string_type, double_array, boolean_array, string_array
                    from MixTypes where row_id =1", TestTypeData);

        return check <json>dt;
    } finally {
        testDB.stop();
    }
}

function testJsonWithNull(string jdbcUrl, string userName, string password) returns (json) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
                  boolean_type, string_type from DataTable WHERE row_id = 2", ());

        return check <json>dt;
    } finally {
        testDB.stop();
    }
}

function testXmlWithNull(string jdbcUrl, string userName, string password) returns (xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    try {
        table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
                   boolean_type, string_type from DataTable WHERE row_id = 2", ());

        return check <xml>dt;
    } finally {
        testDB.stop();
    }
}

function testToXmlWithinTransaction(string jdbcUrl, string userName, string password) returns (string, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    int returnValue = 0;
    string resultXml;
    try {
        transaction {
            table dt = check testDB->select("SELECT int_type, long_type from DataTable WHERE row_id = 1", ());

            var result = check <xml>dt;
            resultXml = io:sprintf("%l", result);
        }
        return (resultXml, returnValue);
    } finally {
        testDB.stop();
    }
}

function testToJsonWithinTransaction(string jdbcUrl, string userName, string password) returns (string, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    int returnValue = 0;
    string result;
    try {
        transaction {
            table dt = check testDB->select("SELECT int_type, long_type from DataTable WHERE row_id = 1", ());

            var j = check <json>dt;
            result = io:sprintf("%j", j);
        }
        return (result, returnValue);
    } finally {
        testDB.stop();
    }
}

function testGetPrimitiveTypes(string jdbcUrl, string userName, string password) returns (int, int, float, float, boolean, string) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    var dtRet = testDB->select("SELECT int_type, long_type, float_type, double_type,
              boolean_type, string_type from DataTable WHERE row_id = 1", ResultPrimitive);
    table dt = check dtRet;

    int i;
    int l;
    float f;
    float d;
    boolean b;
    string s;
    while (dt.hasNext()) {
        ResultPrimitive rs = check <ResultPrimitive>dt.getNext();
        i = rs.INT_TYPE;
        l = rs.LONG_TYPE;
        f = rs.FLOAT_TYPE;
        d = rs.DOUBLE_TYPE;
        b = rs.BOOLEAN_TYPE;
        s = rs.STRING_TYPE;
    }
    testDB.stop();
    return (i, l, f, d, b, s);
}

function testGetComplexTypes(string jdbcUrl, string userName, string password) returns (byte[], string, byte[]) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT blob_type,clob_type,binary_type from ComplexTypes where row_id = 1",
        ResultObject);

    byte[] blobData;
    string clob;
    byte[] binaryData;
    while (dt.hasNext()) {
        ResultObject rs = check <ResultObject>dt.getNext();
        blobData = rs.BLOB_TYPE;
        clob = rs.CLOB_TYPE;
        binaryData = rs.BINARY_TYPE;
    }
    testDB.stop();
    return (blobData, clob, binaryData);
}

function testArrayData(string jdbcUrl, string userName, string password) returns (int[], int[], float[], string[],
            boolean[]) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_array, long_array, float_array, boolean_array,
              string_array from ArrayTypes where row_id = 1", ResultMap);

    int[] int_arr;
    int[] long_arr;
    float[] float_arr;
    string[] string_arr;
    boolean[] boolean_arr;

    while (dt.hasNext()) {
        ResultMap rs = check <ResultMap>dt.getNext();
        int_arr = rs.INT_ARRAY;
        long_arr = rs.LONG_ARRAY;
        float_arr = rs.FLOAT_ARRAY;
        boolean_arr = rs.BOOLEAN_ARRAY;
        string_arr = rs.STRING_ARRAY;
    }
    testDB.stop();
    return (int_arr, long_arr, float_arr, string_arr, boolean_arr);
}

function testArrayDataInsertAndPrint(string jdbcUrl, string userName, string password) returns (int, int, int, int, int,
            int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    int[] dataint = [1, 2, 3];
    float[] datafloat = [33.4, 55.4];
    string[] datastring = ["hello", "world"];
    boolean[] databoolean = [true, false, false, true, true];

    sql:Parameter paraID = { sqlType: sql:TYPE_INTEGER, value: 4 };
    sql:Parameter paraInt = { sqlType: sql:TYPE_ARRAY, value: dataint };
    sql:Parameter paraLong = { sqlType: sql:TYPE_ARRAY, value: dataint };
    sql:Parameter paraFloat = { sqlType: sql:TYPE_ARRAY, value: datafloat };
    sql:Parameter paraString = { sqlType: sql:TYPE_ARRAY, value: datastring };
    sql:Parameter paraBool = { sqlType: sql:TYPE_ARRAY, value: databoolean };

    int intArrLen;
    int longArrLen;
    int floatArrLen;
    int boolArrLen;
    int strArrLen;

    int updateRet = check testDB->update("insert into ArrayTypes(row_id, int_array, long_array, float_array,
                                string_array, boolean_array) values (?,?,?,?,?,?)",
        paraID, paraInt, paraLong, paraFloat, paraString, paraBool);

    var dtRet = testDB->select("SELECT int_array, long_array, float_array, boolean_array, string_array
                                 from ArrayTypes where row_id = 4", ResultMap);
    table dt = check dtRet;

    while (dt.hasNext()) {
        ResultMap rs = check <ResultMap>dt.getNext();
        io:println(rs.INT_ARRAY);
        intArrLen = lengthof rs.INT_ARRAY;
        io:println(rs.LONG_ARRAY);
        longArrLen = lengthof rs.LONG_ARRAY;
        io:println(rs.FLOAT_ARRAY);
        floatArrLen = lengthof rs.FLOAT_ARRAY;
        io:println(rs.BOOLEAN_ARRAY);
        boolArrLen = lengthof rs.BOOLEAN_ARRAY;
        io:println(rs.STRING_ARRAY);
        strArrLen = lengthof rs.STRING_ARRAY;
    }
    testDB.stop();
    return (updateRet, intArrLen, longArrLen, floatArrLen, boolArrLen, strArrLen);
}

function testDateTime(string jdbcUrl, string userName, string password, int datein, int timein, int timestampin)
             returns (string, string, string, string) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };
    string date;
    string time;
    string timestamp;
    string datetime;

    sql:Parameter para0 = { sqlType: sql:TYPE_INTEGER, value: 1 };
    sql:Parameter para1 = { sqlType: sql:TYPE_DATE, value: datein };
    sql:Parameter para2 = { sqlType: sql:TYPE_TIME, value: timein };
    sql:Parameter para3 = { sqlType: sql:TYPE_TIMESTAMP, value: timestampin };
    sql:Parameter para4 = { sqlType: sql:TYPE_DATETIME, value: timestampin };

    int countRet = check testDB->update("Insert into DateTimeTypes
        (row_id, date_type, time_type, timestamp_type, datetime_type) values (?,?,?,?,?)",
        para0, para1, para2, para3, para4);

    table dt = check testDB->select("SELECT date_type, time_type, timestamp_type, datetime_type
                from DateTimeTypes where row_id = 1", ResultDates);

    while (dt.hasNext()) {
        ResultDates rs = check <ResultDates>dt.getNext();
        time = rs.TIME_TYPE;
        date = rs.DATE_TYPE;
        timestamp = rs.TIMESTAMP_TYPE;
        datetime = rs.DATETIME_TYPE;
    }
    testDB.stop();
    return (date, time, timestamp, datetime);
}

function testDateTimeAsTimeStruct(string jdbcUrl, string userName, string password) returns (int, int, int, int, int,
            int, int, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    int dateInserted;
    int dateRetrieved;
    int timeInserted;
    int timeRetrieved;
    int timestampInserted;
    int timestampRetrieved;
    int datetimeInserted;
    int datetimeRetrieved;

    time:Time dateStruct = time:createTime(2017, 5, 23, 0, 0, 0, 0, "");

    time:Timezone zoneValue = { zoneId: "UTC" };
    time:Time timeStruct = new(51323000, zoneValue);

    time:Time timestampStruct = time:createTime(2017, 1, 25, 16, 12, 23, 0, "UTC");
    time:Time datetimeStruct = time:createTime(2017, 1, 31, 16, 12, 23, 332, "UTC");
    dateInserted = dateStruct.time;
    timeInserted = timeStruct.time;
    timestampInserted = timestampStruct.time;
    datetimeInserted = datetimeStruct.time;

    sql:Parameter para0 = { sqlType: sql:TYPE_INTEGER, value: 31 };
    sql:Parameter para1 = { sqlType: sql:TYPE_DATE, value: dateStruct };
    sql:Parameter para2 = { sqlType: sql:TYPE_TIME, value: timeStruct };
    sql:Parameter para3 = { sqlType: sql:TYPE_TIMESTAMP, value: timestampStruct };
    sql:Parameter para4 = { sqlType: sql:TYPE_DATETIME, value: datetimeStruct };

    int count = check testDB->update("Insert into DateTimeTypes
        (row_id, date_type, time_type, timestamp_type, datetime_type) values (?,?,?,?,?)",
        para0, para1, para2, para3, para4);

    table dt = check testDB->select("SELECT date_type, time_type, timestamp_type, datetime_type
                from DateTimeTypes where row_id = 31", ResultDatesStruct);

    while (dt.hasNext()) {
        ResultDatesStruct rs = check <ResultDatesStruct>dt.getNext();
        dateRetrieved = rs.DATE_TYPE.time;
        timeRetrieved = rs.TIME_TYPE.time;
        timestampRetrieved = rs.TIMESTAMP_TYPE.time;
        datetimeRetrieved = rs.DATETIME_TYPE.time;
    }
    testDB.stop();
    return (dateInserted, dateRetrieved, timeInserted, timeRetrieved, timestampInserted, timestampRetrieved,
    datetimeInserted, datetimeRetrieved);
}

function testDateTimeInt(string jdbcUrl, string userName, string password, int datein, int timein, int timestampin)
returns (int, int, int, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    sql:Parameter para0 = { sqlType: sql:TYPE_INTEGER, value: 32 };
    sql:Parameter para1 = { sqlType: sql:TYPE_DATE, value: datein };
    sql:Parameter para2 = { sqlType: sql:TYPE_TIME, value: timein };
    sql:Parameter para3 = { sqlType: sql:TYPE_TIMESTAMP, value: timestampin };
    sql:Parameter para4 = { sqlType: sql:TYPE_DATETIME, value: timestampin };

    int date;
    int time;
    int timestamp;
    int datetime;

    int countt = check testDB->update("Insert into DateTimeTypes
        (row_id, date_type, time_type, timestamp_type, datetime_type) values (?,?,?,?,?)",
        para0, para1, para2, para3, para4);

    table<ResultDatesInt> dt = check testDB->select("SELECT date_type, time_type, timestamp_type, datetime_type
                from DateTimeTypes where row_id = 32", ResultDatesInt);

    while (dt.hasNext()) {
        ResultDatesInt rs = check <ResultDatesInt>dt.getNext();
        time = rs.TIME_TYPE;
        date = rs.DATE_TYPE;
        timestamp = rs.TIMESTAMP_TYPE;
        datetime = rs.DATETIME_TYPE;
    }
    testDB.stop();
    return (date, time, timestamp, datetime);
}

function testBlobData(string jdbcUrl, string userName, string password) returns (byte[]) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    string blobStringData;
    table dt = check testDB->select("SELECT blob_type from ComplexTypes where row_id = 1", ResultBlob);

    byte[] blobData;
    while (dt.hasNext()) {
        ResultBlob rs = check <ResultBlob>dt.getNext();
        blobData = rs.BLOB_TYPE;
    }

    testDB.stop();
    return blobData;
}

function testColumnAlias(string jdbcUrl, string userName, string password) returns (int, int, float, float, boolean,
            string, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT dt1.int_type, dt1.long_type, dt1.float_type,
           dt1.double_type,dt1.boolean_type, dt1.string_type,dt2.int_type as dt2int_type from DataTable dt1
           left join DataTableRep dt2 on dt1.row_id = dt2.row_id WHERE dt1.row_id = 1;", ResultSetTestAlias);

    int i;
    int l;
    float f;
    float d;
    boolean b;
    string s;
    int i2;

    while (dt.hasNext()) {
        ResultSetTestAlias rs = check <ResultSetTestAlias>dt.getNext();
        i = rs.INT_TYPE;
        l = rs.LONG_TYPE;
        f = rs.FLOAT_TYPE;
        d = rs.DOUBLE_TYPE;
        b = rs.BOOLEAN_TYPE;
        s = rs.STRING_TYPE;
        i2 = rs.DT2INT_TYPE;
    }
    testDB.stop();
    return (i, l, f, d, b, s, i2);
}

function testBlobInsert(string jdbcUrl, string userName, string password) returns (int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT blob_type from ComplexTypes where row_id = 1", ResultBlob);

    byte[] blobData;
    while (dt.hasNext()) {
        ResultBlob rs = check <ResultBlob>dt.getNext();
        blobData = rs.BLOB_TYPE;
    }
    sql:Parameter para0 = { sqlType: sql:TYPE_INTEGER, value: 10 };
    sql:Parameter para1 = { sqlType: sql:TYPE_BLOB, value: blobData };
    int insertCount = check testDB->update("Insert into ComplexTypes (row_id, blob_type) values (?,?)", para0, para1);

    testDB.stop();
    return insertCount;
}


function testTableAutoClose(string jdbcUrl, string userName, string password) returns (int, json) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type from DataTable WHERE row_id = 1", ResultPrimitiveInt);

    int i;
    string test;
    while (dt.hasNext()) {
        var rs = check <ResultPrimitiveInt>dt.getNext();
        i = rs.INT_TYPE;
    }

    table dt2 = check testDB->select("SELECT int_type, long_type, float_type, double_type,
              boolean_type, string_type from DataTable WHERE row_id = 1", ());

    json jsonData = check <json>dt2;
    _ = jsonData.remove("int_type");

    _ = testDB->select("SELECT int_type, long_type, float_type, double_type,
              boolean_type, string_type from DataTable WHERE row_id = 1", ());

    testDB.stop();
    return (i, jsonData);
}

function testTableManualClose(string jdbcUrl, string userName, string password) returns (int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type from DataTable", ResultPrimitiveInt);

    int i = 0;
    while (dt.hasNext()) {
        ResultPrimitiveInt rs = check <ResultPrimitiveInt>dt.getNext();
        int ret = rs.INT_TYPE;
        i = i + 1;
        if (i == 1) {
            break;
        }
    }
    dt.close();

    int data;
    table dt2 = check testDB->select("SELECT int_type from DataTable WHERE row_id = 1", ResultPrimitiveInt);

    while (dt2.hasNext()) {
        ResultPrimitiveInt rs2 = check <ResultPrimitiveInt>dt2.getNext();
        data = rs2.INT_TYPE;
    }
    dt2.close();
    testDB.stop();
    return data;
}

function testCloseConnectionPool(string jdbcUrl, string userName, string password, string connectionCountQuery) returns (int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select (connectionCountQuery, ResultCount);

    int count;
    while (dt.hasNext()) {
        ResultCount rs = check <ResultCount>dt.getNext();
        count = rs.COUNTVAL;
    }
    testDB.stop();
    return count;
}

function testTablePrintAndPrintln(string jdbcUrl, string userName, string password) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
        boolean_type, string_type from DataTable WHERE row_id = 1", ());

    io:println(dt);
    io:print(dt);
    testDB.stop();
}

function testMutltipleRows(string jdbcUrl, string userName, string password) returns (int, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type from DataTableRep", ResultPrimitiveInt);

    ResultPrimitiveInt rs1 = { INT_TYPE: -1 };
    ResultPrimitiveInt rs2 = { INT_TYPE: -1 };
    int i = 0;
    while (dt.hasNext()) {
        if (i == 0) {
            rs1 = check <ResultPrimitiveInt>dt.getNext();
        } else {
            rs2 = check <ResultPrimitiveInt>dt.getNext();
        }
        i = i + 1;
    }
    testDB.stop();
    return (rs1.INT_TYPE, rs2.INT_TYPE);
}

function testMutltipleRowsWithoutLoop(string jdbcUrl, string userName, string password) returns (int, int, int, int,
            string, string) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    //Iterate the whole result
    table dt = check testDB->select("SELECT int_type from DataTableRep order by int_type desc", ResultPrimitiveInt);

    int i1;
    int i2;
    int i3;
    int i4;
    string st1;
    string st2;
    while (dt.hasNext()) {
        ResultPrimitiveInt rs = check <ResultPrimitiveInt>dt.getNext();
        i1 = rs.INT_TYPE;
    }

    //Pick the first row only
    dt = check testDB->select("SELECT int_type from DataTableRep order by int_type desc", ResultPrimitiveInt);

    if (dt.hasNext()) {
        ResultPrimitiveInt rs = check <ResultPrimitiveInt>dt.getNext();
        i2 = rs.INT_TYPE;
    }
    dt.close();

    //Pick all the rows without checking
    dt = check testDB->select("SELECT int_type from DataTableRep order by int_type desc", ResultPrimitiveInt);

    ResultPrimitiveInt rs1 = check <ResultPrimitiveInt>dt.getNext();
    i3 = rs1.INT_TYPE;

    ResultPrimitiveInt rs2 = check <ResultPrimitiveInt>dt.getNext();
    i4 = rs2.INT_TYPE;
    dt.close();

    //Pick the first row by checking and next row without checking
    string s1 = "";
    dt = check testDB->select("SELECT int_type from DataTableRep order by int_type desc", ResultPrimitiveInt);

    if (dt.hasNext()) {
        ResultPrimitiveInt rs = check <ResultPrimitiveInt>dt.getNext();
        int i = rs.INT_TYPE;
        s1 = s1 + i;
    }

    ResultPrimitiveInt rs = check <ResultPrimitiveInt>dt.getNext();
    int i = rs.INT_TYPE;
    s1 = s1 + "_" + i;

    if (dt.hasNext()) {
        s1 = s1 + "_" + "HAS";
    } else {
        s1 = s1 + "_" + "NOT";
    }

    //Pick the first row without checking, then check and no fetch, and finally fetch row by checking
    string s2 = "";
    dt = check testDB->select("SELECT int_type from DataTableRep order by int_type desc", ResultPrimitiveInt);

    rs = check <ResultPrimitiveInt>dt.getNext();
    i = rs.INT_TYPE;
    s2 = s2 + i;
    if (dt.hasNext()) {
        s2 = s2 + "_" + "HAS";
    } else {
        s2 = s2 + "_" + "NO";
    }
    if (dt.hasNext()) {
        s2 = s2 + "_" + "HAS";
    } else {
        s2 = s2 + "_" + "NO";
    }
    if (dt.hasNext()) {
        rs = check <ResultPrimitiveInt>dt.getNext();
        i = rs.INT_TYPE;
        s2 = s2 + "_" + i;
    }
    if (dt.hasNext()) {
        s2 = s2 + "_" + "HAS";
    } else {
        s2 = s2 + "_" + "NO";
    }
    if (dt.hasNext()) {
        s2 = s2 + "_" + "HAS";
    } else {
        s2 = s2 + "_" + "NO";
    }
    testDB.stop();
    return (i1, i2, i3, i4, s1, s2);
}

function testHasNextWithoutConsume(string jdbcUrl, string userName, string password) returns (boolean, boolean, boolean)
{
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type from DataTableRep order by int_type desc", ResultPrimitiveInt);

    boolean b1 = false;
    boolean b2 = false;
    boolean b3 = false;

    if (dt.hasNext()) {
        b1 = true;
    }
    if (dt.hasNext()) {
        b2 = true;
    }
    if (dt.hasNext()) {
        b3 = true;
    }
    testDB.stop();
    return (b1, b2, b3);
}

function testGetFloatTypes(string jdbcUrl, string userName, string password) returns (float, float, float, float) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT float_type, double_type,
                  numeric_type, decimal_type from FloatTable WHERE row_id = 1", ResultSetFloat);

    float f;
    float d;
    float num;
    float dec;

    while (dt.hasNext()) {
        var rs = check <ResultSetFloat>dt.getNext();
        f = rs.FLOAT_TYPE;
        d = rs.DOUBLE_TYPE;
        num = rs.NUMERIC_TYPE;
        dec = rs.DECIMAL_TYPE;
    }
    testDB.stop();
    return (f, d, num, dec);
}

function testSignedIntMaxMinValues(string jdbcUrl, string userName, string password) returns (int, int, int, string,
            string, string) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    string insertSQL = "INSERT INTO IntegerTypes(id,tinyIntData, smallIntData, intData, bigIntData) VALUES (?,?, ?,?,?)"
    ;
    string selectSQL = "SELECT id,tinyIntData,smallIntData,intData,bigIntData FROM IntegerTypes";

    int maxInsert;
    int minInsert;
    int nullInsert;
    string jsonStr;
    string xmlStr;
    string str;

    //Insert signed max
    sql:Parameter para1 = { sqlType: sql:TYPE_INTEGER, value: 1 };
    sql:Parameter para2 = { sqlType: sql:TYPE_TINYINT, value: 127 };
    sql:Parameter para3 = { sqlType: sql:TYPE_SMALLINT, value: 32767 };
    sql:Parameter para4 = { sqlType: sql:TYPE_INTEGER, value: 2147483647 };
    sql:Parameter para5 = { sqlType: sql:TYPE_BIGINT, value: 9223372036854775807 };
    maxInsert = check testDB->update(insertSQL, para1, para2, para3, para4, para5);

    //Insert signed min
    para1 = { sqlType: sql:TYPE_INTEGER, value: 2 };
    para2 = { sqlType: sql:TYPE_TINYINT, value: -128 };
    para3 = { sqlType: sql:TYPE_SMALLINT, value: -32768 };
    para4 = { sqlType: sql:TYPE_INTEGER, value: -2147483648 };
    para5 = { sqlType: sql:TYPE_BIGINT, value: -9223372036854775808 };
    minInsert = check testDB->update(insertSQL, para1, para2, para3, para4, para5);

    //Insert null
    para1 = { sqlType: sql:TYPE_INTEGER, value: 3 };
    para2 = { sqlType: sql:TYPE_TINYINT, value: () };
    para3 = { sqlType: sql:TYPE_SMALLINT, value: () };
    para4 = { sqlType: sql:TYPE_INTEGER, value: () };
    para5 = { sqlType: sql:TYPE_BIGINT, value: () };
    nullInsert = check testDB->update(insertSQL, para1, para2, para3, para4, para5);

    var dtRet = testDB->select(selectSQL, ());
    table dt = check dtRet;

    var j = check <json>dt;
    jsonStr = io:sprintf("%j", j);

    dtRet = testDB->select(selectSQL, ());
    dt = check dtRet;

    var x = check <xml>dt;
    xmlStr = io:sprintf("%l", x);

    dtRet = testDB->select(selectSQL, ResultSignedInt);
    dt = check dtRet;

    str = "";
    while (dt.hasNext()) {
        var result = check <ResultSignedInt>dt.getNext();
        str = str + result.ID + "|" + (result.TINYINTDATA but { () => -1 }) + "|" + (result.SMALLINTDATA but { () =>
            -1 }) + "|" + (result.INTDATA but { () => -1 }) + "|" + (result.BIGINTDATA but { () => -1 }) + "#";
    }
    testDB.stop();
    return (maxInsert, minInsert, nullInsert, jsonStr, xmlStr, str);
}

function testComplexTypeInsertAndRetrieval(string jdbcUrl, string userName, string password) returns (int, int, string,
            string, string, byte[][]) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    string insertSQL = "INSERT INTO ComplexTypes(row_id, blob_type, clob_type, binary_type) VALUES (?,?,?,?)";
    string selectSQL =
    "SELECT row_id, blob_type, clob_type, binary_type FROM ComplexTypes where row_id = 100 or row_id = 200";
    string text = "Sample Text";
    byte[] content = text.toByteArray("UTF-8");

    int retDataInsert;
    int retNullInsert;
    string jsonStr;
    string xmlStr;
    string str;

    //Insert data
    sql:Parameter para1 = { sqlType: sql:TYPE_INTEGER, value: 100 };
    sql:Parameter para2 = { sqlType: sql:TYPE_BLOB, value: content };
    sql:Parameter para3 = { sqlType: sql:TYPE_CLOB, value: text };
    sql:Parameter para4 = { sqlType: sql:TYPE_BINARY, value: content };
    retDataInsert = check testDB->update(insertSQL, para1, para2, para3, para4);

    //Insert null values
    para1 = { sqlType: sql:TYPE_INTEGER, value: 200 };
    para2 = { sqlType: sql:TYPE_BLOB, value: () };
    para3 = { sqlType: sql:TYPE_CLOB, value: () };
    para4 = { sqlType: sql:TYPE_BINARY, value: () };
    retNullInsert = check testDB->update(insertSQL, para1, para2, para3, para4);

    var dtRet = testDB->select(selectSQL, ());
    table dt = check dtRet;

    var j = check <json>dt;
    jsonStr = io:sprintf("%j", j);

    dtRet = testDB->select(selectSQL, ());
    dt = check dtRet;

    var x = check <xml>dt;
    xmlStr = io:sprintf("%l", x);

    dt = check testDB->select(selectSQL, ResultComplexTypes);

    str = "";
    byte[][] expected;
    int i = 0;
    while (dt.hasNext()) {
        var result = check <ResultComplexTypes>dt.getNext();
        string blobType;
        match result.BLOB_TYPE {
            byte[] b => {
                expected[i] = b;
                blobType = "nonNil";
            }
            () => blobType = "nil";
        }
        str = str + result.ROW_ID + "|" + blobType + "|" + (result.CLOB_TYPE but { () => "nil" }) + "|";
        i++;
    }
    testDB.stop();
    return (retDataInsert, retNullInsert, jsonStr, xmlStr, str, expected);
}

function testJsonXMLConversionwithDuplicateColumnNames(string jdbcUrl, string userName, string password) returns (json,
            xml) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 2 }
    };

    table dt = check testDB->select("SELECT dt1.row_id, dt1.int_type, dt2.row_id, dt2.int_type from DataTable dt1 left
            join DataTableRep dt2 on dt1.row_id = dt2.row_id WHERE dt1.row_id = 1", ());
    json j = check <json>dt;

    table dt2 = check testDB->select("SELECT dt1.row_id, dt1.int_type, dt2.row_id, dt2.int_type from DataTable dt1 left
            join DataTableRep dt2 on dt1.row_id = dt2.row_id WHERE dt1.row_id = 1", ());
    xml x = check <xml>dt2;

    testDB.stop();
    return (j, x);
}

function testStructFieldNotMatchingColumnName(string jdbcUrl, string userName, string password) returns (int, int, int,
            int, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT count(*) from DataTable WHERE row_id = 1", ResultCount);

    int countAll;
    int i1;
    int i2;
    int i3;
    int i4;
    while (dt.hasNext()) {
        ResultCount rs = check <ResultCount>dt.getNext();
        countAll = rs.COUNTVAL;
    }

    table dt2 = check testDB->select("SELECT dt1.row_id, dt1.int_type, dt2.row_id, dt2.int_type from DataTable dt1 left
            join DataTableRep dt2 on dt1.row_id = dt2.row_id WHERE dt1.row_id = 1", ResultTest);

    while (dt2.hasNext()) {
        ResultTest rs = check <ResultTest>dt2.getNext();
        i1 = rs.t1Row;
        i2 = rs.t1Int;
        i3 = rs.t2Row;
        i4 = rs.t2Int;
    }
    testDB.stop();
    return (countAll, i1, i2, i3, i4);
}

function testGetPrimitiveTypesWithForEach(string jdbcUrl, string userName, string password) returns (int, int, float,
            float, boolean, string) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table<ResultPrimitive> dt = check testDB->select("SELECT int_type, long_type, float_type, double_type,
              boolean_type, string_type from DataTable WHERE row_id = 1", ResultPrimitive);

    int i;
    int l;
    float f;
    float d;
    boolean b;
    string s;
    foreach x in dt {
        i = x.INT_TYPE;
        l = x.LONG_TYPE;
        f = x.FLOAT_TYPE;
        d = x.DOUBLE_TYPE;
        b = x.BOOLEAN_TYPE;
        s = x.STRING_TYPE;
    }
    testDB.stop();
    return (i, l, f, d, b, s);
}

function testMutltipleRowsWithForEach(string jdbcUrl, string userName, string password) returns (int, int) {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table<ResultPrimitiveInt> dt = check testDB->select("SELECT int_type from DataTableRep", ResultPrimitiveInt);

    ResultPrimitiveInt rs1 = { INT_TYPE: -1 };
    ResultPrimitiveInt rs2 = { INT_TYPE: -1 };
    int i = 0;
    foreach x in dt {
        if (i == 0) {
            rs1 = x;
        } else {
            rs2 = x;
        }
        i = i + 1;
    }
    testDB.stop();
    return (rs1.INT_TYPE, rs2.INT_TYPE);
}

function testTableAddInvalid(string jdbcUrl, string userName, string password) returns string {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type from DataTableRep", ResultPrimitiveInt);

    string s;
    try {
        ResultPrimitiveInt row = { INT_TYPE: 443 };
        var ret = dt.add(row);
        match (ret) {
            error e => s = e.message;
            () => s = "nil";
        }
    } finally {
        testDB.stop();
    }
    return s;
}

function testTableRemoveInvalid(string jdbcUrl, string userName, string password) returns string {
    endpoint jdbc:Client testDB {
        url: jdbcUrl,
        username: userName,
        password: password,
        poolOptions: { maximumPoolSize: 1 }
    };

    table dt = check testDB->select("SELECT int_type from DataTableRep", ResultPrimitiveInt);
    string s;
    try {
        ResultPrimitiveInt row = { INT_TYPE: 443 };
        var ret = dt.remove(isDelete);
        match (ret) {
            int count => s = <string>count;
            error e => s = e.message;
        }
    } finally {
        testDB.stop();
    }
    return s;
}

function isDelete(ResultPrimitiveInt p) returns (boolean) {
    return p.INT_TYPE < 2000;
}
