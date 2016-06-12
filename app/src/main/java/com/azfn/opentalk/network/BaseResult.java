package com.azfn.opentalk.network;
/**
 * User: xiaoming
 * Date: 2016-06-05
 * Time: 16:38
 * http请求返回的数据结构类型
 */

import retrofit2.http.Body;

/**
 * Created by apple on 16/6/5.
 */
public class BaseResult {
    public int status;//1成功，2失败
    public ErrorMsg error;//
    public Body body;



    class ErrorMsg{
        int code;//错误代码
        String msg;//错误信息
    }


}
