# Testerina

Testerina is the test framework built for the Ballerina language. This will be a part of ```ballerina-tools-<release-version>.zip``` distribution [1].

Testerina provides ```ballerina test``` command.  

Once you run tests using ```ballerina test``` command, Testerina will print a summary of test results on the console.  

To test a file written in Ballerina language, use the test command as follows.  
```./ballerina test <package_name>```

#### Testerina provides following functions.

package ballerina.test;

 - startService(string servicename)
 - assertTrue(boolean condition, string message)
 - assertFalse(boolean condition, string message) 
 - assertStringEquals(string actual, string expected, string errorMessage)
 - assertIntEquals(int actual, int expected, string errorMessage)
 - assertFloatEquals(float actual, float expected, string errorMessage)
 - assertBooleanEquals(boolean actual, boolean expected, string errorMessage)
 - assertStringArrayEquals(string[] actual, string[] expected, string errorMessage)
 - assertFloatArrayEquals(float[] actual, float[] expected, string errorMessage)
 - assertIntArrayEquals(int[] actual, int[] expected, string errorMessage)
 - assertFail(string errorMessage)
 - createBallerinaError (string errorMessage, string category) (AssertError)
 
### Writing ballerina tests

- Test files should contain ```_test.bal``` suffix.  
- Test functions should contain ```test``` prefix.  
e.g.: ```testAddTwoNumbers()```
- Each test function may contain one or more asserts.  
e.g. 1: 
```ballerina
import ballerina.test;
function testAddTwoNumbers() {
    test:assertIntEquals(addTwoNumbers(1, 2), 3, "Number addition failed for positive numbers");
    test:assertIntEquals(addTwoNumbers(-1, -2), -3, "Number addition failed for negative numbers");
    test:assertIntEquals(addTwoNumbers(0, 0), 0, "Number addition failed for number zero");
}
```

If at least one assert fails, whole test function will be marked as failed.
Detailed information is shown in the test result summary.  
> One package may contain more than one ```*._test.bal``` file.

#### Tutorial

1 Download [1] ```ballerina-tools-<release-version>.zip``` distribution and unzip.  
2 Unzip and go to ```ballerina-tools-<release-version>```.  
3 Create a directory ```samples/foo/bar```.  
4 Create the following two files inside this directory.  

e.g.: sample.bal
```ballerina
package samples.foo.bar;

function main (string[] args) {
    int i = intAdd(1, 2);
    println("Result: " + i);
}
 
function intAdd(int a, int b) (int) {
    return a + b;
}

```  
e.g.: sample_test.bal
```ballerina
package samples.foo.bar;
 
import ballerina.test;
 
function testInt() {	
    int answer = 0;
    answer = intAdd(1, 2);
    test:assertIntEquals(answer, 3, "IntAdd function failed");
	
}
```
Note the package hierarchy in above files.   
 
5 Run tests using following command.  
```> ./bin/ballerina test samples/foo/bar/```  

Following is a sample console output. 

```
result: 
tests run: 1, passed: 1, failed: 0
```
### Reference:  
[1] https://github.com/ballerinalang/distribution