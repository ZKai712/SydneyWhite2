package com.dyxy.zkai.sydneywhite.service;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

public interface SystemService {

    @FormUrlEncoded
    @POST("file/upload")
    Observable<?> preUploadFile(@Field("fileName") String fileName, @Field("fileMd5") String fileMd5);

    @Multipart
    @POST("file/upload")
    Observable<?> uploadFile(@PartMap Map<String, RequestBody> params);

}
