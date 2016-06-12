package com.azfn.opentalk.tools.loadImageView;

public class ImageLoadHelper {

    private static IImageLoader imageLoader;

    public static IImageLoader getImageLoader(){
        if (imageLoader == null){
            imageLoader = new GlideImageLoader();
        }
        return imageLoader;
    }

}
