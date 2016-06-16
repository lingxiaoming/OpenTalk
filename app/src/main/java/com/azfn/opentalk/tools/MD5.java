package com.azfn.opentalk.tools;
/**
 * User: xiaoming
 * Date: 2016-05-16
 * Time: 00:30
 * 描述一下这个类吧
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/6/9.
 */
public class MD5 {

    private static final String TAG = "MD5";

    /**
     * MD5 32位加密方法一 小写
     *
     * @param
     *
     * @return
     */

    public static String get32MD5(String s) {
      char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        String encodeStr;
        try {
            byte[] strTemp = s.getBytes();
            //使用MD5创建MessageDigest对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                //将每个数(int)b进行双字节加密
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            encodeStr = new String(str);
        } catch (Exception e) {
            return null;
        }
        //32位的加密
        return encodeStr;
    }

    /**
     * Md5 32位 or 16位 加密
     *
     * @param plainText
     *
     * @return 32位加密
     */
    public static String Md5(String plainText) {
        String encodeStr = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            encodeStr = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        CLog.e(TAG, "Md5 32 encodeStr: " + encodeStr);//32位的加密
//        CLog.e(TAG,"Md5 16 encodeStr: " + encodeStr.substring(8,24));//16位的加密
        return encodeStr;
    }

    /**
     * MD5 32位加密方法二 小写
     *
     * @param str
     *
     * @return
     */

    public final static String get32MD5Str(String str) {
        String encodeStr = null;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }

        }

        encodeStr = md5StrBuff.toString();
        return encodeStr;
    }
}

