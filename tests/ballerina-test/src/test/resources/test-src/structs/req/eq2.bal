package req;

public struct userPFoo {
    int age;
    string name;
    string address;
    string zipcode = "23468";
}

public function <userPFoo u> getName () returns (string) {
    return u.name + ":userPFoo";
}

public function <userPFoo u> getAge () returns (int) {
    return u.age;
}