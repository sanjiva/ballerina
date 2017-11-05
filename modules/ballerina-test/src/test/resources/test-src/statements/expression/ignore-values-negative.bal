function add (int x, int y) (string) {
    string result = "result is " + (x + y);
    return result;
}

function checkAndAdd (int x, int y) (string, error) {
    if (x < 0 || y < 0) {
        error err = {msg:"can't add negative values."};
        return "", err;
    }
    string result = "result is " + (x + y);
    return result, null;
}

function main(string[] args){
    int i = 10;
    int j = 15;
    int k = -1;
    add(i , j);
    checkAndAdd(i, k);
}
