package com.example.cameron.ethereumtest1.ethereum;
import android.content.Context;
import com.example.cameron.ethereumtest1.R;
import org.ethereum.geth.Enode;
import org.ethereum.geth.Enodes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by cameron on 8/9/17.
 */

public class EthereumConstants {

    public static final String  USER_CONTENT_REGISTER_ADDRESS_RINKEBY = "0xd23cc1eb9a1969ac4216a0d4918e4ab436643aa3";
    public static final String  PUBLICATION_REGISTER_ADDRESS_RINKEBY = "0x699ab1082b4c051b77f013620397468e306e29b3";
    public static final String  TAG_REGISTER_ADDRESS_RINKEBY = "0x795171e7b8e2b7a0c78f89613b18aa45eafb40b0";

    public static final String  USER_CONTENT_REGISTER_ABI = "[{\"constant\":true,\"inputs\":[{\"name\":\"whichUser\",\"type\":\"address\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getUserContentBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"numUsers\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"userName\",\"type\":\"string\"},{\"name\":\"metaData\",\"type\":\"string\"}],\"name\":\"registerNewUser\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichUser\",\"type\":\"address\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getUserContent\",\"outputs\":[{\"name\":\"content\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"content\",\"type\":\"string\"}],\"name\":\"publishContent\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"newUsername\",\"type\":\"string\"}],\"name\":\"updateMyUserName\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"userName\",\"type\":\"string\"}],\"name\":\"getUserAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_metaData\",\"type\":\"string\"}],\"name\":\"updateMetaData\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"username\",\"type\":\"string\"}],\"name\":\"checkUserNameTaken\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"contentIndex\",\"type\":\"uint256\"},{\"name\":\"links\",\"type\":\"string\"}],\"name\":\"updateContentLinks\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichUser\",\"type\":\"address\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getContentLinks\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"registered\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"userIndex\",\"outputs\":[{\"name\":\"userName\",\"type\":\"string\"},{\"name\":\"profileMetaData\",\"type\":\"string\"},{\"name\":\"numContent\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
    public static final String  PUBLICATION_REGISTER_ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"userContentRegisterAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"getNumPublished\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"getAdmin\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"publicationIndex\",\"outputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"metaData\",\"type\":\"string\"},{\"name\":\"admin\",\"type\":\"address\"},{\"name\":\"open\",\"type\":\"bool\"},{\"name\":\"numPublished\",\"type\":\"uint256\"},{\"name\":\"minSupportCostWei\",\"type\":\"uint256\"},{\"name\":\"adminPaymentPercentage\",\"type\":\"uint8\"},{\"name\":\"uniqueSupporters\",\"type\":\"uint256\"},{\"name\":\"adminClaimOwedWei\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"postIndex\",\"type\":\"uint256\"},{\"name\":\"optionalComment\",\"type\":\"bytes32\"}],\"name\":\"supportPost\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentAuthor\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"metaData\",\"type\":\"string\"},{\"name\":\"minUpVoteCostWei\",\"type\":\"uint256\"},{\"name\":\"adminPaymentPercentage\",\"type\":\"uint8\"}],\"name\":\"createPublication\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContent\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"}],\"name\":\"checkNameTaken\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentUniqueSupporters\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"},{\"name\":\"commentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentCommentByIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"withdrawAuthorClaim\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"author\",\"type\":\"address\"}],\"name\":\"checkAuthorClaim\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentNumComments\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"x\",\"type\":\"bytes32\"}],\"name\":\"bytes32ToString\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"publishContent\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"author\",\"type\":\"address\"},{\"name\":\"giveAccess\",\"type\":\"bool\"}],\"name\":\"permissionAuthor\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"withdrawAdminClaim\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentRevenue\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"numPublications\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"}],\"name\":\"getPublicationIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_userContentRegisterAddress\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
    public static final String  TAG_REGISTER_ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"tag\",\"type\":\"string\"},{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"whichContent\",\"type\":\"uint256\"}],\"name\":\"tagContent\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"publicationTagIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"}],\"name\":\"createTag\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"publicationRegister\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"numTags\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"tagIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"contentTagIndex\",\"outputs\":[{\"name\":\"publicationIndex\",\"type\":\"uint256\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"publicationAlreadyTagged\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"},{\"name\":\"tagPublicationIndex\",\"type\":\"uint256\"}],\"name\":\"getTagPublication\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"numContentTagsIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"}],\"name\":\"checkTagTaken\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tag\",\"type\":\"string\"},{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"tagPublication\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"}],\"name\":\"getTagIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"},{\"name\":\"tagContentIndex\",\"type\":\"uint256\"}],\"name\":\"getTagContent\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"numPublicationTagsIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"publicationRegisterAddress\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

    public final static String KEY_STORE = "/geth_keystore";
    public final static String ETH_DATA_DIRECTORY = "/rinkeby";
    public static final String LIGHT_SERV_PEER_NODE_ENODE_ADDRESS = "enode://780d82a16507a29c534a1e1ae5ea8e91d92868c45693200b77473242325aec8ad47f9d9da95055cad207014a7f28e5b2aff0192eb4f9ba9ff390d240cc3e4a5d@198.27.253.200:30305";
    public static final String LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_2 = "enode://343149e4feefa15d882d9fe4ac7d88f885bd05ebb735e547f12e12080a9fa07c8014ca6fd7f373123488102fe5e34111f8509cf0b7de3f5b44339c9f25e87cb8@52.3.158.184:30303";
    public static final String LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_3 = "enode://1cc27a5a41130a5c8b90db5b2273dc28f7b56f3edfc0dcc57b665d451274b26541e8de49ea7a074281906a82209b9600239c981163b6ff85c3038a8e2bc5d8b8@51.15.68.93:30303";
    public static final String LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_4 = "enode://505d64aeb1e3ad7695532123687a6f9da6b4fe4f6ad59ccbd8391405472378b9b31926144b8e9a732e7a8606b0967dc1f26ebd494850ac3d9dc84dd79fb81a68@157.131.181.20:30305";
    public static final String LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_5 = "enode://3dce588e0177ab2464dacc3619195881e6bf6040de696c81ee1338207875f7ebe784e5d2d73aa4f52c0497d48b06d6dfda81e20f11d74e681b9b1751d60031e7@183.131.7.106:30303";
    public static final String LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_6 = "enode://a24ac7c5484ef4ed0c5eb2d36620ba4e4aa13b8c84684e1b4aab0cebea2ae45cb4d375b77eab56516d34bfbd3c1a833fc51296ff084b770b94fb9028c4d25ccf@52.169.42.101:30303";

    public static final String[] LIGHT_SERVERS = {"enode://03f178d5d4511937933b50b7af683b467abaef8cfc5f7c2c9b271f61e228578ae192aaafc7f0d8035dfa994e734c2c2f72c229e383706be2f4fa43efbe9f94f4@163.172.149.200:30303",
                                                  "enode://15a479bff68597c7d06462872842d5d18c92a0cd169b3c089aae8ffebec30086768d21d9397865b4a787ad571c5d440b412168a68d10a7e9ce3bf7cdcead6ecd@139.162.119.5:30303",
                                                  "enode://16d92fc94f4ec4386aca44d255853c27cbe97a4274c0df98d2b642b0cc4b2f2330e99b00b46db8a031da1a631c85e2b4742d52f5eaeca46612cd28db41fb1d7f@91.223.175.173:30303",
                                                  "enode://1a31d4480f9298f5c88bee63c62b58b7c2ec8c79b305f04fcd10cfb53459a51f9ace2cf91c9d9b6b91499a7b85c15dbe25a2fc4a5177a1f72b2ba95f1d24b73a@109.232.77.19:30303",
                                                  "enode://1d70e87a2ee28a2762f1b2cd56f1b9134824a84264030539bba297f67a5bc9ec7ae3016b5f900dc59b1c27b4e258a63fc282a37b2dd6e25a8377473530513394@208.88.169.151:30303",
                                                  "enode://2097dc8e40ca43a681fef2a0bf7e64126ebcc82b3573a27a3a72d533aafe24efaf5d417b159e4daed37ef7daad9cfa79aca089cb7c9caff6ef936bb289db71ca@109.232.77.12:30303",
                                                  "enode://251d855cf9206425fa1fd0cee06210894d492134658126379bcea80a1e1c7f5efeca93510efcc4f4750459ad0bf2acaaabb5d8bace77adddf26f0b538f5de2f2@109.232.77.17:30303",
                                                  "enode://31b5db1136a0ebceeb0ab6879e95dc66e8c52bcce9c8de50e2f722b5868f782aa0306b6b137b9e0c6271a419c5562a194d7f2abd78e22dcd1f55700dfc30c46a@35.165.17.127:30303",
                                                  "enode://35e5bae6f4a72588ed998bbe436c16d41a75d310e9ac59132f70f3d9b43e700e1acd34cca4a3fc805e9f8de2f214ab96fdea3ccc7b9be5f3f96f638d2bd7fb6c@109.232.77.20:30303",
                                                  "enode://3afdfd40713a8b188a94e4c7a9ddc61bc6ef176c3abbb13d1dd35eb367725b95329a7570039044dbffa49c50d4aa65f0a1f99ee68e46b8e2f09100d11d4fc85a@31.16.0.92:30303",
                                                  "enode://3e9301c797f3863d7d0f29eec9a416f13956bd3a14eec7e0cf5eb56942841526269209edf6f57cd1315bef60c4ebbe3476bc5457bed4e479cac844c8c9e375d3@109.232.77.21:30303",
                                                  "enode://44c67e74ba7abdc9a8c53679621d1364faac585d7af873d553c81263d3c0b3ba322130c225c680b52f8b8a140c05df796583e51c3521fcfe4c5b1320cb6097a7@109.232.77.11:30303",
                                                  "enode://47cc9ddb429f783ff66f61c902e871020346a0cf2696153c2af297cd5a9beacad6a83065db87b2494db11f8002342752e128351bf0abe18b3b190a5a4e5d85d2@109.232.77.8:30303",
                                                  "enode://5a4ed3587c4199e4fa050d0094164ee37935cd055574f56034d344f495a9b6752b303ee7251335785c36f2f82a5656f1118e95d548fd4f66173ca335480e3c17@89.40.10.150:30303",
                                                  "enode://78de8a0916848093c73790ead81d1928bec737d565119932b98c6b100d944b7a95e94f847f689fc723399d2e31129d182f7ef3863f2b4c820abbf3ab2722344d@191.235.84.50:30303",
                                                  "enode://89495deb21261a4542d50167d6e69cf3b1a585609e6843a23becbd349d92755bd2ddcc55bb1f2c017099b774454d95ef5ebccbed1859fc530fb34843ddfc32e2@52.39.91.131:30303",
                                                  "enode://95176fe178be55d40aae49a5c11f21aa58968e13c681a6b1f571b2bb3e45927a7fb3888361bef85c0e28a52ea0e4afa17dcaa9d6c61baf504b3559f056f78581@163.172.145.241:30303",
                                                  "enode://954481639520d993ddd6e02a5a9204d66a6bdd43809f4ca6add188851ceee7a4daf979945aaa6a58cf8905f4a45de49c8a4721de7be8107216f29245a367cdf9@109.232.77.24:30303",
                                                  "enode://9e0b96d69ac772f00de164f25c42dc88d8096e0f71dc9606ff2aeabc6bda5a3cfe14064da241a0aa9267d9ebad82562012b32678f6807515f1802d613ee5f74a@109.232.77.9:30303",
                                                  "enode://af8983f0592c5f6938ad9e3014985ad87e51f0ca4d10094fa6a3c05851e7a14969de3dee7f800b0a7715245c7ee5bbb6ec8a7cf217401f741d061e50f3fc6437@109.232.77.27:30303",
                                                  "enode://bdb0d4eb53941022f6b6eab37761941087892b2fe23a1f115d386009b047a2d5ff62ab0620c187f77a5c0a0cf6dfb793275467c264b9b8368cdaa7334c4cf8e6@109.232.77.15:30303",
                                                  "enode://d17489a8fbdb16587fce5cea0383a3b2e87c53d01eddbd4b2a8ba1b4af353c1bdd9551f8894acfa0d770aaa7eca9e334e15a304a356b4ec214a274f60f2435d6@107.188.141.56:30303",
                                                  "enode://d679ade95bdd30c9277f492a59a34ed3bb6d98f262ac87b9a768732232167b3a7b2b815e4bd4f6ae39824dd89d75c7149a46fd895384966d355df70139874b73@109.232.77.29:30303",
                                                  "enode://e70d9a9175a2cd27b55821c29967fdbfdfaa400328679e98ed61060bc7acba2e1ddd175332ee4a651292743ffd26c9a9de8c4fce931f8d7271b8afd7d221e851@35.226.238.26:30303",
                                                  "enode://ea1737bf696928b4b686a2ccf61a6f2295d149281a80b0d83a9bce242e7bb084434c0837a2002d4cc2840663571ecf3e45517545499c466e4373c69951d090fe@163.172.181.92:30303",
                                                  "enode://f251404ab66f10df6f541d69d735616a7d78e04673ec40cdfe6bf3d1fb5d84647ba627f22a1e8c5e2aa45629c88e33bc394cc1633a63fed11d84304892e51fe9@196.54.41.29:24900",
                                                  "enode://f8ff6758d6b9e5b239051f17648ef57d63b589353b22dd5b53d34b1340b29d55b481259252af7ae490510a3a7dbc1be47222c315033d66db320df8769a1bfdd9@109.232.77.10:30303",
                                                  "enode://a9ed8d7b0cafdc2a30813b0758febe6e94d2a3a625644b6b5854d4463083b060de0bfbf1681811432487c761064c13d854e33ce203b964e28fea3fb866345fde@138.197.108.157:30303",
                                                  "enode://9a4e67ab6482ce584db01b79e19eab2b2f18ff8a65caf73eef69be5dd12be1d115496876b2504fa7f96f3bfac6cd5a4393d4d803c691dc481b9e7586689e312a@159.89.168.113:30303",
                                                  "enode://c8f41b59efa6290df76324339778b95b86bacb2592eda71fc2ed3dc1f3c1f0f1f04fa162e31768f9ab345f0f1e40f2d6a74014b29e24d550153f1c17075467ab@188.166.216.1:30303",
                                                  "enode://b6b28890b006743680c52e64e0d16db57f28124885595fa03a562be1d2bf0f3a1da297d56b13da25fb992888fd556d4c1a27b1f39d531bde7de1921c90061cc6@159.89.28.211:30303"
    };

    public static final String IPFS_GATEWAY_URL = "http://ipfs.io/ipfs/";

    public static String getRinkebyGenesis(Context ctx) {
        return readRawTextFile(ctx, R.raw.rinkeby);
    }

    public final static int RINKEBY_NETWORK_ID = 4;

    public final static Enodes getRinkebyBootNodes() {
        Enodes bootnodes = new Enodes();
        bootnodes.append(new Enode("enode://a24ac7c5484ef4ed0c5eb2d36620ba4e4aa13b8c84684e1b4aab0cebea2ae45cb4d375b77eab56516d34bfbd3c1a833fc51296ff084b770b94fb9028c4d25ccf@52.169.42.101:30303"));
//        bootnodes.append(new Enode("enode://0cc5f5ffb5d9098c8b8c62325f3797f56509bff942704687b6530992ac706e2cb946b90a34f1f19548cd3c7baccbcaea354531e5983c7d1bc0dee16ce4b6440b@40.118.3.223:30304"));
//        bootnodes.append(new Enode("enode://1c7a64d76c0334b0418c004af2f67c50e36a3be60b5e4790bdac0439d21603469a85fad36f2473c9a80eb043ae60936df905fa28f1ff614c3e5dc34f15dcd2dc@40.118.3.223:30306"));
//        bootnodes.append(new Enode("enode://85c85d7143ae8bb96924f2b54f1b3e70d8c4d367af305325d30a61385a432f247d2c75c45c6b4a60335060d072d7f5b35dd1d4c45f76941f62a4f83b6e75daaf@40.118.3.223:30307"));


        /*

        "enode://0cc5f5ffb5d9098c8b8c62325f3797f56509bff942704687b6530992ac706e2cb946b90a34f1f19548cd3c7baccbcaea354531e5983c7d1bc0dee16ce4b6440b@40.118.3.223:30304",
+	"enode://1c7a64d76c0334b0418c004af2f67c50e36a3be60b5e4790bdac0439d21603469a85fad36f2473c9a80eb043ae60936df905fa28f1ff614c3e5dc34f15dcd2dc@40.118.3.223:30306",
+	"enode://85c85d7143ae8bb96924f2b54f1b3e70d8c4d367af305325d30a61385a432f247d2c75c45c6b4a60335060d072d7f5b35dd1d4c45f76941f62a4f83b6e75daaf@40.118.3.223:30307"
         */

        return bootnodes;
    }

    public final static String NO_SUITABLE_PEERS_ERROR = "no suitable peers available";

    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

}
