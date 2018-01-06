package com.example.bacllo.coinmarketcap.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bacllo.coinmarketcap.Constants;
import com.example.bacllo.coinmarketcap.R;
import com.example.bacllo.coinmarketcap.api.Coin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.bacllo.coinmarketcap.Constants.REAL_CURRENCY_CODES;

public class CalculateFragment extends Fragment {

    final static String VIRTUAL_COIN="virtual";
    final static String REAL_COIN="real";

    private Realm mRealm;
    @BindView(R.id.virtualCurrencySpinner)
    Spinner mVirtualCurrencySpinner;
    @BindView(R.id.realCurrencySpinner)
    Spinner mRealCurrencySpinner;

    @BindView(R.id.virtualCurrencyEditText)
    EditText mVirtualCurrencyEditText;
    @BindView(R.id.realCurrencyEditText)
    EditText mRealCurrencyEditText;

    private List<String> mCoinNames;
    private List<Coin> mCoins = new ArrayList<>();
    private String mSelectedVirtualCurrency = "bitcoin";
    private String mSelectedRealCurrency = "USD";
    private double mPrice;

    private String wasEntered = VIRTUAL_COIN;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calculate, container, false);
        ButterKnife.bind(this, view);
        mRealm = Realm.getDefaultInstance();

        loadFromRealm();
        mCoinNames = new ArrayList<>();
        for (Coin c : mCoins
                ) {
            mCoinNames.add(c.getName());
        }

        initSpinners();

        mVirtualCurrencyEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    mVirtualCurrencyEditText.requestFocus();
                    mRealCurrencyEditText.setText("");
                    if(mPrice ==0){
                        wasEntered = VIRTUAL_COIN;
                        getResponse();
                    } else {
                        double value = Double.valueOf(mVirtualCurrencyEditText.getText().toString());
                        mRealCurrencyEditText.setText(String.valueOf(mPrice *value));
                    }
                }
                mVirtualCurrencyEditText.requestFocus();
                return false;
            }
        });

        mRealCurrencyEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    mRealCurrencyEditText.requestFocus();
                    mVirtualCurrencyEditText.setText("");
                    if(mPrice ==0){
                        wasEntered =REAL_COIN;
                        getResponse();
                    } else {
                        double value = Double.valueOf(mRealCurrencyEditText.getText().toString());
                        mVirtualCurrencyEditText.setText(String.valueOf(value/ mPrice));
                    }
                }
                return false;
            }
        });

        mVirtualCurrencyEditText.setText("1");
        getResponse();
        return view;
    }

    private void initSpinners() {

        VirtualCurrencySpinnerAdapter adapter = new VirtualCurrencySpinnerAdapter(getContext(), R.layout.spinner_row, R.id.coinName, mCoinNames);
        mVirtualCurrencySpinner.setAdapter(adapter);
        mVirtualCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedVirtualCurrency = mCoins.get(position).getId();
                mPrice = 0;
                /*mRealCurrencyEditText.setText("");
                getResponse();*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> realCurrencyAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, REAL_CURRENCY_CODES);
        realCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRealCurrencySpinner.setAdapter(realCurrencyAdapter);
        mRealCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedRealCurrency = REAL_CURRENCY_CODES[position];
                mPrice = 0;
               /* mVirtualCurrencyEditText.setText("");
                getResponse();*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadFromRealm(){
        RealmResults<Coin> coins = mRealm.where(Coin.class).findAll();
        mCoins.clear();
        mCoins.addAll(coins);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean getResponse(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.coinmarketcap.com/v1/ticker/"+ mSelectedVirtualCurrency +"/";

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        builder.addQueryParameter("convert", mSelectedRealCurrency);

        Request request = new Request.Builder().url(builder.build()).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                String responseString = response.body().string();
                String responseClear = responseString.substring(1,responseString.length()-1);


                try {
                    JSONObject jsonObject = new JSONObject(responseClear);

                    mPrice = jsonObject.getDouble("price_" + mSelectedRealCurrency.toLowerCase());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(wasEntered.equals(VIRTUAL_COIN)) {
                                double value = Double.valueOf(mVirtualCurrencyEditText.getText().toString());
                                mRealCurrencyEditText.setText(String.valueOf(mPrice * value));
                            }
                            if(wasEntered.equals(REAL_COIN)){
                                double value = Double.valueOf(mRealCurrencyEditText.getText().toString());
                                mVirtualCurrencyEditText.setText(String.valueOf(value/ mPrice));
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return !(mPrice == 0);
    }

    public class VirtualCurrencySpinnerAdapter  extends ArrayAdapter<String> {

        public VirtualCurrencySpinnerAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);

            TextView label = row.findViewById(R.id.coinName);
            label.setText(mCoinNames.get(position));

            ImageView icon =  row.findViewById(R.id.coinIcon);
            Glide.with(getContext()).load(Constants.URL_ICON + mCoins.get(position).getId() + Constants.ICON_EXTENSION).into(icon);

            return row;
        }
    }
}