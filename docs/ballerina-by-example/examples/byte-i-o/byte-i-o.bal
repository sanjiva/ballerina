import ballerina.io;

@Description{value:"This function returns a ByteChannel from a given file location according to the specified file permission (whether the file should be opened for reading/writing)."}
function getFileChannel (string filePath, string permission) (io:ByteChannel) {
    // Here is how the ByteChannel is retrieved from the file.
    io:ByteChannel channel = io:openFile(filePath, permission);
    return channel;
}

@Description{value:"This function reads the specified number of bytes from the given channel."}
function readBytes (io:ByteChannel channel, int numberOfBytes) (blob, int) {
    blob bytes;
    int numberOfBytesRead;
    // Here is how the bytes are read from the channel.
    bytes, numberOfBytesRead = channel.readBytes(numberOfBytes);
    return bytes, numberOfBytesRead;
}

@Description{value:"This function writes a byte content with the given offset to a channel."}
function writeBytes (io:ByteChannel channel, blob content, int startOffset) (int) {
    // Here is how the bytes are written to the channel.
    int numberOfBytesWritten = channel.writeBytes(content, startOffset);
    return numberOfBytesWritten;
}

@Description{value:"This function copies content from the source channel to a destination channel."}
function copy (io:ByteChannel src, io:ByteChannel dst) {
    // Specifies the number of bytes that should be read from a single read operation.
    int bytesChunk = 10000;
    blob readContent;
    int readCount = -1;
    int numberOfBytesWritten = 0;
    // Here is how to specify to read all the content from
    // the source and copy it to the destination.
    while (readCount != 0) {
        readContent, readCount = readBytes(src, bytesChunk);
        numberOfBytesWritten = writeBytes(dst, readContent, 0);
    }
}

@Description{value:"This function copies all content from a source channel to a destination channel."}
function copyAll (io:ByteChannel src, io:ByteChannel dst) {
    blob readContent;
    int numberOfBytesRead;
    // This is how you can read all content from the source.
    readContent,numberOfBytesRead = src.readAllBytes();
    int numberOfBytesWritten = dst.writeBytes(readContent, 0);
}

function main (string[] args) {
    // Read specified number of bytes from the given channel and write.
    string srcFilePath = "./files/ballerina.jpg";
    string dstFilePath = "./files/ballerinaCopy1.jpg";
    io:ByteChannel sourceChannel = getFileChannel(srcFilePath, "r");
    io:ByteChannel destinationChannel = getFileChannel(dstFilePath, "w");
    io:println("Start to copy files from " + srcFilePath + " to " + dstFilePath);
    copy(sourceChannel, destinationChannel);
    io:println("File copy completed. The copied file could be located in " + dstFilePath);
    // Close the created connections.
    sourceChannel.close();
    destinationChannel.close();
    //Read all bytes from the given channel and write.
    dstFilePath = "./files/ballerinaCopy2.jpg";
    sourceChannel = getFileChannel(srcFilePath, "r");
    destinationChannel = getFileChannel(dstFilePath, "w");
    io:println("Start to copy files from " + srcFilePath + " to " + dstFilePath);
    copyAll(sourceChannel, destinationChannel);
    io:println("File copy completed. The copied file could be located in " + dstFilePath);
    // Close the created connections.
    sourceChannel.close();
    destinationChannel.close();
}
