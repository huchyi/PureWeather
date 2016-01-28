package hanjie.app.pureweather.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.activities.HomeActivity;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.services.DeskWidget41Service;
import hanjie.app.pureweather.services.NotificationService;
import hanjie.app.pureweather.utils.BroadcastUtils;
import hanjie.app.pureweather.utils.HttpUtils;
import hanjie.app.pureweather.utils.WeatherDataParser;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        final WeatherDao dao = new WeatherDao(context);

        SharedPreferences mSP = context.getSharedPreferences(context.getString(R.string.config), Context.MODE_PRIVATE);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            for (int i = 0; i < networkInfos.length; i++) {
                NetworkInfo.State state = networkInfos[i].getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    String mainWeatherId = dao.getMainAreaId();
                    boolean need = false;
                    if (System.currentTimeMillis() - Long.valueOf(dao.getLastUpdateTime(mainWeatherId)) < 3600000) {
                    } else {
                        need = true;
                    }
                    if (need) {
                        new AsyncTask<String, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(String... params) {
                                try {
                                    String areaId = params[0];
                                    String s = HttpUtils.requestData(context.getString(R.string.base_api_url) + areaId);
                                    WeatherDataParser.parserToDB(context, s, areaId);
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
                                    BroadcastUtils.sendNotificationBroadcast(context);
                                    BroadcastUtils.sendShowHomeDataBroadcast(context);
                                    BroadcastUtils.sendUpdateWidgetWeatherBroadcast(context);
                                }
                            }
                        }.execute(mainWeatherId);
                    } else {
                        Log.d("bingo", "不需要更新呢");
                    }
                    return;
                }
            }
        }
    }

}
