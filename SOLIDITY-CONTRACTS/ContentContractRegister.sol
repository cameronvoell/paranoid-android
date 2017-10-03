pragma solidity ^0.4.11;

contract ContentContractRegister {
    
    struct ContentContract {
        string name;
        string description;
        mapping (uint256 => string) contentList;
        uint256 contentListSize;
        address admin;
    }
    
    struct RegisterEntry {
        bool isRemote;
        address remoteAddress;
        bool visible;
    }
    
    mapping (string => uint256) nameOrder;
    mapping (uint256 => ContentContract) public localContentContracts;
    mapping (uint256 => RegisterEntry) public register;
    uint256 public numRegistered;
    address public registerAdmin;
    
    function ContentContractRegister() {
        numRegistered = 0;
        registerAdmin = msg.sender;
    }
    
    function getContentIndex(string name) public returns (uint256) {
        return nameOrder[name] - 1;
    }
    
    function registerRemoteContentContract(address contractAddress) public{
        numRegistered++;
        register[numRegistered - 1] = RegisterEntry(true, contractAddress, true);
    }
    
    function registerLocalContentContract(string name, string description) public {
        if (nameOrder[name] != 0) throw;
        
        numRegistered++;
        nameOrder[name] = numRegistered;
        localContentContracts[numRegistered -1] = ContentContract(name, description, 0, msg.sender);
        register[numRegistered - 1] = RegisterEntry(false, msg.sender, true);
    }
    
    function registerLocalContent(string name, string contentHash) public {
        if (contentContract.admin != msg.sender) throw;
        uint256 localIndex = nameOrder[name] - 1;
        ContentContract contentContract = localContentContracts[localIndex];
        contentContract.contentListSize++;
        contentContract.contentList[contentContract.contentListSize - 1] = contentHash;
    }
    
    function updateContentContractDescription(string name, string newDescription) public {
        if (msg.sender != localContentContracts[nameOrder[name] - 1].admin) throw;
        localContentContracts[nameOrder[name] - 1].description = newDescription;
    }
    
    function getLocalContentListSize(string name) public returns (uint256) {
         return localContentContracts[nameOrder[name] - 1].contentListSize;
    }
    
    function getLocalContent(string name, uint256 index) public returns (string) {
        return localContentContracts[nameOrder[name] - 1].contentList[index];
    }
    
    function updateVisibility(uint256 registerIndex, bool visible) public {
        if (msg.sender != registerAdmin) throw;
        register[registerIndex].visible = visible;
    }
}
