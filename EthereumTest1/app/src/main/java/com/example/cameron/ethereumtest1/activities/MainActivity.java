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
import com.example.cameron.ethereumtest1.util.EthereumConstants;

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

import io.ipfs.kotlin.IPFS;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import static com.example.cameron.ethereumtest1.util.EthereumConstants.CONTENT_CONTRACT_REGISTER_ABI;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.CONTENT_CONTRACT_REGISTER_RINKEBY_ADDRESS;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.NO_SUITABLE_PEERS_ERROR;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.SLUSH_PILE_ABI;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.SLUSH_PILE_RINKEBY_ADDRESS;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.getRinkebyGenesis;

public class MainActivity extends AppCompatActivity implements ContentListFragment.OnListFragmentInteractionListener,
                                                    ContentContractListFragment.OnListFragmentInteractionListener{

    private final static String KEY_STORE = "/geth_keystore";
    public final static String sharedPreferencesName = "paranoid_preferences";

    private TextView mSynchInfoTextView;
    private TextView mSynchLogTextView;
    private TextView mAccountTextView;
    private TextView mAccountListTextView;
    private ContentListFragment mContentListFragment;
    private ContentContractListFragment mContentContractListFragment;
    private ImageButton mContententListButton;
    private ImageButton mContractListButton;

    private LinearLayout mNetworkSynchView;
    private RelativeLayout mAccountPageView;


    private EthereumClient mEthereumClient;
    private Context mContext;
    private Node mNode;
    private ArrayList<Account> mAccounts = new ArrayList<>();
    private KeyStore mKeyStore;

    private int mCounter = 0;
    private long mHighest = 0;
    private boolean mLoadedSlush = false;
    private long mLastUpdated = 0;

    private IPFSDaemon mIpfsDaemon = new IPFSDaemon(this);

    private Content mContent;
    private MyContentItemRecyclerViewAdapter mMyContentItemAdapter;
    private MyContentContractRecyclerViewAdapter mMyContentContractRecyclerViewAdapter;
    private boolean mShowingContracts = false;

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
        if (!mIpfsDaemon.isReady()) {
            mIpfsDaemon.download(this, new Function0<Unit>() {
                @Override
                public Unit invoke() {
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
                                    Toast.makeText(getApplicationContext(), "woah", Toast.LENGTH_SHORT).show();
                                    Log.e("AHHHH", "GOOD");
                                }
                            });
                        }
                    } ).start();
                    return null;
                }
            });
        } else {
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
                            Toast.makeText(getApplicationContext(), "woah", Toast.LENGTH_SHORT).show();
                            Log.e("AHHHH", "GOOD");
                        }
                    });
                }
            } ).start();
        }

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
                                mSynchLogTextView.setText("BUG" +  " info.get " + e.getMessage());
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
                };
                h.post(checkPeers);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION","Permission is granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //File write logic here
            }
        }
    }

    private NewHeadHandler mNewHeadHandler = new NewHeadHandler() {
        @Override
        public void onError(String error) {
            mSynchLogTextView.setText("error");
            mSynchLogTextView.invalidate();
        }

        @Override
        public void onNewHead(final Header header) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (mHighest == 0) {
                        try {
                            mHighest = mEthereumClient.syncProgress(mContext).getHighestBlock();
                        } catch (Exception e) {
                            mHighest = -1;
                            e.printStackTrace();
                        }
                    }
                    if (mHighest != -1) {
                        if (header.getNumber() - mLastUpdated > 1000) {
                            mLastUpdated = header.getNumber();
                            mSynchInfoTextView.setText(header.getNumber() + "/" + mHighest);
                            if (!mLoadedSlush && header.getNumber() > 723887) {
                                fetchFromPile();
                            }
                            if (mLastUpdated > mHighest) {
                                mHighest = -1;
                            }
                        }
                    } else {
                        mSynchInfoTextView.setText("Block: " + header.getNumber());
                    }

                }
            });
        }
    };

    private void showSynchInfo() {
        try {
            mEthereumClient = mNode.getEthereumClient();
            mSynchLogTextView.append("\nLatestBlock: " + mEthereumClient.getBlockByNumber(mContext, -1).getNumber() + ", synching...\n");
            mEthereumClient.subscribeNewHead(mContext, mNewHeadHandler, 16);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            Handler h = new Handler();
            if (mCounter == 0) {
                mSynchLogTextView.append("Awaiting EthereumClient Peer Acknowledgment (" + e.getMessage() + ")\n");
            } else {
                mSynchLogTextView.append(mCounter + ", ");
            }
            mCounter++;
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

    public void fetchFromContentContractRegister() {
        if (mContent == null) mContent = new Content();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Address address = new Address(CONTENT_CONTRACT_REGISTER_RINKEBY_ADDRESS);
                    BoundContract contract = Geth.bindContract(address, CONTENT_CONTRACT_REGISTER_ABI, mEthereumClient);

                    CallOpts callOpts = Geth.newCallOpts();
                    callOpts.setContext(mContext);

                    //Check num of Registered Contracts
                    Interfaces paramsNumRegisteredCallData = Geth.newInterfaces(0);
                    Interfaces paramsNumRegisteredReturnData = Geth.newInterfaces(1);
                    Interface paramNumRegisteredReturnParameter = Geth.newInterface();
                    paramNumRegisteredReturnParameter.setDefaultBigInt();
                    paramsNumRegisteredReturnData.set(0,paramNumRegisteredReturnParameter);

                    contract.call(callOpts, paramsNumRegisteredReturnData, "numRegistered", paramsNumRegisteredCallData);
                    long numRegisteredResponse = paramsNumRegisteredReturnData.get(0).getBigInt().getInt64();

                    //Load in registered ContentContracts
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

                    mContent.clearContractItems();
                    mContent.addContractItem(new Content.ContentContract.ContentContractItem("slush-pile", "Anyone can post here!", 0, "n/a"));
                    for (int i = (int)(numRegisteredResponse - 1); i >= 0 && i > numRegisteredResponse - 10; i--) {
                        Interface paramFetchCallParameter = Geth.newInterface();
                        paramFetchCallParameter.setBigInt(new BigInt(i));
                        paramsFetchRegisteredContractsCallData.set(0, paramFetchCallParameter);
                        contract.call(callOpts, paramsFetchRegisteredContractsReturnData, "localContentContracts", paramsFetchRegisteredContractsCallData);
                        final String name = paramsFetchRegisteredContractsReturnData.get(0).getString();
                        final String description = paramsFetchRegisteredContractsReturnData.get(1).getString();
                        final long num = paramsFetchRegisteredContractsReturnData.get(2).getBigInt().getInt64();
                        final String admin = paramsFetchRegisteredContractsReturnData.get(3).getAddress().getHex();
                        mContent.addContractItem(new Content.ContentContract.ContentContractItem(name, description, num, admin));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyContentContractRecyclerViewAdapter = new MyContentContractRecyclerViewAdapter(mContent.CONTRACT_ITEMS, MainActivity.this);
                            mContentContractListFragment.setAdapter(mMyContentContractRecyclerViewAdapter);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void fetchFromSelectedContract() {
        if (mContent == null) mContent = new Content();
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Address address = new Address(CONTENT_CONTRACT_REGISTER_RINKEBY_ADDRESS);
                    BoundContract contract = Geth.bindContract(address, CONTENT_CONTRACT_REGISTER_ABI, mEthereumClient);

                    CallOpts callOpts = Geth.newCallOpts();
                    callOpts.setContext(mContext);

                    Interfaces paramsContentSizeCallData = Geth.newInterfaces(1);
                    Interfaces paramsContentSizeReturnData = Geth.newInterfaces(1); //return size
                    Interface paramContentSizeReturnParameter = Geth.newInterface();
                    paramContentSizeReturnParameter.setDefaultBigInt();
                    paramsContentSizeReturnData.set(0, paramContentSizeReturnParameter);
                    Interface paramContentSizeCallParameter = Geth.newInterface();
                    paramContentSizeCallParameter.setString(getSharedPreferences(sharedPreferencesName, 0).getString("selected", ""));
                    paramsContentSizeCallData.set(0, paramContentSizeCallParameter);

                    contract.call(callOpts, paramsContentSizeReturnData, "getLocalContentListSize", paramsContentSizeCallData);
                    long numContent = paramsContentSizeReturnData.get(0).getBigInt().getInt64();

                    Interfaces paramsFetchReturnData = Geth.newInterfaces(1);
                    Interface paramFetchReturnParameter = Geth.newInterface();
                    paramFetchReturnParameter.setDefaultString();
                    paramsFetchReturnData.set(0, paramFetchReturnParameter);
                    Interfaces paramsFetchCallData = Geth.newInterfaces(2);
                    Interface nameParameter = Geth.newInterface();
                    nameParameter.setString(getSharedPreferences(sharedPreferencesName, 0).getString("selected", ""));
                    paramsFetchCallData.set(0, nameParameter);
                    mContent.clearItems();
                    for (int i = (int)(numContent - 1); i >= 0 && i > numContent - 10; i--) {
                        Interface paramFetchCallParameter = Geth.newInterface();
                        paramFetchCallParameter.setBigInt(new BigInt(i));
                        paramsFetchCallData.set(1, paramFetchCallParameter);
                        contract.call(callOpts, paramsFetchReturnData, "getLocalContent", paramsFetchCallData);
                        final String response = paramsFetchReturnData.get(0).getString();
                        final String content = new IPFS().getGet().cat(response);
                        mContent.addContentItem(new Content.ContentItem(i + "", content));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyContentItemAdapter = new MyContentItemRecyclerViewAdapter(mContent.ITEMS, MainActivity.this);
                            mContentListFragment.setAdapter(mMyContentItemAdapter);
                        }
                    });
                    mLoadedSlush = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyContentItemAdapter = new MyContentItemRecyclerViewAdapter(mContent.ITEMS, MainActivity.this);
                            mContentListFragment.setAdapter(mMyContentItemAdapter);
                        }
                    });
                    mLoadedSlush = true;
                }
            }
        }).start();
    }


    public void fetchFromPile() {
        if (mContent == null) mContent = new Content();
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Address address = new Address(SLUSH_PILE_RINKEBY_ADDRESS);
                    BoundContract contract = Geth.bindContract(address, SLUSH_PILE_ABI, mEthereumClient);

                    CallOpts callOpts = Geth.newCallOpts();
                    callOpts.setContext(mContext);

                    Interfaces paramsPileSizeCallData = Geth.newInterfaces(0);
                    Interfaces paramsPileSizeReturnData = Geth.newInterfaces(1); //return size
                    Interface paramPileSizeReturnParameter = Geth.newInterface();
                    paramPileSizeReturnParameter.setDefaultBigInt();
                    paramsPileSizeReturnData.set(0, paramPileSizeReturnParameter);

                    contract.call(callOpts, paramsPileSizeReturnData, "pileSize", paramsPileSizeCallData);
                    long response2 = paramsPileSizeReturnData.get(0).getBigInt().getInt64();

                    Interfaces paramsFetchReturnData = Geth.newInterfaces(1);
                    Interface paramFetchReturnParameter = Geth.newInterface();
                    paramFetchReturnParameter.setDefaultString();
                    paramsFetchReturnData.set(0, paramFetchReturnParameter);
                    Interfaces paramsFetchCallData = Geth.newInterfaces(1);
                    mContent.clearItems();
                    for (int i = (int)(response2 - 1); i >= 0 && i > response2 - 10; i--) {
                        Interface paramFetchCallParameter = Geth.newInterface();
                        paramFetchCallParameter.setBigInt(new BigInt(i));
                        paramsFetchCallData.set(0, paramFetchCallParameter);
                        contract.call(callOpts, paramsFetchReturnData, "fetchFromPile", paramsFetchCallData);
                        final String response = paramsFetchReturnData.get(0).getString();
                        final String content = new IPFS().getGet().cat(response);
                        mContent.addContentItem(new Content.ContentItem(i + "", content));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyContentItemAdapter = new MyContentItemRecyclerViewAdapter(mContent.ITEMS, MainActivity.this);
                            mContentListFragment.setAdapter(mMyContentItemAdapter);
                        }
                    });
                    mLoadedSlush = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyContentItemAdapter = new MyContentItemRecyclerViewAdapter(mContent.ITEMS, MainActivity.this);
                            mContentListFragment.setAdapter(mMyContentItemAdapter);
                        }
                    });
                    mLoadedSlush = true;
                }
            }
        }).start();
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

    public void closeNetworkSynch(View view) {
        mNetworkSynchView.setVisibility(View.GONE);
    }

    public void openNetworkSynch(View view) {
        closeAccountPage(null);
        mNetworkSynchView.setVisibility(View.VISIBLE);
    }

    public void openAccountInfo(View view) {
        closeNetworkSynch(null);
        mAccountPageView.setVisibility(View.VISIBLE);
        mAccountListTextView.setText("");
//        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        int numAccounts = sp.getInt("NUM_ACCOUNTS", 0);
        long numAccounts = mKeyStore.getAccounts().size();
        if (numAccounts > 0) {
            mAccounts = new ArrayList<>();
            for (int i = 0; i < numAccounts; i++) {
//                byte[] jsonAcc = sp.getString("account" + i, "").getBytes();
                try {
                    Account account = mKeyStore.getAccounts().get(i);
//                    Account account = mKeyStore.importKey(jsonAcc, "Export", "Import");
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

    public void closeAccountPage(View view) {
        mAccountPageView.setVisibility(View.GONE);
    }

    public void createAccount(View view) {

        // custom dialog
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
//                    final byte[] jsonAcc = mKeyStore.exportKey(newAcc, text.getText().toString(), text.getText().toString());
//                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//                    int numAccounts = pref.getInt("NUM_ACCOUNTS", 0);
//                    pref.edit().putString("account" + numAccounts, new String(jsonAcc, "UTF-8")).commit();
//                    pref.edit().putInt("NUM_ACCOUNTS", ++numAccounts).commit();
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

    public void postToSlushPile(View view) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_post_to_slush);
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
                        final String multihash = new IPFS().getAdd().string(message).getHash();
                        Address address = new Address(SLUSH_PILE_RINKEBY_ADDRESS);
                        try {
                            BoundContract contract = Geth.bindContract(address, SLUSH_PILE_ABI, mEthereumClient);
                            TransactOpts tOpts = new TransactOpts();
                            tOpts.setContext(mContext);
                            tOpts.setFrom(mAccounts.get(0).getAddress());
                            tOpts.setSigner(new Signer() {
                                @Override
                                public Transaction sign(Address address, Transaction transaction) throws Exception {
//                                    Account account = mKeyStore.importKey(getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString("account" + 0, "").getBytes(), password.getText().toString(), password.getText().toString());
                                    Account account = mKeyStore.getAccounts().get(0);
                                    mKeyStore.unlock(account, password.getText().toString());
                                    Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                                    mKeyStore.lock(account.getAddress());
                                    return signed;
                                }
                            });
                            tOpts.setValue(new BigInt(0));
                            long noncePending  = mEthereumClient.getPendingNonceAt(mContext, mAccounts.get(0).getAddress());
                            long nonce = mEthereumClient.getNonceAt(mContext, mAccounts.get(0).getAddress(), 0);
                            tOpts.setNonce(Math.max(nonce, noncePending));
                            Interfaces params = Geth.newInterfaces(1);
                            Interface param = Geth.newInterface();
                            param.setString(multihash);
                            params.set(0, param);

                            Transaction tx = contract.transact(tOpts, "addToPile", params);
                            mEthereumClient.sendTransaction(mContext, tx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    public void onListFragmentInteraction(Content.ContentItem item) {

    }

    @Override
    public void onListFragmentInteraction(Content.ContentContract.ContentContractItem item) {
        SharedPreferences sp = getSharedPreferences(sharedPreferencesName, 0);
        sp.edit().putString("selected", item.name).commit();
        fetchFromContentContractRegister();
    }
}
