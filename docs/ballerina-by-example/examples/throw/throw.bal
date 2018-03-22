import ballerina/io;

struct Record {
    int id;
    string name;
}

@Description {value:"Here's how you can throw an error. Next example shows you how to catch thrown errors."}
function readRecord (Record|null value) {
    var result = value;
    match result{
        Record rec =>{
            io:println("Record ID: " + rec.id + ", value: " + rec.name);
        }
        (any|null) =>{
            error err = {message:"Record is null"};
            throw err;
        }
    }
}

function main (string[] args) {
    Record r1 = {id:1, name:"record1"};
    readRecord(r1);
    Record|null r2;
    // Record r2 is null.
    match r2{
        Record rec =>{
           io:println("Record: " + rec.name);
        }
        (any|null) =>{
            readRecord(r2);
        }
    }
    // Following lines will not execute.
    Record r3 = {id:3, name:"record3"};
    readRecord(r3);
}
