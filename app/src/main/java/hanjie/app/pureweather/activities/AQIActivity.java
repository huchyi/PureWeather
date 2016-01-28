package hanjie.app.pureweather.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.bean.WeatherAQI;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.view.HalfCircleProgressView;
import hanjie.app.pureweather.view.TriangleDataDisplayView;

public class AQIActivity extends BaseToolbarActivity {

    private HalfCircleProgressView hcpv_aqi;
    private WeatherDao dao;
    private WeatherAQI aqi;

    private TextView tv_quality;
    private TriangleDataDisplayView tddv_pm;
    private TriangleDataDisplayView tddv_sono;
    private TextView tv_src;
    private TextView tv_time;

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new WeatherDao(this);
        getSupportActionBar().setTitle(dao.getMainAreaName());
        aqi = dao.getDataFromAQI(dao.getMainAreaId());
        findViews();
        hcpv_aqi.setMaxValue(500);
        hcpv_aqi.setValue(Float.parseFloat(aqi.getAqi()));
        showData();
    }

    private void showData() {
        tv_quality.setText(aqi.getQuality());
        int level = hcpv_aqi.getLevel();
        if (level == 1) {
            tv_quality.setTextColor(getResources().getColor(R.color.level_one));
        } else if (level == 2) {
            tv_quality.setTextColor(getResources().getColor(R.color.level_two));
        } else if (level == 3) {
            tv_quality.setTextColor(getResources().getColor(R.color.level_three));
        } else if (level == 4) {
            tv_quality.setTextColor(getResources().getColor(R.color.level_four));
        } else if (level == 5) {
            tv_quality.setTextColor(getResources().getColor(R.color.level_five));
        } else if (level == 6) {
            tv_quality.setTextColor(getResources().getColor(R.color.level_six));
        }
        tddv_pm.setLeftValue(aqi.getPm25());
        tddv_pm.setRightValue(aqi.getPm10());
        tddv_sono.setLeftValue(aqi.getSo2());
        tddv_sono.setRightValue(aqi.getNo2());
        tv_src.setText(aqi.getSrc());
        String time = aqi.getTime();
        tv_time.setText(time + " 发布");
    }

    private void findViews() {
        hcpv_aqi = (HalfCircleProgressView) findViewById(R.id.hcpv_aqi);
        tv_quality = (TextView) findViewById(R.id.tv_quality);
        tddv_pm = (TriangleDataDisplayView) findViewById(R.id.tddv_pm);
        tddv_sono = (TriangleDataDisplayView) findViewById(R.id.tddv_sono);
        tv_src = (TextView) findViewById(R.id.tv_src);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

    @Override
    public int createContentView() {
        return R.layout.activity_aqi;
    }

}
