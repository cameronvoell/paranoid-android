package com.example.cameron.ethereumtest1.data;
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

    public static final String  USER_CONTENT_REGISTER_ADDRESS_RINKEBY = "0x6375c9f3ea4ba7e92a1f0b8ff72168d6a9ff860a";
    public static final String  PUBLICATION_REGISTER_ADDRESS_RINKEBY = "0x51632a476c36255ce719c30d0e68c82fa0d01c5d";
    public static final String  TAG_REGISTER_ADDRESS_RINKEBY = "0xf0dd6c9b1b24b653e5c2466f231d3353e9bf3ada";

    public static final String  USER_CONTENT_REGISTER_ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"numUsers\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"userName\",\"type\":\"string\"},{\"name\":\"metaData\",\"type\":\"string\"}],\"name\":\"registerNewUser\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichUser\",\"type\":\"address\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"getUserContent\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"newUsername\",\"type\":\"string\"}],\"name\":\"updateMyUserName\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"username\",\"type\":\"string\"}],\"name\":\"checkUserNameTaken\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[],\"name\":\"UserContentRegistry\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"registered\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"userIndex\",\"outputs\":[{\"name\":\"userName\",\"type\":\"string\"},{\"name\":\"profileMetaData\",\"type\":\"string\"},{\"name\":\"numContent\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"content\",\"type\":\"bytes32\"}],\"name\":\"publishContent\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"}]";
    public static final String  PUBLICATION_REGISTER_ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"userContentRegisterAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"publicationIndex\",\"outputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"metaData\",\"type\":\"string\"},{\"name\":\"admin\",\"type\":\"address\"},{\"name\":\"open\",\"type\":\"bool\"},{\"name\":\"numPublished\",\"type\":\"uint256\"},{\"name\":\"minSupportCostWei\",\"type\":\"uint256\"},{\"name\":\"adminPaymentPercentage\",\"type\":\"uint256\"},{\"name\":\"uniqueSupporters\",\"type\":\"uint32\"},{\"name\":\"adminClaimOwedWei\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"postIndex\",\"type\":\"uint256\"},{\"name\":\"optionalComment\",\"type\":\"bytes32\"}],\"name\":\"supportPost\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":true,\"type\":\"function\",\"stateMutability\":\"payable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContent\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"metaData\",\"type\":\"string\"},{\"name\":\"minUpVoteCostWei\",\"type\":\"uint256\"},{\"name\":\"adminPaymentPercentage\",\"type\":\"uint256\"}],\"name\":\"addPublication\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentUniqueSupporters\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"},{\"name\":\"commentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentCommentByIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"withdrawAuthorClaim\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"author\",\"type\":\"address\"}],\"name\":\"checkAuthorClaim\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentNumComments\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"index\",\"type\":\"uint256\"}],\"name\":\"publishContent\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"author\",\"type\":\"address\"},{\"name\":\"giveAccess\",\"type\":\"bool\"}],\"name\":\"permissionAuthor\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"}],\"name\":\"withdrawAdminClaim\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"whichPublication\",\"type\":\"uint256\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"getContentRevenue\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[],\"name\":\"numPublications\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"inputs\":[{\"name\":\"_userContentRegisterAddress\",\"type\":\"address\"}],\"payable\":false,\"type\":\"constructor\",\"stateMutability\":\"nonpayable\"}]";
    public static final String  TAG_REGISTER_ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"}],\"name\":\"createTag\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[],\"name\":\"numTags\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"},{\"name\":\"tagContentIndex\",\"type\":\"uint256\"}],\"name\":\"getTagUserContent\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"tagIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"publicationNumTagsIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"},{\"name\":\"tagPublicationIndex\",\"type\":\"uint256\"}],\"name\":\"getTagPublication\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[],\"name\":\"UserContentRegistry\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"}],\"name\":\"checkTagTaken\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"tag\",\"type\":\"string\"},{\"name\":\"contentIndex\",\"type\":\"uint256\"}],\"name\":\"tagUserContent\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"tag\",\"type\":\"string\"},{\"name\":\"publicationIndex\",\"type\":\"uint256\"}],\"name\":\"tagPublication\",\"outputs\":[],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"tagName\",\"type\":\"string\"}],\"name\":\"getTagIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"userContentNumTagsIndex\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\",\"stateMutability\":\"view\"}]";



    public static String getRinkebyGenesis(Context ctx) {
        return readRawTextFile(ctx, R.raw.rinkeby);
    }

    public final static int RINKEBY_NETWORK_ID = 4;

    public final static Enodes getRinkebyBootNodes() {
        Enodes bootnodes = new Enodes();
        bootnodes.append(new Enode("enode://a24ac7c5484ef4ed0c5eb2d36620ba4e4aa13b8c84684e1b4aab0cebea2ae45cb4d375b77eab56516d34bfbd3c1a833fc51296ff084b770b94fb9028c4d25ccf@52.169.42.101:30303?discport=30304"));
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
