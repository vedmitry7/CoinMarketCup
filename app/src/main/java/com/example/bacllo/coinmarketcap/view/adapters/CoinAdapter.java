package com.example.bacllo.coinmarketcap.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bacllo.coinmarketcap.Constants;
import com.example.bacllo.coinmarketcap.R;
import com.example.bacllo.coinmarketcap.api.Coin;
import com.example.bacllo.coinmarketcap.view.activities.CoinDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.ViewHolder> {

    private List<Coin> mCoins;
    private Context mContext;
    private Realm mRealm;
    private boolean isFavoriteCoinList;

    public void setFavoriteCoinList(boolean favoriteCoinList) {
        isFavoriteCoinList = favoriteCoinList;
    }

    public CoinAdapter(List<Coin> users, Context context, Realm realm) {
        this.mCoins = users;
        this.mContext = context;
        mRealm = realm;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final Coin coin = mCoins.get(position);
        viewHolder.name.setText(coin.getName());
        viewHolder.change.setText(coin.getPercentChange24h()+"%");
        if(coin.getPercentChange24h().startsWith("-")){
            viewHolder.change.setTextColor(Color.RED);
        } else {
            viewHolder.change.setTextColor(Color.GREEN);

        }

        if(coin.isFavorite()){
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealm.beginTransaction();
                if(coin.isFavorite()){
                    coin.setFavorite(false);
                    if(isFavoriteCoinList) {
                        mCoins.remove(position);
                        notifyDataSetChanged();
                    }
                } else {
                    coin.setFavorite(true);
                }
                mRealm.commitTransaction();
            }
        });

        Glide.with(mContext).load(Constants.URL_ICON + coin.getId() + Constants.ICON_EXTENSION).into(viewHolder.icon);
    }


    @Override
    public int getItemCount() {
        return mCoins.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.coinIcon)
        ImageView icon;
        @BindView(R.id.coinName)
        TextView name;
        @BindView(R.id.change)
        TextView change;
        @BindView(R.id.checkBox)
        CheckBox checkBox;

        private ViewHolder(View itemView)  {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.setIsRecyclable(false);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, CoinDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.KEY_COIN, mCoins.get(this.getAdapterPosition()).getId());
            mContext.startActivity(intent);
        }
    }

    public void update(List<Coin> coins) {
        mCoins = coins;
        notifyDataSetChanged();

    }
}
