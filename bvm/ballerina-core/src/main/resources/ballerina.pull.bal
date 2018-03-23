import ballerina/io;
import ballerina/net.http;
import ballerina/runtime;
import ballerina/file;
import ballerina/mime;

function pullPackage (string url, string destDirPath, string fullPkgPath, string fileSeparator) {
    endpoint http:ClientEndpoint httpEndpoint {
        targets: [
        {
            uri: url,
            secureSocket: {
                trustStore: {
                    filePath: "${ballerina.home}/bre/security/ballerinaTruststore.p12",
                    password: "ballerina"
                },
                hostNameVerification:false,
                sessionCreation: true
             }
        }
        ]
    };
    http:Request req = {};
    http:Response res = {};
    req.addHeader("Accept-Encoding", "identity");
    var httpResponse = httpEndpoint -> get("", req);
    match httpResponse {
     http:HttpConnectorError errRes => {
         var errorResp = <error> errRes;
         match errorResp {
             error err =>  throw err;
         }
     }
     http:Response response => res = response;
    }

    if (res.statusCode != 200) {
        var jsonResponse = res.getJsonPayload();
        match jsonResponse {
            mime:EntityError errRes => {
                var errorResp = <error> errRes;
                match errorResp {
                    error err =>  throw err;
                }
            }
            json jsonObj => io:println(jsonObj.msg.toString());
        }
    } else {
        string contentLengthHeader;
        if (res.hasHeader("content-length")) {
            contentLengthHeader = res.getHeader("content-length");
        } else {
            error err = {message:"Unable to find the Content-Length header"};
            throw err;
        }
        int pkgSize;
        var conversion = <int> contentLengthHeader;
        match conversion{
            error conversionErr => throw conversionErr;
            int size => pkgSize = size;
        }
        io:ByteChannel sourceChannel = {};
        var srcChannel = res.getByteChannel();
        match srcChannel {
            mime:EntityError errRes => {
                var errorResp = <error> errRes;
                match errorResp {
                    error err =>  throw err;
                }
            }  
            io:ByteChannel channel => sourceChannel = channel;
        }

        // Get the package version from the canonical header of the response
        string linkHeaderVal;
        if (res.hasHeader("Link")) {
            linkHeaderVal = res.getHeader("Link");
        } else {
            error err = {message:"Unable to find the Canonical Link header"};
            throw err;
        }

        string canonicalLinkURL = linkHeaderVal.subString(linkHeaderVal.indexOf("<") + 1, linkHeaderVal.indexOf(">"));
        string pkgVersion = canonicalLinkURL.subString(canonicalLinkURL.lastIndexOf("/") + 1, canonicalLinkURL.length());

        string pkgName = fullPkgPath.subString(fullPkgPath.lastIndexOf("/") + 1, fullPkgPath.length());
        fullPkgPath = fullPkgPath + ":" + pkgVersion;

        // Create the version directory
        destDirPath = destDirPath + fileSeparator + pkgVersion;
        createDirectories(destDirPath);

        string archiveFileName = pkgName + ".zip";
        string destArchivePath = destDirPath  + fileSeparator + archiveFileName;

        io:ByteChannel destDirChannel = getFileChannel(destArchivePath, "w");
        string toAndFrom = " [central.ballerina.io -> home repo]";

        io:IOError destDirChannelCloseError = {};
        io:IOError srcCloseError = {};

        copy(pkgSize, sourceChannel, destDirChannel, fullPkgPath, toAndFrom);
        if (destDirChannel != null) {
            destDirChannelCloseError = destDirChannel.close();
        }
        srcCloseError = sourceChannel.close();
    }
}

public function main(string[] args){
    pullPackage(args[0], args[1], args[2], args[3]);
}


function getFileChannel (string filePath, string permission) returns (io:ByteChannel) {
    io:ByteChannel channel = io:openFile(untaint filePath, permission);
    return channel;
}

function readBytes (io:ByteChannel channel, int numberOfBytes) returns (blob, int) {
    blob bytes;
    int numberOfBytesRead;

    var bytesRead = channel.read(numberOfBytes);
    match bytesRead {
        (blob, int) byteResponse => {
            (bytes, numberOfBytesRead) = byteResponse;         
        }
        io:IOError errRes => {
                var errorResp = <error> errRes;
                match errorResp {
                    error err =>  throw err;
                }
        }  
    }
    return (bytes, numberOfBytesRead);
}

function writeBytes (io:ByteChannel channel, blob content, int startOffset) returns (int) {
    int numberOfBytesWritten;
    var bytesWritten = channel.write(content, startOffset);
    match bytesWritten {
        io:IOError errRes => {
                var errorResp = <error> errRes;
                match errorResp {
                    error err =>  throw err;
                }
        }  
        int noOfBytes => numberOfBytesWritten = noOfBytes;
    }
    return numberOfBytesWritten;
}

function copy (int pkgSize, io:ByteChannel src, io:ByteChannel dest, string fullPkgPath, string toAndFrom) {
    string truncatedFullPkgPath = truncateString(fullPkgPath);
    string msg = truncatedFullPkgPath + toAndFrom;
    int bytesChunk = 8;
    blob readContent;
    int readCount = -1;
    float totalCount = 0.0;
    int numberOfBytesWritten = 0;
    string noOfBytesRead;
    string equals = "==========";
    string tabspaces = "          ";
    boolean done = false;
    try {
        while (!done) {
            (readContent, readCount) = readBytes(src, bytesChunk);
            if (readCount <= 0) {
                done = true;
            }
            if (dest != null) {
                numberOfBytesWritten = writeBytes(dest, readContent, 0);
            }
            totalCount = totalCount + readCount;
            float percentage = totalCount / pkgSize;
            runtime:sleepCurrentWorker(100);
            noOfBytesRead = totalCount + "/" + pkgSize;
            string bar = equals.subString(0, <int>(percentage * 10));
            string spaces = tabspaces.subString(0, 10 - <int>(percentage * 10));
            io:print("\r" + rightPad(msg, 100) + "[" + bar + ">" + spaces + "] " + <int>totalCount + "/" + pkgSize);
        }
    } catch (error err) {
        throw err;
    }
    io:print("\r" + rightPad(fullPkgPath + toAndFrom, (115 + noOfBytesRead.length())) + "\n");
}

function rightPad (string msg, int length) returns (string) {
    int i = -1;
    length = length - msg.length();
    string char = " ";
    while (i < length) {
        msg = msg + char;
        i = i + 1;
    }
    return msg;
}

function truncateString (string text) returns (string) {
    int indexOfVersion = text.lastIndexOf(":");
    string withoutVersion = text.subString(0, indexOfVersion);
    string versionOfPkg = text.subString(indexOfVersion, text.length());
    int minLength = 57;
    int lengthWithoutVersion = withoutVersion.length();
    if (lengthWithoutVersion > minLength) {
        int noOfCharactersToBeRemoved = lengthWithoutVersion - minLength;
        int half = noOfCharactersToBeRemoved / 2;
        int middleOfWithoutVersion = lengthWithoutVersion / 2;
        int leftFromMiddle = middleOfWithoutVersion - half;
        int rightFromMiddle = middleOfWithoutVersion + half;

        string truncatedLeftStr = withoutVersion.subString(0, leftFromMiddle);
        string truncatedRightStr = withoutVersion.subString(rightFromMiddle, lengthWithoutVersion);

        string truncatedStr = truncatedLeftStr + "…" + truncatedRightStr;
        return truncatedStr + versionOfPkg;
    }
    return text;
}

function createDirectories (string directoryPath) {
    boolean folderCreated;
    file:File folder = {path:directoryPath};
    if (!folder.exists()) {
        var makeDirs = folder.mkdirs();
        match makeDirs {
            boolean created => folderCreated = created;
            error err => throw err;
        }
    }
}
