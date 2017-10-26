package com.example.cameron.ethereumtest1.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.activities.MainActivity;
import com.example.cameron.ethereumtest1.adapters.MyContentItemRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.adapters.UserFragmentContentItemListAdapter;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.model.Content;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.model.UserFragmentContentItem;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import org.ethereum.geth.KeyStore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_ACCOUNT_BALANCE;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_ACCOUNT_USER_NAME;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_USER_CONTENT_LIST;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_PUBLISH_USER_CONTENT;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_ADDRESS_STRING;

public class UserFragment extends Fragment {

    private final static String TAG = UserFragment.class.getName();
    private OnFragmentInteractionListener mListener;
    private OnListFragmentInteractionListener mListInteractionListener;

    private TextView mUsernameTextView;
    private TextView mSelectedAccountTextView;
    private TextView mEthBalanceTextView;
    private RecyclerView mRecyclerView;

    private KeyStore mKeyStore;
    private long mSelectedAccount;
    private String mSelectedAddress;
    private List<UserFragmentContentItem> mContentItems;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EthereumClientService.UI_UPDATE_ACCOUNT_USER_NAME:
                    String userName = intent.getStringExtra(EthereumClientService.PARAM_USER_NAME);
                    mUsernameTextView.setText(userName);
                    break;
                case EthereumClientService.UI_UPDATE_ACCOUNT_BALANCE:
                    long accountBalance = intent.getLongExtra(EthereumClientService.PARAM_BALANCE_LONG, 0);
                    mEthBalanceTextView.setText(accountBalance + " WEI");
                    break;
                case EthereumClientService.UI_UPDATE_USER_CONTENT_LIST:
                    String contentString = intent.getStringExtra(EthereumClientService.PARAM_CONTENT_STRING);
                    reloadUserContentList(contentString);
                    break;
            }
        }
    };

    public UserFragment() {}

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EthereumClientService.UI_UPDATE_ACCOUNT_BALANCE);
        filter.addAction(EthereumClientService.UI_UPDATE_ACCOUNT_USER_NAME);
        filter.addAction(EthereumClientService.UI_UPDATE_USER_CONTENT_LIST);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
        bm.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        mUsernameTextView = (TextView)v.findViewById(R.id.userName);
        mSelectedAccountTextView = (TextView)v.findViewById(R.id.selectedAccount);
        mEthBalanceTextView = (TextView)v.findViewById(R.id.ethBalance);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.userFragmentContentItemList);

        loadSelectedAccount();
        loadAccountBalance();
        loadAccountUserName();
        loadContentList();

        return v;
    }

    private void loadContentList() {
        mContentItems = new DatabaseHelper(getContext()).getUserFragmentContentItems(mSelectedAddress, 0, 1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new UserFragmentContentItemListAdapter(mContentItems, mListInteractionListener));

        if ((PrefUtils.shouldUpdateAccountContentList(getActivity()))) {
            try {
                getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                        .putExtra(PARAM_ADDRESS_STRING, mKeyStore.getAccounts().get(mSelectedAccount)
                                .getAddress().getHex()).setAction(ETH_FETCH_USER_CONTENT_LIST));
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
            }
        }
    }



    private void reloadUserContentList(String contentString) {
        String publishedBy = mSelectedAddress;
//        String contentTypeDictionaryAddress = "empty";
//        String contentType = "empty";
        long publishedDate = System.currentTimeMillis();
        String primaryImageUrl = "empty";
        String primaryHttpLink = "empty";
        String primaryContentAddressedLink = "empty";
        String primaryText = "empty";
        ContentItem ci = new ContentItem(publishedBy, contentString,
                publishedDate, primaryText, primaryImageUrl, primaryHttpLink, primaryContentAddressedLink);


        mContentItems.add(new UserFragmentContentItem(0, ci, "", "", ""));
        mRecyclerView.setAdapter(new UserFragmentContentItemListAdapter(mContentItems, mListInteractionListener));
    }

    private void loadSelectedAccount() {
        mKeyStore = ((MainActivity)getActivity()).getKeyStore();
        long numAccounts = mKeyStore.getAccounts().size();
        if (numAccounts > 0) {
            mSelectedAccount = PrefUtils.getSelectedAccount(getActivity());
        }
        try {
            mSelectedAddress = mKeyStore.getAccounts().get(mSelectedAccount).getAddress().getHex();
            mSelectedAccountTextView.setText(mSelectedAddress);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving account: " + e.getMessage());
        }
    }

    private void loadAccountUserName() {
        String userName = PrefUtils.getSelectedAccountUserName(getActivity());
        mUsernameTextView.setText(userName);
        if (PrefUtils.shouldUpdateAccountUserName(getActivity())) {
            try {
                getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                        .putExtra(PARAM_ADDRESS_STRING, mSelectedAddress).setAction(ETH_FETCH_ACCOUNT_USER_NAME));
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
            }
        }
    }

    private void loadAccountBalance() {
        long accountBalance = PrefUtils.getSelectedAccountBalance(getActivity());
        mEthBalanceTextView.setText(accountBalance + " WEI");
        if (PrefUtils.shouldUpdateAccountBalance(getActivity())) {
            try {
                Intent intent = new Intent(getContext(), EthereumClientService.class);
                intent.putExtra(PARAM_ADDRESS_STRING, mSelectedAddress);
                intent.setAction(ETH_FETCH_ACCOUNT_BALANCE);
                getActivity().startService(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
        bm.unregisterReceiver(mBroadcastReceiver);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ContentItem item);
    }
}

//        if (numAccounts > 0) {
//            mAccounts = new ArrayList<>();
//            for (int i = 0; i < numAccounts; i++) {
//                try {
//                    Account account = mKeyStore.getAccounts().get(i);
//                    mAccounts.add(account);
//                    String accountString = account.getAddress().getHex();
//                    mAccountListTextView.append("Account: " + accountString + "\n");
//                    if (i == 0) {
//                        mAccountTextView.setText(accountString.substring(0, 4) + "..." + accountString.substring(accountString.length() - 4, accountString.length()));
//                    }
//
//
//                    BigInt balance = new BigInt(0);//mEthereumClient.getBalanceAt(mContext, account.getAddress(), -1);
//                    mAccountListTextView.append("Balance: " + balance + " Wei" + "\n\n");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            mAccountListTextView.setText("no accounts yet...");
//        }
