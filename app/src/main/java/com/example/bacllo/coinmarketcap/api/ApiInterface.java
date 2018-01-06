package com.example.bacllo.coinmarketcap.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/v1/ticker/")
    Call<List<Coin>> getCoins(@Query("limit") int since);

}