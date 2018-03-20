import ballerina.io;

@Description {value:"This function returns a ByteChannel from a given file location according to the specified file permission (whether the file should be opened for reading/writing)."}
function getFileChannel (string filePath, string permission) (io:ByteChannel) {
    // Here is how the ByteChannel is retrieved from the file.
    io:ByteChannel channel = io:openFile(filePath, permission);
    return channel;
}

@Description {value:"This function reads the specified number of bytes from the given channel."}
function readBytes (io:ByteChannel channel, int numberOfBytes) (blob, int) {
    blob bytes;
    int numberOfBytesRead;
    io:IOError readError;
    // Here is how the bytes are read from the channel.
    bytes, numberOfBytesRead, readError = channel.read(numberOfBytes);
    if (readError != null) {
        throw readError.cause;
    }
    return bytes, numberOfBytesRead;
}

@Description {value:"This function writes a byte content with the given offset to a channel."}
function writeBytes (io:ByteChannel channel, blob content, int startOffset = 0) (int) {
    int numberOfBytesWritten;
    io:IOError writeError;
    // Here is how the bytes are written to the channel.
    numberOfBytesWritten, writeError = channel.write(content,startOffset);
    if (writeError != null) {
        throw writeError.cause;
    }
    return numberOfBytesWritten;
}

@Description {value:"This function copies content from the source channel to a destination channel."}
function copy (io:ByteChannel src, io:ByteChannel dst) {
    // Specifies the number of bytes that should be read from a single read operation.
    int bytesChunk = 10000;
    int numberOfBytesWritten = 0;
    int readCount = 0;
    int offset = 0;
    blob readContent;
    boolean done = false;
    try {
        // Here is how to specify to read all the content from
        // the source and copy it to the destination.
        while (!done) {
            readContent, readCount = readBytes(src,1000);
            if (readCount <= 0) {
                //If no content is read we end the loop
                done = true;
            }
            numberOfBytesWritten = writeBytes(dst, readContent);
        }
    } catch (error err) {
        throw err;
    }
}

function main (string[] args) {
    // Read specified number of bytes from the given channel and write.
    string srcFilePath = "./files/ballerina.jpg";
    string dstFilePath = "./files/ballerinaCopy.jpg";
    io:ByteChannel sourceChannel = getFileChannel(srcFilePath, "r");
    io:ByteChannel destinationChannel = getFileChannel(dstFilePath, "w");
    io:IOError srcCloseError;
    io:IOError dstCloseError;
    try {
        io:println("Start to copy files from " + srcFilePath + " to " + dstFilePath);
        copy(sourceChannel, destinationChannel);
        io:println("File copy completed. The copied file could be located in " + dstFilePath);
    }catch (error err) {
        io:println("error occurred while performing copy " + err.message);
    }finally {
        // Close the created connections.
        srcCloseError = sourceChannel.close();
        dstCloseError = destinationChannel.close();
    }
}
