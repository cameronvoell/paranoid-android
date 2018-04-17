package com.example.cameron.ethereumtest1.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.fragments.PublicationListFragment;
import com.example.cameron.ethereumtest1.model.Content;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.fragments.ContentListFragment;
import com.example.cameron.ethereumtest1.fragments.UserFragment;
import com.example.cameron.ethereumtest1.ipfs_daemon.IPFSDaemon;
import com.example.cameron.ethereumtest1.ipfs_daemon.IPFSDaemonService;
import com.example.cameron.ethereumtest1.util.DataUtils;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import com.google.gson.Gson;
import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import java.util.ArrayList;
import io.ipfs.kotlin.IPFS;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import static com.example.cameron.ethereumtest1.ethereum.EthereumConstants.KEY_STORE;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_REGISTER_USER;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_UPDATE_USER_PIC;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_PASSWORD;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_USER_IMAGE_PATH;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_USER_NAME;
import static com.example.cameron.ethereumtest1.util.PrefUtils.SELECTED_CONTENT_LIST;
import static com.example.cameron.ethereumtest1.util.PrefUtils.SELECTED_PUBLICATION_LIST;
import static com.example.cameron.ethereumtest1.util.PrefUtils.SELECTED_USER_FRAGMENT;

public class MainActivity extends AppCompatActivity implements
        ContentListFragment.OnListFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();

    private int PICK_IMAGE_REQUEST = 1;

    private TextView mSynchInfoTextView;
    private TextView mAccountTextView;
    private ImageButton mSwitchAccountButton;

    private ContentListFragment mContentListFragment;
    private UserFragment mUserFragment;
    private PublicationListFragment mContentContractListFragment;

    private ImageButton mContentListButton;
    private ImageButton mUserFragmentButton;
    private ImageButton mEthereumButton;
    private ImageButton mIPFSButton;

    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mFloatingActionButton1;
    private FloatingActionButton mFloatingActionButton2;
    private FloatingActionButton mFloatingActionButton3;

    View.OnClickListener mSwitchAccountOnClickListener;
    PopupMenu.OnMenuItemClickListener mSwitchAccountPopupListener;

    private KeyStore mKeyStore;

    private IPFSDaemon mIpfsDaemon = new IPFSDaemon(this);

    private boolean mIsFabOpen = false;
    private int mNumAccounts = 0;
    private long mSelectedAccount;


    // handler for received data from service
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            if (intent.getAction().equals(EthereumClientService.UI_UPDATE_ETH_BLOCK)) {
                final long blockNumber = intent.getLongExtra(EthereumClientService.PARAM_BLOCK_NUMBER, 0);
                mSynchInfoTextView.setText(DataUtils.formatBlockNumber(blockNumber));
                ObjectAnimator colorAnim = ObjectAnimator.ofInt(mSynchInfoTextView, "textColor",
                        Color.GREEN, Color.WHITE);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.setDuration(15000);
                colorAnim.start();
            }
        }
    };

    /*
     * Lifecycle Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSynchInfoTextView = (TextView) findViewById(R.id.synchInfo);
        mAccountTextView = (TextView) findViewById(R.id.accountInfo);
        mSwitchAccountButton = (ImageButton) findViewById(R.id.account_switch);

        mContentListButton = (ImageButton) findViewById(R.id.button_content_list);
        mUserFragmentButton = (ImageButton) findViewById(R.id.user_fragment_button);
        mEthereumButton = (ImageButton) findViewById(R.id.button_ethereum);
        mIPFSButton = (ImageButton) findViewById(R.id.button_ipfs);

        mContentListButton.setColorFilter(Color.WHITE);
        mUserFragmentButton.setColorFilter(Color.DKGRAY);
        mEthereumButton.setColorFilter(Color.DKGRAY);
        mIPFSButton.setColorFilter(Color.DKGRAY);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton1 = (FloatingActionButton) findViewById(R.id.fab1);
        mFloatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab2);
        mFloatingActionButton3 = (FloatingActionButton) findViewById(R.id.fab3);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EthereumClientService.UI_UPDATE_ETH_BLOCK);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);

        mKeyStore = new KeyStore(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + KEY_STORE, Geth.LightScryptN, Geth.LightScryptP);
        mNumAccounts = (int)mKeyStore.getAccounts().size();
        if (mNumAccounts > 0) {
            mSelectedAccount = PrefUtils.getSelectedAccountNum(getBaseContext());
        }
        try {
            String accountString = mKeyStore.getAccounts().get(mSelectedAccount).getAddress().getHex();
            mAccountTextView.setText(accountString.substring(0, 4) + "..." + accountString.substring(accountString.length() - 4, accountString.length()));
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving account" + e.getMessage());
        }

        refreshAccounts();
        mSwitchAccountButton.setOnClickListener(mSwitchAccountOnClickListener);

        startIPFSDaemon();
        startService(new Intent(MainActivity.this, EthereumClientService.class).setAction(EthereumClientService.START_ETHEREUM_SERVICE));

        int selectedFragment = PrefUtils.getSelectedFragment(getBaseContext());
        switch (selectedFragment) {
            case SELECTED_CONTENT_LIST:
                showContentList(null);
                break;
            case SELECTED_PUBLICATION_LIST:
                showEthereum(null);
                break;
            case SELECTED_USER_FRAGMENT:
                showUserFragment(null);
                break;
            default:
                Log.e("ERROR", "ERROR");
                break;
        }
    }

    private void refreshAccounts() {
        mSwitchAccountPopupListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("NEW ACCOUNT")) {
                    createAccount();
                } else {
                    String selectedAddress = "error";
                    try {
                        selectedAddress = mKeyStore.getAccounts().get(item.getItemId()).getAddress().getHex();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int selectedAccountNum = item.getItemId();
                    PrefUtils.saveSelectedAccount(MainActivity.this, selectedAccountNum, selectedAddress);
                    mAccountTextView.setText(DataUtils.formatEthereumAccount(selectedAddress));
                    if (mUserFragment != null) mUserFragment.reloadUserInfo();
                    Toast.makeText(MainActivity.this, "switch to " + item.getTitle(), Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        };

        mSwitchAccountOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, mSwitchAccountButton);
                if (mNumAccounts > 0) {
                    try {
                        for (int i = 0; i < mNumAccounts; i++) {
                            popupMenu.getMenu().add(i, i, i, DataUtils.formatEthereumAccount(mKeyStore.getAccounts().get(i).getAddress().getHex()));
                        }
                    } catch (Exception e) {

                    }
                }
                popupMenu.getMenu().add(mNumAccounts, mNumAccounts, mNumAccounts, "NEW ACCOUNT");
                popupMenu.setOnMenuItemClickListener(mSwitchAccountPopupListener);
                popupMenu.show();
            }
        };
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
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.unregisterReceiver(mBroadcastReceiver);
    }

    /*
     * Methods for managing IPFS Connectivity
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

    /*
     * Methods for Managing Ethereum Accounts
     */
    public void createAccount() {
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
                    mNumAccounts = (int)mKeyStore.getAccounts().size();
                    PrefUtils.saveSelectedAccount(MainActivity.this, mNumAccounts - 1, account);
                    mAccountTextView.setText(account.substring(0,4) + "..." + account.substring(account.length() -4,account.length() - 1));
                    refreshAccounts();
                    if (mUserFragment != null) mUserFragment.reloadUserInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onListFragmentInteraction(ContentItem item) {
        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

        }

        Intent intent = new Intent(this, ViewContentActivity.class);
        ArrayList<ContentItem> contentItems = new ArrayList<>();
        contentItems.add(item);
        intent.putParcelableArrayListExtra("content_items", contentItems);
        startActivity(intent);
    }

    public void previewPost(View view) {
        Toast.makeText(getApplicationContext(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
    }

    public void registerUser(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_register_user);
        dialog.setTitle("Register user");

        final EditText usernameEditText = (EditText) dialog.findViewById(R.id.editUsername);
        usernameEditText.setHint("desired username");
        final EditText pw = (EditText) dialog.findViewById(R.id.editPassword);
        pw.setHint("account password");
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDone);
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    startService(new Intent(MainActivity.this, EthereumClientService.class)
                            .putExtra(PARAM_USER_NAME, usernameEditText.getText().toString())
                            .putExtra(PARAM_PASSWORD, pw.getText().toString())
                            .setAction(ETH_REGISTER_USER));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void createNewContent(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(this, EditContentActivity.class);
                startActivity(intent);
        }
    }

    public void updateMetaData(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_upload_profile_pic);

        final EditText passwordEditText = (EditText) dialog.findViewById(R.id.editPassword);

        Button dialogUploadButton = (Button) dialog.findViewById(R.id.dialogUploadButton);
        dialogUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonSubmit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, EthereumClientService.class)
                        .putExtra(PARAM_USER_IMAGE_PATH, mUri)
                        .putExtra(PARAM_PASSWORD, passwordEditText.getText().toString())
                        .setAction(ETH_UPDATE_USER_PIC));
                dialog.dismiss();
                animateFabMenu(null);
            }
        });
        dialog.show();
    }

    /*
     * Code used for selecting a photo to upload
     */

    private String mUri;

    public void uploadPhoto(View v) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = getRealPathFromURI(data.getData());
            Toast.makeText(getApplicationContext(), "Upload this photo " + mUri, Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(contentURI);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /*
     * UI Response Methods
     */

    public void showContentList(View view) {
        if (mContentListFragment == null)
            mContentListFragment = ContentListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mContentListFragment);
        transaction.commit();
        mContentListButton.setColorFilter(Color.WHITE);
        mUserFragmentButton.setColorFilter(Color.DKGRAY);
        mEthereumButton.setColorFilter(Color.DKGRAY);
        mIPFSButton.setColorFilter(Color.DKGRAY);
        PrefUtils.saveSelectedFragment(getBaseContext(), SELECTED_CONTENT_LIST);
        showFAB(true);
    }

    public void showUserFragment(View view) {
        if (mUserFragment == null)
            mUserFragment = UserFragment.newInstance("","");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mUserFragment);
        transaction.commit();
        mContentListButton.setColorFilter(Color.DKGRAY);
        mUserFragmentButton.setColorFilter(Color.WHITE);
        mEthereumButton.setColorFilter(Color.DKGRAY);
        mIPFSButton.setColorFilter(Color.DKGRAY);
        PrefUtils.saveSelectedFragment(getBaseContext(), SELECTED_USER_FRAGMENT);
        showFAB(true);
    }

    public void showEthereum(View view) {
        if (mContentContractListFragment == null)
            mContentContractListFragment = PublicationListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mContentContractListFragment);
        transaction.commit();
        mContentListButton.setColorFilter(Color.DKGRAY);
        mUserFragmentButton.setColorFilter(Color.DKGRAY);
        mEthereumButton.setColorFilter(Color.WHITE);
        mIPFSButton.setColorFilter(Color.DKGRAY);
        PrefUtils.saveSelectedFragment(getBaseContext(), SELECTED_PUBLICATION_LIST);
        showFAB(true);
    }

    public void showIPFS(View view) {
        if (mContentContractListFragment == null)
            mContentContractListFragment = PublicationListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mContentContractListFragment);
        transaction.commit();
        mContentListButton.setColorFilter(Color.DKGRAY);
        mUserFragmentButton.setColorFilter(Color.DKGRAY);
        mEthereumButton.setColorFilter(Color.DKGRAY);
        mIPFSButton.setColorFilter(Color.WHITE);
        PrefUtils.saveSelectedFragment(getBaseContext(), SELECTED_PUBLICATION_LIST);
        showFAB(true);
    }

    public void scrollToTop(View view) {
        mContentListFragment.scrollToTop();
        showFAB(true);
    }

    public void showFAB(boolean shouldShow) {
        if (shouldShow) {
           mFloatingActionButton.show();
           mFloatingActionButton1.show();
           mFloatingActionButton2.show();
           mFloatingActionButton3.show();
        } else {
            mFloatingActionButton3.hide();
            mFloatingActionButton2.hide();
            mFloatingActionButton1.hide();
            mFloatingActionButton.hide();
        }
    }

    public void animateFabMenu(View v) {
        if (mIsFabOpen) {
            mIsFabOpen=false;
            mFloatingActionButton1.animate().translationY(0);
            mFloatingActionButton2.animate().translationY(0);
            mFloatingActionButton3.animate().translationY(0);
        } else {
            mIsFabOpen = true;
            mFloatingActionButton1.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
            mFloatingActionButton2.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
            mFloatingActionButton3.animate().translationY(-getResources().getDimension(R.dimen.standard_180));
        }
    }

}

