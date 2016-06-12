package com.azfn.opentalk.network;
/**
 * User: xiaoming
 * Date: 2016-06-09
 * Time: 11:21
 * 描述一下这个类吧
 */

import com.azfn.opentalk.model.LoginUser;
import com.azfn.opentalk.model.Version;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by apple on 16/6/9.
 */
public class HttpRequset {
    private static HttpRequset mInstance;

    public static HttpRequset getInstance(){
        if(mInstance == null){
            mInstance = new HttpRequset();
        }
        return mInstance;
    }


    private HttpInterface mInterface;

    private HttpRequset(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();
        mInterface = retrofit.create(HttpInterface.class);
    }

    public interface ILoadFinish {
        void success(Object object);
        void fail(String errorMsg);
    }

    public void login(String nickname, String password, final ILoadFinish iLoadFinish){
        Call<LoginUser> loginUserCall = mInterface.login(nickname, password, "token");
//        LoginUser loginUser = loginUserCall.execute();//同步
        loginUserCall.enqueue(new Callback<LoginUser>() {//异步
            @Override
            public void onResponse(Call<LoginUser> call, Response<LoginUser> response) {
                LoginUser loginUser = response.body();
                iLoadFinish.success(loginUser);
            }

            @Override
            public void onFailure(Call<LoginUser> call, Throwable throwable) {
                iLoadFinish.fail(throwable.getMessage());
            }
        });
    }

    public void checkVersion(String currentVersion, String password, final ILoadFinish iLoadFinish){
        Call<Version> versionCall = mInterface.version(currentVersion, password, "token");
//        LoginUser loginUser = loginUserCall.execute();//同步
        versionCall.enqueue(new Callback<Version>() {//异步
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                Version loginUser = response.body();
                iLoadFinish.success(loginUser);
            }

            @Override
            public void onFailure(Call<Version> call, Throwable throwable) {
                iLoadFinish.fail(throwable.getMessage());
            }
        });
    }
}
