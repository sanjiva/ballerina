struct User {
    string name;
    int age;
}

function test () (User user) {
    user.a<caret>
}
