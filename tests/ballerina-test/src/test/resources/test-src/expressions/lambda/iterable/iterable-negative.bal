int count;
string word;

function test1(){
    int x = 0;
    x.foreach(function (int i) { count = count + i;});
    string y = "foo";
    y.map(function (string s) returns (int) { return lengthof s;});
}

function test2(){
    string[] y = ["1", "a"];

    y.count();

    y.filter(function (int i, string x) returns (boolean){
        return true;})
     .foreach(function (string x){ word = x;}).count();
}

function test3(){
    map z = { a : "1", b : "2"};
    string[] keys = z.map(function (any x) returns (string, string){
          var s=? <string> x;
          return (s, "value");
    }).keys();
}

function test4() {
    map z = { a : "1", b : "2"};
    string[] a = z.map(function (any x) returns (string, string){
          var s =? <string> x;
          return (s, "value");
    });
    map m = z.filter(function (any x) returns (boolean){
          var s =? <string> x;
          return s == null;
    });
    any x = z.filter(function (any x) returns (boolean ){
         var s =? <string> x;
         return s == null;
     });
}

function test5(){
    string[] s = ["1", "a"];
    int x;
    x = s.foreach(function (string s){word = word + s;});
    string y;
    var r = s.map(function (int i, string v) returns (int, string){ return (i*2, v + v);});
}

function test6(){
    string[] s = ["1", "a"];
    s.count(test5);
    s.filter();
    int i = 10;
    s.foreach(i);
}

function test7(){
    string[] s = ["foo", "bar"];
    s.foreach(function (string x, string y, string z){});
    s.foreach(function (){});
    s.filter(function(string s) returns (boolean, int){return (true, 1);});
    s.filter(function(string s){});
    s.filter(function(person p){});
    _ = s.filter(function(string s) returns (person){return null;});
}

