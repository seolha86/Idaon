package com.example.idaon.home;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


public class Base64Util {
    public static String encode(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
//        byte[] data = text.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
    public static String decode(String text) throws UnsupportedEncodingException {
        return new String(Base64.decode(text, Base64.DEFAULT),"UTF-8");
    }

}
