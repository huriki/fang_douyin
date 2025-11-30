package com.example.fangdouyin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ItemCardApiService {
    @GET("/api/itemcard")
    Call<itemCardResponse> getAllItem(@Query("count") int count);

    @POST("/api/update")
    Call<updateRequest> updateImageCount(@Body updateRequest request);


}
