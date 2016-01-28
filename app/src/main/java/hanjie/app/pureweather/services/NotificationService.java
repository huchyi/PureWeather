package hanjie.app.pureweather.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.activities.HomeActivity;
import hanjie.app.pureweather.activities.SettingsActivity;
import hanjie.app.pureweather.bean.WeatherRealtime;
import hanjie.app.pureweather.db.dao.CityQueryDao;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.utils.WeatherUtils;

public class NotificationService extends Service {

    private WeatherDao dao;
    private NotificationManager notificationManager;
    private NotificationReceiver notificationReceiver;
    private SharedPreferences mSP;

    public static final String ACTION_SHOW_NOTICATION = "hanjie.app.pureweather.action_show_notication";

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收到显示通知的广播就更新Notification
            showNotification();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSP = getSharedPreferences(getString(R.string.config), MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        dao = new WeatherDao(NotificationService.this);
        showNotification();
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, new IntentFilter(ACTION_SHOW_NOTICATION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(0);
        if (notificationReceiver != null) {
            unregisterReceiver(notificationReceiver);
        }
        super.onDestroy();
    }

    /**
     * 在通知栏显示相关主Area的信息
     */
    private void showNotification() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this);
        notifyBuilder.setShowWhen(false);
        // 取出设置的通知栏底色id值
        int notificationColor = mSP.getInt(getString(R.string.notification_color), SettingsActivity.NOTIFICATION_COLOR_DEFAULT);
        RemoteViews remoteView;
        String weatherId = dao.getMainAreaId();
        if (!TextUtils.isEmpty(weatherId)) {
            if (dao.haveCache(weatherId)) {
                remoteView = new RemoteViews(getPackageName(), R.layout.notification_realtime);
                // 设置通知栏底色
                remoteView.setInt(R.id.rootView_notification, "setBackgroundResource", SettingsActivity.mNotificationBGColorIds[notificationColor]);
                remoteView.setTextViewText(R.id.tv_notifyLocation, CityQueryDao.getAreaNameByWeatherId(weatherId));
                remoteView.setInt(R.id.tv_notifyLocation, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
                WeatherRealtime realtime = dao.getDataFromRealtime(weatherId);
                String type = realtime.getWeather();
                notifyBuilder.setSmallIcon(WeatherUtils.getWhiteIconIdByTypeName(type));
                String temp = realtime.getWendu();
                remoteView.setTextViewText(R.id.tv_notifyTypeAndTemp, type + "  " + temp + "°C");
                remoteView.setInt(R.id.tv_notifyTypeAndTemp, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
                if (notificationColor == 2) {
                    // 设置icon为黑色的
                    remoteView.setImageViewResource(R.id.iv_notifyTypeIcon, WeatherUtils.getBlackIconIdByTypeName(type));
                } else {
                    // 设置icon为白色的
                    remoteView.setImageViewResource(R.id.iv_notifyTypeIcon, WeatherUtils.getWhiteIconIdByTypeName(type));
                }
                remoteView.setTextViewText(R.id.tv_notifyUpdateTime, realtime.getUpdatetime() + " 发布");
                remoteView.setInt(R.id.tv_notifyUpdateTime, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
                if (notificationColor == 2) {
                    remoteView.setImageViewResource(R.id.iv_notifyTime, R.mipmap.ic_time_black);
                } else {
                    remoteView.setImageViewResource(R.id.iv_notifyTime, R.mipmap.ic_time_white);
                }
                if (dao.haveAlarm(weatherId)) {
                    remoteView.setTextViewText(R.id.tv_notifyAlarm, dao.getSimpleAlarmDesc(weatherId));
                    remoteView.setInt(R.id.tv_notifyAlarm, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
                } else {
                    remoteView.setTextViewText(R.id.tv_notifyAlarm, "");
                }
            } else {
                notifyBuilder.setSmallIcon(R.mipmap.ic_na);
                remoteView = new RemoteViews(getPackageName(), R.layout.notification_realtime_nodata);
                // 设置通知栏底色
                remoteView.setInt(R.id.rootView_notification, "setBackgroundResource", SettingsActivity.mNotificationBGColorIds[notificationColor]);
                remoteView.setTextViewText(R.id.tv_notifyLocation, CityQueryDao.getAreaNameByWeatherId(weatherId));
                remoteView.setInt(R.id.tv_notifyLocation, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
                if (notificationColor == 2) {
                    remoteView.setImageViewResource(R.id.iv_notifyNAIcon, R.mipmap.ic_na_black);
                } else {
                    remoteView.setImageViewResource(R.id.iv_notifyNAIcon, R.mipmap.ic_na);
                }
                remoteView.setInt(R.id.tv_noData, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
                remoteView.setViewVisibility(R.id.ll_data, View.VISIBLE);
                remoteView.setViewVisibility(R.id.tv_noCity, View.INVISIBLE);
            }
        } else {
            notifyBuilder.setSmallIcon(R.mipmap.ic_na);
            remoteView = new RemoteViews(getPackageName(), R.layout.notification_realtime_nodata);
            // 设置通知栏底色
            remoteView.setInt(R.id.rootView_notification, "setBackgroundResource", SettingsActivity.mNotificationBGColorIds[notificationColor]);
            remoteView.setInt(R.id.tv_noCity, "setTextColor", getResources().getColor(SettingsActivity.mNotificationTextColorIds[notificationColor]));
            if (notificationColor == 2) {
                remoteView.setImageViewResource(R.id.iv_notifyNAIcon, R.mipmap.ic_na_black);
            } else {
                remoteView.setImageViewResource(R.id.iv_notifyNAIcon, R.mipmap.ic_na);
            }
            remoteView.setViewVisibility(R.id.ll_data, View.INVISIBLE);
            remoteView.setViewVisibility(R.id.tv_noCity, View.VISIBLE);
        }
        notifyBuilder.setContent(remoteView);
        Intent notifyIntent = new Intent(this, HomeActivity.class);
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);
        startForeground(950827, notifyBuilder.build());
    }

}
