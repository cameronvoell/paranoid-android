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
import com.example.cameron.ethereumtest1.activities.MainActivity;
import com.example.cameron.ethereumtest1.adapters.MyContentItemRecyclerViewAdapter;
import com.example.cameron.ethereumtest1.ethereum.EthereumClientService;
import com.example.cameron.ethereumtest1.model.Content;
import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.model.PublicationContentItem;
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
    private ArrayList<PublicationContentItem> mContentItems;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EthereumClientService.UI_UPDATE_PUBLICATION_CONTENT:
                    ArrayList<String> jsonArray = intent.getStringArrayListExtra(
                            EthereumClientService.PARAM_ARRAY_CONTENT_STRING);
                    ArrayList<String> revenueArray = intent.getStringArrayListExtra(
                            EthereumClientService.PARAM_ARRAY_CONTENT_REVENUE_STRING);
                    reloadContentList(jsonArray, revenueArray);
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

        loadContentFeed();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setAdapter(new MyContentItemRecyclerViewAdapter(Content.ITEMS, getContext()));
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                    if (dy > 0)
                        ((MainActivity)getActivity()).showFAB(false);
                    else if (dy < 0)
                        ((MainActivity)getActivity()).showFAB(true);
                }
            });
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

    private void reloadContentList(ArrayList<String> jsonArray, ArrayList<String> revenueArray) {
        mContentItems = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String json = jsonArray.get(i);
            String revenue = revenueArray.get(i);
            ContentItem ci = convertJsonToContentItem(json);
            PublicationContentItem pci = new PublicationContentItem(i, ci, revenue, 0);
            mContentItems.add(pci);
        }
        mRecyclerView.setAdapter(new MyContentItemRecyclerViewAdapter(mContentItems, getContext()));
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

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
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
