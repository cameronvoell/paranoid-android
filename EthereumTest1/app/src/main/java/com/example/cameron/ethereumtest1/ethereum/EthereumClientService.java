package com.example.cameron.ethereumtest1.ethereum;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.example.cameron.ethereumtest1.data.EthereumConstants;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.BoundContract;
import org.ethereum.geth.CallOpts;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.Interface;
import org.ethereum.geth.Interfaces;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;

import java.util.ArrayList;

import static com.example.cameron.ethereumtest1.data.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.USER_CONTENT_REGISTER_ABI;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.getRinkebyGenesis;

public class EthereumClientService extends Service {

    private final static String TAG = EthereumClientService.class.getName();

    private static final java.lang.String MESSAGE_ACTION = "message.action";

    public static final String START_ETHEREUM_SERVICE = "com.example.cameron.ethereumtest1.ethereum.action.start";

    public static final String UI_UPDATE_ETH_BLOCK = "UI.update.eth.block";
    public static final String PARAM_BLOCK_NUMBER = "param.block.number";

    public static final String ETH_FETCH_ACCOUNT_BALANCE = "eth.fetch.account.balance";
    public static final String PARAM_ADDRESS_STRING = "param.address.string";
    public static final String UI_UPDATE_ACCOUNT_BALANCE = "ui.update.account.balance";
    public static final String PARAM_BALANCE_LONG = "param.balance.long";

    public static final String ETH_FETCH_ACCOUNT_USER_NAME = "eth.fetch.account.user.name";
    public static final String UI_UPDATE_ACCOUNT_USER_NAME = "ui.update.account.user.name";
    public static final String PARAM_USER_NAME = "param.user.name";

    public static final String ETH_FETCH_USER_CONTENT_LIST = "eth.fetch.user.content.list";
    public static final String UI_UPDATE_USER_CONTENT_LIST = "ui.update.user.content.list";

    public static final String ETH_PUBLISH_USER_CONTENT = "eth.publish.user.content";

    private EthereumClient mEthereumClient;
    private org.ethereum.geth.Context mContext;
    private Node mNode;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private volatile boolean mIsReady = false;

    public EthereumClientService() {}

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            switch(b.getString(MESSAGE_ACTION)) {
                case START_ETHEREUM_SERVICE:
                    handleStartEthereumService();
                    break;
                case ETH_FETCH_ACCOUNT_BALANCE:
                    String addressString = b.getString(PARAM_ADDRESS_STRING);
                    handleUpdateAccountBalance(addressString);
                    break;
                case ETH_FETCH_ACCOUNT_USER_NAME:
                    addressString = b.getString(PARAM_ADDRESS_STRING);
                    handleUpdateAccountUserName(addressString);
                    break;
                case ETH_FETCH_USER_CONTENT_LIST:
                    addressString = b.getString(PARAM_ADDRESS_STRING);
                    handleFetchUserContentList(addressString);
                default:
                    break;
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        Bundle b = new Bundle();
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ETH_FETCH_ACCOUNT_BALANCE:
                case ETH_FETCH_ACCOUNT_USER_NAME:
                case ETH_FETCH_USER_CONTENT_LIST:
                    b.putString(PARAM_ADDRESS_STRING, intent.getStringExtra(PARAM_ADDRESS_STRING));
                    break;
            }
            b.putString(MESSAGE_ACTION, intent.getAction());
            msg.setData(b);
            mServiceHandler.sendMessage(msg);
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        try {
            mNode.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleStartEthereumService() { //String param1, String param2) {
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
    }

    private NewHeadHandler mNewHeadHandler = new NewHeadHandler() {
        @Override
        public void onError(String error) {
        }

        @Override
        public void onNewHead(final Header header) {
            Intent intent = new Intent(UI_UPDATE_ETH_BLOCK);
            intent.putExtra(PARAM_BLOCK_NUMBER, header.getNumber());
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);
            //Log.e("New HEAD", "Blocks received by IntentService");
            mIsReady = true;
        }
    };

    private void handleUpdateAccountBalance(String addressString) {
        BigInt balance = new BigInt(0);
        boolean successful = false;
        while (!mIsReady) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            balance = mEthereumClient.getBalanceAt(mContext, new Address(addressString), -1);
            successful = true;
        } catch (Exception e) {
            successful = false;
            //Log.e(TAG, "Error retrieving balance: " + e.getMessage());
        }

        if (successful) {
            PrefUtils.saveSelectedAccountBalance(getBaseContext(), balance.getInt64());
            //Log.e(TAG, "SUCCESSFULLY UPDATED BALANCE");

            Intent intent = new Intent(UI_UPDATE_ACCOUNT_BALANCE);
            intent.putExtra(PARAM_BALANCE_LONG, balance.getInt64());
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);
        }
    }

    private void handleFetchUserContentList(String addressString) {
        ArrayList<String> contentList = new ArrayList<>();
        
    }

    private void handleUpdateAccountUserName(String addressString) {
        String userName = "";
        while (!mIsReady) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            BoundContract contract = Geth.bindContract(
                    new Address(EthereumConstants.USER_CONTENT_REGISTER_ADDRESS_RINKEBY),
                    USER_CONTENT_REGISTER_ABI, mEthereumClient);
            CallOpts callOpts = Geth.newCallOpts();
            callOpts.setContext(mContext);
            Interfaces callData;
            Interfaces returnData;

            returnData = Geth.newInterfaces(3);
            Interface userNameData = Geth.newInterface();
            Interface metaData = Geth.newInterface();
            Interface numContent = Geth.newInterface();
            userNameData.setDefaultString();
            metaData.setDefaultString();
            numContent.setDefaultBigInt();
            returnData.set(0, userNameData);
            returnData.set(1, metaData);
            returnData.set(2, numContent);

            callData = Geth.newInterfaces(1);
            Interface paramFetchUsernameCallParameter = Geth.newInterface();
            paramFetchUsernameCallParameter.setAddress(new Address(addressString));
            callData.set(0, paramFetchUsernameCallParameter);

            contract.call(callOpts, returnData, "userIndex", callData);
            userName = returnData.get(0).getString();

            if (!TextUtils.isEmpty(userName)) {
                Log.e(TAG, "SUCCESSFULLY UPDATED USER NAME");
            } else {
                userName = "not yet registered...";
                Log.e(TAG, "NO USER NAME FOUND FOR " + addressString);
            }
            PrefUtils.saveSelectedAccountUserName(getBaseContext(), userName);

            Intent intent = new Intent(UI_UPDATE_ACCOUNT_USER_NAME);
            intent.putExtra(PARAM_USER_NAME, userName);
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving username: " + e.getMessage());
        }
    }

}
