pragma solidity ^0.4.11;

contract TagRegister {
    
    struct UserContentTag {
        address user;
        uint256 index;
    }
    
    uint256 public numTags;
    mapping (uint256 => uint256) public userContentNumTagsIndex;
    mapping (uint256 => uint256) public publicationNumTagsIndex;
    mapping (string => uint256) checkTagIndexLocation;
    mapping (uint256 => string) public tagIndex;
    mapping (uint256 => mapping (uint256 => UserContentTag)) userContentTagIndex;
    mapping (uint256 => mapping (uint256 => uint256)) publicationTagIndex;
    
    
    mapping (string => bool) _checkTagTaken; 

    function UserContentRegistry() {}
    
    function createTag(string tagName) public {
        if (!_checkTagTaken[tagName]) {
            _checkTagTaken[tagName] = true;
            tagIndex[numTags] = tagName;
            checkTagIndexLocation[tagName] = numTags;
            numTags++;
        }
    }
    
    function tagUserContent(string tag, uint256 contentIndex) public  {
        if (!_checkTagTaken[tag]) createTag(tag);
        uint256 index = checkTagIndexLocation[tag];
        userContentTagIndex[index][userContentNumTagsIndex[index]] = UserContentTag(msg.sender, contentIndex);
        userContentNumTagsIndex[index]++;
    }
    
    function tagPublication(string tag, uint256 publicationIndex) public  {
        if (!_checkTagTaken[tag]) createTag(tag);
        uint256 index = checkTagIndexLocation[tag];
        publicationTagIndex[index][publicationNumTagsIndex[index]] = publicationIndex;
        publicationNumTagsIndex[index]++;
    }
    
    function checkTagTaken(string tagName) public returns (bool) {
        return _checkTagTaken[tagName];
    }
    
    function getTagIndex(string tagName) public returns (uint256) {
        return checkTagIndexLocation[tagName];
    }
    
    function getTagUserContent(string tagName, uint256 tagContentIndex) returns (address, uint256) {
        UserContentTag tag = userContentTagIndex[checkTagIndexLocation[tagName]][tagContentIndex];
        return (tag.user, tag.index);
    }
    
    function getTagPublication(string tagName, uint256 tagPublicationIndex) returns (uint256) {
        return publicationTagIndex[checkTagIndexLocation[tagName]][tagPublicationIndex];
    }
}
