package hanjie.app.pureweather.db.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import hanjie.app.pureweather.bean.WeatherAQI;
import hanjie.app.pureweather.bean.WeatherAlarm;
import hanjie.app.pureweather.bean.WeatherForecast;
import hanjie.app.pureweather.bean.WeatherRealtime;
import hanjie.app.pureweather.bean.WeatherYesterday;
import hanjie.app.pureweather.bean.WeatherZhiShu;
import hanjie.app.pureweather.db.WeatherDBOpenHelper;

public class WeatherDao {

    /**
     * 数据库操作对象
     */
    private SQLiteDatabase mDB;
    /**
     * 上下文对象
     */
    private Context mContext;

    public WeatherDao(Context context) {
        mContext = context;
        WeatherDBOpenHelper helper = new WeatherDBOpenHelper(context);
        mDB = helper.getWritableDatabase();
    }

    //------------------------REALTIME TABLE MODIFY START--------------------------------

    /**
     * 向realtime表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     * @param realtime        WeatherRealtime类的实例
     */
    public void addToRealtime(String area_weather_id, WeatherRealtime realtime) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.REALTIME_UPDATE_TIME, realtime.getUpdatetime());
            cv.put(WeatherDBOpenHelper.REALTIME_WENDU, realtime.getWendu());
            cv.put(WeatherDBOpenHelper.REALTIME_FENGLI, realtime.getFengli());
            cv.put(WeatherDBOpenHelper.REALTIME_SHIDU, realtime.getShidu());
            cv.put(WeatherDBOpenHelper.REALTIME_FENGXIANG, realtime.getFengxiang());
            cv.put(WeatherDBOpenHelper.REALTIME_SUNRISE, realtime.getSunrise());
            cv.put(WeatherDBOpenHelper.REALTIME_SUNSET, realtime.getSunset());
            cv.put(WeatherDBOpenHelper.REALTIME_WEATHER, realtime.getWeather());
            if (isExistInRealtime(area_weather_id)) {
                mDB.update(WeatherDBOpenHelper.TABLE_REALTIME, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            } else {
                mDB.insert(WeatherDBOpenHelper.TABLE_REALTIME, null, cv);
            }
            mDB.setTransactionSuccessful();
            Log.d("bingo", "setTransactionSuccessful realTime~~~");
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县的天气id查询其是否在realtime表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInRealtime(String area_weather_id) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_REALTIME, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县的天气id从realtime表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromRealtime(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_REALTIME, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 从数据库查询实时天气
     *
     * @param area_weather_id 要查询的area_weather_id
     * @return WeatherRealtime实例
     */
    public WeatherRealtime getDataFromRealtime(String area_weather_id) {
        WeatherRealtime realtime = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_REALTIME, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String updatetime = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_UPDATE_TIME));
                String wendu = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_WENDU));
                String fengli = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_FENGLI));
                String shidu = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_SHIDU));
                String fengxiang = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_FENGXIANG));
                String sunrise = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_SUNRISE));
                String sunset = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_SUNSET));
                String weather = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_WEATHER));
                realtime = new WeatherRealtime(updatetime, wendu, fengli, shidu, fengxiang, sunrise, sunset, weather);
            }
        }
        return realtime;
    }

    /**
     * 获取所有已选择的市/县的实时天气的集合,有天气信息就将温度放进去，没有温度就将N/A放进去
     *
     * @return ArrayList集合
     */
    public ArrayList<String> getAreaRTTempList() {
        ArrayList<String> RTTempList = new ArrayList<String>();
        ArrayList<String> areasIdList = getAreasIdList();
        for (String id : areasIdList) {
            if (isExistInRealtime(id)) {
                Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_REALTIME, new String[]{WeatherDBOpenHelper.REALTIME_WENDU}, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{id}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        RTTempList.add(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.REALTIME_WENDU)));
                    }
                    cursor.close();
                }
            } else {
                RTTempList.add("N/A");
            }
        }
        return RTTempList;
    }


    //------------------------REALTIME TABLE MODIFY END--------------------------------

    //----------------------YESTERDAY TABLE MODIFY START---------------------------

    /**
     * 向yesterday表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     * @param yesterday       WeatherYesterday实例
     */
    public void addToYesterday(String area_weather_id, WeatherYesterday yesterday) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.YESTERDAY_DATE, yesterday.getDate());
            cv.put(WeatherDBOpenHelper.YESTERDAY_TEMPMIN, yesterday.getTempMin());
            cv.put(WeatherDBOpenHelper.YESTERDAY_TEMPMAX, yesterday.getTempMax());
            cv.put(WeatherDBOpenHelper.YESTERDAY_WEATHERSTART, yesterday.getWeatherStart());
            cv.put(WeatherDBOpenHelper.YESTERDAY_WEATHEREND, yesterday.getWeatherEnd());
            if (isExistInYesterday(area_weather_id)) {
                mDB.update(WeatherDBOpenHelper.TABLE_YESTERDAY, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            } else {
                mDB.insert(WeatherDBOpenHelper.TABLE_YESTERDAY, null, cv);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县的天气id获取其昨天的天气
     *
     * @param area_weather_id 市/县的天气id
     * @return 对应的WeatherYesterday实例
     */
    public WeatherYesterday getDataFromYesterday(String area_weather_id) {
        WeatherYesterday yesterday = new WeatherYesterday();
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_YESTERDAY, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                yesterday.setDate(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.YESTERDAY_DATE)));
                yesterday.setTempMin(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.YESTERDAY_TEMPMIN)));
                yesterday.setTempMax(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.YESTERDAY_TEMPMAX)));
                yesterday.setWeatherStart(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.YESTERDAY_WEATHERSTART)));
                yesterday.setWeatherEnd(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.YESTERDAY_WEATHEREND)));
            }
            cursor.close();
        }
        return yesterday;
    }

    /**
     * 根据市/县的天气id查询其是否在yesterday表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInYesterday(String area_weather_id) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_YESTERDAY, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县的天气id从yesterday表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromYesterday(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_YESTERDAY, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    //------------------------YESTERDAY TABLE MODIFY END--------------------------------

    //------------------------FORECAST TABLE MODIFY START--------------------------------

    /**
     * 向forecast表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     * @param forecast        WeatherForecast实例
     */
    public void addToForecast(String area_weather_id, WeatherForecast forecast) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.FORECAST_DATE_ID, forecast.getDate_id());
            cv.put(WeatherDBOpenHelper.FORECAST_WEEK, forecast.getWeek());
            cv.put(WeatherDBOpenHelper.FORECAST_WEATHERSTART, forecast.getWeatherStart());
            cv.put(WeatherDBOpenHelper.FORECAST_WEATHEREND, forecast.getWeatherEnd());
            cv.put(WeatherDBOpenHelper.FORECAST_TEMPMIN, forecast.getTempMin());
            cv.put(WeatherDBOpenHelper.FORECAST_TEMPMAX, forecast.getTempMax());
            cv.put(WeatherDBOpenHelper.FORECAST_FX, forecast.getFx());
            cv.put(WeatherDBOpenHelper.FORECAST_FL, forecast.getFl());
            if (isExistInForecast(area_weather_id, forecast.getDate_id())) {
                mDB.update(WeatherDBOpenHelper.TABLE_FORECAST, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ? and " + WeatherDBOpenHelper.FORECAST_DATE_ID + " = ?", new String[]{area_weather_id, String.valueOf(forecast.getDate_id())});
            } else {
                mDB.insert(WeatherDBOpenHelper.TABLE_FORECAST, null, cv);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县的天气id查询其是否在forecast表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInForecast(String area_weather_id, int date_id) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_FORECAST, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ? and " + WeatherDBOpenHelper.FORECAST_DATE_ID + " = ?", new String[]{area_weather_id, String.valueOf(date_id)}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县的天气id从forecast表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromForecast(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_FORECAST, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县天气id获取其未来5天的天气预报(包括今天)
     *
     * @param area_weather_id 市/县天气id
     * @return 内含有5个WeatherForecast实例的ArrayList集合
     */
    public ArrayList<WeatherForecast> getDataListFromForecast(String area_weather_id) {
        ArrayList<WeatherForecast> forecastList = new ArrayList<WeatherForecast>();
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_FORECAST, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, WeatherDBOpenHelper.FORECAST_DATE_ID);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int date_id = cursor.getInt(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_DATE_ID));
                String week = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_WEEK));
                String weatherStart = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_WEATHERSTART));
                String weatherEnd = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_WEATHEREND));
                String tempMin = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_TEMPMIN));
                String tempMax = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_TEMPMAX));
                String fx = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_FX));
                String fl = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.FORECAST_FL));
                WeatherForecast forecast = new WeatherForecast(date_id, week, weatherStart, weatherEnd, tempMin, tempMax, fx, fl);
                forecastList.add(forecast);
            }
            cursor.close();
        }
        return forecastList;
    }

    //------------------------FORECAST TABLE MODIFY END--------------------------------

    //------------------------ZHISHU TABLE MODIFY END--------------------------------

    /**
     * 向zhishu表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     * @param zhiShu          WeatherZhiShu实例
     */
    public void addToZhishu(String area_weather_id, WeatherZhiShu zhiShu) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.ZHISHU_NAME, zhiShu.getName());
            cv.put(WeatherDBOpenHelper.ZHISHU_NAME_ID, zhiShu.getName_id());
            cv.put(WeatherDBOpenHelper.ZHISHU_VALUE, zhiShu.getValue());
            if (isExistInZhishu(area_weather_id, zhiShu.getName())) {
                mDB.update(WeatherDBOpenHelper.TABLE_ZHISHU, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ? and " + WeatherDBOpenHelper.ZHISHU_NAME + " = ?", new String[]{area_weather_id, zhiShu.getName()});
            } else {
                mDB.insert(WeatherDBOpenHelper.TABLE_ZHISHU, null, cv);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县天气id获取其生活指数
     *
     * @param area_weather_id 市/县天气id
     * @return 含有WeatherZhiShu实例的ArrayList集合
     */
    public ArrayList<WeatherZhiShu> getDataListFromZhishu(String area_weather_id) {
        ArrayList<WeatherZhiShu> zhiShuList = new ArrayList<WeatherZhiShu>();
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_ZHISHU, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, WeatherDBOpenHelper.ZHISHU_NAME_ID);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ZHISHU_NAME));
                int name_id = cursor.getInt(cursor.getColumnIndex(WeatherDBOpenHelper.ZHISHU_NAME_ID));
                String value = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ZHISHU_VALUE));
                WeatherZhiShu zhishu = new WeatherZhiShu(name, name_id, value);
                Log.d("mytag", zhishu.toString());
                zhiShuList.add(zhishu);
            }
        }
        return zhiShuList;
    }

    /**
     * 根据市/县的天气id查询其是否在zhishu表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInZhishu(String area_weather_id, String name) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_ZHISHU, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ? and " + WeatherDBOpenHelper.ZHISHU_NAME + " = ?", new String[]{area_weather_id, name}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县的天气id从zhishu表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromZhishu(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_ZHISHU, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    //------------------------ZHISHU TABLE MODIFY END--------------------------------

    //------------------------AREAS TABLE MODIFY START------------------------------

    /**
     * 向areas表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void addToAreas(String area_weather_id, String name) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.AREAS_NAME, name);
            if (isExistInAreasById(area_weather_id)) {
                mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            } else {
                mDB.insert(WeatherDBOpenHelper.TABLE_AREAS, null, cv);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 设置mainarea
     *
     * @param area_weather_id 要设置的市/县的天气id
     * @param mainArea        0表示设置成非主area，1表示设置成主area
     */
    public void setMainArea(String area_weather_id, int mainArea) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREAS_MAIN_AREA, mainArea);
            mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 设置缓存
     *
     * @param area_weather_id 市/县天气id
     * @param cache           0表示没有，1表示有
     */
    public void setCache(String area_weather_id, int cache) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREAS_CACHE, cache);
            mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 设置上次的更新时间(毫秒值)
     *
     * @param area_weather_id
     * @param lastUpdateTime
     */
    public void setLastUpdateTime(String area_weather_id, String lastUpdateTime) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREAS_LAST_UPDATE_TIME, lastUpdateTime);
            mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 设置是否有天气预警
     *
     * @param area_weather_id 市/县天气id
     * @param alarm           0表示没有，1表示有
     */
    public void setAlarm(String area_weather_id, int alarm) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREAS_ALARM, alarm);
            mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 设置是否有AQI信息
     *
     * @param area_weather_id 市/县天气id
     * @param aqi             0表示没有，1表示有
     */
    public void setAqi(String area_weather_id, int aqi) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREAS_AQI, aqi);
            mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县天气id获取上次更新时间(系统毫秒值)
     *
     * @param area_weather_id 市/县天气id
     * @return 返回上次更新的时间(系统毫秒值), 没有更新过默认返回0
     */
    public String getLastUpdateTime(String area_weather_id) {
        String lastUpdateTime = "0";
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                lastUpdateTime = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_LAST_UPDATE_TIME));
            }
            cursor.close();
        }
        return lastUpdateTime;
    }


    /**
     * 根据市/县的天气id查询其是否在areas表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInAreasById(String area_weather_id) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县的名称查询其是否在areas表中有记录
     *
     * @param name 市/县的名称
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInAreasByName(String name) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREAS_NAME + " = ?", new String[]{name}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县的天气id从areas表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromAreas(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_AREAS, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 查询areas表中的所有记录
     *
     * @return 返回记录的天气的id集合
     */
    public ArrayList<String> getAreasIdList() {
        ArrayList<String> areasIdList = new ArrayList<String>();
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                areasIdList.add(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AREA_WEATHER_ID)));
            }
            cursor.close();
        }
        return areasIdList;
    }

    /**
     * 查询areas表中的所有记录
     *
     * @return 返回记录的市/县名称的集合
     */
    public ArrayList<String> getAreasNameList() {
        ArrayList<String> areasNameList = new ArrayList<String>();
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                areasNameList.add(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_NAME)));
            }
            cursor.close();
        }
        return areasNameList;
    }

    /**
     * 获取主市/县的天气ID
     *
     * @return 有主市/县则返回其天气id，没有则返回null
     */
    public String getMainAreaId() {
        String mainAreaId = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREAS_MAIN_AREA + " = ?", new String[]{"1"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mainAreaId = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AREA_WEATHER_ID));
            }
            cursor.close();
        }
        return mainAreaId;
    }

    /**
     * 获取主市/县的名称
     *
     * @return 有主市/县则返回其名称，没有则返回null
     */
    public String getMainAreaName() {
        String mainAreaName = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREAS_MAIN_AREA + " = ?", new String[]{"1"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mainAreaName = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_NAME));
            }
            cursor.close();
        }
        return mainAreaName;
    }

    /**
     * 根据市/县天气id查询其在数据库是否有缓存的天气数据
     *
     * @param area_weather_id 市/县天气id
     * @return 存在返回true，否则返回false
     */
    public boolean haveCache(String area_weather_id) {
        boolean haveCache = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int cache = cursor.getInt(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_CACHE));
                if (cache == 1) {
                    haveCache = true;
                }
            }
            cursor.close();
        }
        return haveCache;
    }

    /**
     * 根据市/县天气id查询其在数据库是否有天气预警信息
     *
     * @param area_weather_id 市/县天气id
     * @return 存在返回true，否则返回false
     */
    public boolean haveAlarm(String area_weather_id) {
        boolean haveAlarm = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int cache = cursor.getInt(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_ALARM));
                if (cache == 1) {
                    haveAlarm = true;
                }
            }
            cursor.close();
        }
        return haveAlarm;
    }

    /**
     * 根据市/县天气id设置其预警上次的发布时间
     *
     * @param area_weather_id 市/县天气id
     * @param time            预警上次的发布时间
     */
    public void setAlarmLastUpdateTime(String area_weather_id, String time) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREAS_ALARM_LAST_UPDATE_TIME, time);
            mDB.update(WeatherDBOpenHelper.TABLE_AREAS, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县天气id查询其预警的上次发布时间
     *
     * @param area_weather_id 市/县天气id
     * @return 查询其预警的上次发布时间
     */
    public String getAlarmLastUpdateTime(String area_weather_id) {
        String time = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                time = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_ALARM_LAST_UPDATE_TIME));
            }
            cursor.close();
        }
        return time;
    }


    //------------------------AREAS TABLE MODIFY END--------------------------------

    //------------------------ALARMS TABLE MODIFY START--------------------------------

    /**
     * 向alarms表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     * @param alarm           WeatherAlarm类实例
     */
    public void addToAlarms(String area_weather_id, WeatherAlarm alarm) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.ALARMS_CITY_NAME, alarm.getCityName());
            cv.put(WeatherDBOpenHelper.ALARMS_TYPE, alarm.getAlarmType());
            cv.put(WeatherDBOpenHelper.ALARMS_DEGREE, alarm.getAlarmDegree());
            cv.put(WeatherDBOpenHelper.ALARMS_TEXT, alarm.getAlarmText());
            cv.put(WeatherDBOpenHelper.ALARMS_DETAILS, alarm.getAlarm_details());
            cv.put(WeatherDBOpenHelper.ALARMS_TIME, alarm.getTime());
            if (isExistInAlarms(area_weather_id)) {
                deleteFromAlarms(area_weather_id);
            }
            mDB.insert(WeatherDBOpenHelper.TABLE_ALARMS, null, cv);
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县的天气id查询其是否在alarms表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInAlarms(String area_weather_id) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_ALARMS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县天气id获取其天气预警信息
     *
     * @param area_weather_id 市/县天气id
     * @return 天气预警信息WeatherAlarm类实例
     */
    public WeatherAlarm getDataFromAlarms(String area_weather_id) {
        WeatherAlarm alarm = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_ALARMS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                alarm = new WeatherAlarm();
                alarm.setCityName(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_CITY_NAME)));
                alarm.setAlarmType(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_TYPE)));
                alarm.setAlarmDegree(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_DEGREE)));
                alarm.setAlarmText(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_TEXT)));
                alarm.setAlarm_details(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_DETAILS)));
                alarm.setTime(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_TIME)));
            }
        }
        return alarm;
    }

    /**
     * 根据市/县天气id获取其预警的简要说明信息(类型+级别)
     *
     * @param area_weather_id 市/县天气id
     * @return 预警的简要说明信息(类型+级别)
     */
    public String getSimpleAlarmDesc(String area_weather_id) {
        String simpleDesc = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_ALARMS, new String[]{WeatherDBOpenHelper.ALARMS_TYPE, WeatherDBOpenHelper.ALARMS_DEGREE}, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String alarmType = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_TYPE));
                String alarmDegree = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_DEGREE));
                StringBuilder sb = new StringBuilder();
                sb.append(alarmType);
                sb.append(alarmDegree);
                sb.append("预警");
                simpleDesc = sb.toString();
            }
            cursor.close();
        }
        return simpleDesc;
    }

    /**
     * 根据市/县天气id获取其预警的发布时间
     *
     * @param area_weather_id 市/县天气id
     * @return 预警的发布时间
     */
    public String getAlarmUpdateTime(String area_weather_id) {
        String time = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_ALARMS, new String[]{WeatherDBOpenHelper.ALARMS_TIME}, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                time = cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.ALARMS_TIME));
            }
            cursor.close();
        }
        return time;
    }

    /**
     * 根据市/县的天气id从alarms表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromAlarms(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_ALARMS, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    //------------------------ALARMS TABLE MODIFY END--------------------------------

    //------------------------AQI TABLE MODIFY START---------------------------------

    /**
     * 向AQI表中插入一条记录，如果该市/县的天气id已在表中存在，则更新此条记录，反之添加一条新的记录
     *
     * @param area_weather_id 市/县的天气id
     * @param aqi             WeatherAQI类实例
     */
    public void addToAQI(String area_weather_id, WeatherAQI aqi) {
        mDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(WeatherDBOpenHelper.AREA_WEATHER_ID, area_weather_id);
            cv.put(WeatherDBOpenHelper.AQI_AQI, aqi.getAqi());
            cv.put(WeatherDBOpenHelper.AQI_PM25, aqi.getPm25());
            cv.put(WeatherDBOpenHelper.AQI_PM10, aqi.getPm10());
            cv.put(WeatherDBOpenHelper.AQI_TIME, aqi.getTime());
            cv.put(WeatherDBOpenHelper.AQI_SO2, aqi.getSo2());
            cv.put(WeatherDBOpenHelper.AQI_NO2, aqi.getNo2());
            cv.put(WeatherDBOpenHelper.AQI_SRC, aqi.getSrc());
            cv.put(WeatherDBOpenHelper.AQI_QUALITY, aqi.getQuality());
            if (isExistInAQI(area_weather_id)) {
                mDB.update(WeatherDBOpenHelper.TABLE_AQI, cv, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            } else {
                mDB.insert(WeatherDBOpenHelper.TABLE_AQI, null, cv);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县的天气id查询其是否在AQI表中有记录
     *
     * @param area_weather_id 市/县的天气id
     * @return 存在返回true，不存在返回false
     */
    public boolean isExistInAQI(String area_weather_id) {
        boolean isExist = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AQI, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    /**
     * 根据市/县天气id获取其AQI信息
     *
     * @param area_weather_id 市/县天气id
     * @return 天气预警信息WeatherAQI类实例
     */
    public WeatherAQI getDataFromAQI(String area_weather_id) {
        WeatherAQI aqi = null;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AQI, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                aqi = new WeatherAQI();
                aqi.setAqi(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_AQI)));
                aqi.setPm25(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_PM25)));
                aqi.setPm10(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_PM10)));
                aqi.setTime(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_TIME)));
                aqi.setSo2(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_SO2)));
                aqi.setNo2(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_NO2)));
                aqi.setSrc(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_SRC)));
                aqi.setQuality(cursor.getString(cursor.getColumnIndex(WeatherDBOpenHelper.AQI_QUALITY)));
            }
        }
        return aqi;
    }

    /**
     * 根据市/县的天气id从AQI表中删除其记录
     *
     * @param area_weather_id 市/县的天气id
     */
    public void deleteFromAQI(String area_weather_id) {
        mDB.beginTransaction();
        try {
            mDB.delete(WeatherDBOpenHelper.TABLE_AQI, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id});
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    /**
     * 根据市/县天气id查询其在数据库是否有AQI信息
     *
     * @param area_weather_id 市/县天气id
     * @return 存在返回true，否则返回false
     */
    public boolean haveAQI(String area_weather_id) {
        boolean haveAQI = false;
        Cursor cursor = mDB.query(WeatherDBOpenHelper.TABLE_AREAS, null, WeatherDBOpenHelper.AREA_WEATHER_ID + " = ?", new String[]{area_weather_id}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int aqi = cursor.getInt(cursor.getColumnIndex(WeatherDBOpenHelper.AREAS_AQI));
                if (aqi == 1) {
                    haveAQI = true;
                }
            }
            cursor.close();
        }
        return haveAQI;
    }


    //------------------------AQI TABLE MODIFY END----------------------------------


    /**
     * 删除area_weather_id在数据库中的所有记录
     *
     * @param area_weather_id
     * @return
     */
    public boolean deleteFromDB(String area_weather_id) {
        boolean isSuccess = false;
        mDB.beginTransaction();
        try {
            deleteFromAlarms(area_weather_id);
            deleteFromZhishu(area_weather_id);
            deleteFromYesterday(area_weather_id);
            deleteFromForecast(area_weather_id);
            deleteFromRealtime(area_weather_id);
            deleteFromAreas(area_weather_id);
            deleteFromAQI(area_weather_id);
            mDB.setTransactionSuccessful();
            isSuccess = true;
        } finally {
            mDB.endTransaction();
        }
        return isSuccess;
    }

}
