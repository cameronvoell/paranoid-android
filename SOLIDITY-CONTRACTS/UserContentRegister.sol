pragma solidity ^0.4.11;

contract UserContentRegister {
    
    struct User {
        string userName;
        string profileMetaData;
        uint256 numContent;
        mapping (uint256 => bytes32) contentIndex;
    }
    
    mapping (address => User) public userIndex;
    mapping (address => bool) public registered;
    uint256 public numUsers;
    mapping (string => bool) _checkUserNameTaken; 

    function UserContentRegistry() {}
    
    function registerNewUser(string userName, string metaData) public {
        if (!registered[msg.sender] && !_checkUserNameTaken[userName]) {
            userIndex[msg.sender] = User(userName, metaData, 0);
            _checkUserNameTaken[userName] = true;
            registered[msg.sender] = true;
            numUsers++;
        }
    }
    
    function publishContent(bytes32 content) public  {
        if (!registered[msg.sender])throw;
        userIndex[msg.sender].contentIndex[userIndex[msg.sender].numContent] = content;
        userIndex[msg.sender].numContent++;
    }
    
    function updateMyUserName(string newUsername) public {
        if (registered[msg.sender] && !_checkUserNameTaken[newUsername]) {
            userIndex[msg.sender].userName = newUsername;
            _checkUserNameTaken[newUsername] = true;
        }
    }
    
    function checkUserNameTaken(string username) public returns (bool) {
        return _checkUserNameTaken[username];
    }
    
    function getUserContent(address whichUser, uint256 index) public returns (bytes32) {
        return userIndex[whichUser].contentIndex[index];
    }
}
