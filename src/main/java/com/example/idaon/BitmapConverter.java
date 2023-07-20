package com.example.idaon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapConverter {

    public static String linkConvert(String str) {
        String newStr = str.replace("https://", "https://www.");
        return newStr;
    }

    public static String nullToSpace(String str) {
        String newStr = str.replace(null, " ");
        return newStr;
    }

    public static String RemoveSpace(String str) {
        String newStr = str.replaceAll(" ", "+");
        return newStr;
    }

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
        byte[] imagebyte = outputStream.toByteArray();
        String imageStr = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return imageStr;
    }

    public static Bitmap stringToBitmap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            // Base64 코드를 디코딩하여 바이트 형태로 저장
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            // 바이트 형태를 디코딩하여 비트맵 형태로 저장
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
