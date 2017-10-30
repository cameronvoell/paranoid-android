package com.example.cameron.ethereumtest1.fragments;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.cameron.ethereumtest1.util.DataUtils;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import com.google.gson.Gson;

import org.ethereum.geth.Account;
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
    private TextView mEthAddressTextView;
    private TextView mEthBalanceTextView;
    private RecyclerView mRecyclerView;
    private Spinner mAccountSelectionSpinner;
    private Button mRegisterButton;

    private KeyStore mKeyStore;
    private long mSelectedAccountNum;
    private long mNumAccounts;
    private String mSelectedAddress;
    private List<UserFragmentContentItem> mContentItems;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EthereumClientService.UI_UPDATE_ACCOUNT_USER_NAME:
                    String userName = intent.getStringExtra(EthereumClientService.PARAM_USER_NAME);
                    if (userName.equals(getString(R.string.user_name_not_registered))) {
                        mRegisterButton.setVisibility(View.VISIBLE);
                    } else {
                        mRegisterButton.setVisibility(View.GONE);
                    }
                    mUsernameTextView.setText(userName);
                    break;
                case EthereumClientService.UI_UPDATE_ACCOUNT_BALANCE:
                    long accountBalance = intent.getLongExtra(EthereumClientService.PARAM_BALANCE_LONG, 0);
                    mEthBalanceTextView.setText(accountBalance + " WEI");
                    break;
                case EthereumClientService.UI_UPDATE_USER_CONTENT_LIST:
                    ArrayList<String> jsonArray = intent.getStringArrayListExtra(EthereumClientService.PARAM_ARRAY_CONTENT_STRING);
                    reloadUserContentList(jsonArray);
                    break;
                case EthereumClientService.UI_REGISTER_USER_PENDING_CONFIRMATION:
                    String username = intent.getStringExtra(EthereumClientService.PARAM_USER_NAME);
                    mRegisterButton.setVisibility(View.GONE);
                    mUsernameTextView.setText(username + " (pending confirmation)");
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
        filter.addAction(EthereumClientService.UI_REGISTER_USER_PENDING_CONFIRMATION);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
        bm.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        mUsernameTextView = (TextView)v.findViewById(R.id.userName);
        mEthAddressTextView = (TextView)v.findViewById(R.id.ethAddress);
        mEthBalanceTextView = (TextView)v.findViewById(R.id.ethBalance);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.userFragmentContentItemList);
        mAccountSelectionSpinner = (Spinner) v.findViewById(R.id.accountSelectionSpinner);
        mRegisterButton = (Button) v.findViewById(R.id.registerButton);

        refreshKeyStore();
        reloadUserInfo();

        return v;
    }

    private void reloadUserInfo() {
        loadSelectedAccount();
        loadAccountBalance();
        loadAccountUserName();
        loadContentList();
    }

    private void loadSelectedAccount() {
        mSelectedAccountNum = PrefUtils.getSelectedAccountNum(getContext());
        mSelectedAddress = PrefUtils.getSelectedAccountAddress(getContext());
        mEthAddressTextView.setText(mSelectedAddress);
    }

    private void refreshKeyStore() {
        mKeyStore = ((MainActivity)getActivity()).getKeyStore();
        mNumAccounts = mKeyStore.getAccounts().size();
        ArrayList<String> accountArray = new ArrayList<>();
        if (mNumAccounts > 0) {
            try {
                for (int i = 0; i < mNumAccounts; i++) {
                    accountArray.add(DataUtils.formatEthereumAccount(mKeyStore.getAccounts().get(i).getAddress().getHex()));
                }
            } catch (Exception e) {

            }
        }
        accountArray.add("NEW ACCOUNT");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, accountArray);
        mAccountSelectionSpinner.setAdapter(spinnerArrayAdapter);
        mAccountSelectionSpinner.setOnItemSelectedListener(mSpinnerItemSelectedListener);
        //mAccountSelectionSpinner.setSelection((int)mSelectedAccountNum);
    }

    private void loadContentList() {
        //mContentItems = new DatabaseHelper(getContext()).getUserFragmentContentItems(mSelectedAddress, 0, 1);
        mContentItems = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new UserFragmentContentItemListAdapter(mContentItems, mListInteractionListener));

        if ((PrefUtils.shouldUpdateAccountContentList(getActivity()))) {
            try {
                getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                        .putExtra(PARAM_ADDRESS_STRING, mKeyStore.getAccounts().get(mSelectedAccountNum)
                                .getAddress().getHex()).setAction(ETH_FETCH_USER_CONTENT_LIST));
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
            }
        }
    }

    private ContentItem convertJsonToContentItem(String json) {
        Gson gson = new Gson();
        ContentItem contentItem = gson.fromJson(json, ContentItem.class);
        return contentItem;
    }

    private void reloadUserContentList(ArrayList<String> jsonArray) {
        for (String json: jsonArray) {
            ContentItem ci = convertJsonToContentItem(json);
            mContentItems.add(new UserFragmentContentItem(0, ci, "", "", ""));
        }
        mRecyclerView.setAdapter(new UserFragmentContentItemListAdapter(mContentItems, mListInteractionListener));
    }



    private AdapterView.OnItemSelectedListener mSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == mSelectedAccountNum) {
                //do Nothing
            } else if (position == mNumAccounts) {
                createAccount();
            } else {
                try {
                    mSelectedAddress = mKeyStore.getAccounts().get(position).getAddress().getHex();
                } catch (Exception e) {
                    Log.e(TAG, "Error retrieving account: " + e.getMessage());;
                }
                mSelectedAccountNum = position;
                PrefUtils.saveSelectedAccount(getContext(), mSelectedAccountNum, mSelectedAddress);
                reloadUserInfo();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //mSelectedAccountNum = 0;
        }
    };

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
        if (userName.equals(getString(R.string.user_name_not_registered))) {
            mRegisterButton.setVisibility(View.VISIBLE);
        } else {
            mRegisterButton.setVisibility(View.GONE);
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

    /*
     * Methods for Managing Ethereum Accounts
     */
    public void createAccount() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_new_account);
        dialog.setTitle("Enter New Account Password");

        final EditText text = (EditText) dialog.findViewById(R.id.editMessage);
        text.setHint("account password");
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDone);
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    Account newAcc = mKeyStore.newAccount(text.getText().toString());
                    refreshKeyStore();
                    //reloadUserInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}