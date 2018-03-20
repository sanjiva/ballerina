import ballerina.data.sql;
import ballerina.io;

function main (string[] args) {

    endpoint<sql:Client> testDBEP {
        database: sql:DB.MYSQL,
        host: "localhost",
        port: 3306,
        name: "testdb",
        username: "root",
        password: "root",
        options: {maximumPoolSize:5}
    }

    var testDB = testDBEP.getConnector();

    //Create the tables required for the transaction.
    int updatedRows = testDB -> update("CREATE TABLE IF NOT EXISTS CUSTOMER (ID INT,
        NAME VARCHAR(30))", null);
    updatedRows = testDB -> update("CREATE TABLE IF NOT EXISTS SALARY (ID INT,
        MON_SALARY FLOAT)", null);
    //Here is the transaction block. You can use a Try catch here since update action can
    //throw errors due to SQL errors, connection pool errors etc. The retry count is the
    //number of times the transaction is tried before aborting. By default a transaction
    //is tried three times before aborting. Only integer literals or constants are
    //allowed for retry count.
    boolean transactionSuccess = false;
    transaction with retries(4){
        //This is the first action participate in the transaction.
        int c = testDB -> update("INSERT INTO CUSTOMER(ID,NAME) VALUES (1, 'Anne')", null);
        //This is the second action participate in the transaction.
        c = testDB -> update("INSERT INTO SALARY (ID, MON_SALARY) VALUES (1, 2500)", null);

        io:println("Inserted count:" + c);
        //Anytime the transaction can be forcefully aborted using the abort keyword.
        if (c == 0) {
            abort;
        }
        transactionSuccess = true;
        //The end curly bracket marks the end of the transaction and the transaction will
        //be committed or rolled back at this point.
    } failed {
        //The failed block will be executed if the transaction is failed due to an
        //exception or a throw statement. This block will execute each time
        //transaction is failed until retry count is reached.
        io:println("Transaction failed");
        transactionSuccess = false;
    }
    if (transactionSuccess) {
        io:println("Transaction committed");
    }
    //Close the connection pool.
    testDB -> close();
}
