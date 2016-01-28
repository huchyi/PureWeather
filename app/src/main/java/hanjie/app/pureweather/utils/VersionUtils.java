package hanjie.app.pureweather.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtils {

    /**
     * 获取当前App版本号:versionName
     *
     * @return 返回当前App的版本号
     */
    public static String getVersionName(Context context) {
        // 得到包管理器
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

}
