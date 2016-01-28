package hanjie.app.pureweather.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.ArrayList;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.activities.SettingsActivity;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.utils.BroadcastUtils;
import hanjie.app.pureweather.utils.HttpUtils;
import hanjie.app.pureweather.utils.WeatherDataParser;

public class AutoUpdateService extends Service {

    private WeatherDao dao;
    private BackgroundUpdateReceiver backgroundUpdateReceiver;
    private SharedPreferences mSP;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public static final String ACTION_BACKGROUND_UPDATE = "hanjie.app.pureweather.action_background_update";
    public static final String EXTRA_INTERVAL_CHANGED = "hanjie.app.pureweather.extra_interval_changed";

    private class BackgroundUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(EXTRA_INTERVAL_CHANGED, false)) {
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + getCurrentUpdateInterval(), getCurrentUpdateInterval(), pendingIntent);
                }
            } else {
                int mode = mSP.getInt(getString(R.string.auto_update_weather_mode), SettingsActivity.AUTO_UPDATE_MODE_DEFAULT);
                if (mode == SettingsActivity.MODE_UPDATE_CURRENT_AREA) {

                    new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {
                            try {
                                String areaId = params[0];
                                String s = HttpUtils.requestData(getString(R.string.base_api_url) + areaId);
                                WeatherDataParser.parserToDB(AutoUpdateService.this, s, areaId);
                                dao.setCache(areaId, 1);
                                dao.setLastUpdateTime(areaId, String.valueOf(System.currentTimeMillis()));
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean isSuccess) {
                            if (isSuccess) {
                                BroadcastUtils.sendNotificationBroadcast(AutoUpdateService.this);
                                BroadcastUtils.sendUpdateWidgetWeatherBroadcast(AutoUpdateService.this);
                            }
                        }
                    }.execute(dao.getMainAreaId());

                } else if (mode == SettingsActivity.MODE_UPDATE_ALL_AREA) {

                    new AsyncTask<ArrayList, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(ArrayList... params) {
                            ArrayList<String> areaIdList = params[0];
                            for (String id : areaIdList) {
                                try {
                                    String s = HttpUtils.requestData(getString(R.string.base_api_url) + id);
                                    WeatherDataParser.parserToDB(AutoUpdateService.this, s, id);
                                    dao.setCache(id, 1);
                                    dao.setLastUpdateTime(id, String.valueOf(System.currentTimeMillis()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            }
                            return true;
                        }

                        @Override
                        protected void onPostExecute(Boolean isSuccess) {
                            if (isSuccess) {
                                BroadcastUtils.sendNotificationBroadcast(AutoUpdateService.this);
                                BroadcastUtils.sendUpdateWidgetWeatherBroadcast(AutoUpdateService.this);
                            }
                        }

                    }.execute(dao.getAreasIdList());

                }
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
        dao = new WeatherDao(this);
        mSP = getSharedPreferences(getString(R.string.config), MODE_PRIVATE);

        // 注册BroadcastReceiver
        backgroundUpdateReceiver = new BackgroundUpdateReceiver();
        registerReceiver(backgroundUpdateReceiver, new IntentFilter(ACTION_BACKGROUND_UPDATE));

        // 设置定时
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(ACTION_BACKGROUND_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(AutoUpdateService.this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + getCurrentUpdateInterval(), getCurrentUpdateInterval(), pendingIntent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取当前配置文件中设置的更新频率值
     *
     * @return delayMillis
     */
    public long getCurrentUpdateInterval() {
        long mills = SettingsActivity.mUpdateIntervalValues[mSP.getInt(getString(R.string.auto_update_weather_interval), SettingsActivity.INTERVAL_DEFAULT)];
        return mills;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
        if (backgroundUpdateReceiver != null) {
            unregisterReceiver(backgroundUpdateReceiver);
        }
    }
}
