package hanjie.app.pureweather.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtils {

    /**
     * 检查一个服务是否在运行中
     *
     * @param context     上下文
     * @param serviceName 需要检查的服务的全类名
     * @return 服务的运行状态，true为运行中，false为不在运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo rsi : servicesList) {
            String mServiceName = rsi.service.getClassName();
            if (mServiceName.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

}
