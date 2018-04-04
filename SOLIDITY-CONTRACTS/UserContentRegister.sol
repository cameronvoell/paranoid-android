pragma solidity ^0.4.21;

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
        assert(registered[msg.sender]);
        assert(contentIndex < userIndex[msg.sender].numContent);
        userIndex[msg.sender].contentLinks[contentIndex] = links;
    }
    
    function updateMyUserName(string newUsername) public {
        assert(registered[msg.sender] && !_checkUserNameTaken[newUsername]);
        userIndex[msg.sender].userName = newUsername;
        _checkUserNameTaken[newUsername] = true;
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
    
    function getUserContentBytes(address whichUser, uint256 index) public constant returns (bytes32) {
        return stringToBytes32(userIndex[whichUser].contentIndex[index]);
    }
    
    function stringToBytes32(string memory source) public pure returns (bytes32 result) {
        assembly {
            result := mload(add(source, 32))
        }
    }
    
    function getContentLinks(address whichUser, uint256 index) public constant returns (string) {
        return userIndex[whichUser].contentLinks[index];
    }
    
    function getNumContent(address whichUser) public constant returns (uint256) {
        return userIndex[whichUser].numContent;
    }
}
