package hanjie.app.pureweather.db.dao;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CityQueryDao {

    private static String sDBPath = "data/data/hanjie.app.pureweather/files/location.db";

    /**
     * 获取中国所有省份的名称(包括直辖市)
     *
     * @return 所有省份(直辖市)的名称集合
     */
    public static ArrayList<String> getProvinceList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sDBPath, null, SQLiteDatabase.OPEN_READONLY);
        ArrayList<String> provinceList = null;
        Cursor cursor = db.rawQuery("select distinct province_name from weathers", null);
        if (cursor != null) {
            provinceList = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("province_name"));
                provinceList.add(name);
            }
            cursor.close();
        }
        return provinceList;
    }

    /**
     * 根据省份(直辖市)获取该省份的所有城市
     *
     * @return 该省份(直辖市)的所有城市的名称集合
     */
    public static ArrayList<String> getCityListByProvince(String province) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sDBPath, null, SQLiteDatabase.OPEN_READONLY);
        ArrayList<String> cityList = null;
        Cursor cursor = db.rawQuery("select distinct city_name from weathers where province_name = ?", new String[]{province});
        if (cursor != null) {
            cityList = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("city_name"));
                cityList.add(name);
            }
            cursor.close();
        }
        return cityList;
    }

    /**
     * 根据省份(直辖市)获取该省份的所有城市
     *
     * @return 该省份(直辖市)的所有城市的名称集合
     */
    public static ArrayList<String> getAreaListByCity(String city) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sDBPath, null, SQLiteDatabase.OPEN_READONLY);
        ArrayList<String> areaList = null;
        Cursor cursor = db.rawQuery("select distinct area_name from weathers where city_name = ?", new String[]{city});
        if (cursor != null) {
            areaList = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("area_name"));
                areaList.add(name);
            }
            cursor.close();
        }
        return areaList;
    }

    /**
     * 根据市(县)名获取其天气id
     *
     * @return 市(县)的天气id
     */
    public static String getWeatherIdByAreaName(String areaName) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sDBPath, null, SQLiteDatabase.OPEN_READONLY);
        String weather_id = null;
        Cursor cursor = db.rawQuery("select distinct weather_id from weathers where area_name = ?", new String[]{areaName});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                weather_id = cursor.getString(cursor.getColumnIndex("weather_id"));
            }
            cursor.close();
        }
        return weather_id;
    }

    /**
     * 根据天气id获取其市/县名
     *
     * @param weatherId 天气id
     * @return 市/县名
     */
    public static String getAreaNameByWeatherId(String weatherId) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sDBPath, null, SQLiteDatabase.OPEN_READONLY);
        String areaName = null;
        Cursor cursor = db.rawQuery("select area_name from weathers where weather_id = ?", new String[]{weatherId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                areaName = cursor.getString(cursor.getColumnIndex("area_name"));
            }
            cursor.close();
        }
        return areaName;
    }

    /**
     * 根据关键字从数据库中检索包含关键字的城市
     *
     * @param keyWord 需要搜索的关键字
     * @return 返回搜索到的城市的集合
     */
    public static ArrayList<String> searchByKeyWord(String keyWord) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sDBPath, null, SQLiteDatabase.OPEN_READONLY);
        ArrayList<String> searchList = new ArrayList<String>();
        Cursor cursor = db.rawQuery("select area_name from weathers where area_name like ?", new String[]{"%" + keyWord + "%"});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                searchList.add(cursor.getString(cursor.getColumnIndex("area_name")));
            }
            cursor.close();
        }
        return searchList;
    }

}
