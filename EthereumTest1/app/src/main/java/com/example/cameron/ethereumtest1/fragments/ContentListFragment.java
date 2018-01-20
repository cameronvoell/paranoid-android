package com.example.cameron.ethereumtest1.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.adapters.MyContentItemRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.model.Content;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.google.gson.Gson;
import java.util.ArrayList;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.ETH_FETCH_PUBLICATION_CONTENT;
import static com.example.cameron.ethereumtest1.ethereum.EthereumClientService.PARAM_PUBLICATION_INDEX;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContentListFragment extends Fragment {

    private final static String TAG = UserFragment.class.getName();

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private Spinner mPublicationSpinner;
    private Spinner mTagSpinner;
    private Spinner mSortBySpinner;
    private ArrayList<ContentItem> mContentItems;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EthereumClientService.UI_UPDATE_PUBLICATION_CONTENT:
                    ArrayList<String> jsonArray = intent.getStringArrayListExtra(EthereumClientService.PARAM_ARRAY_CONTENT_STRING);
                    reloadContentList(jsonArray);
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContentListFragment() {
    }

    public static ContentListFragment newInstance() {
        ContentListFragment fragment = new ContentListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EthereumClientService.UI_UPDATE_PUBLICATION_CONTENT);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
        bm.registerReceiver(mBroadcastReceiver, filter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_item_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mPublicationSpinner = (Spinner) view.findViewById(R.id.publication);
        mTagSpinner = (Spinner) view.findViewById(R.id.tag);
        mSortBySpinner = (Spinner) view.findViewById(R.id.sortBy);

        setPublicationSpinnerOptions();
        setTagSpinnerOptions();
        setSortBySpinnerOptions();

        loadContentFeed();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setAdapter(new MyContentItemRecyclerViewAdapter(Content.ITEMS, mListener));
        }
        return view;
    }

    private void loadContentFeed() {
        try {
            mContentItems = new ArrayList<>();
            getActivity().startService(new Intent(getContext(), EthereumClientService.class)
                    .putExtra(PARAM_PUBLICATION_INDEX, 0)
                    .setAction(ETH_FETCH_PUBLICATION_CONTENT));
        } catch (Exception e) {
            Log.e(TAG, "Error updating content feed: " + e.getMessage());
        }
    }

    private ContentItem convertJsonToContentItem(String json) {
        Gson gson = new Gson();
        ContentItem contentItem;
        try {
            contentItem = gson.fromJson(json, ContentItem.class);
        } catch (Exception e) {
            contentItem = new ContentItem("", "content not available", 0, "", "", "", "");
        }
        return contentItem;
    }

    private void reloadContentList(ArrayList<String> jsonArray) {
        mContentItems = new ArrayList<>();
        for (String json: jsonArray) {
            ContentItem ci = convertJsonToContentItem(json);
            mContentItems.add(ci);
        }
        mRecyclerView.setAdapter(new MyContentItemRecyclerViewAdapter(mContentItems, mListener));
    }

    private void setPublicationSpinnerOptions() {
        ArrayList<String> publicationOptions = new ArrayList<>();
        publicationOptions.add("none");
        publicationOptions.add("slush-pile");
        publicationOptions.add("publication options");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, publicationOptions);
        mPublicationSpinner.setAdapter(spinnerArrayAdapter);
        mPublicationSpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mPublicationSpinner.setSelection(0);
    }
    private void setTagSpinnerOptions() {
        ArrayList<String> tagOptions = new ArrayList<>();
        tagOptions.add("all");
        tagOptions.add("tag options");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, tagOptions);
        mTagSpinner.setAdapter(spinnerArrayAdapter);
        mTagSpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mTagSpinner.setSelection(0);
    }
    private void setSortBySpinnerOptions() {
        ArrayList<String> sortByOptions = new ArrayList<>();
        sortByOptions.add("Date Added Desc");
        sortByOptions.add("Date Added Asc");
        sortByOptions.add("Upvotes Desc");
        sortByOptions.add("Upvotes Asc");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, sortByOptions);
        mSortBySpinner.setAdapter(spinnerArrayAdapter);
        mSortBySpinner.setOnItemSelectedListener(mPublicationSpinnerItemSelectedListener);
        mSortBySpinner.setSelection(0);
    }

    private AdapterView.OnItemSelectedListener mPublicationSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void setAdapter(MyContentItemRecyclerViewAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

//        getActivity().getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ContentItem item);
    }
}
