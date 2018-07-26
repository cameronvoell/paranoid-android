package com.example.cameron.ethereumtest1.fragments;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
    private OnListFragmentInteractionListener mListInteractionListener;

    private ImageView mUserIconImageView;
    private TextView mUsernameTextView;
    private TextView mEthAddressTextView;
    private TextView mEthBalanceTextView;
    private RecyclerView mRecyclerView;
    private Button mRegisterButton;

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
                    ArrayList<String> jsonArray = intent.getStringArrayListExtra(EthereumClientService.PARAM_ARRAY_CONTENT_STRING);
                    reloadUserContentList(jsonArray);
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

        mListInteractionListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(final int position, DBUserContentItem item) {
                final int postIndex = item.userContentIndex;
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_post_content_to_slush_pile);
                dialog.setTitle("Publish post index " + postIndex + " to your slush feed?");

                final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = password.getText().toString();
                        getActivity().startService(new Intent(getActivity(), EthereumClientService.class)
                                .putExtra(PARAM_USER_CONTENT_INDEX, postIndex)
                                .putExtra(PARAM_PASSWORD, s)
                                .setAction(ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION));
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        mUserIconImageView = (ImageView)v.findViewById(R.id.userIcon);
        mUsernameTextView = (TextView)v.findViewById(R.id.userName);
        mEthAddressTextView = (TextView)v.findViewById(R.id.ethAddress);
        mEthBalanceTextView = (TextView)v.findViewById(R.id.ethBalance);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.userFragmentContentItemList);
        mRegisterButton = (Button) v.findViewById(R.id.registerButton);

        reloadUserInfo();

        return v;
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
        //mContentItems = new DatabaseHelper(getContext()).getUserFragmentContentItems(mSelectedAddress, 0, 1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new UserFragmentContentItemRecyclerViewAdapter(getContext(), new DatabaseHelper(getContext()).getUserContentCursor(mSelectedAddress, 10), mListInteractionListener));

        if ((PrefUtils.shouldUpdateAccountContentList(getActivity()))) {
            try {
                getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                        .putExtra(PARAM_ADDRESS_STRING, mSelectedAddress).setAction(ETH_FETCH_USER_CONTENT_LIST));
            } catch (Exception e) {
                Log.e(TAG, "Error updating account balance: " + e.getMessage());
            }
        }
    }

    private ContentItem convertJsonToContentItem(String json) {
        Gson gson = new Gson();
        ContentItem contentItem;
        try {
            contentItem = gson.fromJson(json, ContentItem.class);
        } catch (Exception e) {
            contentItem = new ContentItem("", "", 0, "", "", 0);
        }
        return contentItem;
    }

    private void reloadUserContentList(ArrayList<String> jsonArray) {
        mRecyclerView.setAdapter(new UserFragmentContentItemRecyclerViewAdapter(getContext(), new DatabaseHelper(getContext()).getUserContentCursor(mSelectedAddress, 10), mListInteractionListener));
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