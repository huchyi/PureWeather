package hanjie.app.pureweather.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.activities.HomeActivity;
import hanjie.app.pureweather.activities.SettingsActivity;
import hanjie.app.pureweather.bean.WeatherForecast;
import hanjie.app.pureweather.bean.WeatherRealtime;
import hanjie.app.pureweather.db.dao.CityQueryDao;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.receivers.WeatherWidget42;
import hanjie.app.pureweather.utils.DateUtils;
import hanjie.app.pureweather.utils.WeatherUtils;

public class DeskWidget42Service extends Service {

    private AppWidgetManager widgetManager;
    private WeatherDao dao;
    private WidgetUpdateReceiver widgetUpdateReceiver;
    private SharedPreferences mSP;
    private RemoteViews remoteViews;

    private class WidgetUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收到更新Widget的广播就更新WidgetUI
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK) || action.equals(Intent.ACTION_TIME_CHANGED)) {
                // 更新时间
                updateWidgetTime();
            } else if (action.equals(DeskWidget41Service.ACTION_UPDATE_WIDGET_WEATHER)) {
                // 更新插件的天气信息
                updateWidgetWeather();
                updateWidgetTime();
            } else if (action.equals(DeskWidget41Service.ACTION_UPDATE_WIDGET_TEXT_COLOR)) {
                // 更新插件的颜色
                updateWidgetTextColor();
            } else if (action.equals(DeskWidget41Service.ACTION_SET_WIDGET_CLICK_LISTENER)) {
                setClickListener();
            }
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
        dao = new WeatherDao(DeskWidget42Service.this);
        widgetManager = AppWidgetManager.getInstance(DeskWidget42Service.this);
        setClickListener();
        widgetUpdateReceiver = new WidgetUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK); // 时间的流逝
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED); // 时间被改变，人为
        intentFilter.addAction(DeskWidget41Service.ACTION_UPDATE_WIDGET_WEATHER); // 监听更新桌面天气信息的广播
        intentFilter.addAction(DeskWidget41Service.ACTION_UPDATE_WIDGET_TEXT_COLOR); // 监听更新桌面插件颜色的广播
        intentFilter.addAction(DeskWidget41Service.ACTION_SET_WIDGET_CLICK_LISTENER); // 监听设置桌面插件点击监听的广播
        registerReceiver(widgetUpdateReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWidgetWeather();
        updateWidgetTime();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 设置插件点击事件
     */
    private void setClickListener() {
        ComponentName provider = new ComponentName(DeskWidget42Service.this, WeatherWidget42.class);
        RemoteViews widgetView = getRemoteViews();
        // 设置widget的点击监听
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) SystemClock.uptimeMillis(),
                new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        widgetView.setOnClickPendingIntent(R.id.ll_realTimeArea, pendingIntent);

        pendingIntent = PendingIntent.getActivity(this, (int) SystemClock.uptimeMillis(),
                new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        widgetView.setOnClickPendingIntent(R.id.ll_forecastArea, pendingIntent);

        pendingIntent = PendingIntent.getActivity(this, (int) SystemClock.uptimeMillis(),
                new Intent(AlarmClock.ACTION_SHOW_ALARMS), PendingIntent.FLAG_UPDATE_CURRENT);
        widgetView.setOnClickPendingIntent(R.id.ll_timeArea, pendingIntent);
        widgetManager.updateAppWidget(provider, widgetView);
    }

    /**
     * 更新桌面插件UI
     */
    private void updateWidgetWeather() {
        ComponentName provider = new ComponentName(DeskWidget42Service.this, WeatherWidget42.class);
        RemoteViews widgetView = getRemoteViews();
        // 获取设置的部件Text颜色值
        int widgetTextColor = mSP.getInt(getString(R.string.widget_text_color), SettingsActivity.WIDGET_TEXT_COLOR_DEFAULT);
        String weatherId = dao.getMainAreaId();
        widgetView.setViewVisibility(R.id.rl_init, View.INVISIBLE);
        if (!TextUtils.isEmpty(weatherId)) {
            // 有城市
            widgetView.setViewVisibility(R.id.rl_noCity, View.INVISIBLE);
            if (dao.haveCache(weatherId)) {
                // 城市有缓存
                widgetView.setViewVisibility(R.id.rl_noData, View.INVISIBLE);
                widgetView.setViewVisibility(R.id.ll_dataArea, View.VISIBLE);
                WeatherRealtime realtime = dao.getDataFromRealtime(weatherId);
                String type = realtime.getWeather();
                String temp = realtime.getWendu();
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_typeIcon, WeatherUtils.getBlackIconIdByTypeName(type));
                } else {
                    widgetView.setImageViewResource(R.id.iv_typeIcon, WeatherUtils.getWhiteIconIdByTypeName(type));
                }
                StringBuilder sb = new StringBuilder();
                sb.append(CityQueryDao.getAreaNameByWeatherId(weatherId));
                sb.append("  |  ");
                sb.append(type + "  " + temp + "°C");
                widgetView.setTextViewText(R.id.tv_areaAndTempAndTemp, sb.toString());
                widgetView.setInt(R.id.tv_areaAndTempAndTemp, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                // 显示天气预报部分
                ArrayList<WeatherForecast> forecastList = dao.getDataListFromForecast(weatherId);
                WeatherForecast forecast1 = forecastList.get(0);
                widgetView.setTextViewText(R.id.tv_week1, "今天");
                widgetView.setInt(R.id.tv_week1, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon1, WeatherUtils.getBlackIconIdByTypeName(forecast1.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon1, WeatherUtils.getWhiteIconIdByTypeName(forecast1.getWeatherStart()));
                }
                widgetView.setTextViewText(R.id.tv_temp1, forecast1.getTempMin() + " / " + forecast1.getTempMax() + "°");
                widgetView.setInt(R.id.tv_temp1, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast2 = forecastList.get(1);
                widgetView.setTextViewText(R.id.tv_week2, "明天");
                widgetView.setInt(R.id.tv_week2, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon2, WeatherUtils.getBlackIconIdByTypeName(forecast2.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon2, WeatherUtils.getWhiteIconIdByTypeName(forecast2.getWeatherStart()));
                }
                widgetView.setTextViewText(R.id.tv_temp2, forecast2.getTempMin() + " / " + forecast2.getTempMax() + "°");
                widgetView.setInt(R.id.tv_temp2, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast3 = forecastList.get(2);
                widgetView.setTextViewText(R.id.tv_week3, "后天");
                widgetView.setInt(R.id.tv_week3, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon3, WeatherUtils.getBlackIconIdByTypeName(forecast3.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon3, WeatherUtils.getWhiteIconIdByTypeName(forecast3.getWeatherStart()));
                }
                widgetView.setTextViewText(R.id.tv_temp3, forecast3.getTempMin() + " / " + forecast3.getTempMax() + "°");
                widgetView.setInt(R.id.tv_temp3, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast4 = forecastList.get(3);
                widgetView.setTextViewText(R.id.tv_week4, forecast4.getWeek());
                widgetView.setInt(R.id.tv_week4, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon4, WeatherUtils.getBlackIconIdByTypeName(forecast4.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon4, WeatherUtils.getWhiteIconIdByTypeName(forecast4.getWeatherStart()));
                }
                widgetView.setTextViewText(R.id.tv_temp4, forecast4.getTempMin() + " / " + forecast4.getTempMax() + "°");
                widgetView.setInt(R.id.tv_temp4, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast5 = forecastList.get(4);
                widgetView.setTextViewText(R.id.tv_week5, forecast5.getWeek());
                widgetView.setInt(R.id.tv_week5, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon5, WeatherUtils.getBlackIconIdByTypeName(forecast5.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon5, WeatherUtils.getWhiteIconIdByTypeName(forecast5.getWeatherStart()));
                }
                widgetView.setTextViewText(R.id.tv_temp5, forecast5.getTempMin() + " / " + forecast5.getTempMax() + "°");
                widgetView.setInt(R.id.tv_temp5, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

            } else {
                // 城市无缓存
                widgetView.setViewVisibility(R.id.rl_noData, View.VISIBLE);
                widgetView.setViewVisibility(R.id.ll_dataArea, View.INVISIBLE);
                widgetView.setInt(R.id.tv_noData, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
            }
        } else {
            // 无城市
            widgetView.setViewVisibility(R.id.rl_noCity, View.VISIBLE);
            widgetView.setViewVisibility(R.id.rl_noData, View.INVISIBLE);
            widgetView.setViewVisibility(R.id.ll_dataArea, View.INVISIBLE);
            widgetView.setInt(R.id.tv_noCity, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
        }
        // 更新
        widgetManager.updateAppWidget(provider, widgetView);
    }

    /**
     * 更新桌面插件时间
     */
    private void updateWidgetTime() {
        ComponentName provider = new ComponentName(DeskWidget42Service.this, WeatherWidget42.class);
        RemoteViews widgetView = getRemoteViews();
        // 获取设置的部件Text颜色值
        int widgetTextColor = mSP.getInt(getString(R.string.widget_text_color), SettingsActivity.WIDGET_TEXT_COLOR_DEFAULT);
        widgetView.setTextViewText(R.id.tv_systemTime, DateUtils.getSimpleSystemTime());
        widgetView.setInt(R.id.tv_systemTime, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtils.getSimpleSystemDate());
        sb.append("  |  ");
        sb.append(DateUtils.getSimpleLunarToday());
        widgetView.setTextViewText(R.id.tv_systemDate, sb.toString());
        widgetView.setInt(R.id.tv_systemDate, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
        // 更新
        widgetManager.updateAppWidget(provider, widgetView);
    }

    /**
     * 更新桌面插件的颜色
     */
    private void updateWidgetTextColor() {
        ComponentName provider = new ComponentName(DeskWidget42Service.this, WeatherWidget42.class);
        RemoteViews widgetView = getRemoteViews();
        // 获取设置的部件Text颜色值
        int widgetTextColor = mSP.getInt(getString(R.string.widget_text_color), SettingsActivity.WIDGET_TEXT_COLOR_DEFAULT);
        String weatherId = dao.getMainAreaId();
        if (!TextUtils.isEmpty(weatherId)) {
            // 有城市
            widgetView.setViewVisibility(R.id.rl_noCity, View.INVISIBLE);
            if (dao.haveCache(weatherId)) {
                // 城市有缓存
                widgetView.setViewVisibility(R.id.rl_noData, View.INVISIBLE);
                widgetView.setViewVisibility(R.id.ll_dataArea, View.VISIBLE);
                WeatherRealtime realtime = dao.getDataFromRealtime(weatherId);
                String type = realtime.getWeather();
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_typeIcon, WeatherUtils.getBlackIconIdByTypeName(type));
                } else {
                    widgetView.setImageViewResource(R.id.iv_typeIcon, WeatherUtils.getWhiteIconIdByTypeName(type));
                }
                widgetView.setInt(R.id.tv_areaAndTempAndTemp, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                // 显示天气预报部分
                ArrayList<WeatherForecast> forecastList = dao.getDataListFromForecast(weatherId);
                WeatherForecast forecast1 = forecastList.get(0);
                widgetView.setInt(R.id.tv_week1, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon1, WeatherUtils.getBlackIconIdByTypeName(forecast1.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon1, WeatherUtils.getWhiteIconIdByTypeName(forecast1.getWeatherStart()));
                }
                widgetView.setInt(R.id.tv_temp1, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast2 = forecastList.get(1);
                widgetView.setInt(R.id.tv_week2, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon2, WeatherUtils.getBlackIconIdByTypeName(forecast2.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon2, WeatherUtils.getWhiteIconIdByTypeName(forecast2.getWeatherStart()));
                }
                widgetView.setInt(R.id.tv_temp2, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast3 = forecastList.get(2);
                widgetView.setInt(R.id.tv_week3, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon3, WeatherUtils.getBlackIconIdByTypeName(forecast3.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon3, WeatherUtils.getWhiteIconIdByTypeName(forecast3.getWeatherStart()));
                }
                widgetView.setInt(R.id.tv_temp3, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast4 = forecastList.get(3);
                widgetView.setInt(R.id.tv_week4, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon4, WeatherUtils.getBlackIconIdByTypeName(forecast4.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon4, WeatherUtils.getWhiteIconIdByTypeName(forecast4.getWeatherStart()));
                }
                widgetView.setInt(R.id.tv_temp4, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                WeatherForecast forecast5 = forecastList.get(4);
                widgetView.setInt(R.id.tv_week5, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                if (widgetTextColor == 1) {
                    widgetView.setImageViewResource(R.id.iv_icon5, WeatherUtils.getBlackIconIdByTypeName(forecast5.getWeatherStart()));
                } else {
                    widgetView.setImageViewResource(R.id.iv_icon5, WeatherUtils.getWhiteIconIdByTypeName(forecast5.getWeatherStart()));
                }
                widgetView.setInt(R.id.tv_temp5, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));

                // 更新插件时间和日期的颜色
                widgetView.setInt(R.id.tv_systemTime, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
                widgetView.setInt(R.id.tv_systemDate, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
            } else {
                // 城市无缓存
                widgetView.setViewVisibility(R.id.rl_noData, View.VISIBLE);
                widgetView.setViewVisibility(R.id.ll_dataArea, View.INVISIBLE);
                widgetView.setInt(R.id.tv_noData, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
            }
        } else {
            // 无城市
            widgetView.setViewVisibility(R.id.rl_noCity, View.VISIBLE);
            widgetView.setViewVisibility(R.id.rl_noData, View.INVISIBLE);
            widgetView.setViewVisibility(R.id.ll_dataArea, View.INVISIBLE);
            widgetView.setInt(R.id.tv_noCity, "setTextColor", getResources().getColor(SettingsActivity.mWidgetTextColorIds[widgetTextColor]));
        }
        // 更新
        widgetManager.updateAppWidget(provider, widgetView);
    }

    public RemoteViews getRemoteViews() {
        if (remoteViews == null) {
            remoteViews = new RemoteViews(getPackageName(), R.layout.widget_desk_weather_42);
        }
        return remoteViews;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (widgetUpdateReceiver != null) {
            unregisterReceiver(widgetUpdateReceiver);
        }
    }
}
