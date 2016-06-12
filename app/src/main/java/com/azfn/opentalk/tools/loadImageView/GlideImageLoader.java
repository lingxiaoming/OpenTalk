package com.azfn.opentalk.tools.loadImageView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.azfn.opentalk.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class GlideImageLoader implements IImageLoader{

    private static final int RES_DEFAULT_HEAD  = R.drawable.icon_single_avatar;
    private static final int RES_ERROR_HEAD    = R.drawable.icon_single_avatar;
    private static final int RES_DEFAULT_PIC   = R.drawable.icon_single_avatar;
    private static final int RES_ERROR_PIC     = R.drawable.icon_single_avatar;

    @Override
    public void loadImage(Object object, ImageView imageView, String imgUrl) {
        loadImage(object, imageView, imgUrl, RES_DEFAULT_PIC, RES_ERROR_PIC);
    }

    @Override
    public void loadImage(Object object, ImageView imageView, String imgUrl, int resDefault, int resError) {
        RequestManager requestManager = getRequestManager(object);
        if (checkParams(requestManager,imageView,imgUrl)){
            requestManager.load(imgUrl)
                    .fitCenter()
                    .placeholder(resDefault)
                    .error(resError)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                            return false;
                        }
                    })
                    .into(imageView);
        }else if(imageView != null){
            imageView.setImageResource(resError);
        }
    }

    @Override
    public void loadImageFile(Object object, ImageView imageView, String imgPath) {
        loadImageFile(object, imageView, imgPath, RES_DEFAULT_PIC, RES_ERROR_PIC);
    }

    @Override
    public void loadImageFile(Object object, ImageView imageView, String imgPath, int resDefault, int resError) {
        RequestManager requestManager = getRequestManager(object);
        if (checkParams(requestManager,imageView,imgPath)){
            requestManager.load(new File(imgPath))
                    .asBitmap()
                    .fitCenter()
//                    .animate(R.anim.fading_in_0_1)
                    .placeholder(resDefault)
                    .error(resError)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<File, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, File file, Target<Bitmap> target, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap bitmap, File file, Target<Bitmap> target, boolean b, boolean b1) {
                            return false;
                        }
                    })
                    .into(imageView);
        }else if(imageView != null){
            imageView.setImageResource(resError);
        }
    }

    @Override
    public void loadCircleImage(Object object, ImageView imageView, String imgUrl) {
        loadCircleImage(object, imageView, imgUrl, RES_DEFAULT_HEAD, RES_ERROR_HEAD);
    }

    @Override
    public void loadCircleImage(Object object, ImageView imageView, String imgUrl, int resDefault, int resError) {
        RequestManager requestManager = getRequestManager(object);
        if (checkParams(requestManager,imageView,imgUrl)){
            requestManager.load(imgUrl)
                    .centerCrop()
                    .placeholder(resDefault)
                    .error(resError)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                            return false;
                        }
                    })
                    .into(imageView);
        }else if(imageView != null){
            imageView.setImageResource(resError);
        }
    }

    @Override
    public boolean checkParams(Object object, ImageView imageView, String imgUrl) {
        return (imageView != null && !TextUtils.isEmpty(imgUrl) && object != null);
    }

    private RequestManager getRequestManager(Object object){

        RequestManager requestManager = null;

        if (object instanceof AppCompatActivity){
            requestManager = Glide.with((Activity)object);
        }else if (object instanceof Fragment){
            requestManager = Glide.with((Fragment)object);
        }else if (object instanceof android.app.Fragment){
            requestManager = Glide.with((android.app.Fragment)object);
        }else if (object instanceof Activity){
            requestManager = Glide.with((Activity)object);
        }else if (object instanceof Context){
            requestManager = Glide.with((Context)object);
        }
        return requestManager;
    }

}
