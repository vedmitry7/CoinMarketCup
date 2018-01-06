package com.example.bacllo.coinmarketcap.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bacllo.coinmarketcap.R;
import com.example.bacllo.coinmarketcap.api.Coin;
import com.example.bacllo.coinmarketcap.view.adapters.CoinAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class FavoriteCurrencyListFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private CoinAdapter mAdapter;

    private Realm mRealm;
    private List<Coin> mCoins = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currncy_list, container, false);
        ButterKnife.bind(this, view);

        mRealm = Realm.getInstance(new RealmConfiguration.Builder(getActivity().getApplicationContext()).deleteRealmIfMigrationNeeded().build());

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CoinAdapter(new ArrayList<Coin>(), getActivity().getApplicationContext(), mRealm);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setFavoriteCoinList(true);

        loadFavoriteCoinFromRealm();
        mAdapter.update(mCoins);

        return view;
    }


    private void loadFavoriteCoinFromRealm(){
        RealmResults<Coin> coins = mRealm.where(Coin.class)
                .equalTo("favorite",true)
                .findAll();
        mCoins.clear();
        mCoins.addAll(coins);
    }

    public void update() {
        loadFavoriteCoinFromRealm();
        mAdapter.update(mCoins);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteCoinFromRealm();
        mAdapter.update(mCoins);
    }
}