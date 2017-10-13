package com.example.cameron.ethereumtest1.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.adapters.MyContentContractRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.adapters.MyContentItemRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.data.Content;
import com.example.cameron.ethereumtest1.data.ContentItem;
import com.example.cameron.ethereumtest1.fragments.ContentContractListFragment;
import com.example.cameron.ethereumtest1.fragments.ContentListFragment;
import com.example.cameron.ethereumtest1.ipfs.IPFSDaemon;
import com.example.cameron.ethereumtest1.ipfs.IPFSDaemonService;
import com.example.cameron.ethereumtest1.util.EthereumConstants;
import com.google.gson.Gson;

import org.ethereum.geth.Account;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.BoundContract;
import org.ethereum.geth.CallOpts;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.Interface;
import org.ethereum.geth.Interfaces;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.ethereum.geth.NodeInfo;
import org.ethereum.geth.PeerInfo;
import org.ethereum.geth.PeerInfos;
import org.ethereum.geth.Signer;
import org.ethereum.geth.TransactOpts;
import org.ethereum.geth.Transaction;
import java.util.ArrayList;
import java.util.HashMap;

import io.ipfs.kotlin.IPFS;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.CONTENT_CONTRACT_REGISTER_ABI;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.CONTENT_CONTRACT_REGISTER_RINKEBY_ADDRESS;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.SLUSH_PILE_ABI;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.SLUSH_PILE_RINKEBY_ADDRESS;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.USERNAME_CONTRACT_ABI;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.USERNAME_CONTRACT_RINKEBY;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.getRinkebyGenesis;

public class MainActivity extends AppCompatActivity implements ContentListFragment.OnListFragmentInteractionListener,
        ContentContractListFragment.OnListFragmentInteractionListener{

    private final static String KEY_STORE = "/geth_keystore";
    public final static String sharedPreferencesName = "paranoid_preferences";

    private static enum WHICH_CONTRACT{SLUSH_PILE, CONTRACT_REGISTER, USERNAME};

    private final static int ETH_CALL_NUM_CONTENT_CONTRACTS_REGISTERED = 0;
    private final static int ETH_CALL_FETCH_CONTENT_CONTRACT = 1;
    private final static int ETH_CALL_FETCH_CONTENT_LIST_SIZE = 2;
    private final static int ETH_CALL_FETCH_CONTENT_FROM_SELECTED_CONTRACT = 3;
    private final static int ETH_CALL_PILE_SIZE= 4;
    private final static int ETH_CALL_FETCH_FROM_PILE = 5;
    private final static int ETH_CALL_FETCH_USERNAME = 6;

    private final static int ETH_TRANSACT_CREATE_CONTENT_FEED = 0;
    private final static int ETH_TRANSACT_POST_TO_SELECTED_FEED = 1;
    private final static int ETH_TRANSACT_POST_TO_SLUSH_PILE = 2;
    private static final int ETH_TRANSACT_UPDATE_USERNAME = 3;

    private TextView mSynchInfoTextView;
    private TextView mSynchLogTextView;
    private TextView mAccountTextView;
    private TextView mAccountListTextView;
    private ContentListFragment mContentListFragment;
    private ContentContractListFragment mContentContractListFragment;
    private ImageButton mContententListButton;
    private ImageButton mContractListButton;
    private FloatingActionButton mFloatingActionButton1;
    private FloatingActionButton mFloatingActionButton2;
    private FloatingActionButton mFloatingActionButton3;
    private LinearLayout mNetworkSynchView;
    private RelativeLayout mAccountPageView;

    private EthereumClient mEthereumClient;
    private Context mContext;
    private Node mNode;
    private ArrayList<Account> mAccounts = new ArrayList<>();
    private KeyStore mKeyStore;

    private IPFSDaemon mIpfsDaemon = new IPFSDaemon(this);

    private Content mContent;
    private MyContentItemRecyclerViewAdapter mMyContentItemAdapter;
    private MyContentContractRecyclerViewAdapter mMyContentContractRecyclerViewAdapter;

    private boolean mShowingContracts = false;
    private boolean mIsFabOpen = false;
    private int mCounter = 0;
    private long mHighest = 0;
    private boolean mLoadedSlush = false;
    private long mLastUpdated = 0;

    /*
     * Lifecycle Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSynchLogTextView = (TextView) findViewById(R.id.synchLog);
        mSynchInfoTextView = (TextView) findViewById(R.id.synchInfo);
        mAccountTextView = (TextView) findViewById(R.id.accountInfo);
        mAccountListTextView = (TextView) findViewById(R.id.accountList);

        mNetworkSynchView = (LinearLayout) findViewById(R.id.networkSynch);
        mAccountPageView = (RelativeLayout) findViewById(R.id.accountPage);
        mContententListButton = (ImageButton) findViewById(R.id.button_content_list);
        mContractListButton = (ImageButton) findViewById(R.id.button_contract_list);
        mFloatingActionButton1 = (FloatingActionButton) findViewById(R.id.fab1);
        mFloatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab2);
        mFloatingActionButton3 = (FloatingActionButton) findViewById(R.id.fab3);

        mContentListFragment = ContentListFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mContentListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mKeyStore = new KeyStore(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);

        mSynchLogTextView.append("Connecting to peers...");
        mContext = new Context();

        openAccountInfo(null);
        openNetworkSynch(null);

        startIPFSDaemon();
        startEthereumClientAndConnectToPeers();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION","Permission is granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
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

    /*
     * Methods for managing Ethereum and IPFS Connectivity
     */

    private void startIPFSDaemon() {
        if (!mIpfsDaemon.isReady()) {
            mIpfsDaemon.download(this, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    startIPFSServiceAndCheckForConnectivity.run();
                    return null;
                }
            });
        } else {
            startIPFSServiceAndCheckForConnectivity.run();
        }
    }

    private Runnable startIPFSServiceAndCheckForConnectivity = new Runnable() {
        @Override
        public void run() {
            startService(new Intent(MainActivity.this, IPFSDaemonService.class));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    IPFS ipfs = new IPFS();
                    String version = null;
                    while (version == null) {
                        try {
                            version = ipfs.getInfo().version().getVersion();
                        } catch (Exception e) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                Log.e("AHHHH", e1.getMessage());
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "connected to IPFS!", Toast.LENGTH_SHORT).show();
                            Log.e("IPFS", "CONNECTED!");
                        }
                    });
                }
            } ).start();
        }
    };

    private void startEthereumClientAndConnectToPeers() {
        try {
            final NodeConfig config = new NodeConfig();
            config.setEthereumEnabled(true);
            config.setEthereumGenesis(getRinkebyGenesis(getBaseContext()));
            config.setEthereumNetworkID(RINKEBY_NETWORK_ID);
            config.setBootstrapNodes(EthereumConstants.getRinkebyBootNodes());
            if (mNode == null) {
                mNode = Geth.newNode(getFilesDir() + "/rinkeby", config);
            }
            mNode.start();

            long numPeersInitial = mNode.getPeersInfo().size();

            if (numPeersInitial < 1) {
                final Handler h = new Handler();
                final Runnable checkPeers = new Runnable() {
                    @Override
                    public void run() {
                        if (mNode != null) {
                            long numPeers = mNode.getPeersInfo().size();

                            if (numPeers < 1) {
                                mSynchLogTextView.setText("Connecting to peers... " + ++mCounter + " seconds" + "\n");
                                h.postDelayed(this, 1000);
                            } else {
                                PeerInfos info = mNode.getPeersInfo();
                                PeerInfo firstPeer = null;
                                try {
                                    firstPeer = info.get(0);
                                } catch (Exception e) {
                                    mSynchLogTextView.setText("BUG" + " info.get " + e.getMessage());
                                }
                                mSynchLogTextView.append("Connected to: \n" + firstPeer.getName() + "\n");
                                NodeInfo myInfo = mNode.getNodeInfo();
                                mSynchLogTextView.append("\nMy name: " + myInfo.getName() + "\n");
                                mSynchLogTextView.append("My address: " + myInfo.getListenerAddress() + "\n");
                                mSynchLogTextView.append("My protocols: " + myInfo.getProtocols() + "\n\n");
                                mCounter = 0;
                                showSynchInfo();
                            }
                        }
                    }
                };
                checkPeers.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable updateBlockNumber = new Runnable() {
        @Override
        public void run() {
            Log.e("Error", "GOT CALLED2");
            if (mHighest == 0) {
                Log.e("Error", "GOT CALLED3");
                try {
                    mHighest = mEthereumClient.syncProgress(mContext).getHighestBlock();
                } catch (Exception e) {
                    mHighest = -1;
                    Log.e("Error", "called 4" + e.getMessage());

                }
            }
            if (mHighest != -1) {
                Log.e("Error", "GOT CALLED5");
                if (mHeader.getNumber() - mLastUpdated > 1000) {
                    mLastUpdated = mHeader.getNumber();
                    mSynchInfoTextView.setText(mHeader.getNumber() + "/" + mHighest);
                    if (!mLoadedSlush && mHeader.getNumber() > 723887) {
                        //fetchFromPile();
                    }
                    if (mLastUpdated > mHighest) {
                        mHighest = -1;
                    }
                }
            } else {
                Log.e("Error", "GOT CALLED6");
                mSynchInfoTextView.setText("Block: " + mHeader.getNumber());
            }
            uiUpdated = true;
        }
    };

    Header mHeader = null;
    boolean uiUpdated = false;
    boolean firstTime = true;
    int attemptsWhileWaitingForUIUpdate = 0;

    private NewHeadHandler mNewHeadHandler = new NewHeadHandler() {
        @Override
        public void onError(String error) {
            mSynchLogTextView.setText("error");
            mSynchLogTextView.invalidate();
        }

        @Override
        public void onNewHead(final Header header) {
            mHeader = header;
            Log.e("Error", "GOT CALLED1");
            if (firstTime || uiUpdated || attemptsWhileWaitingForUIUpdate > 10) {
                uiUpdated = false;
                firstTime = false;
                attemptsWhileWaitingForUIUpdate = 0;
                Log.e("Error", "ATTEMPTED UI UPDATE");
                runOnUiThread(updateBlockNumber);
            } else {
                if (!firstTime && !uiUpdated)
                    attemptsWhileWaitingForUIUpdate++;
            }
            firstTime = false;
            Log.e("Error", "GOT CALLED AFTER");
        }
    };

    private void showSynchInfo() {
        try {
            mEthereumClient = mNode.getEthereumClient();
            mEthereumClient.subscribeNewHead(mContext, mNewHeadHandler, 16);
        } catch(Exception e) {
            Log.e("Error", "poop" + e.getMessage());
        }
        final Handler h = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final long blockNumber = mEthereumClient.getBlockByNumber(mContext, -1).getNumber();
                    Log.e("Error", "hello?");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSynchLogTextView.append("\nLatestBlock: " + blockNumber + ", synching...\n");
                        }
                    });
                } catch (final Exception e) {
                    Log.e("Error", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mCounter == 0) {
                                mSynchLogTextView.append("Awaiting EthereumClient Peer Acknowledgment (" + e.getMessage() + ")\n");
                            } else {
                                mSynchLogTextView.append(mCounter + ", ");
                            }
                            mCounter++;
                        }
                    });
                    h.postDelayed(this, 1000);
                }
            }
        }).start();
    }

    /*
     * Methods for Posting to and Fetching data from Ethereum + IPFS
     */
    public void fetchFromContentContractRegister() {
        if (mContent == null) mContent = new Content();
        mContent.clearContractItems();
        mContent.addContractItem(new Content.ContentContract.ContentContractItem("slush-pile", "Anyone can post here!", 0, "n/a"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Interfaces returnData = callEthereumContract(ETH_CALL_NUM_CONTENT_CONTRACTS_REGISTERED, -1);
                    long numRegisteredResponse = returnData.get(0).getBigInt().getInt64();
                    for (int i = (int) (numRegisteredResponse - 1); i >= 0 && i > numRegisteredResponse - 10; i--) {
                        Interfaces fetchContentContractReturnData = callEthereumContract(ETH_CALL_FETCH_CONTENT_CONTRACT, i);
                        final String name = fetchContentContractReturnData.get(0).getString();
                        final String description = fetchContentContractReturnData.get(1).getString();
                        final long num = fetchContentContractReturnData.get(2).getBigInt().getInt64();
                        final String admin = fetchContentContractReturnData.get(3).getAddress().getHex();
                        mContent.addContractItem(new Content.ContentContract.ContentContractItem(name, description, num, admin));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMyContentContractRecyclerViewAdapter = new MyContentContractRecyclerViewAdapter(mContent.CONTRACT_ITEMS, MainActivity.this);
                        mContentContractListFragment.setAdapter(mMyContentContractRecyclerViewAdapter);
                    }
                });
            }
        }).start();
    }

    private void fetchFromSelectedContract() {
        if (mContent == null) mContent = new Content();
        mContent.clearItems();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Interfaces returnData = callEthereumContract(ETH_CALL_FETCH_CONTENT_LIST_SIZE, -1);
                    long numContent = returnData.get(0).getBigInt().getInt64();
                    for (int i = (int)(numContent - 1); i >= 0 && i > numContent - 10; i--) {
                        Interfaces contentData = callEthereumContract(ETH_CALL_FETCH_CONTENT_FROM_SELECTED_CONTRACT, i);
                        final String response = contentData.get(0).getString();
                        String json = "";
                        try {
                            json = new IPFS().getGet().cat(response);
                        } catch (Exception e) {
                            json = "CONTENT CURRENTLY UNAVAILABLE";// + e.getMessage();
                           // Log.e("oops", e.getMessage());
                        }
                        ContentItem contentItem = convertJsonToContentItem(json);
                        Interfaces userNameResponse = callEthereumContract(ETH_CALL_FETCH_USERNAME, 0, contentItem.publishedBy);
                        if (userNameResponse != null && userNameResponse.get(0).getString().length() > 0)
                            contentItem.publishedBy = userNameResponse.get(0).getString();
                        mContent.addContentItem(contentItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMyContentItemAdapter = new MyContentItemRecyclerViewAdapter(mContent.ITEMS, MainActivity.this);
                        mContentListFragment.setAdapter(mMyContentItemAdapter);
                    }
                });
                mLoadedSlush = true;
            }
        }).start();
    }

    private ContentItem convertJsonToContentItem(String json) {
        Gson gson = new Gson();
        ContentItem contentItem = gson.fromJson(json, ContentItem.class);
        return contentItem;
    }

    public void fetchFromPile() {
        if (mContent == null) mContent = new Content();
        mContent.clearItems();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Interfaces pileSizeResponse = callEthereumContract(ETH_CALL_PILE_SIZE, -1);
                    long pileSize = pileSizeResponse.get(0).getBigInt().getInt64();
                    for (int i = (int)(pileSize - 1); i >= 0 && i > pileSize - 10; i--) {
                        Interfaces pileReturnData = callEthereumContract(ETH_CALL_FETCH_FROM_PILE, i);
                        final String response = pileReturnData.get(0).getString();
                        final String json = new IPFS().getGet().cat(response);
                        ContentItem contentItem = convertJsonToContentItem(json);
                        mContent.addContentItem(contentItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMyContentItemAdapter = new MyContentItemRecyclerViewAdapter(mContent.ITEMS, MainActivity.this);
                        mContentListFragment.setAdapter(mMyContentItemAdapter);
                    }
                });
                mLoadedSlush = true;
            }
        }).start();
    }

    private Interfaces callEthereumContract(int whichContractCall, int integerParameter) {
        return callEthereumContract(whichContractCall, integerParameter, "");
    }

    private Interfaces callEthereumContract(int whichContractCall, int integerParameter, String stringParameter) {
        try {
            WHICH_CONTRACT whichContract;
            if (whichContractCall == ETH_CALL_PILE_SIZE ||
                whichContractCall == ETH_CALL_FETCH_FROM_PILE) {
                whichContract = WHICH_CONTRACT.SLUSH_PILE;
            } else if (whichContractCall == ETH_CALL_FETCH_USERNAME) {
                whichContract = WHICH_CONTRACT.USERNAME;
            } else {
                whichContract = WHICH_CONTRACT.CONTRACT_REGISTER;
            }

            Address address;
            BoundContract contract;
            switch(whichContract) {
                case SLUSH_PILE:
                    address = new Address(SLUSH_PILE_RINKEBY_ADDRESS);
                    contract = Geth.bindContract(address, SLUSH_PILE_ABI, mEthereumClient);
                    break;
                case USERNAME:
                    address = new Address(USERNAME_CONTRACT_RINKEBY);
                    contract = Geth.bindContract(address, USERNAME_CONTRACT_ABI, mEthereumClient);
                    break;
                default:
                    address = new Address(CONTENT_CONTRACT_REGISTER_RINKEBY_ADDRESS);
                    contract = Geth.bindContract(address, CONTENT_CONTRACT_REGISTER_ABI, mEthereumClient);
                    break;
            }

            CallOpts callOpts = Geth.newCallOpts();
            callOpts.setContext(mContext);

            switch (whichContractCall) {
                case ETH_CALL_NUM_CONTENT_CONTRACTS_REGISTERED:
                    Interfaces paramsNumRegisteredCallData = Geth.newInterfaces(0);
                    Interfaces returnData = Geth.newInterfaces(1);
                    Interface paramNumRegisteredReturnParameter = Geth.newInterface();
                    paramNumRegisteredReturnParameter.setDefaultBigInt();
                    returnData.set(0, paramNumRegisteredReturnParameter);
                    contract.call(callOpts, returnData, "numRegistered", paramsNumRegisteredCallData);
                    return returnData;
                case ETH_CALL_FETCH_CONTENT_CONTRACT:
                    Interfaces paramsFetchRegisteredContractsCallData = Geth.newInterfaces(1);
                    Interfaces paramsFetchRegisteredContractsReturnData = Geth.newInterfaces(4);
                    Interface contractName = Geth.newInterface();
                    Interface contractDescription = Geth.newInterface();
                    Interface numPosts = Geth.newInterface();
                    Interface contractAdmin = Geth.newInterface();
                    contractName.setDefaultString();
                    contractDescription.setDefaultString();
                    numPosts.setDefaultBigInt();
                    contractAdmin.setDefaultAddress();
                    paramsFetchRegisteredContractsReturnData.set(0, contractName);
                    paramsFetchRegisteredContractsReturnData.set(1, contractDescription);
                    paramsFetchRegisteredContractsReturnData.set(2, numPosts);
                    paramsFetchRegisteredContractsReturnData.set(3, contractAdmin);
                    Interface paramFetchCallParameter = Geth.newInterface();
                    paramFetchCallParameter.setBigInt(new BigInt(integerParameter));
                    paramsFetchRegisteredContractsCallData.set(0, paramFetchCallParameter);
                    contract.call(callOpts, paramsFetchRegisteredContractsReturnData, "localContentContracts", paramsFetchRegisteredContractsCallData);
                    return paramsFetchRegisteredContractsReturnData;
                case ETH_CALL_FETCH_CONTENT_LIST_SIZE:
                    Interfaces paramsContentSizeCallData = Geth.newInterfaces(1);
                    Interfaces paramsContentSizeReturnData = Geth.newInterfaces(1);
                    Interface paramContentSizeReturnParameter = Geth.newInterface();
                    paramContentSizeReturnParameter.setDefaultBigInt();
                    paramsContentSizeReturnData.set(0, paramContentSizeReturnParameter);
                    Interface paramContentSizeCallParameter = Geth.newInterface();
                    paramContentSizeCallParameter.setString(getSharedPreferences(sharedPreferencesName, 0).getString("selected", ""));
                    paramsContentSizeCallData.set(0, paramContentSizeCallParameter);
                    contract.call(callOpts, paramsContentSizeReturnData, "getLocalContentListSize", paramsContentSizeCallData);
                    return paramsContentSizeReturnData;
                case ETH_CALL_FETCH_CONTENT_FROM_SELECTED_CONTRACT:
                    Interfaces paramsFetchReturnData = Geth.newInterfaces(1);
                    Interface paramFetchReturnParameter = Geth.newInterface();
                    paramFetchReturnParameter.setDefaultString();
                    paramsFetchReturnData.set(0, paramFetchReturnParameter);
                    Interfaces paramsFetchCallData = Geth.newInterfaces(2);
                    Interface nameParameter = Geth.newInterface();
                    nameParameter.setString(getSharedPreferences(sharedPreferencesName, 0).getString("selected", ""));
                    paramsFetchCallData.set(0, nameParameter);
                    Interface paramFetchContentCallParameter = Geth.newInterface();
                    paramFetchContentCallParameter.setBigInt(new BigInt(integerParameter));
                    paramsFetchCallData.set(1, paramFetchContentCallParameter);
                    contract.call(callOpts, paramsFetchReturnData, "getLocalContent", paramsFetchCallData);
                    return paramsFetchReturnData;
                case ETH_CALL_PILE_SIZE:
                    Interfaces paramsPileSizeCallData = Geth.newInterfaces(0);
                    Interfaces paramsPileSizeReturnData = Geth.newInterfaces(1);
                    Interface paramPileSizeReturnParameter = Geth.newInterface();
                    paramPileSizeReturnParameter.setDefaultBigInt();
                    paramsPileSizeReturnData.set(0, paramPileSizeReturnParameter);
                    contract.call(callOpts, paramsPileSizeReturnData, "pileSize", paramsPileSizeCallData);
                    return paramsPileSizeReturnData;
                case ETH_CALL_FETCH_FROM_PILE:
                    Interfaces paramsFetchFromPileReturnData = Geth.newInterfaces(1);
                    Interface paramFetchFromPileReturnParameter = Geth.newInterface();
                    paramFetchFromPileReturnParameter.setDefaultString();
                    paramsFetchFromPileReturnData.set(0, paramFetchFromPileReturnParameter);
                    Interfaces paramsFetchFromPileCallData = Geth.newInterfaces(1);
                    Interface paramFetchFromPileCallParameter = Geth.newInterface();
                    paramFetchFromPileCallParameter.setBigInt(new BigInt(integerParameter));
                    paramsFetchFromPileCallData.set(0, paramFetchFromPileCallParameter);
                    contract.call(callOpts, paramsFetchFromPileReturnData, "fetchFromPile", paramsFetchFromPileCallData);
                    return paramsFetchFromPileReturnData;
                case ETH_CALL_FETCH_USERNAME:
                    Interfaces paramsFetchUsernameReturnData = Geth.newInterfaces(1);
                    Interface paramFetchUsernameReturnParameter = Geth.newInterface();
                    paramFetchUsernameReturnParameter.setDefaultString();
                    paramsFetchUsernameReturnData.set(0, paramFetchUsernameReturnParameter);
                    Interfaces paramsFetchUsernameCallData = Geth.newInterfaces(1);
                    Interface paramFetchUsernameCallParameter = Geth.newInterface();
                    paramFetchUsernameCallParameter.setAddress(new Address(stringParameter));
                    paramsFetchUsernameCallData.set(0, paramFetchUsernameCallParameter);
                    contract.call(callOpts, paramsFetchUsernameReturnData, "getUsername", paramsFetchUsernameCallData);
                    return paramsFetchUsernameReturnData;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void createNewContentFeed(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_content_feed);
        dialog.setTitle("New Content Feed");

        final EditText editName = (EditText) dialog.findViewById(R.id.editName);
        final EditText editDescription = (EditText) dialog.findViewById(R.id.editDescription);
        final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editName.getText().toString();
                final String description = editDescription.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendEthereumTransaction(ETH_TRANSACT_CREATE_CONTENT_FEED, password.getText().toString(), name, description);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                animateFabMenu(null);
                            }
                        });
                    }
                }).start();
            }
        });
        dialog.show();
    }

    private String convertContentItemToJSON(ContentItem contentItem) {
        Gson gson = new Gson();
        String json = gson.toJson(contentItem);
        return json;
    }

    private ContentItem convertDialogInputToContentItem(String title, String primaryText) {
        String publishedBy = mAccounts.get(0).getAddress().getHex();
        String contentTypeDictionaryAddress = "empty";
        String contentType = "empty";
        long publishedDate = System.currentTimeMillis();
        String primaryImageUrl = "empty";
        String primaryHttpLink = "empty";
        String primaryContentAddressedLink = "empty";
        return new ContentItem(publishedBy, contentTypeDictionaryAddress, contentType, title,
                publishedDate, primaryText, primaryImageUrl, primaryHttpLink, primaryContentAddressedLink);
    }

    public void postToSelectedFeed(View view) {
        SharedPreferences sp = getSharedPreferences(sharedPreferencesName, 0);
        final String selected = sp.getString("selected", "");
        if (selected.equals("slush-pile")){
            postToSlushPile(view);
        } else {
            // custom dialog
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_post_content_to_feed);
            dialog.setTitle("Post to " + selected);

            final EditText title = (EditText) dialog.findViewById(R.id.editTitle);
            final EditText body = (EditText) dialog.findViewById(R.id.editBody);
            final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentItem contentItem = convertDialogInputToContentItem(title.getText().toString(), body.getText().toString());
                    final String json = convertContentItemToJSON(contentItem);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendEthereumTransaction(ETH_TRANSACT_POST_TO_SELECTED_FEED, password.getText().toString(), json, "");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    animateFabMenu(null);
                                }
                            });
                        }
                    } ).start();
                }
            });
            dialog.show();
        }
    }

    public void postToSlushPile(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_post_content_to_feed);
        dialog.setTitle("Post Message");

        final EditText text = (EditText) dialog.findViewById(R.id.editMessage);
        final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = text.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendEthereumTransaction(ETH_TRANSACT_POST_TO_SLUSH_PILE, password.getText().toString(), message, "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                animateFabMenu(null);
                            }
                        });
                    }
                } ).start();
            }
        });
        dialog.show();
    }

    public void updateUsername(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update_username);
        dialog.setTitle("Update Username");

        final EditText editUsername = (EditText) dialog.findViewById(R.id.editUsername);
        final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = editUsername.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendEthereumTransaction(ETH_TRANSACT_UPDATE_USERNAME, password.getText().toString(), message, "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                } ).start();
            }
        });
        dialog.show();
    }

    private void sendEthereumTransaction(int whichTransact, final String password, String stringParameterOne, String stringParameterTwo) {
        try {
            WHICH_CONTRACT whichContract;
            if (whichTransact == ETH_TRANSACT_POST_TO_SLUSH_PILE) {
                whichContract = WHICH_CONTRACT.SLUSH_PILE;
            } else if (whichTransact == ETH_TRANSACT_UPDATE_USERNAME) {
                whichContract = WHICH_CONTRACT.USERNAME;
            } else {
                whichContract = WHICH_CONTRACT.CONTRACT_REGISTER;
            }

            Address address;
            BoundContract contract;
            switch(whichContract) {
                case SLUSH_PILE:
                    address = new Address(SLUSH_PILE_RINKEBY_ADDRESS);
                    contract = Geth.bindContract(address, SLUSH_PILE_ABI, mEthereumClient);
                    break;
                case USERNAME:
                    address = new Address(USERNAME_CONTRACT_RINKEBY);
                    contract = Geth.bindContract(address, USERNAME_CONTRACT_ABI, mEthereumClient);
                    break;
                default:
                    address = new Address(CONTENT_CONTRACT_REGISTER_RINKEBY_ADDRESS);
                    contract = Geth.bindContract(address, CONTENT_CONTRACT_REGISTER_ABI, mEthereumClient);
                    break;
            }

            TransactOpts tOpts = new TransactOpts();
            tOpts.setContext(mContext);
            tOpts.setFrom(mAccounts.get(0).getAddress());
            tOpts.setSigner(new Signer() {
                @Override
                public Transaction sign(Address address, Transaction transaction) throws Exception {
                    Account account = mKeyStore.getAccounts().get(0);
                    mKeyStore.unlock(account, password);
                    Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                    mKeyStore.lock(account.getAddress());
                    return signed;
                }
            });
            tOpts.setValue(new BigInt(0));

            switch (whichTransact) {
                case ETH_TRANSACT_CREATE_CONTENT_FEED:
                    long noncePending  = mEthereumClient.getPendingNonceAt(mContext, mAccounts.get(0).getAddress());
                    long nonce = mEthereumClient.getNonceAt(mContext, mAccounts.get(0).getAddress(), 0);
                    tOpts.setNonce(Math.max(nonce, noncePending));
                    Interfaces params = Geth.newInterfaces(2);
                    Interface nameParam = Geth.newInterface();
                    Interface descriptionParam = Geth.newInterface();
                    nameParam.setString(stringParameterOne);
                    descriptionParam.setString(stringParameterTwo);
                    params.set(0, nameParam);
                    params.set(1, descriptionParam);
                    final Transaction tx = contract.transact(tOpts, "registerLocalContentContract", params);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), tx.getHash().getHex(), Toast.LENGTH_LONG).show();
                        }
                    });
                    mEthereumClient.sendTransaction(mContext, tx);
                    break;
                case ETH_TRANSACT_POST_TO_SELECTED_FEED:
                    final String multihash = new IPFS().getAdd().string(stringParameterOne).getHash();
                    long noncePendingPostFeed  = mEthereumClient.getPendingNonceAt(mContext, mAccounts.get(0).getAddress());
                    long noncePostFeed = mEthereumClient.getNonceAt(mContext, mAccounts.get(0).getAddress(), 0);
                    tOpts.setNonce(Math.max(noncePostFeed, noncePendingPostFeed));
                    Interfaces paramsPostFeed = Geth.newInterfaces(2);
                    Interface param = Geth.newInterface();
                    param.setString(getSharedPreferences(sharedPreferencesName, 0).getString("selected", "impossible to arrive here, autoselects slush pile"));
                    paramsPostFeed.set(0, param);
                    Interface param2 = Geth.newInterface();
                    param2.setString(multihash);
                    paramsPostFeed.set(1, param2);

                    final Transaction txPostFeed = contract.transact(tOpts, "registerLocalContent", paramsPostFeed);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), txPostFeed.getHash().getHex(), Toast.LENGTH_LONG).show();
                        }
                    });
                    mEthereumClient.sendTransaction(mContext, txPostFeed);
                    break;
                case ETH_TRANSACT_POST_TO_SLUSH_PILE:
                    final String multihashSlush = new IPFS().getAdd().string(stringParameterOne).getHash();
                    long noncePendingSlush  = mEthereumClient.getPendingNonceAt(mContext, mAccounts.get(0).getAddress());
                    long nonceSlush = mEthereumClient.getNonceAt(mContext, mAccounts.get(0).getAddress(), 0);
                    tOpts.setNonce(Math.max(nonceSlush, noncePendingSlush));
                    Interfaces paramsSlush = Geth.newInterfaces(1);
                    Interface paramSlush = Geth.newInterface();
                    paramSlush.setString(multihashSlush);
                    paramsSlush.set(0, paramSlush);

                    final Transaction txSlush = contract.transact(tOpts, "addToPile", paramsSlush);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), txSlush.getHash().getHex(), Toast.LENGTH_LONG).show();
                        }
                    });
                    mEthereumClient.sendTransaction(mContext, txSlush);
                    break;
                case ETH_TRANSACT_UPDATE_USERNAME:
                    long noncePendingUsername  = mEthereumClient.getPendingNonceAt(mContext, mAccounts.get(0).getAddress());
                    long nonceUsername = mEthereumClient.getNonceAt(mContext, mAccounts.get(0).getAddress(), 0);
                    tOpts.setNonce(Math.max(nonceUsername, noncePendingUsername));
                    Interfaces paramsUsername = Geth.newInterfaces(1);
                    Interface paramUsername = Geth.newInterface();
                    paramUsername.setString(stringParameterOne);
                    paramsUsername.set(0, paramUsername);

                    final Transaction txUsername = contract.transact(tOpts, "updateMyUserName", paramsUsername);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), txUsername.getHash().getHex(), Toast.LENGTH_LONG).show();
                        }
                    });
                    mEthereumClient.sendTransaction(mContext, txUsername);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Methods for Managing Ethereum Accounts
     */
    public void createAccount(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_account);
        dialog.setTitle("Enter New Account Password");

        final EditText text = (EditText) dialog.findViewById(R.id.editMessage);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDone);
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    Account newAcc = mKeyStore.newAccount(text.getText().toString());
                    String account = newAcc.getAddress().getHex();
                    mAccountTextView.setText(account.substring(0,4) + "..." + account.substring(account.length() -5,account.length() - 1));
                    openAccountInfo(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openAccountInfo(View view) {
        closeNetworkSynch(null);
        mAccountPageView.setVisibility(View.VISIBLE);
        mAccountListTextView.setText("");
        long numAccounts = mKeyStore.getAccounts().size();
        if (numAccounts > 0) {
            mAccounts = new ArrayList<>();
            for (int i = 0; i < numAccounts; i++) {
                try {
                    Account account = mKeyStore.getAccounts().get(i);
                    mAccounts.add(account);
                    String accountString = account.getAddress().getHex();
                    mAccountListTextView.append("Account: " + accountString + "\n");
                    if (i == 0) {
                        mAccountTextView.setText(accountString.substring(0, 9) + "..." + accountString.substring(accountString.length() - 4, accountString.length()));
                    }


                    BigInt balance = mEthereumClient.getBalanceAt(mContext, account.getAddress(), -1);
                    mAccountListTextView.append("Balance: " + balance + " Wei" + "\n\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mAccountListTextView.setText("no accounts yet...");
        }

    }

    /*
     * UI Response Methods
     */
    public void closeNetworkSynch(View view) {
        mNetworkSynchView.setVisibility(View.GONE);
    }

    public void openNetworkSynch(View view) {
        closeAccountPage(null);
        mNetworkSynchView.setVisibility(View.VISIBLE);
    }

    public void closeAccountPage(View view) {
        mAccountPageView.setVisibility(View.GONE);
    }

    public void reloadFeed(View view) {
        if (mShowingContracts) {
            fetchFromContentContractRegister();
        } else {
            if (getSharedPreferences(sharedPreferencesName, 0).getString("selected", "").equals("slush-pile")) {
                fetchFromPile();
            } else {
                fetchFromSelectedContract();
            }
        }
    }

    public void showContentContracts(View view) {
        if (mContentContractListFragment == null)
            mContentContractListFragment = ContentContractListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mContentContractListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mContententListButton.setColorFilter(Color.DKGRAY);
        mContractListButton.setColorFilter(Color.WHITE);
        mShowingContracts = true;
    }

    public void showContentList(View view) {
        if (mContentListFragment == null)
            mContentListFragment = ContentListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mContentListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mContententListButton.setColorFilter(Color.WHITE);
        mContractListButton.setColorFilter(Color.DKGRAY);
        mShowingContracts = false;
    }

    @Override
    public void onListFragmentInteraction(ContentItem item) {
    }

    @Override
    public void onListFragmentInteraction(Content.ContentContract.ContentContractItem item) {
        SharedPreferences sp = getSharedPreferences(sharedPreferencesName, 0);
        sp.edit().putString("selected", item.name).commit();
        fetchFromContentContractRegister();
    }

    public void animateFabMenu(View v) {
        if (mIsFabOpen) {
            mIsFabOpen=false;
            mFloatingActionButton1.animate().translationY(0);
            mFloatingActionButton2.animate().translationY(0);
            mFloatingActionButton3.animate().translationY(0);
        } else {
            mIsFabOpen = true;
            mFloatingActionButton1.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
            mFloatingActionButton2.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
            mFloatingActionButton3.animate().translationY(-getResources().getDimension(R.dimen.standard_195));
        }
    }


    public void previewPost(View view) {
        Toast.makeText(getApplicationContext(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
    }
}
