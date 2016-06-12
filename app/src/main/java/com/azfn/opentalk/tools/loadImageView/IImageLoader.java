package com.azfn.opentalk.tools.loadImageView;

import android.widget.ImageView;

/**
 * 图片加载接口
 */
public interface IImageLoader {

    void loadImage(Object object, ImageView imageView, String imgUrl);

    void loadImage(Object object, ImageView imageView, String imgUrl, int resDefault, int resError);

    void loadImageFile(Object object, ImageView imageView, String imgPath);

    void loadImageFile(Object object, ImageView imageView, String imgPath, int resDefault, int resError);

    void loadCircleImage(Object object, ImageView imageView, String imgUrl);

    void loadCircleImage(Object object, ImageView imageView, String imgUrl, int resDefault, int resError);

    boolean checkParams(Object object, ImageView imageView, String imgUrl);


}
