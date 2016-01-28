package hanjie.app.pureweather.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.activities.SettingsActivity;
import hanjie.app.pureweather.services.AutoUpdateService;
import hanjie.app.pureweather.services.NotificationService;
import hanjie.app.pureweather.utils.BroadcastUtils;

public class BootReceiver extends BroadcastReceiver {

    private SharedPreferences mSP;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSP = context.getSharedPreferences(context.getString(R.string.config), Context.MODE_PRIVATE);
        // 获取当前的相关设置启动服务
        if (mSP.getBoolean(context.getString(R.string.notification_service), SettingsActivity.NOTIFICATION_DEFAULT)) {
            context.startService(new Intent(context, NotificationService.class));
        }
        if (mSP.getBoolean(context.getString(R.string.auto_update_weather), SettingsActivity.AUTO_UPDATE_DEFAULT)) {
            context.startService(new Intent(context, AutoUpdateService.class));
        }
        Log.d("bingo", "开启完成，更新widget");
        BroadcastUtils.sendUpdateWidgetWeatherBroadcast(context);
        BroadcastUtils.sendSetWidgetClickListenerBroadcast(context);
    }

}
