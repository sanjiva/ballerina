const int /*def*/a;

service<http> test {

    resource test (message m) {
        int value = /*ref*/a;
    }
}
