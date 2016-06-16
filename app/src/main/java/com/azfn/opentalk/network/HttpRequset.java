package com.azfn.opentalk.network;
/**
 * User: xiaoming
 * Date: 2016-06-09
 * Time: 11:21
 * 描述一下这个类吧
 */

import com.azfn.opentalk.model.Version;
import com.azfn.opentalk.tools.MD5;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by apple on 16/6/9.
 */
public class HttpRequset {
    private HttpInterface mInterface;
    private static HttpRequset mInstance;
    private OkHttpClient okHttpClient;
    private static String TAG = "HttpRequest";


    public static HttpRequset getInstance(){
        if(mInstance == null){
            mInstance = new HttpRequset();
        }
        return mInstance;
    }




    private HttpRequset(){
        initHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://114.55.63.81/fnapp/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mInterface = retrofit.create(HttpInterface.class);
    }

    private void initHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public interface ILoadFinish {
        void success(Object object);
        void fail(String errorMsg);
    }

//    public Observable<LoginUser> login(String nickname, String password){
//        return mInterface.login(nickname, password, System.currentTimeMillis()+"", "ijjjmmjddd")
//                .compose(RxUtils.rxSchedulerHelper())
//                .map(new Func1<LoginUser, User>() {
//                    @Override
//                    public User call(User user) {
//                        return user;
//                    }
//                })
//                .flatMap(new Func1<User>, Observable<User>) {
//                    @Override
//                    public Observable<User> call(User> users) {
//                        return Observable.from(resultsBeen);
//                    }
//                })
//    }

    public void checkVersion(String currentVersion, final ILoadFinish iLoadFinish){
//        LogUtils.e(TAG, "checkVersion"+currentVersion);
        String timeSamp = System.currentTimeMillis()+"";
        Call<Version> versionCall = mInterface.version(currentVersion, timeSamp, MD5.Md5(currentVersion+timeSamp+"ijjjmmjddd"));
//        LoginUser loginUser = loginUserCall.execute();//同步
        versionCall.enqueue(new Callback<Version>() {//异步
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                Version version = response.body();
                iLoadFinish.success(version);
            }

            @Override
            public void onFailure(Call<Version> call, Throwable throwable) {
                iLoadFinish.fail(throwable.getMessage());
            }
        });
    }
}
