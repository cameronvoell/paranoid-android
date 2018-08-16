package com.example.cameron.ethereumtest1.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.activities.MainActivity;
import com.example.cameron.ethereumtest1.adapters.UserFragmentContentItemRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.database.DBPublication;
import com.example.cameron.ethereumtest1.database.DBUserContentItem;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.util.PrefUtils;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_USER_CONTENT_LIST;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_ADDRESS_STRING;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_PASSWORD;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_USER_CONTENT_INDEX;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_WHICH_PUBLICATION;

public class UserContentRecyclerViewFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private String mSelectedAddress;
    private UserFragment.OnListFragmentInteractionListener mListInteractionListener;


    public static UserContentRecyclerViewFragment newInstance(String selectedAddress) {

        UserContentRecyclerViewFragment fragment = new UserContentRecyclerViewFragment();
        Bundle b = new Bundle();
        b.putString("selected_address", selectedAddress);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mSelectedAddress = b.getString("selected_address");
        mListInteractionListener = new UserFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(final int position, final DBUserContentItem item) {
                final int postIndex = item.userContentIndex;

                final ArrayList<DBPublication> pubsToPublishTo = new ArrayList<>();
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_post_content_to_slush_pile);
                final TextView pubInstruction = (TextView)dialog.findViewById(R.id.publishInstruction);
                final Button selectPublicationButton = (Button) dialog.findViewById(R.id.selectPublicationButton);

                selectPublicationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getContext(), selectPublicationButton);
                        final ArrayList<DBPublication> pubs = new DatabaseHelper(getContext()).getPublicationsWeCanPublishTo(mSelectedAddress);
                        for (int i = 0; i < pubs.size(); i++) {
                            popupMenu.getMenu().add(i, i, i, pubs.get(i).name);
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DBPublication pub = pubs.get(item.getItemId());
                                pubInstruction.setText("publish to " + pub.name);
                                pubsToPublishTo.add(pub);
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });

                final EditText password = (EditText) dialog.findViewById(R.id.editPassword);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPost);
                dialogButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String s = password.getText().toString();
                        getActivity().startService(new Intent(getActivity(), EthereumClientService.class)
                                .putExtra(PARAM_WHICH_PUBLICATION, pubsToPublishTo.get(0).publicationID)
                                .putExtra(PARAM_USER_CONTENT_INDEX, postIndex)
                                .putExtra(PARAM_PASSWORD, s)
                                .setAction(ETH_PUBLISH_USER_CONTENT_TO_PUBLICATION));
                        dialog.dismiss();
                    }
                });
                Button deleteButton = (Button) dialog.findViewById(R.id.deleteButton);
                Button editButton = (Button) dialog.findViewById(R.id.editButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int itemIndex = item.userContentIndex;
                        String user = item.publishedByEthAddress;
                        new DatabaseHelper(getContext()).deleteUserItem(user, itemIndex);
                        dialog.dismiss();
                    }
                });
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getActivity()).editContent(item);
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
        View v = inflater.inflate(R.layout.fragment_user_content_recyclerview, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userFragmentContentItemList);
        loadContentList();
        return v;
    }

    private void loadContentList() {
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
}
