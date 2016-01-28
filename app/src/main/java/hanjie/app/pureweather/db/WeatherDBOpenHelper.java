package hanjie.app.pureweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherDBOpenHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    public static final String DB_NAME = "weather.db";
    /**
     * 数据库版本
     */
    public static final int DB_VERSION = 1;
    /**
     * 市/县的天气ID
     */
    public static final String AREA_WEATHER_ID = "area_weather_id";
    /**
     * realtime表
     */
    public static final String TABLE_REALTIME = "realtime";
    /**
     * realtime表---天气的更新时间
     */
    public static final String REALTIME_UPDATE_TIME = "updatetime";
    /**
     * realtime表---温度
     */
    public static final String REALTIME_WENDU = "wendu";
    /**
     * realtime表---风力
     */
    public static final String REALTIME_FENGLI = "fengli";
    /**
     * realtime表---湿度
     */
    public static final String REALTIME_SHIDU = "shidu";
    /**
     * realtime表---风向
     */
    public static final String REALTIME_FENGXIANG = "fengxiang";
    /**
     * realtime表---日出时间
     */
    public static final String REALTIME_SUNRISE = "sunrise";
    /**
     * realtime表---日落时间
     */
    public static final String REALTIME_SUNSET = "sunset";
    /**
     * realtime表---实时天气
     */
    public static final String REALTIME_WEATHER = "weather";
    /**
     * yesterday表
     */
    public static final String TABLE_YESTERDAY = "yesterday";
    /**
     * yesterday表---昨天的日期
     */
    public static final String YESTERDAY_DATE = "date";
    /**
     * yesterday表---昨天的最低温度
     */
    public static final String YESTERDAY_TEMPMIN = "tempMin";
    /**
     * yesterday表---昨天的最高温度
     */
    public static final String YESTERDAY_TEMPMAX = "tempMax";
    /**
     * yesterday表---昨天的Start天气
     */
    public static final String YESTERDAY_WEATHERSTART = "weatherStart";
    /**
     * yesterday表---昨天的End天气
     */
    public static final String YESTERDAY_WEATHEREND = "weatherEnd";
    /**
     * forecast表
     */
    public static final String TABLE_FORECAST = "forecast";
    /**
     * forecast表---预报天数的id
     */
    public static final String FORECAST_DATE_ID = "date_id";
    /**
     * forecast表---预报的日期星期
     */
    public static final String FORECAST_WEEK = "week";
    /**
     * forecast表---Start天气
     */
    public static final String FORECAST_WEATHERSTART = "weatherStart";
    /**
     * forecast表---End天气
     */
    public static final String FORECAST_WEATHEREND = "weatherEnd";
    /**
     * forecast表---最低温度
     */
    public static final String FORECAST_TEMPMIN = "tempMin";
    /**
     * forecast表---最高温度
     */
    public static final String FORECAST_TEMPMAX = "tempMax";
    /**
     * forecast表---风向
     */
    public static final String FORECAST_FX = "fx";
    /**
     * forecast表---风力
     */
    public static final String FORECAST_FL = "fl";

    /**
     * zhishu表
     */
    public static final String TABLE_ZHISHU = "zhishu";
    /**
     * zhishu表---名称
     */
    public static final String ZHISHU_NAME = "name";
    /**
     * zhishu表---名称id
     */
    public static final String ZHISHU_NAME_ID = "name_id";
    /**
     * zhishu表---值
     */
    public static final String ZHISHU_VALUE = "value";
    /**
     * areas表
     */
    public static final String TABLE_AREAS = "areas";
    /**
     * areas表---市/县的名称
     */
    public static final String AREAS_NAME = "name";
    /**
     * areas表---是否为主要城市字段
     */
    public static final String AREAS_MAIN_AREA = "mainarea";
    /**
     * areas表---数据库是否有缓存数据
     */
    public static final String AREAS_CACHE = "cache";
    /**
     * areas表---上次的更新时间(系统毫秒值)
     */
    public static final String AREAS_LAST_UPDATE_TIME = "lastupdatetime";
    /**
     * areas表---是否有预警
     */
    public static final String AREAS_ALARM = "alarm";
    /**
     * areas表---是否有AQI
     */
    public static final String AREAS_AQI = "aqi";
    /**
     * areas表---预警的上次发布日期
     */
    public static final String AREAS_ALARM_LAST_UPDATE_TIME = "alarmlastupdatetime";
    /**
     * alarms表
     */
    public static final String TABLE_ALARMS = "alarms";
    /**
     * alarms表---预警城市名
     */
    public static final String ALARMS_CITY_NAME = "cityName";
    /**
     * alarms表---预警类型
     */
    public static final String ALARMS_TYPE = "alarmType";
    /**
     * alarms表---预警级别
     */
    public static final String ALARMS_DEGREE = "alarmDegree";
    /**
     * alarms表---预警文本
     */
    public static final String ALARMS_TEXT = "alarmText";
    /**
     * alarms表---预警详细信息
     */
    public static final String ALARMS_DETAILS = "alarm_details";
    /**
     * alarms表---预警发布时间
     */
    public static final String ALARMS_TIME = "time";
    /**
     * AQI表
     */
    public static final String TABLE_AQI = "aqi";
    /**
     * AQI表---AQI指数
     */
    public static final String AQI_AQI = "aqi";
    /**
     * AQI表---PM2.5值
     */
    public static final String AQI_PM25 = "pm25";
    /**
     * AQI表---PM10值
     */
    public static final String AQI_PM10 = "pm10";
    /**
     * AQI表---发布时间
     */
    public static final String AQI_TIME = "time";
    /**
     * AQI表---SO2
     */
    public static final String AQI_SO2 = "so2";
    /**
     * AQI表---NO2
     */
    public static final String AQI_NO2 = "no2";
    /**
     * AQI表---数据来源
     */
    public static final String AQI_SRC = "src";
    /**
     * AQI表---AQI质量
     */
    public static final String AQI_QUALITY = "quality";


    public WeatherDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("mytag", "DB onCreate");
        // 创建realtime表
        db.execSQL("create table " + TABLE_REALTIME + "(" + AREA_WEATHER_ID + " text primary key ," + REALTIME_UPDATE_TIME + " text," + REALTIME_WENDU + " text," + REALTIME_FENGLI + " text," + REALTIME_SHIDU + " text," + REALTIME_FENGXIANG + " text," + REALTIME_SUNRISE + " text," + REALTIME_SUNSET + " text," + REALTIME_WEATHER + " text)");
        db.execSQL("create table " + TABLE_YESTERDAY + "(" + AREA_WEATHER_ID + " text primary key , " + YESTERDAY_DATE + " text , " + YESTERDAY_TEMPMIN + " text , " + YESTERDAY_TEMPMAX + " text , " + YESTERDAY_WEATHERSTART + " text , " + YESTERDAY_WEATHEREND + " text)");
        db.execSQL("create table " + TABLE_FORECAST + "(" + AREA_WEATHER_ID + " text , " + FORECAST_DATE_ID + " Integer ," + FORECAST_WEEK + " text , " + FORECAST_WEATHERSTART + " text ," + FORECAST_WEATHEREND + " text , " + FORECAST_TEMPMIN + " text ," + FORECAST_TEMPMAX + " text ," + FORECAST_FX + " text , " + FORECAST_FL + " text , primary key( " + AREA_WEATHER_ID + " , " + FORECAST_DATE_ID + "))");
        db.execSQL("create table " + TABLE_ZHISHU + "(" + AREA_WEATHER_ID + " text," + ZHISHU_NAME_ID + " Integer," + ZHISHU_NAME + " text," + ZHISHU_VALUE + " text,primary key(" + AREA_WEATHER_ID + "," + ZHISHU_NAME_ID + "))");
        db.execSQL("create table " + TABLE_AREAS + "(" + AREA_WEATHER_ID + " text primary key, " + AREAS_NAME + " text ," + AREAS_MAIN_AREA + " integer default 0," + AREAS_CACHE + " integer default 0," + AREAS_LAST_UPDATE_TIME + " text default 0," + AREAS_ALARM + " integer default 0," + AREAS_ALARM_LAST_UPDATE_TIME + " text default 0," + AREAS_AQI + " text default 0)");
        db.execSQL("create table " + TABLE_ALARMS + "(" + AREA_WEATHER_ID + " text primary key , " + ALARMS_CITY_NAME + " text , " + ALARMS_TYPE + " text , " + ALARMS_DEGREE + " text , " + ALARMS_TEXT + " text , " + ALARMS_DETAILS + " text , " + ALARMS_TIME + " text)");
        db.execSQL("create table " + TABLE_AQI + "(" + AREA_WEATHER_ID + " text primary key , " + AQI_AQI + " text , " + AQI_PM25 + " text , " + AQI_PM10 + " text , " + AQI_TIME + " text , " + AQI_SO2 + " text , " + AQI_NO2 + " text , " + AQI_SRC + " text , " + AQI_QUALITY + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("mytag", "DB onUpgrade");
    }
}
