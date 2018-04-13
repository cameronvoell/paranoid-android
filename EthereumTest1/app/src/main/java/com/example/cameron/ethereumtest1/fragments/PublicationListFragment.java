package com.example.cameron.ethereumtest1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.model.Content;

/**
 * A fragment representing a list of Items.
 */
public class PublicationListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PublicationListFragment() {
    }

    @SuppressWarnings("unused")
    public static PublicationListFragment newInstance() {
        PublicationListFragment fragment = new PublicationListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_contract_list, container, false);

        return view;
    }
}
