import ballerina.file;
import ballerina.io;

io:CharacterChannel characterChannel;

function initFileChannel(string filePath,string permission,string encoding){
    file:File src = {path:filePath};
    io:ByteChannel channel = src.openChannel(permission);
    characterChannel = channel.toCharacterChannel(encoding);
}

function readCharacters (int numberOfCharacters) (string) {
    string characters = characterChannel.readCharacters(numberOfCharacters);
    return characters;
}

function writeCharacters (string content, int startOffset) (int) {
    int numberOfCharactersWritten = characterChannel.writeCharacters(content, startOffset);
    return numberOfCharactersWritten;
}

function close(){
    characterChannel.closeCharacterChannel();
}
