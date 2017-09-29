package com.example.cameron.ethereumtest1.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.util.EthereumConstants;

import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;
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
import org.ethereum.geth.Nonce;
import org.ethereum.geth.PeerInfo;
import org.ethereum.geth.PeerInfos;
import org.ethereum.geth.Signer;
import org.ethereum.geth.TransactOpts;
import org.ethereum.geth.Transaction;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.cameron.ethereumtest1.util.EthereumConstants.NO_SUITABLE_PEERS_ERROR;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.RINKEBY_NETWORK_ID;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.SLUSH_PILE_ABI;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.SLUSH_PILE_RINKEBY_ADDRESS;
import static com.example.cameron.ethereumtest1.util.EthereumConstants.getRinkebyGenesis;

public class MainActivity extends AppCompatActivity {

    private final static String KEY_STORE = "/geth_keystore3";
    private final static String SHARED_PREFS = "prefs3";

    private TextView mSynchInfoTextView;
    private TextView mFetchResponseTextView;
    private TextView mSynchLogTextView;
    private TextView mAccountTextView;
    private TextView mAccountListTextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSynchLogTextView = (TextView) findViewById(R.id.synchLog);
        mFetchResponseTextView = (TextView) findViewById(R.id.fetchResponse);
        mSynchInfoTextView = (TextView) findViewById(R.id.synchInfo);
        mAccountTextView = (TextView) findViewById(R.id.accountInfo);
        mAccountListTextView = (TextView) findViewById(R.id.accountList);

        mNetworkSynchView = (LinearLayout) findViewById(R.id.networkSynch);
        mAccountPageView = (RelativeLayout) findViewById(R.id.accountPage);

        mKeyStore = new KeyStore(getFilesDir() + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);

        mSynchLogTextView.append("Connecting to peers...");
        mContext = new Context();
        openAccountInfo(null);
        openNetworkSynch(null);

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

    public void fetchFromPile() {
        try {
            Address address = new Address(SLUSH_PILE_RINKEBY_ADDRESS);
            BoundContract contract = Geth.bindContract(address, SLUSH_PILE_ABI, mEthereumClient);

            CallOpts callOpts = Geth.newCallOpts();
            callOpts.setContext(mContext);

            Interfaces paramsPileSizaCallData = Geth.newInterfaces(0);
            Interfaces paramsPileSizeReturnData = Geth.newInterfaces(1); //return size
            Interface paramPileSizeReturnParameter = Geth.newInterface();
            paramPileSizeReturnParameter.setDefaultBigInt();
            paramsPileSizeReturnData.set(0, paramPileSizeReturnParameter);

            contract.call(callOpts, paramsPileSizeReturnData, "pileSize", paramsPileSizaCallData);
            long response2 = paramsPileSizeReturnData.get(0).getBigInt().getInt64();

            Interfaces paramsFetchReturnData = Geth.newInterfaces(1);
            Interface paramFetchReturnParameter = Geth.newInterface();
            paramFetchReturnParameter.setDefaultString();
            paramsFetchReturnData.set(0, paramFetchReturnParameter);
            Interfaces paramsFetchCallData = Geth.newInterfaces(1);
            for (int i = (int)(response2 - 1); i >= 0 && i > response2 - 10; i--) {
                Interface paramFetchCallParameter = Geth.newInterface();
                paramFetchCallParameter.setBigInt(new BigInt(i));
                paramsFetchCallData.set(0, paramFetchCallParameter);
                contract.call(callOpts, paramsFetchReturnData, "fetchFromPile", paramsFetchCallData);
                String response = paramsFetchReturnData.get(0).getString();
                mFetchResponseTextView.append(response + "\n");
            }
            //mFetchResponseTextView.append(response2 + "");
            mLoadedSlush = true;

        } catch (Exception e) {
            e.printStackTrace();
            mLoadedSlush = true;
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
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int numAccounts = sp.getInt("NUM_ACCOUNTS", 0);
        if (numAccounts > 0) {
            mAccounts = new ArrayList<>();
            for (int i = 0; i < numAccounts; i++) {
                byte[] jsonAcc = sp.getString("account" + i, "").getBytes();
                try {
                    Account account = mKeyStore.importKey(jsonAcc, "Export", "Import");
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
        try {
            Account newAcc = mKeyStore.newAccount("insecure");
            byte[] jsonAcc = mKeyStore.exportKey(newAcc, "insecure", "Export");
            SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            int numAccounts = pref.getInt("NUM_ACCOUNTS", 0);
            pref.edit().putString("account" + numAccounts, new String(jsonAcc, "UTF-8")).commit();
            pref.edit().putInt("NUM_ACCOUNTS", ++numAccounts).commit();
            String account = newAcc.getAddress().getHex();
            mAccountTextView.setText(account.substring(0,4) + "..." + account.substring(account.length() -5,account.length() - 1));
            openAccountInfo(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postToSlushPile(View view) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_post_to_slush);
        dialog.setTitle("Post Message");

        final EditText text = (EditText) dialog.findViewById(R.id.editMessage);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = text.getText().toString();
                Address address = new Address(SLUSH_PILE_RINKEBY_ADDRESS);
                try {
                    BoundContract contract = Geth.bindContract(address, SLUSH_PILE_ABI, mEthereumClient);
                    TransactOpts tOpts = new TransactOpts();
                    tOpts.setContext(mContext);
                    tOpts.setFrom(mAccounts.get(1).getAddress());
                    tOpts.setSigner(new Signer() {
                        @Override
                        public Transaction sign(Address address, Transaction transaction) throws Exception {
                            Account account = mKeyStore.importKey(getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString("account" + 1, "").getBytes(), "Export", "Import");
                            mKeyStore.unlock(account, "Import");
                            Transaction signed = mKeyStore.signTx(account, transaction, new BigInt(4));
                            mKeyStore.lock(account.getAddress());
                            return signed;
                        }
                    });
                    tOpts.setValue(new BigInt(0));
                    long noncePending  = mEthereumClient.getPendingNonceAt(mContext, mAccounts.get(1).getAddress());
                    long nonce = mEthereumClient.getNonceAt(mContext, mAccounts.get(1).getAddress(), 0);
                    tOpts.setNonce(Math.max(nonce, noncePending));
                    Interfaces params = Geth.newInterfaces(1);
                    Interface param = Geth.newInterface();
                    param.setString(message);
                    params.set(0, param);

                    Transaction tx = contract.transact(tOpts, "addToPile", params);
                    mEthereumClient.sendTransaction(mContext, tx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void reloadFeed(View view) {
        mFetchResponseTextView.setText("reloading...\n");
        fetchFromPile();
    }
}
