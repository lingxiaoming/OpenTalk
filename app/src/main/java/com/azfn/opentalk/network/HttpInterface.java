package com.azfn.opentalk.network;

import com.azfn.opentalk.model.Group;
import com.azfn.opentalk.model.LoginUser;
import com.azfn.opentalk.model.Version;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * User: xiaoming
 * Date: 2016-06-05
 * Time: 16:04
 * Http请求接口定义
 */

public interface HttpInterface {
    @GET("users/{user}/repos")
    Call<List<Group>> listGroups(@Path("user") String user);


    @FormUrlEncoded
    @POST("/")
    Call<LoginUser> login(@Field(ParamContants.USER_ID) String name, @Field(ParamContants.TIMESTAMP) String occupation, @Field(ParamContants.TOKEN) String token);


    @FormUrlEncoded
    @POST("/app/softwareUpdateCheck.do")
    Call<Version> version(@Field(ParamContants.VERSION) String version, @Field(ParamContants.TIMESTAMP) String occupation, @Field(ParamContants.TOKEN) String token);



    @FormUrlEncoded
    @POST
    Call<BaseResult> lbsUpload(@FieldMap Map<String, String> params);

    @Multipart
    @POST("upload")//文件上传
    Call<BaseResult> upload(@Part("description") RequestBody description, @Part MultipartBody.Part file);





}
