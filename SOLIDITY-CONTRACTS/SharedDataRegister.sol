pragma solidity ^0.4.21;

contract UserContentRegisterInterface {
    function getUserContentBytes(address whichUser, uint256 index) public constant returns (bytes32);
    function getNumContent(address whichUser) public constant returns (uint256);
    function registered(address whichUser) public constant returns (bool);
}

contract SharedDataRegister {
    
    uint256 public numPublished;
    mapping (uint256 => address) public publishedAuthorIndex;
    mapping (uint256 => uint256) public publishedPostIndex;
    mapping (uint256 => string) public publishedContentIndex;
    
    UserContentRegisterInterface public userContentRegister;
    
    function SharedDataRegister(address _userContentRegisterAddress) public {
        numPublished = 0;
        userContentRegister = UserContentRegisterInterface(_userContentRegisterAddress);
    }
    
    function publishContent(uint256 index) public returns (uint256) {
       assert(index < userContentRegister.getNumContent(msg.sender));
       publishedAuthorIndex[numPublished] = msg.sender;
       publishedPostIndex[numPublished] = index;
       publishedContentIndex[numPublished] = getContent(index);
       numPublished++;
    }
    
    function getContentBytes(uint256 contentIndex) private constant returns (bytes32) {
        address user = publishedAuthorIndex[contentIndex];
        uint256 userContentIndex = publishedPostIndex[contentIndex];
        bytes32 contentString = userContentRegister.getUserContentBytes(user, userContentIndex);
        return contentString;
    }
    
    function getContent(uint256 contentIndex) private constant returns (string) {
        return bytes32ToString(getContentBytes(contentIndex));
    }
    
    function getContentAuthor(uint256 contentIndex) public constant returns (address) {
        return publishedAuthorIndex[contentIndex];
    }
    
    function bytes32ToString(bytes32 x) private pure returns (string) {
        bytes memory bytesString = new bytes(32);
        uint charCount = 0;
        for (uint j = 0; j < 32; j++) {
            byte char = byte(bytes32(uint(x) * 2 ** (8 * j)));
            if (char != 0) {
                bytesString[charCount] = char;
                charCount++;
            }
        }
        bytes memory bytesStringTrimmed = new bytes(charCount);
        for (j = 0; j < charCount; j++) {
            bytesStringTrimmed[j] = bytesString[j];
        }
        return string(bytesStringTrimmed);
    }

}
