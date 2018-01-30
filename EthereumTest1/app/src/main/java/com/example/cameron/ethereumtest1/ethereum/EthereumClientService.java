package com.example.cameron.ethereumtest1.ethereum;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import org.ethereum.geth.Account;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.BoundContract;
import org.ethereum.geth.CallOpts;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.Interface;
import org.ethereum.geth.Interfaces;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.ethereum.geth.Signer;
import org.ethereum.geth.TransactOpts;
import org.ethereum.geth.Transaction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import io.ipfs.kotlin.IPFS;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.ETH_DATA_DIRECTORY;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.KEY_STORE;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.PUBLICATION_REGISTER_ABI;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.USER_CONTENT_REGISTER_ABI;
import static com.example.cameron.ethereumtest1.data.EthereumConstants.getRinkebyGenesis;

public class EthereumClientService extends Service {

    private final static String TAG = EthereumClientService.class.getName();

    private static final java.lang.String MESSAGE_ACTION = "message.action";

    public static final String START_ETHEREUM_SERVICE = "com.example.cameron.ethereumtest1.ethereum.action.start";

    public static final String UI_UPDATE_ETH_BLOCK = "ui.update.eth.block";
    public static final String PARAM_BLOCK_NUMBER = "param.block.number";

    public static final String ETH_FETCH_ACCOUNT_BALANCE = "eth.fetch.account.balance";
    public static final String PARAM_ADDRESS_STRING = "param.address.string";
    public static final String UI_UPDATE_ACCOUNT_BALANCE = "ui.update.account.balance";
    public static final String PARAM_BALANCE_LONG = "param.balance.long";

    public static final String ETH_FETCH_ACCOUNT_USER_INFO = "eth.fetch.account.user.name";
    public static final String UI_UPDATE_ACCOUNT_USER_INFO = "ui.update.account.user.name";
    public static final String PARAM_USER_NAME = "param.user.name";
    public static final String PARAM_USER_ICON_URL = "param.user.icon.url";
    public static final String PARAM_PASSWORD = "param.password";

    public static final String ETH_FETCH_USER_CONTENT_LIST = "eth.fetch.user.content.list";
    public static final String UI_UPDATE_USER_CONTENT_LIST = "ui.update.user.content.list";
    public static final String PARAM_CONTENT_STRING = "param.content.string";
    public static final String PARAM_ARRAY_CONTENT_STRING = "param.content.string";

    public static final String ETH_REGISTER_USER = "eth.register.user";
    public static final String UI_REGISTER_USER_PENDING_CONFIRMATION = "ui.register.user.pending.confirmation";

    public static final String ETH_UPDATE_USER_PIC = "eth.update.user.pic";
    public static final String UI_UPDATE_USER_PIC_PENDING_CONFIRMATION = "ui.update.user.pic.pending.confirmation";
    public static final String PARAM_USER_IMAGE_PATH = "param.user.image.path";


    public static final String ETH_PUBLISH_USER_CONTENT = "eth.publish.user.content";
    public static final String UI_PUBLISH_USER_CONTENT_PENDING_CONFIRMATION = "ui.publish.user.content.pending.confirmation";

    public static final String ETH_FETCH_PUBLICATION_CONTENT = "eth.fetch.publication.content";
    public static final String UI_UPDATE_PUBLICATION_CONTENT = "ui.update.publication.content";
    public static final String PARAM_PUBLICATION_INDEX = "param.publication.index";

    public static final String ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION = "eth.publish.user.content.to.publication";
    public static final String UI_PUBLISH_USER_CONTENT_TO_PUBLICATION_PENDING_CONFIRMATION = "ui.publish.user.content.to.publication..pending.confirmation";
    public static final String PARAM_USER_CONTENT_INDEX = "param.user.content.index";


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
                case ETH_FETCH_ACCOUNT_USER_INFO:
                    addressString = b.getString(PARAM_ADDRESS_STRING);
                    handleUpdateAccountUserInfo(addressString);
                    break;
                case ETH_FETCH_USER_CONTENT_LIST:
                    addressString = b.getString(PARAM_ADDRESS_STRING);
                    handleFetchUserContentList(addressString);
                    break;
                case ETH_REGISTER_USER:
                    String userName = b.getString(PARAM_USER_NAME);
                    String password = b.getString(PARAM_PASSWORD);
                    handleRegisterUser(userName, password);
                    break;
                case ETH_PUBLISH_USER_CONTENT:
                    String content = b.getString(PARAM_CONTENT_STRING);
                    password = b.getString(PARAM_PASSWORD);
                    handlePublishUserContent(content, password);
                    break;
                case ETH_FETCH_PUBLICATION_CONTENT:
                    int index = b.getInt(PARAM_PUBLICATION_INDEX);
                    handleFetchPublicationContent(index);
                    break;
                case ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION:
                    index = b.getInt(PARAM_USER_CONTENT_INDEX);
                    password = b.getString(PARAM_PASSWORD);
                    handlePublishUserContentToPublication(index, password);
                    break;
                case ETH_UPDATE_USER_PIC:
                    String picPath = b.getString(PARAM_USER_IMAGE_PATH);
                    password = b.getString(PARAM_PASSWORD);
                    handleUpdateUserPic(picPath, password);
                    break;
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
                case ETH_FETCH_ACCOUNT_USER_INFO:
                case ETH_FETCH_USER_CONTENT_LIST:
                    b.putString(PARAM_ADDRESS_STRING, intent.getStringExtra(PARAM_ADDRESS_STRING));
                    break;
                case ETH_REGISTER_USER:
                    b.putString(PARAM_USER_NAME, intent.getStringExtra(PARAM_USER_NAME));
                    b.putString(PARAM_PASSWORD, intent.getStringExtra(PARAM_PASSWORD));
                    break;
                case ETH_PUBLISH_USER_CONTENT:
                    b.putString(PARAM_CONTENT_STRING, intent.getStringExtra(PARAM_CONTENT_STRING));
                    b.putString(PARAM_PASSWORD, intent.getStringExtra(PARAM_PASSWORD));
                    break;
                case ETH_FETCH_PUBLICATION_CONTENT:
                    b.putInt(PARAM_PUBLICATION_INDEX, intent.getIntExtra(PARAM_PUBLICATION_INDEX, 0));
                    break;
                case ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION:
                    b.putInt(PARAM_USER_CONTENT_INDEX, intent.getIntExtra(PARAM_USER_CONTENT_INDEX, 0));
                    b.putString(PARAM_PASSWORD, intent.getStringExtra(PARAM_PASSWORD));
                    break;
                case ETH_UPDATE_USER_PIC:
                    b.putString(PARAM_USER_IMAGE_PATH, intent.getStringExtra(PARAM_USER_IMAGE_PATH));
                    b.putString(PARAM_PASSWORD, intent.getStringExtra(PARAM_PASSWORD));
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
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(new FileOutputStream(new File(getFilesDir() + ETH_DATA_DIRECTORY + "/GethDroid", "static-nodes.json")));
            String string = "[" +
                    "  \"" + EthereumConstants.LIGHT_SERV_PEER_NODE_ENODE_ADDRESS + "\"," +
                    "  \"" + EthereumConstants.LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_2 + "\"," +
                    "  \"" + EthereumConstants.LIGHT_SERV_PEER_NODE_ENODE_ADDRESS_3 + "\"" +
                    //"  \", enode://pubkey@ip:port\"\n" +
                    "]";
            outputStreamWriter.append(string);
            outputStreamWriter.close();

            if (mNode == null) {
                mNode = Geth.newNode(getFilesDir() + ETH_DATA_DIRECTORY, config);
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
            try {
                Log.e("PEER", "Num Peers: " + mNode.getPeersInfo().size());
                for (int i = 0; i < mNode.getPeersInfo().size(); i++) {
                    Log.e("PEER", "Peer " + i + ": " + mNode.getPeersInfo().get(i).getID());
                    Log.e("PEER", "Peer " + i + ": " + mNode.getPeersInfo().get(i).getRemoteAddress());
                    Log.e("PEER", "Peer " + i + ": " + mNode.getPeersInfo().get(i).getName());
                    Log.e("PEER", "Peer " + i + ": " + mNode.getPeersInfo().get(i).getLocalAddress());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            Log.e(TAG, "Error retrieving balance: " + e.getMessage());
        }

        if (successful) {
            PrefUtils.saveSelectedAccountBalance(getBaseContext(), balance.getInt64());
            Log.e(TAG, "SUCCESSFULLY UPDATED BALANCE");

            Intent intent = new Intent(UI_UPDATE_ACCOUNT_BALANCE);
            intent.putExtra(PARAM_BALANCE_LONG, balance.getInt64());
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);
        }
    }

    private void handleFetchUserContentList(String addressString) {
        String contentString = "";
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

            //Find number of articles
            callData = Geth.newInterfaces(1);
            Interface address = Geth.newInterface();
            address.setAddress(Geth.newAddressFromHex(addressString));
            callData.set(0, address);

            returnData = Geth.newInterfaces(3);
            Interface userNameParam = Geth.newInterface();
            userNameParam.setDefaultString();
            Interface metaParam = Geth.newInterface();
            metaParam.setDefaultString();
            Interface numContentParam = Geth.newInterface();
            numContentParam.setDefaultBigInt();
            returnData.set(0, userNameParam);
            returnData.set(1, metaParam);
            returnData.set(2, numContentParam);

            contract.call(callOpts, returnData, "userIndex", callData);
            long numContent = returnData.get(2).getBigInt().getInt64();

            ArrayList<String> postJsonArray = new ArrayList<>();

            for (long i = numContent - 1; i >= 0; i--) {
                callData = Geth.newInterfaces(2);
                Interface index = Geth.newInterface();
                index.setBigInt(Geth.newBigInt(i));
                callData.set(0, address);
                callData.set(1, index);

                returnData = Geth.newInterfaces(1);
                Interface content = Geth.newInterface();
                content.setDefaultString();
                returnData.set(0, content);

                contentString = contract.callForString(callOpts, "getUserContent", callData);

                String json = "";
                try {
                    json = new IPFS().getGet().cat(contentString);

                } catch (Exception e) {
                    json = "CONTENT CURRENTLY UNAVAILABLE";// + e.getMessage();
                    // Log.e("oops", e.getMessage());
                }
                postJsonArray.add(json);
            }

            //PrefUtils.saveSelectedAccountUserName(getBaseContext(), userName);

            Intent intent = new Intent(UI_UPDATE_USER_CONTENT_LIST);
            intent.putStringArrayListExtra(PARAM_ARRAY_CONTENT_STRING, postJsonArray);
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving contentList: " + e.getMessage());
        }

    }

    private void handleUpdateAccountUserInfo(String addressString) {
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

            callData = Geth.newInterfaces(1);
            Interface paramFetchUsernameCallParameter = Geth.newInterface();
            paramFetchUsernameCallParameter.setAddress(new Address(addressString));
            callData.set(0, paramFetchUsernameCallParameter);

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

            contract.call(callOpts, returnData, "userIndex", callData);
            userName = returnData.get(0).getString();
            String metadataIconUrl = returnData.get(1).getString();

            if (!TextUtils.isEmpty(userName)) {
                Log.e(TAG, "SUCCESSFULLY UPDATED USER NAME");
            } else {
                userName = "not yet registered...";
                Log.e(TAG, "NO USER NAME FOUND FOR " + addressString);
            }
            PrefUtils.saveSelectedAccountUserName(getBaseContext(), userName);

            Intent intent = new Intent(UI_UPDATE_ACCOUNT_USER_INFO);
            intent.putExtra(PARAM_USER_NAME, userName);
            intent.putExtra(PARAM_USER_ICON_URL, metadataIconUrl);
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving username: " + e.getMessage());
        }
    }



    private void handleRegisterUser(String userName, final String password) {
        try {
            final KeyStore mKeyStore = new KeyStore(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);

            BoundContract contract = Geth.bindContract(
                    new Address(EthereumConstants.USER_CONTENT_REGISTER_ADDRESS_RINKEBY),
                    USER_CONTENT_REGISTER_ABI, mEthereumClient);

            Address address = Geth.newAddressFromHex(PrefUtils.getSelectedAccountAddress(getBaseContext()));

            TransactOpts tOpts = new TransactOpts();
            tOpts.setContext(mContext);
            tOpts.setFrom(address);
            tOpts.setSigner(new Signer() {
                @Override
                public Transaction sign(Address address, Transaction transaction) throws Exception {
                    Account account = mKeyStore.getAccounts().get(PrefUtils.getSelectedAccountNum(getBaseContext()));
                    mKeyStore.unlock(account, password);
                    Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                    mKeyStore.lock(account.getAddress());
                    return signed;
                }
            });
            tOpts.setValue(new BigInt(0));
            long noncePending  = mEthereumClient.getPendingNonceAt(mContext, address);
            tOpts.setNonce(noncePending);
            Interfaces callParams = Geth.newInterfaces(2);
            Interface paramUsername = Geth.newInterface();
            paramUsername.setString(userName);
            callParams.set(0, paramUsername);
            Interface paramMetaData = Geth.newInterface();
            paramMetaData.setString("meta");
            callParams.set(1, paramMetaData);
            final Transaction txRegisterUser = contract.transact(tOpts, "registerNewUser", callParams);
            mEthereumClient.sendTransaction(mContext, txRegisterUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(UI_REGISTER_USER_PENDING_CONFIRMATION);
        intent.putExtra(PARAM_USER_NAME, userName);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
        bm.sendBroadcast(intent);
    }


    private void handlePublishUserContent(String content, final String password) {
        try {
            final KeyStore mKeyStore = new KeyStore(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);

            BoundContract userContract = Geth.bindContract(
                    new Address(EthereumConstants.USER_CONTENT_REGISTER_ADDRESS_RINKEBY),
                    USER_CONTENT_REGISTER_ABI, mEthereumClient);

            Address address = Geth.newAddressFromHex(PrefUtils.getSelectedAccountAddress(getBaseContext()));

            TransactOpts tOpts = new TransactOpts();
            tOpts.setContext(mContext);
            tOpts.setFrom(address);
            tOpts.setSigner(new Signer() {
                @Override
                public Transaction sign(Address address, Transaction transaction) throws Exception {
                    Account account = mKeyStore.getAccounts().get(PrefUtils.getSelectedAccountNum(getBaseContext()));
                    mKeyStore.unlock(account, password);
                    Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                    mKeyStore.lock(account.getAddress());
                    return signed;
                }
            });
            tOpts.setValue(new BigInt(0));
            long noncePending  = mEthereumClient.getPendingNonceAt(mContext, address);
            tOpts.setNonce(noncePending);
            final String contentHash = new IPFS().getAdd().string(content).getHash();

            //publish to user feed
            Interfaces callParams = Geth.newInterfaces(1);
            Interface paramContentHash = Geth.newInterface();
            paramContentHash.setString(contentHash);
            callParams.set(0, paramContentHash);
            final Transaction txPublishContent = userContract.transact(tOpts, "publishContent", callParams);
            mEthereumClient.sendTransaction(mContext, txPublishContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(UI_PUBLISH_USER_CONTENT_PENDING_CONFIRMATION);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
        bm.sendBroadcast(intent);
    }

    private void handleFetchPublicationContent(int index) {
        String contentString = "";
        while (!mIsReady) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            BoundContract contract = Geth.bindContract(
                    new Address(EthereumConstants.PUBLICATION_REGISTER_ADDRESS_RINKEBY),
                    PUBLICATION_REGISTER_ABI, mEthereumClient);

            CallOpts callOpts = Geth.newCallOpts();
            callOpts.setContext(mContext);
            Interfaces callData;
            Interfaces returnData;

            //Find number of content items
            callData = Geth.newInterfaces(1);
            Interface publicationIndex = Geth.newInterface();
            publicationIndex.setBigInt(new BigInt(index));
            callData.set(0, publicationIndex);

            returnData = Geth.newInterfaces(1);
            Interface numPublishedParam = Geth.newInterface();
            numPublishedParam.setDefaultBigInt();
            returnData.set(0, numPublishedParam);

            contract.callFix(callOpts, returnData, "getNumPublished", callData);
            long numPublished = returnData.get(0).getBigInt().getInt64();

            ArrayList<String> postJsonArray = new ArrayList<>();

            for (long i = numPublished - 1; i >= 1; i--) {
                callData = Geth.newInterfaces(2);
                callData.set(0, publicationIndex);
                Interface contentIndex = Geth.newInterface();
                contentIndex.setBigInt(Geth.newBigInt(i));
                callData.set(1, contentIndex);

                returnData = Geth.newInterfaces(1);
                Interface content = Geth.newInterface();
                content.setDefaultString();
                returnData.set(0, content);

                //contract.call(callOpts, returnData, "getContent", callData);
                contentString = contract.callForString(callOpts, "getContent", callData);
                //contentString = new String(returnData.get(0).getString());

                String json = "";
                try {
                    json = new IPFS().getGet().cat(contentString);
                } catch (Exception e) {
                    json = "CONTENT CURRENTLY UNAVAILABLE";// + e.getMessage();
                    // Log.e("oops", e.getMessage());
                }
                postJsonArray.add(json);
            }


            //PrefUtils.saveSelectedAccountUserName(getBaseContext(), userName);

            Intent intent = new Intent(UI_UPDATE_PUBLICATION_CONTENT);
            intent.putStringArrayListExtra(PARAM_ARRAY_CONTENT_STRING, postJsonArray);
            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
            bm.sendBroadcast(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving contentList: " + e.getMessage());
        }
    }



    private void handlePublishUserContentToPublication(int index, final String password) {

        try {
            final KeyStore mKeyStore = new KeyStore(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);

            BoundContract publicationContract = Geth.bindContract(
                    new Address(EthereumConstants.PUBLICATION_REGISTER_ADDRESS_RINKEBY),
                    PUBLICATION_REGISTER_ABI, mEthereumClient);

            Address address = Geth.newAddressFromHex(PrefUtils.getSelectedAccountAddress(getBaseContext()));

            TransactOpts tOpts = new TransactOpts();
            tOpts.setContext(mContext);
            tOpts.setFrom(address);
            tOpts.setSigner(new Signer() {
                @Override
                public Transaction sign(Address address, Transaction transaction) throws Exception {
                    Account account = mKeyStore.getAccounts().get(PrefUtils.getSelectedAccountNum(getBaseContext()));
                    mKeyStore.unlock(account, password);
                    Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                    mKeyStore.lock(account.getAddress());
                    return signed;
                }
            });
            tOpts.setValue(new BigInt(0));
            long noncePending  = mEthereumClient.getPendingNonceAt(mContext, address);
            tOpts.setNonce(noncePending);

            // publish to slush pile
            Interfaces callParams = Geth.newInterfaces(2);
            Interface paramWhichPublication = Geth.newInterface();
            paramWhichPublication.setBigInt(new BigInt(0));
            Interface paramUserContentIndex = Geth.newInterface();
            paramUserContentIndex.setBigInt(Geth.newBigInt(index));
            callParams.set(0, paramWhichPublication);
            callParams.set(1, paramUserContentIndex);
            final Transaction txPublishToPublication = publicationContract.transact(tOpts, "publishContent", callParams);
            mEthereumClient.sendTransaction(mContext, txPublishToPublication);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(UI_PUBLISH_USER_CONTENT_TO_PUBLICATION_PENDING_CONFIRMATION);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
        bm.sendBroadcast(intent);
    }

    private void handleUpdateUserPic(final String picPath, final String password) {
        try {
            final KeyStore mKeyStore = new KeyStore(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);

            BoundContract contract = Geth.bindContract(
                    new Address(EthereumConstants.USER_CONTENT_REGISTER_ADDRESS_RINKEBY),
                    USER_CONTENT_REGISTER_ABI, mEthereumClient);

            Address address = Geth.newAddressFromHex(PrefUtils.getSelectedAccountAddress(getBaseContext()));

            TransactOpts tOpts = new TransactOpts();
            tOpts.setContext(mContext);
            tOpts.setFrom(address);
            tOpts.setSigner(new Signer() {
                @Override
                public Transaction sign(Address address, Transaction transaction) throws Exception {
                    Account account = mKeyStore.getAccounts().get(PrefUtils.getSelectedAccountNum(getBaseContext()));
                    mKeyStore.unlock(account, password);
                    Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                    mKeyStore.lock(account.getAddress());
                    return signed;
                }
            });
            tOpts.setValue(new BigInt(0));
            long noncePending  = mEthereumClient.getPendingNonceAt(mContext, address);
            tOpts.setNonce(noncePending);

            //add file to IPFS
            File f = new File(picPath);
            final String contentHash = new IPFS().getAdd().file(f).getHash();


            Interfaces callParams = Geth.newInterfaces(1);
            Interface paramMetaData = Geth.newInterface();
            paramMetaData.setString(contentHash);
            callParams.set(0, paramMetaData);
            final Transaction txRegisterUser = contract.transact(tOpts, "updateMetaData", callParams);
            mEthereumClient.sendTransaction(mContext, txRegisterUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(UI_UPDATE_USER_PIC_PENDING_CONFIRMATION);
        //intent.putExtra(PARAM_USER_NAME, userName);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(EthereumClientService.this);
        bm.sendBroadcast(intent);
    }

}
