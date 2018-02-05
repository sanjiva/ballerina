function valueAssignmentAndRetrieval() (int) {
    int[] x = [3];
    int[] y = [4, 5];

    int[][] xx = [x, y];
    return xx[0][0];
}

function arrayInitializationAndRetrieval() (int) {
    int[][] x = [];
    x[0] = [];
    x[0][0] = 1;

    return x[0][0];
}

function arrayToArrayAssignment() (int) {
    int[] x;
    x = [9];

    int[][] xx = [];
    xx[0] = x;

    return xx[0][0];
}

function threeDarray() (int) {
    int[] x = [1, 2];
    int[] y = [3, 4];

    int[][] xx = [x, y];

    int[][][] xxx = [xx];

    return xxx[0][0][1];
}

function threeDarrayValueAccess() (int) {
    int[][][] xxx;
    xxx = [];

    xxx[0] = [];
    xxx[0][0] = [];
    xxx[0][0][0] = 99;

    return xxx[0][0][0];
}

function threeDarrayStringValueAccess() (string) {
    string[][][] xxx;
    xxx = [];

    xxx[0] = [];
    xxx[0][0] = [];
    xxx[0][0][0] = "string";

    return xxx[0][0][0];
}

function twoDarrayFunctionCalltest() (int) {
    int[][] xx = [];
    xx[0] = [];
    xx[0][1] = 4;

    return arrayTest(xx);
}

function arrayTest(int[][] yy) (int) {
    return yy[0][1];
}

struct B {
    int x;
}

function twoDarrayStructTest() (int) {
    B b1 = {x:1};
    B b2 = {x:2};

    B[] x1 = [b1, b2];
    B[] x2 = [b2, b1];
    B[][] xx = [x1, x2];

    B b3 = xx[0][1];

    return b3.x;
}

function nestedArrayInit() (int, int) {
    int[][][] a = [[[100, 200, 3], [2, 5, 6]], [[100, 200, 3], [2, 5, 6], [12, 15, 16]]];
    return a[1][2][0], a[0][1][2];
}
