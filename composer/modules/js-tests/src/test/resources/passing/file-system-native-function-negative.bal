import ballerina.net.fs;

function testFileMoveInvalidPath(fs:FileSystemEvent event, string path) {
    event.move(path);
}