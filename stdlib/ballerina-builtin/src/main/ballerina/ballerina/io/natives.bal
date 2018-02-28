// Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package ballerina.io;

@Description {value:"Ballerina ByteChannel represents a channel which will allow I/O operations to be done"}
public struct ByteChannel {
}

@Description {value:"Ballerina CharacterChannel represents a channel which will allow to read/write characters"}
public struct CharacterChannel {
}

@Description {value:"Ballerina DelimitedRecordChannel represents a channel which will allow to read/write text records"}
public struct DelimitedRecordChannel {
}

@Description {value:"Represents a client socket connection. This can be used to communicate with a remote machine"}
@Field {value:"channel: Connection stream that bridge the client and the server"}
@Field {value:"port: Remote server port"}
@Field {value:"localPort: Client side port that open for the communication"}
@Field {value:"address: Remote server domain/IP"}
@Field {value:"localAddress: Client domain/IP"}
public struct Socket {
    ByteChannel channel;
    int port;
    int localPort;
    string address;
    string localAddress;
}

@Description {value:"SocketProperties structs represents the properties which are used to configure TCP connection"}
@Field {value:"localPort: Client side port that open for the communication"}
public struct SocketProperties {
    int localPort;
}

@Description {value:"Function to create a CharacterChannel from ByteChannel"}
@Param {value:"channel: The ByteChannel to be converted"}
@Param {value:"encoding: The charset/encoding of the content (i.e UTF-8, ASCII)"}
@Return {value:"CharacterChannel converted from ByteChannel"}
public native function createCharacterChannel (ByteChannel byteChannel, string encoding) (CharacterChannel);

@Description {value:"Function to create a CharacterChannel to DelimitedRecordChannel"}
@Param {value:"channel: The CharacterChannel to be converted"}
@Param {value:"recordSeparator: Terminating expression to distinguish between records"}
@Param {value:"fieldSeparator: Terminating expression to distinguish between fields"}
@Return {value:"DelimitedRecordChannel converted from CharacterChannel"}
public native function createDelimitedRecordChannel (CharacterChannel channel, string recordSeparator,
                                                     string fieldSeparator)
(DelimitedRecordChannel);

@Description {value:"Function to read text records"}
@Param {value:"channel: The DelimitedRecordChannel to read text records from"}
@Return {value:"Fields listed in the record"}
public native function <DelimitedRecordChannel channel> nextTextRecord () (string[]);

@Description {value:"Function to write text records"}
@Param {value:"channel: The DelimitedRecordChannel to write records"}
@Param {value:"records: Fields which are included in the record"}
public native function <DelimitedRecordChannel channel> writeTextRecord (string[] records);

@Description {value:"Function to close the text record channel"}
@Param {value:"channel: The DelimitedRecordChannel to be closed"}
public native function <DelimitedRecordChannel channel> closeDelimitedRecordChannel ();

@Description {value:"Function to read characters"}
@Param {value:"channel: The CharacterChannel to read characters from"}
@Param {value:"numberOfChars: Number of characters which should be read"}
@Return {value:"The character sequence which was read"}
public native function <CharacterChannel channel> readCharacters (int numberOfChars) (string);

@Description {value:"Function to read all characters in the give I/O source"}
@Return {value:"all characters read"}
public native function <CharacterChannel channel> readAllCharacters () (string);

@Description {value:"Function to write characters"}
@Param {value:"channel: The CharacterChannel to write characters to"}
@Param {value:"content: Text content which should be written"}
@Param {value:"startOffset: If the content needs to be written with an offset, the value of that offset"}
@Return {value:"Number of characters written"}
public native function <CharacterChannel channel> writeCharacters (string content, int startOffset) (int);

@Description {value:"Function to close a character channel"}
@Param {value:"channel: The CharacterChannel to be closed"}
public native function <CharacterChannel channel> closeCharacterChannel ();

@Description {value:"Function to read bytes"}
@Param {value:"channel: The ByteChannel to read bytes from"}
@Param {value:"numberOfBytes: Number of bytes which should be read"}
@Return {value:"The bytes which were read"}
@Return {value:"Number of bytes read"}
public native function <ByteChannel channel> readBytes (int numberOfBytes) (blob, int);

@Description {value:"Function to read all bytes in the given I/O source"}
@Return {value:"all bytes read from the channel"}
@Return {value:"Number of bytes read"}
public native function <ByteChannel channel> readAllBytes () (blob, int);

@Description {value:"Function to write bytes"}
@Param {value:"channel: The ByteChannel to write bytes to"}
@Param {value:"content: Bytes which should be written"}
@Param {value:"startOffset: If the bytes need to be written with an offset, the value of that offset"}
@Return {value:"Number of bytes written"}
public native function <ByteChannel channel> writeBytes (blob content, int startOffset) (int);

@Description {value:"Function to close a byte channel"}
@Param {value:"channel: The ByteChannel to be closed"}
public native function <ByteChannel channel> close ();

@Description {value:"Function to check whether next record is available or not"}
@Param {value:"channel: The DelimitedRecordChannel to read text records from"}
@Return {value:"True if the channel has more records; false otherwise"}
public native function <DelimitedRecordChannel channel> hasNextTextRecord () (boolean);

@Description {value:"Function to return a ByteChannel related to the file. This ByteChannel can then be used to read/write from/to the file."}
@Param {value:"path: The path of the file"}
@Param {value:"accessMode: Specifies whether the file should be opened for reading or writing (r/w)"}
@Return {value:"ByteChannel which will allow to perform I/O operations"}
public native function openFile (string path, string accessMode) (ByteChannel);

@Description {value:"Represents a client socket connection. This can be used to communicate with a remote server"}
@Param {value:"host: Remote server domain/IP"}
@Param {value:"port: Remote server port"}
@Param {value:"options: Connection stream that bridge the client and the server"}
@Return {value:"Socket which will allow to communicate with a remote server"}
public native function openSocket (string host, int port, SocketProperties options) (Socket);

@Description {value:"Close the socket connection with the remote server"}
@Param {value:"socket: The client socket connection to be to be closed"}
public native function <Socket socket> closeSocket ();
