package ethereum;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.cameron.ethereumtest1.data.EthereumConstants;
import com.example.cameron.ethereumtest1.util.PrefUtils;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;

import static com.example.cameron.ethereumtest1.data.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.getRinkebyGenesis;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class EthereumClientService extends IntentService {

    private final static String TAG = EthereumClientService.class.getName();
    private static final String START_ETHEREUM_SERVICE = "ethereum.action.start";

    public static final String UI_UPDATE_ETH_BLOCK = "UI.update.eth.block";
    public static final String PARAM_BLOCK_NUMBER = "param.block.number";

    private static final String ETH_FETCH_ACCOUNT_BALANCE = "eth.fetch.account.balance";
    private static final String PARAM_ADDRESS_STRING = "param.address.string";
    public static final String UI_UPDATE_ACCOUNT_BALANCE = "ui.update.account.balance";
    public static final String PARAM_BALANCE_LONG = "param.balance.long";

    private static final String ETH_FETCH_ACCOUNT_USER_NAME = "eth.fetch.account.user.name";

    private EthereumClient mEthereumClient;
    private org.ethereum.geth.Context mContext;
    private Node mNode;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String addressString;
            switch (action) {
                case START_ETHEREUM_SERVICE:
                    handleStartEthereumService();
                    break;
                case ETH_FETCH_ACCOUNT_BALANCE:
                    addressString = intent.getStringExtra(PARAM_ADDRESS_STRING);
                    handleUpdateAccountBalance(addressString);
                    break;
                case ETH_FETCH_ACCOUNT_USER_NAME:
                    addressString = intent.getStringExtra(PARAM_ADDRESS_STRING);
                    handleUpdateAccountUserName(addressString);
                    break;
            }
        }
    }

    public EthereumClientService() {
        super("EthereumClientService");
    }
    /**
     * Starts this service. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startEthereumService(Context context) {
        Intent intent = new Intent(context, EthereumClientService.class);
        intent.setAction(START_ETHEREUM_SERVICE);
        context.startService(intent);
    }

    public static void fetchAccountUserName(Context context, String addressString) {
        Intent intent = new Intent(context, EthereumClientService.class);
        intent.setAction(ETH_FETCH_ACCOUNT_USER_NAME);
        intent.putExtra(PARAM_ADDRESS_STRING, addressString);
        context.startService(intent);
    }

    private void handleUpdateAccountUserName(String addressString) {
        String username = "";


//        try {
//            balance = mEthereumClient.getBalanceAt(mContext, new Address(addressString), -1);
//        } catch (Exception e) {
//            Log.e(TAG, "Error retrieving balance: " + e.getMessage());
//        }
//
//        PrefUtils.saveSelectedAccountBalance(getBaseContext(), balance.getInt64());
//        Log.e(TAG, "SUCCESSFULLY UPDATED BALANCE");
//
//        Intent intent = new Intent(UI_UPDATE_ACCOUNT_BALANCE);
//        intent.putExtra(PARAM_BALANCE_LONG, balance.getInt64());
//        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
//        bm.sendBroadcast(intent);
    }

    public static void fetchAccountBalance(Context context, String addressString) {
        Intent intent = new Intent(context, EthereumClientService.class);
        intent.setAction(ETH_FETCH_ACCOUNT_BALANCE);
        intent.putExtra(PARAM_ADDRESS_STRING, addressString);
        context.startService(intent);
    }

    private void handleUpdateAccountBalance(String addressString) {
        BigInt balance = new BigInt(0);
        try {
            balance = mEthereumClient.getBalanceAt(mContext, new Address(addressString), -1);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving balance: " + e.getMessage());
        }

        PrefUtils.saveSelectedAccountBalance(getBaseContext(), balance.getInt64());
        Log.e(TAG, "SUCCESSFULLY UPDATED BALANCE");

        Intent intent = new Intent(UI_UPDATE_ACCOUNT_BALANCE);
        intent.putExtra(PARAM_BALANCE_LONG, balance.getInt64());
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
        bm.sendBroadcast(intent);
    }

    private void handleStartEthereumService(){ //String param1, String param2) {
        mContext = new org.ethereum.geth.Context();

        final NodeConfig config = new NodeConfig();
        config.setEthereumEnabled(true);
        config.setEthereumGenesis(getRinkebyGenesis(getBaseContext()));
        config.setEthereumNetworkID(RINKEBY_NETWORK_ID);
        config.setBootstrapNodes(EthereumConstants.getRinkebyBootNodes());
        try {
            if (mNode == null) {
                mNode = Geth.newNode(getFilesDir() + "/rinkeby", config);
            }
            mNode.start();
            mEthereumClient = mNode.getEthereumClient();
            mEthereumClient.subscribeNewHead(mContext, mNewHeadHandler, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }

//            long numPeersInitial = mNode.getPeersInfo().size();
//
//            if (numPeersInitial < 1) {
//                final Handler h = new Handler();
//                final Runnable checkPeers = new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mNode != null) {
//                            long numPeers = mNode.getPeersInfo().size();
//
//                            if (numPeers < 1) {
////                                mSynchLogTextView.setText("Connecting to peers... " + ++mCounter + " seconds" + "\n");
//                                h.postDelayed(this, 1000);
//                            } else {
//                                PeerInfos info = mNode.getPeersInfo();
//                                PeerInfo firstPeer = null;
//                                try {
//                                    firstPeer = info.get(0);
//                                } catch (Exception e) {
////                                    mSynchLogTextView.setText("BUG" + " info.get " + e.getMessage());
//                                }
////                                mSynchLogTextView.append("Connected to: \n" + firstPeer.getName() + "\n");
//                                NodeInfo myInfo = mNode.getNodeInfo();
////                                mSynchLogTextView.append("\nMy name: " + myInfo.getName() + "\n");
////                                mSynchLogTextView.append("My address: " + myInfo.getListenerAddress() + "\n");
////                                mSynchLogTextView.append("My protocols: " + myInfo.getProtocols() + "\n\n");
////                                mCounter = 0;
////                                showSynchInfo();
//                                try {
//                                    mEthereumClient = mNode.getEthereumClient();
//                                    mEthereumClient.subscribeNewHead(mContext, mNewHeadHandler, 16);
//                                } catch (Exception e) {
//                                    Log.e("Error", "poop" + e.getMessage());
//                                }
//                            }
//                        }
//                    }
//                };
//                checkPeers.run();
//            }

    }

    private NewHeadHandler mNewHeadHandler = new NewHeadHandler() {
        @Override
        public void onError(String error) {
//            mSynchLogTextView.setText("error");
//            mSynchLogTextView.invalidate();

        }

        @Override
        public void onNewHead(final Header header) {
//            mHeader = header;
//            Log.e("Error", "GOT CALLED1");
//            if (firstTime || uiUpdated || attemptsWhileWaitingForUIUpdate > 10) {
//                uiUpdated = false;
//                firstTime = false;
//                attemptsWhileWaitingForUIUpdate = 0;
//                Log.e("Error", "ATTEMPTED UI UPDATE");
//                runOnUiThread(updateBlockNumber);
//            } else {
//                if (!firstTime && !uiUpdated)
//                    attemptsWhileWaitingForUIUpdate++;
//            }
//            firstTime = false;
            Intent intent = new Intent(UI_UPDATE_ETH_BLOCK);
            intent.putExtra(PARAM_BLOCK_NUMBER, header.getNumber());
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);
            Log.e("New HEAD", "Blocks received by IntentService");
        }
    };
}
