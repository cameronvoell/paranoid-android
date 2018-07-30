package com.example.cameron.ethereumtest1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.adapters.PublicationsRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;

public class UserPublicationsRecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private String mSelectedAddress;
    private UserFragment.OnListFragmentInteractionListener mListInteractionListener;

    public static UserPublicationsRecyclerViewFragment newInstance(String selectedAddress) {
        UserPublicationsRecyclerViewFragment fragment = new UserPublicationsRecyclerViewFragment();
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
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_content_recyclerview, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userFragmentContentItemList);
        reloadPublicationsDB();
        return v;
    }

    private void reloadPublicationsDB() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new PublicationsRecyclerViewAdapter(getContext(), new DatabaseHelper(getContext()).getPublicationsWeCanPublishToCursor(mSelectedAddress)));
    }
}
