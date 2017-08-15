package com.example.cameron.ethereumtest1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.cameron.ethereumtest1.util.EthereumConstants;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.ethereum.geth.NodeInfo;
import org.ethereum.geth.PeerInfo;
import org.ethereum.geth.PeerInfos;
import org.ethereum.geth.SyncProgress;

import static com.example.cameron.ethereumtest1.util.EthereumConstants.NO_SUITABLE_PEERS_ERROR;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.getRinkebyGenesis;

public class MainActivity extends AppCompatActivity {

    private TextView mTextBox;
    private TextView mText2;
    private EthereumClient mEthereumClient;
    private Context mContext;
    private Node mNode;
    private int mCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Android In-Process Node");
        mTextBox = (TextView) findViewById(R.id.textbox);
        mText2 = (TextView) findViewById(R.id.text2);
        mTextBox.append("Connecting to peers...");
        mContext = new Context();

        try {
            final NodeConfig config = new NodeConfig();
            config.setEthereumEnabled(true);
            config.setEthereumGenesis(getRinkebyGenesis(getBaseContext()));
            config.setEthereumNetworkID(RINKEBY_NETWORK_ID);
            config.setBootstrapNodes(EthereumConstants.getRinkebyBootNodes());
            if (mNode == null)
                mNode = Geth.newNode(getFilesDir() + "/.rinkeby", config);
            mNode.start();

            long numPeersInitial = mNode.getPeersInfo().size();

            if (numPeersInitial < 1) {
                final Handler h = new Handler();
                final Runnable checkPeers = new Runnable() {
                    @Override
                    public void run() {
                        long numPeers = mNode.getPeersInfo().size();

                        if (numPeers < 1) {
                            mTextBox.setText("Connecting to peers... " + ++mCounter + " seconds");
                            h.postDelayed(this, 1000);
                        } else {
                            PeerInfos info = mNode.getPeersInfo();
                            PeerInfo firstPeer = null;
                            try {
                                firstPeer = info.get(0);
                            } catch (Exception e) {
                                mTextBox.setText("BUG" +  " info.get " + e.getMessage());
                            }
                            mTextBox.setText("("+ mCounter + " seconds)Connected to: \n" + firstPeer.getName() + "\n");
                            NodeInfo myInfo = mNode.getNodeInfo();
                            mTextBox.append("\nMy name: " + myInfo.getName() + "\n");
                            mTextBox.append("My address: " + myInfo.getListenerAddress() + "\n");
                            mTextBox.append("My protocols: " + myInfo.getProtocols() + "\n\n");
                            showSynchInfo();
                        }
                    }
                };
                h.post(checkPeers);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NewHeadHandler mNewHeadHandler = new NewHeadHandler() {
        @Override
        public void onError(String error) {
            mTextBox.setText("error");
            mTextBox.invalidate();
        }

        @Override
        public void onNewHead(final Header header) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //ec.sendTransaction(ctx, new Transaction(""));
                    mTextBox.setText("#" + header.getNumber() + ": " + header.getHash().getHex().substring(0, 10) + "...\n");
                }
            });
        }
    };

    private void showSynchInfo() {
        try {
            mEthereumClient = mNode.getEthereumClient();
            mTextBox.append("LatestBlock: " + mEthereumClient.getBlockByNumber(mContext, -1).getNumber() + ", synching...\n");

            mEthereumClient.subscribeNewHead(mContext, mNewHeadHandler, 16);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            Handler h = new Handler();
            mTextBox.append(e.getMessage() + "\n");
            if (NO_SUITABLE_PEERS_ERROR.equals(e.getMessage())) {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSynchInfo();
                    }
                }, 1000);
            }
        }
    }

    public void fetchBlockNumber(View view) {
        try {
            SyncProgress sync = mEthereumClient.syncProgress(mContext);
            long highest = sync.getHighestBlock();
            mText2.setText("starting: " + sync.getStartingBlock()
                    + ", current: " + sync.getCurrentBlock()
                    + ", highest:" + highest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mNode.stop();
            mNode = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
