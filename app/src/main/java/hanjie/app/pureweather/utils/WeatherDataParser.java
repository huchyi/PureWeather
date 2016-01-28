package hanjie.app.pureweather.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hanjie.app.pureweather.bean.WeatherAQI;
import hanjie.app.pureweather.bean.WeatherAlarm;
import hanjie.app.pureweather.bean.WeatherForecast;
import hanjie.app.pureweather.bean.WeatherRealtime;
import hanjie.app.pureweather.bean.WeatherYesterday;
import hanjie.app.pureweather.bean.WeatherZhiShu;
import hanjie.app.pureweather.db.dao.CityQueryDao;
import hanjie.app.pureweather.db.dao.WeatherDao;

public class WeatherDataParser {

    public static final String JSON_REALTIME = "realtime";
    public static final String JSON_AQI = "aqi";
    public static final String JSON_INDEX = "index";
    public static final String JSON_YESTERDAY = "yestoday";
    public static final String JSON_FORECAST = "forecast";
    public static final String JSON_ALERT = "alert";

    public static void parserToDB(Context context, String dataStr, String area_weather_id) throws Exception {

        // 创建数据库DAO对象
        WeatherDao dao = new WeatherDao(context);

        // 将服务器获取到的JSON格式的天气信息转化成JSONObject
        JSONObject dataJObj = new JSONObject(dataStr);

        // 解析之前，删除所有的天气预警信息和AQI信息
        dao.deleteFromAlarms(area_weather_id);
        dao.setAlarm(area_weather_id, 0);
        dao.deleteFromAQI(area_weather_id);
        dao.setAqi(area_weather_id, 0);

        // ---------解析realtime，并插入到数据库---------
        WeatherRealtime realtime = new WeatherRealtime();
        JSONObject realtimeJObj = dataJObj.getJSONObject(JSON_REALTIME);
        realtime.setUpdatetime(realtimeJObj.getString("time"));
        realtime.setWendu(realtimeJObj.getString("temp"));
        realtime.setFengli(realtimeJObj.getString("WS"));
        realtime.setFengxiang(realtimeJObj.getString("WD"));
        realtime.setShidu(realtimeJObj.getString("SD"));
        realtime.setWeather(realtimeJObj.getString("weather"));
        JSONObject sunriseAndSunset = dataJObj.getJSONObject("accu_f5").getJSONArray("DailyForecasts").getJSONObject(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        realtime.setSunrise(sdf.format(new Date(sunriseAndSunset.getLong("Sun_EpochRise") * 1000L)));
        realtime.setSunset(sdf.format(new Date(sunriseAndSunset.getLong("Sun_EpochSet") * 1000L)));
        dao.addToRealtime(area_weather_id, realtime);

        // ---------解析AQI，并插入到数据库---------
        WeatherAQI aqi = new WeatherAQI();
        JSONObject aqiJObj = dataJObj.getJSONObject(JSON_AQI);
        // 判断是否有AQI信息
        if (aqiJObj.length() == 0) {
            dao.setAqi(area_weather_id, 0);
        } else {
            String aqiStr = aqiJObj.getString("aqi");
            int aqiValue = Integer.valueOf(aqiStr);
            aqi.setAqi(aqiStr);
            if (aqiValue <= 50) {
                aqi.setQuality("优");
            } else if (aqiValue <= 100) {
                aqi.setQuality("良");
            } else if (aqiValue <= 150) {
                aqi.setQuality("轻度污染");
            } else if (aqiValue <= 200) {
                aqi.setQuality("中度污染");
            } else if (aqiValue <= 300) {
                aqi.setQuality("重度污染");
            } else {
                aqi.setQuality("严重污染");
            }
            aqi.setTime(aqiJObj.getString("pub_time"));
            aqi.setPm10(aqiJObj.getString("pm10"));
            aqi.setPm25(aqiJObj.getString("pm25"));
            aqi.setNo2(aqiJObj.getString("no2"));
            aqi.setSo2(aqiJObj.getString("so2"));
            aqi.setSrc(aqiJObj.getString("src"));
            dao.addToAQI(area_weather_id, aqi);
            dao.setAqi(area_weather_id, 1);
        }

        // ---------解析生活指数index，并插入到数据库---------
        JSONArray indexJArray = dataJObj.getJSONArray(JSON_INDEX);
        for (int i = 0; i < indexJArray.length(); i++) {
            JSONObject indexJObj = indexJArray.getJSONObject(i);
            WeatherZhiShu zhiShu = new WeatherZhiShu();
            zhiShu.setName_id(i + 1);
            zhiShu.setName(indexJObj.getString("name"));
            zhiShu.setValue(indexJObj.getString("index"));
            dao.addToZhishu(area_weather_id, zhiShu);
        }

        // ---------解析昨日天气，并插入到数据库---------
        JSONObject yesterdayJObj = dataJObj.getJSONObject(JSON_YESTERDAY);
        WeatherYesterday yesterday = new WeatherYesterday();
        yesterday.setDate(yesterdayJObj.getString("date"));
        yesterday.setTempMin(yesterdayJObj.getString("tempMin"));
        yesterday.setTempMax(yesterdayJObj.getString("tempMax"));
        yesterday.setWeatherStart(yesterdayJObj.getString("weatherStart"));
        yesterday.setWeatherEnd(yesterdayJObj.getString("weatherEnd"));
        dao.addToYesterday(area_weather_id, yesterday);

        // ---------解析未来天气，并插入到数据库---------
        JSONObject forecastJObj = dataJObj.getJSONObject(JSON_FORECAST);
        SimpleDateFormat strDateFormat = new SimpleDateFormat("yyyy年MM月dd");
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
        String str_date = forecastJObj.getString("date_y");
        // 将服务器的日期字符串转换成Date对象
        Date date = strDateFormat.parse(str_date);
        // 将日期转成Calendar对象，方便后面的日期加减
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i <= 5; i++) {
            WeatherForecast forecast = new WeatherForecast();
            forecast.setDate_id(i);
            forecast.setWeatherStart(forecastJObj.getString("img_title" + (i * 2 - 1)));
            forecast.setWeatherEnd(forecastJObj.getString("img_title" + i * 2));
            forecast.setFx(forecastJObj.getString("wind" + i));
            forecast.setFl(forecastJObj.getString("fl" + i));
            String tempRange = forecastJObj.getString("temp" + i);
            String[] tempArray = tempRange.split("~");
            String tempMin = tempArray[1];
            forecast.setTempMin(tempMin.substring(0, tempMin.length() - 1));
            String tempMax = tempArray[0];
            forecast.setTempMax(tempMax.substring(0, tempMax.length() - 1));
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, i - 1);//日期加对应的天数
            // 获取加后的日期对象
            Date d = calendar.getTime();
            // 转化成周x的形式存入数据库
            String week = weekFormat.format(d);
            forecast.setWeek("周" + week.substring(week.length() - 1, week.length()));
            dao.addToForecast(area_weather_id, forecast);
        }

        // ---------解析ALERT预警，并插入到数据库---------
        JSONArray alertJArray = dataJObj.getJSONArray(JSON_ALERT);
        if (alertJArray.length() != 0) {
            dao.setAlarm(area_weather_id, 1);
            JSONObject alertJObj = alertJArray.getJSONObject(0);
            WeatherAlarm alarm = new WeatherAlarm();
            alarm.setCityName(CityQueryDao.getAreaNameByWeatherId(alertJObj.getString("city_code")));
            alarm.setAlarmType(alertJObj.getString("type"));
            alarm.setAlarmDegree(alertJObj.getString("level"));
            String title = alertJObj.getString("title");
            alarm.setAlarmText(title.substring(0, title.length() - 1));
            alarm.setAlarm_details(alertJObj.getString("detail"));
            SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm");
            alarm.setTime(sf.format(new Date(alertJObj.getLong("pub_time"))));
            dao.addToAlarms(area_weather_id, alarm);
        }
    }

}
