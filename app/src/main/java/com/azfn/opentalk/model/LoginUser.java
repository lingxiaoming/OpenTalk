package com.azfn.opentalk.model;
/**
 * User: xiaoming
 * Date: 2016-06-05
 * Time: 16:37
 * 登录后返回的用户信息
 */

import com.azfn.opentalk.network.BaseResult;

/**
 * Created by apple on 16/6/5.
 */
public class LoginUser extends BaseResult{
    public String _user_name;
    public String _company_id;
    public String _company_name;
}
