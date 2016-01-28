package hanjie.app.pureweather.activities;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.bean.WeatherForecast;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.utils.WeatherUtils;

public class ForecastDetailsActivity extends BaseToolbarActivity {

    private WeatherDao dao;
    private WeatherForecast forecast;
    private TextView tv_tempRange;
    private ImageView iv_dayType;
    private TextView tv_dayType;
    private ImageView iv_nightType;
    private TextView tv_nightType;
    private TextView tv_fx;
    private TextView tv_fl;
    private LinearLayout root_bg;

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
        setTranslucentBarColor(R.color.notification_transparent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getIntent().getIntExtra("position", 0);
        dao = new WeatherDao(this);
        String mainAreaId = dao.getMainAreaId();
        ArrayList<WeatherForecast> forecastList = dao.getDataListFromForecast(mainAreaId);
        forecast = forecastList.get(position);
        if (position == 0) {
            getSupportActionBar().setTitle("今天");
        } else if (position == 1) {
            getSupportActionBar().setTitle("明天");
        } else if (position == 2) {
            getSupportActionBar().setTitle("后天");
        } else {
            getSupportActionBar().setTitle(forecast.getWeek());
        }
        findViews();
        showData();
        checkBg();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkBg() {
        File customImage = new File(Environment.getExternalStorageDirectory(), "purebg.png");
        if (customImage.exists()) {
            root_bg.setBackground(Drawable.createFromPath(customImage.getAbsolutePath()));
        }
    }

    private void showData() {
        String tempMin = forecast.getTempMin();
        String tempMax = forecast.getTempMax();
        tv_tempRange.setText(tempMin + "° ~ " + tempMax + " °C");
        String dayType = forecast.getWeatherStart();
        iv_dayType.setImageResource(WeatherUtils.getWhiteIconIdByTypeName(dayType));
        tv_dayType.setText(dayType);
        String nightType = forecast.getWeatherEnd();
        iv_nightType.setImageResource(WeatherUtils.getWhiteIconIdByTypeName(nightType));
        tv_nightType.setText(nightType);
        tv_fx.setText(forecast.getFx());
        tv_fl.setText(forecast.getFl());
    }

    private void findViews() {
        tv_tempRange = (TextView) findViewById(R.id.tv_tempRange);
        iv_dayType = (ImageView) findViewById(R.id.iv_dayType);
        tv_dayType = (TextView) findViewById(R.id.tv_dayType);
        iv_nightType = (ImageView) findViewById(R.id.iv_nightType);
        tv_nightType = (TextView) findViewById(R.id.tv_nightType);
        tv_fx = (TextView) findViewById(R.id.tv_fx);
        tv_fl = (TextView) findViewById(R.id.tv_fl);
        root_bg = (LinearLayout) findViewById(R.id.root_bg);
    }

    @Override
    public int createContentView() {
        return R.layout.activity_forecast_details;
    }

}
