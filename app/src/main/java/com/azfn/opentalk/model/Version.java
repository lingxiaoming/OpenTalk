package com.azfn.opentalk.model;
/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 20:32
 * 组信息
 */

import com.azfn.opentalk.network.BaseResult;

/**
 * Created by apple on 16/6/4.
 */
public class Version extends BaseResult{

    public VersionReal body;

    public class VersionReal{
        public String url;
        public int type;//1-不强制更新 2-强制更新 3-不需要更新
    }
}
