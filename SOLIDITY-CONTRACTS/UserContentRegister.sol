pragma solidity ^0.4.11;

contract UserContentRegister {
    
    struct User {
        string userName;
        string profileMetaData;
        uint256 numContent;
        mapping (uint256 => string) contentIndex;
        mapping (uint256 => string) contentLinks; //update Publication or tag info?
    }
    
    mapping (address => User) public userIndex;
    mapping (address => bool) public registered;
    mapping (string => address) _userAddressLookup;
    uint256 public numUsers;
    mapping (string => bool) _checkUserNameTaken; 

    function UserContentRegister() public  {}
    
    function registerNewUser(string userName, string metaData) public returns (bool) {
        if (!registered[msg.sender] && !_checkUserNameTaken[userName]) {
            userIndex[msg.sender] = User(userName, metaData, 0);
            _checkUserNameTaken[userName] = true;
            registered[msg.sender] = true;
            _userAddressLookup[userName] = msg.sender;
            numUsers++;
            return true;
        }
        return false;
    }
    
    function getUserAddress(string userName) public constant returns (address) {
        return _userAddressLookup[userName];
    }
    
    function publishContent(string content) public  {
        assert(registered[msg.sender]);
        userIndex[msg.sender].contentIndex[userIndex[msg.sender].numContent] = content;
        userIndex[msg.sender].numContent++;
    }
    
    function updateContentLinks(uint256 contentIndex, string links) public  {
        assert(!registered[msg.sender]);
        assert(contentIndex < userIndex[msg.sender].numContent);
        userIndex[msg.sender].contentLinks[contentIndex] = links;
    }
    
    function updateMyUserName(string newUsername) public {
        if (registered[msg.sender] && !_checkUserNameTaken[newUsername]) {
            userIndex[msg.sender].userName = newUsername;
            _checkUserNameTaken[newUsername] = true;
        }
    }
    
    function updateMetaData(string _metaData) public {
        if (registered[msg.sender]) {
            userIndex[msg.sender].profileMetaData = _metaData;
        }
    }
    
    function checkUserNameTaken(string username) public constant returns (bool) {
        return _checkUserNameTaken[username];
    }
    
    function getUserContent(address whichUser, uint256 index) public constant returns (string content) {
        return userIndex[whichUser].contentIndex[index];
    }

    function getUserContentBytes(address whichUser, uint256 index) public constant returns (bytes32, bytes32) {
        bytes memory totalMemory = bytes(userIndex[whichUser].contentIndex[index]);
        return (bytesToBytes32(totalMemory, 0), bytesToBytes32(totalMemory, 32));
    }
    
    function bytesToBytes32(bytes b, uint offset) private constant returns (bytes32) {
        bytes32 out;
        for (uint i = 0; i < 32 && offset + i < b.length; i++) {
            out |= bytes32(b[offset + i] & 0xFF) >> (i * 8);
        }
         return out;
    }

    function getContentLinks(address whichUser, uint256 index) public constant returns (string) {
        return userIndex[whichUser].contentLinks[index];
    }
}
