package com.example.bacllo.coinmarketcap.view.activities;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bacllo.coinmarketcap.Constants;
import com.example.bacllo.coinmarketcap.R;
import com.example.bacllo.coinmarketcap.api.Coin;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CoinDetailActivity extends AppCompatActivity {


    @BindView(R.id.coinIcon)
    ImageView icon;
    @BindView(R.id.coinName)
    TextView coinName;
    @Nullable
    @BindView(R.id.change)
    TextView change;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    Realm mRealm;
    @BindView(R.id.idText)
    TextView id;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.symbol)
    TextView symbol;
    @BindView(R.id.rank)
    TextView rank;
    @BindView(R.id.volumeUsd24h)
    TextView volumeUsd24h;
    @BindView(R.id.totalSupply)
    TextView totalSupply;
    @BindView(R.id.marketCupUsd)
    TextView marketCupUsd;
    @BindView(R.id.availableSupply)
    TextView availableSupply;
    @BindView(R.id.priceUsd)
    TextView priceUsd;
    @BindView(R.id.priceBtc)
    TextView priceBtc;
    @BindView(R.id.change1h)
    TextView change1h;
    @BindView(R.id.change24h)
    TextView change24h;
    @BindView(R.id.change7d)
    TextView change7d;
    @BindView(R.id.lastUpdate)
    TextView lastUpdate;

    private Coin mCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();

        mCoin = mRealm.where(Coin.class)
                .equalTo("id", getIntent().getStringExtra(Constants.KEY_COIN))
                .findFirst();

        initView();
    }

    private void initView() {

        coinName.setText(mCoin.getName());

        if(mCoin.isFavorite()){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealm.beginTransaction();
                if(mCoin.isFavorite()){
                    mCoin.setFavorite(false);
                } else {
                    mCoin.setFavorite(true);
                }
                mRealm.commitTransaction();
            }
        });

        Glide.with(this).load(Constants.URL_ICON + mCoin.getId() + Constants.ICON_EXTENSION).into(icon);

        id.setText(mCoin.getId());
        name.setText(mCoin.getName());
        symbol.setText(mCoin.getSymbol());
        rank.setText(mCoin.getRank());
        volumeUsd24h.setText(mCoin.getVolumeUsd24h());
        marketCupUsd.setText(mCoin.getMarketCapUsd());
        totalSupply.setText(mCoin.getTotalSupply());
        availableSupply.setText(mCoin.getAvailableSupply());
        priceUsd.setText(mCoin.getPriceUsd());
        priceBtc.setText(mCoin.getPriceBtc());
        change1h.setText(mCoin.getPercentChange1h());
        change24h.setText(mCoin.getPercentChange24h());
        change7d.setText(mCoin.getPercentChange7d());
        lastUpdate.setText(mCoin.getLastUpdated());
    }
}
