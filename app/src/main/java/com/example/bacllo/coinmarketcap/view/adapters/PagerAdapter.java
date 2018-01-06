package com.example.bacllo.coinmarketcap.view.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.bacllo.coinmarketcap.view.fragments.CalculateFragment;
import com.example.bacllo.coinmarketcap.view.fragments.CurrencyListFragment;
import com.example.bacllo.coinmarketcap.view.fragments.FavoriteCurrencyListFragment;

public class PagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private FragmentManager fm;
    private  FavoriteCurrencyListFragment mFavoriteCurrencyListFragment;
    private  CurrencyListFragment mCurrencyListFragment;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        mFavoriteCurrencyListFragment = new FavoriteCurrencyListFragment();
        mCurrencyListFragment = new CurrencyListFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return mCurrencyListFragment;
            case 1:
                return mFavoriteCurrencyListFragment;
            case 2:
                return new CalculateFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Все валюты";
            case 1:
                return "Избранное";
            case 2:
                return "Калькулятор";
        }
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            mCurrencyListFragment.update();
        }
        if(position==1){
            mFavoriteCurrencyListFragment.update();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}