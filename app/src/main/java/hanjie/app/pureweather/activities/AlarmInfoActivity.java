package hanjie.app.pureweather.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.bean.WeatherAlarm;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.utils.AnimationUtils;

public class AlarmInfoActivity extends BaseToolbarActivity {

    private WeatherDao dao;
    private TextView tv_alarmInfo;
    private TextView tv_alarmText;
    private TextView tv_alarmDetails;
    private TextView tv_time;
    private String mainAreaId;

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new WeatherDao(AlarmInfoActivity.this);
        String mainAreaName = dao.getMainAreaName();
        getSupportActionBar().setTitle(mainAreaName);
        mainAreaId = dao.getMainAreaId();
        findViews();
        showData();
    }

    @Override
    public int createContentView() {
        return R.layout.activity_alarm_info;
    }

    private void showData() {
        WeatherAlarm alarm = dao.getDataFromAlarms(mainAreaId);
        tv_alarmInfo.setText(dao.getSimpleAlarmDesc(mainAreaId));
        tv_alarmText.setText(alarm.getAlarmText());
        tv_alarmDetails.setText(alarm.getAlarm_details());
        tv_time.setText(alarm.getTime() + " 发布");
    }

    private void findViews() {
        tv_alarmInfo = (TextView) findViewById(R.id.tv_alarmInfo);
        tv_alarmText = (TextView) findViewById(R.id.tv_alarmText);
        tv_alarmDetails = (TextView) findViewById(R.id.tv_alarmDetails);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

}
