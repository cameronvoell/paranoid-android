package com.example.cameron.ethereumtest1.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
                return inflater.inflate(R.layout.transaction_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView textViewOne = (TextView) view.findViewById(R.id.text1);
                TextView textViewTwo = (TextView) view.findViewById(R.id.text2);
                TextView textView3 = (TextView) view.findViewById(R.id.text3);
                TextView textView4 = (TextView) view.findViewById(R.id.text4);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.confirmedCheckBox);

                String transactionHash = DataUtils.formatTransactionHash(cursor.getString(
                        cursor.getColumnIndex( DatabaseHelper.KEY_ETH_TX_ID )));
                String actionDisplay = DataUtils.convertActionIdForDisplay(cursor.getInt(
                        cursor.getColumnIndex( DatabaseHelper.KEY_TX_ACTION_ID )));
                String content = cursor.getString(
                        cursor.getColumnIndex(DatabaseHelper.KEY_TX_CONTENT));
                String timestamp = cursor.getString(
                        cursor.getColumnIndex(DatabaseHelper.KEY_TX_TIMESTAMP));
                String blockNumber = DataUtils.formatBlockNumber(cursor.getLong(
                        cursor.getColumnIndex(DatabaseHelper.KEY_BLOCK_NUMBER)));
                long gasCost = cursor.getInt(
                        cursor.getColumnIndex(DatabaseHelper.KEY_GAS_COST));
                boolean confirmed = cursor.getInt(
                        cursor.getColumnIndex(DatabaseHelper.KEY_CONFIRMED)) > 0;



                textViewOne.setText(actionDisplay);
                textViewTwo.setText("block:" + blockNumber + "; gas:" + gasCost);
                textView3.setText(transactionHash);
                //textView4.setText(timestamp);
                checkBox.setChecked(confirmed);
            }
        };

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_eth_transaction_list, container, false);
        mListView = (ListView) v.findViewById(R.id.transactionList);
        mListView.setAdapter(mCursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor txCursor = ((Cursor)(mCursorAdapter.getItem(position)));
                String tx = txCursor.getString(txCursor.getColumnIndex(DatabaseHelper.KEY_ETH_TX_ID));
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
