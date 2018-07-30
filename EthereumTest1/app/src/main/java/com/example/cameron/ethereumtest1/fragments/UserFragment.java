package com.example.cameron.ethereumtest1.fragments;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.adapters.UserFragmentContentItemRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.adapters.UserFragmentPagerAdapter;
import com.example.cameron.ethereumtest1.database.DBUserContentItem;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.ethereum.EthereumConstants;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.model.UserFragmentContentItem;
import com.example.cameron.ethereumtest1.util.DataUtils;
import com.example.cameron.ethereumtest1.util.PrefUtils;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_ACCOUNT_BALANCE;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_ACCOUNT_USER_INFO;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_USER_CONTENT_LIST;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_ADDRESS_STRING;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_PASSWORD;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_USER_CONTENT_INDEX;

public class UserFragment extends Fragment {

    private final static String TAG = UserFragment.class.getName();

    private ImageView mUserIconImageView;
    private TextView mUsernameTextView;
    private TextView mEthAddressTextView;
    private TextView mEthBalanceTextView;
    private Button mRegisterButton;
    private ViewPager mViewPager;

    private String mSelectedAddress;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EthereumClientService.UI_UPDATE_ACCOUNT_USER_INFO:
                    String userName = intent.getStringExtra(EthereumClientService.PARAM_USER_NAME);
                    String userIconImageUrl = intent.getStringExtra(EthereumClientService.PARAM_USER_ICON_URL);
                    if (userName.equals(getString(R.string.user_name_not_registered))) {
                        mRegisterButton.setVisibility(View.VISIBLE);
                    } else {
                        mRegisterButton.setVisibility(View.GONE);
                    }
                    mUsernameTextView.setText(userName);
                    if (! userIconImageUrl.equals("meta")){
                        mUserIconImageView.setVisibility(View.VISIBLE);
                        //Loading image from url into imageView
                        Glide.with(UserFragment.this)
                                .load(EthereumConstants.IPFS_GATEWAY_URL + userIconImageUrl)
                                .into(mUserIconImageView);
                        PrefUtils.saveSelectedAccountUserIconImageURLContext(getActivity(), userIconImageUrl);
                    } else {
                        mUserIconImageView.setVisibility(View.GONE);
                    }

                    break;
                case EthereumClientService.UI_UPDATE_ACCOUNT_BALANCE:
                    String accountBalance = intent.getStringExtra(EthereumClientService.PARAM_BALANCE_WEI_STRING);
                    mEthBalanceTextView.setText(DataUtils.formatAccountBalanceEther(accountBalance, 6));
                    break;
                case EthereumClientService.UI_UPDATE_USER_CONTENT_LIST:
                    reloadUserContentList();
                    break;
                case EthereumClientService.UI_REGISTER_USER_PENDING_CONFIRMATION:
                    String username = intent.getStringExtra(EthereumClientService.PARAM_USER_NAME);
                    mRegisterButton.setVisibility(View.GONE);
                    mUsernameTextView.setText(username + " (pending confirmation)");
                    break;
                case EthereumClientService.UI_PUBLISH_USER_CONTENT_TO_PUBLICATION_PENDING_CONFIRMATION:
                    Toast.makeText(getContext(), "published to feed", Toast.LENGTH_SHORT).show();
                    break;
                case EthereumClientService.UI_UPDATE_USER_PIC_PENDING_CONFIRMATION:
                    Toast.makeText(getContext(), "user pic updated!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public UserFragment() {}

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EthereumClientService.UI_UPDATE_ACCOUNT_BALANCE);
        filter.addAction(EthereumClientService.UI_UPDATE_ACCOUNT_USER_INFO);
        filter.addAction(EthereumClientService.UI_UPDATE_USER_CONTENT_LIST);
        filter.addAction(EthereumClientService.UI_REGISTER_USER_PENDING_CONFIRMATION);
        filter.addAction(EthereumClientService.UI_PUBLISH_USER_CONTENT_TO_PUBLICATION_PENDING_CONFIRMATION);
        filter.addAction(EthereumClientService.UI_UPDATE_USER_PIC_PENDING_CONFIRMATION);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
        bm.registerReceiver(mBroadcastReceiver, filter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        mUserIconImageView = (ImageView)v.findViewById(R.id.userIcon);
        mUsernameTextView = (TextView)v.findViewById(R.id.userName);
        mEthAddressTextView = (TextView)v.findViewById(R.id.ethAddress);
        mEthBalanceTextView = (TextView)v.findViewById(R.id.ethBalance);
        mRegisterButton = (Button) v.findViewById(R.id.registerButton);
        mViewPager = (ViewPager) v.findViewById(R.id.pager);


        reloadUserInfo();
        mViewPager.setAdapter(buildAdapter());
        return v;
    }

    private PagerAdapter buildAdapter() {
        return new UserFragmentPagerAdapter(getChildFragmentManager(), mSelectedAddress);
    }

    public void reloadUserInfo() {
        if (isAdded()) {
            loadSelectedAccount();
            loadAccountBalance();
            loadAccountUserInfo();
            loadContentList();
        }
    }

    private void loadSelectedAccount() {
        mSelectedAddress = PrefUtils.getSelectedAccountAddress(getContext());
        mEthAddressTextView.setText(mSelectedAddress);
    }

    private void loadAccountBalance() {
        String accountBalance = PrefUtils.getSelectedAccountBalance(getActivity());
        mEthBalanceTextView.setText(DataUtils.formatAccountBalanceEther(accountBalance, 6));
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

    private void loadAccountUserInfo() {
        String userName = PrefUtils.getSelectedAccountUserName(getActivity());
        String userIconImageUrl = PrefUtils.getSelectedAccountUserIconImageUrl(getActivity());
        mUsernameTextView.setText(userName);
        if (!userIconImageUrl.equals("meta")){
            mUserIconImageView.setVisibility(View.VISIBLE);
            //Loading image from url into imageView
            Glide.with(UserFragment.this)
                    .load(EthereumConstants.IPFS_GATEWAY_URL + userIconImageUrl)
                    .into(mUserIconImageView);
        } else {
            mUserIconImageView.setVisibility(View.GONE);
        }
        if (PrefUtils.shouldUpdateAccountUserName(getActivity())) {
            try {
                getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                        .putExtra(PARAM_ADDRESS_STRING, mSelectedAddress).setAction(ETH_FETCH_ACCOUNT_USER_INFO));
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

    private void loadContentList() {

    }

    private void reloadUserContentList() {
//        mRecyclerView.setAdapter(new UserFragmentContentItemRecyclerViewAdapter(getContext(), new DatabaseHelper(getContext()).getUserContentCursor(mSelectedAddress, 10), mListInteractionListener));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
        bm.unregisterReceiver(mBroadcastReceiver);
    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position, DBUserContentItem item);
    }
}