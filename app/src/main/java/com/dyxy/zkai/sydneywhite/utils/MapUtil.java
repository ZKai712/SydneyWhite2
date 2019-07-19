package com.dyxy.zkai.sydneywhite.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;

public class MapUtil {
    private Context context;

    public MapUtil(Context context) {
        this.context = context;
    }

    public boolean openMapApp(String packageName, String uriStr , String mapName){
        if (!isInstalled(packageName)) {
            Toast.makeText(context,"请先安装"+mapName+"客户端",Toast.LENGTH_SHORT).show();
            return false;
        }
        Uri uri = Uri.parse(uriStr);
        Intent intent = new Intent("android.intent.action.VIEW",uri);
        context.startActivity(intent);
        return true;
    }
    private boolean isInstalled(String packageName) {
        PackageManager manager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }

}
