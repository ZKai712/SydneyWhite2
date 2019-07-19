package com.dyxy.zkai.sydneywhite.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapToFile {
    public static String saveBitmapFile(Bitmap bitmap){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis()+".jpg";
        File file = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return path;
    }

}
