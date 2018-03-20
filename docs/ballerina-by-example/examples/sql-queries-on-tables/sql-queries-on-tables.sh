$ ballerina run sql-queries-on-tables.bal
The personTable: {data: [{id:1, age:25, salary:1000.5, name:"jane", married:true}, {id:2, age:26, salary:1050.5,
name:"kane", married:false}, {id:3, age:27, salary:1200.5, name:"jack", married:true}, {id:4, age:28, salary:1100.5,
name:"alex", married:false}]}

The orderTable: {data: [{personId:1, orderId:1234, items:"pen, book, eraser", amount:34.75}, {personId:1, orderId:2314,
items:"dhal, rice, carrot", amount:14.75}, {personId:2, orderId:5643, items:"Macbook Pro", amount:2334.75}, {personId:3,
 orderId:8765, items:"Tshirt", amount:20.75}]}

table<Person> personTableCopy = from personTable select *;
personTableCopy: {data: [{id:1, age:25, salary:1000.5, name:"jane", married:true}, {id:2, age:26, salary:1050.5,
name:"kane", married:false}, {id:3, age:27, salary:1200.5, name:"jack", married:true}, {id:4, age:28, salary:1100.5,
name:"alex", married:false}]}

table<Person> orderedPersonTableCopy = from personTable select * order by salary;
orderedPersonTableCopy: {data: [{id:1, age:25, salary:1000.5, name:"jane", married:true}, {id:2, age:26, salary:1050.5,
 name:"kane", married:false}, {id:4, age:28, salary:1100.5, name:"alex", married:false}, {id:3, age:27, salary:1200.5,
 name:"jack", married:true}]}

table<Person> personTableCopyWithFilter = from personTable where name == 'jane' select *;
personTableCopyWithFilter: {data: [{id:1, age:25, salary:1000.5, name:"jane", married:true}]}

table<PersonPublicProfile > childTable = from personTable select name as knownName, age;
childTable: {data: [{knownName:"jane", age:25}, {knownName:"kane", age:26}, {knownName:"jack", age:27},
{knownName:"alex", age:28}]}

table<SummedOrder> summedOrderTable = from orderTable select personId, sum(amount) group by personId;
summedOrderTable: {data: [{personId:1, amount:49.5}, {personId:2, amount:2334.75}, {personId:3, amount:20.75}]}

table<OrderDetails> orderDetailsTable = from personTable as tempPersonTable
            join orderTable as tempOrderTable on tempPersonTable.id == tempOrderTable.personId
            select tempOrderTable.orderId as orderId, tempPersonTable.name as personName, tempOrderTable.items as
            items, tempOrderTable.amount as amount;
orderDetailsTable: {data: [{orderId:1234, personName:"jane", items:"pen, book, eraser", amount:34.75}, {orderId:2314,
personName:"jane", items:"dhal, rice, carrot", amount:14.75}, {orderId:5643, personName:"kane", items:"Macbook Pro",
amount:2334.75}, {orderId:8765, personName:"jack", items:"Tshirt", amount:20.75}]}

table<OrderDetails> orderDetailsWithFilter = from personTable where name != 'jane' as tempPersonTable
        join orderTable where personId != 3 as tempOrderTable on tempPersonTable.id == tempOrderTable.personId
        select tempOrderTable.orderId as orderId, tempPersonTable.name as personName, tempOrderTable.items as items,
        tempOrderTable.amount as amount;
orderDetailsWithFilter: {data: [{orderId:5643, personName:"kane", items:"Macbook Pro", amount:2334.75}]}