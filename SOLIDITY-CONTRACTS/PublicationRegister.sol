pragma solidity ^0.4.11;

contract UserContentRegisterInterface {
    function getUserContent(address whichUser, uint256 index) public returns (bytes32);
}

contract PublicationRegister {
    
    struct Publication {
        string name;
        string metaData; //json, image urls, description, etc
        address admin;
        bool open;
        
        mapping (address => bool) publishingAccessList;
        
        uint256 numPublished;
        mapping (uint256 => address) publishedAuthorIndex;
        mapping (uint256 => uint256) publishedPostIndex;
        
        uint256 minSupportCostWei;
        uint adminPaymentPercentage;
        uint32 uniqueSupporters;
        uint256 adminClaimOwedWei;
        
        mapping (address => uint256) authorClaimsOwedWei;
        mapping (uint256 => uint256) postRevenueWeiIndex;
        mapping (uint256 => uint32) postUniqueSupportersIndex;
        mapping (uint256 => mapping (address => bool)) trackPostSupporters;
        mapping (uint256 => mapping (uint256 => address)) postCommentAddresses;
        mapping (uint256 => mapping (uint256 => bytes32)) postComments;
        mapping (uint256 => uint256) numCommentsIndex;
        mapping (address => bool) trackPublicationSupporters;
    }
    
    mapping (uint256 => Publication) public publicationIndex;
    uint256 public numPublications;
    address public userContentRegisterAddress;
    
    function PublicationRegister(address _userContentRegisterAddress) {
        numPublications = 0;
        userContentRegisterAddress = _userContentRegisterAddress;
    }
    
    //Make names unique
    function addPublication(string name, string metaData, uint256 minUpVoteCostWei, uint adminPaymentPercentage) public {
        publicationIndex[numPublications] = Publication(name, metaData, msg.sender, numPublications == 0, 0, minUpVoteCostWei, adminPaymentPercentage, 0, 0);
        publicationIndex[numPublications].publishingAccessList[msg.sender] = true;
        numPublications++;
    }
    
    function publishContent(uint256 whichPublication, uint256 index) public {
        Publication p = publicationIndex[whichPublication];
        if (p.open || p.publishingAccessList[msg.sender]) { 
            p.publishedAuthorIndex[p.numPublished] = msg.sender;
            p.publishedPostIndex[p.numPublished] = index;
            p.numPublished++;
        }
    }
    
    function permissionAuthor(uint whichPublication, address author, bool giveAccess) public {
        Publication p = publicationIndex[whichPublication];
        if (msg.sender == p.admin) {
            p.publishingAccessList[author] = giveAccess;
        }
    }

    function supportPost(uint256 whichPublication, uint256 postIndex, bytes32 optionalComment) payable returns (bool) {
        Publication p = publicationIndex[whichPublication];
        if (msg.value < p.minSupportCostWei) {
            msg.sender.transfer(msg.value);
            return false;
        } else {
            uint256 weiToAdmin = msg.value / 100 * p.adminPaymentPercentage;
            uint256 weiToAuthor = msg.value - weiToAdmin;
            p.adminClaimOwedWei += weiToAdmin;
            p.authorClaimsOwedWei[p.publishedAuthorIndex[postIndex]] += weiToAuthor;
            p.postRevenueWeiIndex[postIndex] += msg.value;
            if (!p.trackPostSupporters[postIndex][msg.sender]) {
                p.trackPostSupporters[postIndex][msg.sender] = true;
                p.postUniqueSupportersIndex[postIndex]++;
            }
            if (!p.trackPublicationSupporters[msg.sender]) {
                p.trackPublicationSupporters[msg.sender] = true;
                p.uniqueSupporters++;
            }
            if (optionalComment != 0) {
                p.postComments[postIndex][p.numCommentsIndex[postIndex]] = optionalComment;
                p.postCommentAddresses[postIndex][p.numCommentsIndex[postIndex]] = msg.sender;
                p.numCommentsIndex[postIndex]++;
            }
            return true;
        }
        
    }
    
    function withdrawAdminClaim(uint256 whichPublication) {
        if (msg.sender == publicationIndex[whichPublication].admin) {
            uint256 owed = publicationIndex[whichPublication].adminClaimOwedWei;
            publicationIndex[whichPublication].adminClaimOwedWei = 0;
            msg.sender.transfer(owed);
        }
    }
    
    function checkAuthorClaim(uint256 whichPublication, address author) constant returns (uint256){
        return publicationIndex[whichPublication].authorClaimsOwedWei[author];
    }
    
    function withdrawAuthorClaim(uint256 whichPublication) {
        uint256 owed = publicationIndex[whichPublication].authorClaimsOwedWei[msg.sender];
        if (owed > 0) {
            publicationIndex[whichPublication].authorClaimsOwedWei[msg.sender] = 0;
            msg.sender.transfer(owed);
        }
    }
    
    function getContent(uint256 whichPublication, uint256 contentIndex) public returns (bytes32) {
        UserContentRegisterInterface contentRegister = UserContentRegisterInterface(userContentRegisterAddress);
        Publication p = publicationIndex[whichPublication];
        address user = p.publishedAuthorIndex[contentIndex];
        uint256 userContentIndex = p.publishedPostIndex[contentIndex];
        bytes32 contentString = contentRegister.getUserContent(user, userContentIndex);
        return contentString;
    }
    
    function getContentRevenue(uint256 whichPublication, uint256 contentIndex) public returns (uint256) {
        Publication p = publicationIndex[whichPublication];
        return p.postRevenueWeiIndex[contentIndex];
    }
    
    function getContentUniqueSupporters(uint256 whichPublication, uint256 contentIndex) public returns (uint256) {
        Publication p = publicationIndex[whichPublication];
        return p.postUniqueSupportersIndex[contentIndex];
    }
    
    function getContentNumComments(uint256 whichPublication, uint256 contentIndex) public returns (uint256) {
        Publication p = publicationIndex[whichPublication];
        return p.numCommentsIndex[contentIndex];
    }
    
    function getContentCommentByIndex(uint256 whichPublication, uint256 contentIndex, uint256 commentIndex) public returns (bytes32) {
        Publication p = publicationIndex[whichPublication];
        return p.postComments[contentIndex][commentIndex];
    }

}
