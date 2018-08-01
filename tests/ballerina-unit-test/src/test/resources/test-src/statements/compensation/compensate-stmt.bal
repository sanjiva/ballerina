function testNestedScopes() returns (int) {
    int a = 2;

    scope scopeA {
        scope scopeB {
            string s = "abc";
            a = 5;
        } compensation {
            //compensate
        }
    } compensation {

    }
    a = 3;
    compensate scopeA;
    return a;
}

function testLoopScopes() returns (int) {
    int a = 2;

    int k = 1;
    while (k < 5) {
        scope scopeA {
            scope scopeB {
                a = 5;
            } compensation {
                string abc = "abc";
            }
        } compensation {

        }
        k = k +2;

        compensate scopeA;
    }
    a = 3;

    return a;
}

