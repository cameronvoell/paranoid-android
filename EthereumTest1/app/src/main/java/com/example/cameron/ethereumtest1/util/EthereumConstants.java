package com.example.cameron.ethereumtest1.util;
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

    public static final String SLUSH_PILE_RINKEBY_ADDRESS = "0x835bfb6291ef8ee95c6deaea9ad58b49c4c4cbec";
    public static final String SLUSH_PILE_ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"pileSize\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"fetchFromPile\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"slushAddress\",\"type\":\"string\"}],\"name\":\"addToPile\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"type\":\"constructor\"}]";

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
