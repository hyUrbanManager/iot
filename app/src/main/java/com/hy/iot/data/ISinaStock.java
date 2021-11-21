package com.hy.iot.data;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ISinaStock {

    String BASE_URL = "https://hq.sinajs.cn";

    @GET("/")
    Call<ResponseBody> getStock(@Query("list") String stock);

}
