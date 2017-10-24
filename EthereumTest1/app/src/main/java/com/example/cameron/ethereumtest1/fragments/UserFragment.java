package com.example.cameron.ethereumtest1.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.activities.MainActivity;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import org.ethereum.geth.KeyStore;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_ACCOUNT_BALANCE;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_ACCOUNT_USER_NAME;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_ADDRESS_STRING;

public class UserFragment extends Fragment {

    private final static String TAG = UserFragment.class.getName();
    private OnFragmentInteractionListener mListener;
    private TextView mUsernameTextView;
    private TextView mSelectedAccountTextView;
    private TextView mEthBalanceTextView;
    private KeyStore mKeyStore;
    private long mSelectedAccount;

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

        mKeyStore = ((MainActivity)getActivity()).getKeyStore();
        long numAccounts = mKeyStore.getAccounts().size();
        if (numAccounts > 0) {
            mSelectedAccount = PrefUtils.getSelectedAccount(getActivity());
        }
        try {
            mSelectedAccountTextView.setText(mKeyStore.getAccounts().get(mSelectedAccount).getAddress().getHex());
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving account: " + e.getMessage());
        }

        long accountBalance = PrefUtils.getSelectedAccountBalance(getActivity());
        mEthBalanceTextView.setText(accountBalance + " WEI");
        if (PrefUtils.shouldUpdateAccountBalance(getActivity())) {
            try {
                Intent intent = new Intent(getContext(), EthereumClientService.class);
                intent.putExtra(PARAM_ADDRESS_STRING, mKeyStore.getAccounts().get(mSelectedAccount).getAddress().getHex());
                intent.setAction(ETH_FETCH_ACCOUNT_BALANCE);
                getActivity().startService(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
            }
        }

        String userName = PrefUtils.getSelectedAccountUserName(getActivity());
        mUsernameTextView.setText(userName);
        if (PrefUtils.shouldUpdateAccountUserName(getActivity())) {
            try {
                getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                        .putExtra(PARAM_ADDRESS_STRING, mKeyStore.getAccounts().get(mSelectedAccount)
                                .getAddress().getHex()).setAction(ETH_FETCH_ACCOUNT_USER_NAME));
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
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

        return v;
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
}
