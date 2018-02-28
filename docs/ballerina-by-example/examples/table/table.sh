$ ballerina run table-with-sql-connector.bal
Table Data:{data: [{id:1, name:"Jane", salary:300.5}, {id:2, name:"Anne", salary:100.5}, {id:3, name:"John", salary:400.5}, {id:4, name:"Peter", salary:150.0}]}
Name: Jane
Name: Anne
Name: John
Name: Peter
Average of Low salary:125.25
Deleted row count:2
After Delete:{data: [{id:1, name:"Jane", salary:300.5}, {id:3, name:"John", salary:400.5}]}
JSON:[{"id":1,"name":"Jane","salary":300.5},{"id":3,"name":"John","salary":400.5}]
XML:<results><result><id>1</id><name>Jane</name><salary>300.5</salary></result><result><id>3</id><name>John</name><salary>400.5</salary></result></results>
