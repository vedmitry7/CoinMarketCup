package com.example.bacllo.coinmarketcap.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bacllo.coinmarketcap.R;
import com.example.bacllo.coinmarketcap.api.ApiFactory;
import com.example.bacllo.coinmarketcap.api.Coin;
import com.example.bacllo.coinmarketcap.view.adapters.CoinAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyListFragment extends Fragment {

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

        mAdapter.update(mCoins);

        ApiFactory.getApi().getCoins(50).enqueue(new Callback<List<Coin>>() {
            @Override
            public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {
                updateCoins(response.body());
                loadFromRealm();
                mAdapter.update(mCoins);
            }

            @Override
            public void onFailure(Call<List<Coin>> call, Throwable t) {
                loadFromRealm();
                mAdapter.update(mCoins);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.update(mCoins);
    }

    private void loadFromRealm(){
        RealmResults<Coin> mUsers = mRealm.where(Coin.class).findAll();
        mCoins.clear();
        this.mCoins.addAll(mUsers);
    }

    void updateCoins(List<Coin> coins){
        for (Coin c:coins
             ) {
           updateCoin(c);
        }
    }

    void updateCoin(Coin c){
        Coin coin = mRealm.where(Coin.class)
                .equalTo("id", c.getId())
                .findFirst();
        if(coin!=null) {
            c.setFavorite(coin.isFavorite());
        }
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(c);
        mRealm.commitTransaction();
    }

    public void update(){
        mAdapter.update(mCoins);
    }
}