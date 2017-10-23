package com.example.cameron.ethereumtest1.data;

/**
 * Created by cameron on 10/22/17.
 */

public class EthereumContractProtocols {

    private static enum ETH_CONTRACT{USER_CONTENT_REGISTER, PUBLICATION_REGISTER, TAG_REGISTER};

    private static enum ETH_CALL{ FETCH_NUM_QUALITY_TAGS,
        FETCH_QUALITY_TAG,
        FETCH_QUALITY_TAG_CONTENT_LIST_SIZE,
        FETCH_QUALITY_TAG_CONTENT,
        FETCH_USERNAME }

    private static enum ETH_TRANSACT{ CREATE_QUALITY_TAG,
        SUBSCRIBE_TO_QUALITY_TAG,
        UPVOTE_CONTENT,
        REGISTER_NEW_USER,
        POST_NEW_CONTENT_TO_TAG}

}
