package com.example.cameron.ethereumtest1.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cameron.ethereumtest1.R;
import com.example.cameron.ethereumtest1.database.DatabaseHelper;
import com.example.cameron.ethereumtest1.util.DataUtils;
import com.example.cameron.ethereumtest1.util.PrefUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EthTransactionListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EthTransactionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EthTransactionListFragment extends Fragment {

    private CursorAdapter mCursorAdapter;
    private ListView mListView;

    private OnFragmentInteractionListener mListener;

    public EthTransactionListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EthTransactionListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EthTransactionListFragment newInstance(String param1, String param2) {
        EthTransactionListFragment fragment = new EthTransactionListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCursorAdapter = new CursorAdapter(getContext(), new DatabaseHelper(getContext()).getTransactionCursor(PrefUtils.getSelectedAccountAddress(getContext()))) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView textViewOne = (TextView) view.findViewById(android.R.id.text1);
                TextView textViewTwo = (TextView) view.findViewById(android.R.id.text2);
                String address = DataUtils.formatEthereumAccount(cursor.getString(cursor.getColumnIndex( DatabaseHelper.KEY_ETH_ADDRESS)));
                String tx = cursor.getString( cursor.getColumnIndex( DatabaseHelper.KEY_ETH_TX_ID ) );
                textViewOne.setText(address);
                textViewTwo.setText(tx);
            }
        };

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_eth_transaction_list, container, false);
        mListView = (ListView) v.findViewById(R.id.transactionList);
        mListView.setAdapter(mCursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textViewTwo = (TextView) view.findViewById(android.R.id.text2);
                String tx = textViewTwo.getText().toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rinkeby.etherscan.io/tx/" + tx));
                startActivity(browserIntent);
            }
        });
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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
