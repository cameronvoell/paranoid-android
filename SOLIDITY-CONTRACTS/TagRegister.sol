pragma solidity ^0.4.11;

contract PublicationRegisterInterface {
    function numPublications() public returns (uint256);
    function getNumPublished(uint256 whichPublication) public constant returns (uint256);
    function getAdmin(uint256 whichPublication) public constant returns (address);
    function getContentAuthor(uint256 whichPublication, uint256 contentIndex) public returns (address);
    function getContentBytes(uint256 whichPublication, uint256 contentIndex) public returns (bytes32);
}

contract TagRegister {
    
    struct ContentTag {
        uint256 publicationIndex;
        uint256 index;
    }
    
    PublicationRegisterInterface public publicationRegister;
    uint256 public numTags;
    mapping (uint256 => uint256) public numContentTagsIndex;
    mapping (uint256 => uint256) public numPublicationTagsIndex;
    mapping (string => uint256) checkTagIndexLocation;
    mapping (uint256 => string) public tagIndex;
    mapping (uint256 => mapping (uint256 => ContentTag)) public contentTagIndex;
    mapping (uint256 => mapping (uint256 => uint256)) public publicationTagIndex;
    mapping (string => bool) _checkTagTaken; 
    mapping (uint256 => mapping (bytes32 => bool)) public contentAlreadyTagged;
    mapping (uint256 => mapping (uint256 => bool)) public publicationAlreadyTagged;

    function TagRegister(address publicationRegisterAddress) public {
        publicationRegister = PublicationRegisterInterface(publicationRegisterAddress);
    }
    
    function createTag(string tagName) public {
        if (!_checkTagTaken[tagName]) {
            _checkTagTaken[tagName] = true;
            tagIndex[numTags] = tagName;
            checkTagIndexLocation[tagName] = numTags;
            numTags++;
        }
    }
    
    function tagContent(string tag, uint256 whichPublication, uint256 whichContent) public  {
        assert(whichPublication < publicationRegister.numPublications() && //publication num exists
               whichContent < publicationRegister.getNumPublished(whichPublication) && //article num exists
               msg.sender == publicationRegister.getContentAuthor(whichPublication, whichContent) && //msg sender is author
               !contentAlreadyTagged[checkTagIndexLocation[tag]][publicationRegister.getContentBytes(whichPublication, whichContent)]);
       
        if (!_checkTagTaken[tag]) createTag(tag);
        uint256 whichTag = checkTagIndexLocation[tag];
        contentTagIndex[whichTag][numContentTagsIndex[whichTag]] = ContentTag(whichPublication, whichContent);
        numContentTagsIndex[whichTag]++;
        contentAlreadyTagged[whichTag][publicationRegister.getContentBytes(whichPublication, whichContent)] = true;
    }
    
    function tagPublication(string tag, uint256 whichPublication) public  {
        assert(whichPublication < publicationRegister.numPublications() &&
               msg.sender == publicationRegister.getAdmin(whichPublication) &&
               !publicationAlreadyTagged[checkTagIndexLocation[tag]][whichPublication]);
        
        if (!_checkTagTaken[tag]) createTag(tag);
        uint256 index = checkTagIndexLocation[tag];
        publicationTagIndex[index][numPublicationTagsIndex[index]] = whichPublication;
        numPublicationTagsIndex[index]++;
        publicationAlreadyTagged[index][whichPublication] = true;
    }
    
    function checkTagTaken(string tagName) public returns (bool) {
        return _checkTagTaken[tagName];
    }
    
    function getTagIndex(string tagName) public returns (uint256) {
        return checkTagIndexLocation[tagName];
    }
    
    function getTagContent(string tagName, uint256 tagContentIndex) public returns (uint256, uint256) {
        ContentTag storage tag = contentTagIndex[checkTagIndexLocation[tagName]][tagContentIndex];
        return (tag.publicationIndex, tag.index);
    }
    
    function getTagPublication(string tagName, uint256 tagPublicationIndex) public returns (uint256) {
        return publicationTagIndex[checkTagIndexLocation[tagName]][tagPublicationIndex];
    }
}
