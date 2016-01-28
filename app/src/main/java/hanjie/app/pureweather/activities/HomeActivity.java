package hanjie.app.pureweather.activities;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.os.Process;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.bean.WeatherAQI;
import hanjie.app.pureweather.bean.WeatherForecast;
import hanjie.app.pureweather.bean.WeatherRealtime;
import hanjie.app.pureweather.bean.WeatherYesterday;
import hanjie.app.pureweather.bean.WeatherZhiShu;
import hanjie.app.pureweather.db.dao.CityQueryDao;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.services.AutoUpdateService;
import hanjie.app.pureweather.services.DeskWidget41Service;
import hanjie.app.pureweather.services.NotificationService;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.utils.BroadcastUtils;
import hanjie.app.pureweather.utils.DialogUtils;
import hanjie.app.pureweather.utils.HttpUtils;
import hanjie.app.pureweather.utils.ServiceUtils;
import hanjie.app.pureweather.utils.SnackBarUtils;
import hanjie.app.pureweather.utils.VersionUtils;
import hanjie.app.pureweather.utils.WeatherDataParser;
import hanjie.app.pureweather.utils.WeatherUtils;
import hanjie.app.pureweather.view.CircleTempView;
import hanjie.app.pureweather.view.TriangleDataDisplayView;
import hanjie.app.pureweather.view.VerticalDataDisplayView;

public class HomeActivity extends BaseToolbarActivity {

    /**
     * 存取配置文件的SharedPreferences
     */
    private SharedPreferences sp;
    /**
     * 顶部的刷新按钮
     */
    private ImageView iv_refresh;
    /**
     * 抽屉
     */
    private DrawerLayout mDrawer;
    /**
     * ToolBar上的抽屉切换
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * 抽屉菜单的listview适配器
     */
    private MyLocationBaseAdapter mLocationAdapter;
    /**
     * 抽屉菜单(实质不是菜单)
     */
    private ListView lv_drawerMenu;
    /**
     * 主界面下拉刷新控件
     */
    private SwipeRefreshLayout mHomeSwipe;
    /**
     * 左侧抽屉下拉刷新控件
     */
    private SwipeRefreshLayout mLeftSwipe;
    /**
     * 操作天气数据库的Dao
     */
    private WeatherDao dao;
    /**
     * 用于存放所有已选择的城市的集合
     */
    private ArrayList<String> mAreasNameList;
    /**
     * 用于存放所有已选择的城市的实时天气的集合和areasNameList的位置一一对应
     */
    private ArrayList<String> mAreaRTTempList;
    /**
     * 存放左侧抽屉不同temp的icon集合
     */
    private ArrayList<Integer> mTempIconList;
    /**
     * 用于存放未来天气预报的集合
     */
    private ArrayList<WeatherForecast> mForecastList;
    /**
     * 用于存放生活指数的集合
     */
    private ArrayList<WeatherZhiShu> mZhishuList;
    /**
     * 用于存放生活指数对应的icon的集合
     */
    private ArrayList<Integer> resIdList;
    /**
     * 显示天气预报的listview
     */
    private ListView lv_forecast;
    /**
     * 显示生活指数的Listview
     */
    private ListView lv_livingIndex;
    /**
     * 自定义天气预报listview适配器
     */
    private MyForecastBaseAdapter mForecastAdapter;
    /**
     * 自定义生活指数listview适配器
     */
    private MyLivingIndexBaseAdapter mLivingIndexAdapter;
    /**
     * 当前主area的WeatherId
     */
    private String mainWeatherId;
    /**
     * 度°
     */
    private TextView tv_degree;
    /**
     * 顶部显示当前的城市(位置)
     */
    private TextView tv_location;
    /**
     * 更新时间
     */
    private TextView tv_updateTime;
    /**
     * 预警信息显示区域
     */
    private LinearLayout ll_alarmInfo;
    /**
     * 预警信息文本
     */
    private TextView tv_alarmInfo;
    /**
     * 实时温度
     */
    private TextView tv_RTTemp;
    /**
     * 实时天气类型
     */
    private TextView tv_RTType;
    /**
     * 实时天气类型ic
     */
    private ImageView iv_type;
    /**
     * 没有天气预报Forecast数据时显示
     */
    private TextView tv_noForecastData;
    /**
     * 没有生活指数LivingIndex数据时显示
     */
    private TextView tv_noLivingIndexData;
    private RelativeLayout rl_noAQIData;
    private LinearLayout ll_updateTimeArea;
    /**
     * 没有昨日天气数据
     */
    private TextView tv_noYesterdayData;
    private ImageView iv_yesdayDayType;
    private TextView tv_yesdayDayType;
    private ImageView iv_yesdayNightType;
    private TextView tv_yesdayNightType;
    private TextView tv_yesdayTempRange;

    /**
     * 没有实时天气数据
     */
    private TextView tv_noRealTimeData;
    private VerticalDataDisplayView vddv_shidu;
    private TriangleDataDisplayView tddv_feng;
    private TriangleDataDisplayView tddv_sunriseset;
    private VerticalDataDisplayView vddv_aqi;

    /**
     * long数组，2表示双击，3表示三击
     */
    private long[] mHits = new long[2];
    private ViewPager mViewPager;
    private View viewRealTime;
    private View viewForecast;
    private View viewLivingIndex;
    private View viewYesterday;
    private ArrayList<View> mViewList;
    private SmartTabLayout mViewPagerTab;
    private LinearLayout ll_manage;
    private LinearLayout ll_yesterday;
    private LinearLayout ll_realTime;
    /**
     * 左侧抽屉的设置按钮
     */
    private ImageView iv_settings;

    private LinearLayout home_bg;

    private ShowDataReceiver showDataReceiver;

    public static final String ACTION_SHOWDATA = "hanjie.app.pureweather.action_showdata";

    /**
     * 网络错误时调用
     */
    private void networkError() {
        stopRefreshing(mHomeSwipe);
        stopRefreshing(mLeftSwipe);
        Snackbar snackbar = Snackbar.make(tv_location, "请检查网络设置", Snackbar.LENGTH_SHORT);
        SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
        snackbar.show();
    }

    /**
     * 所有城市刷新成功时调用
     */
    private void allAreaRefreshSuccess() {
        Snackbar snackbar = Snackbar.make(tv_location, "所有城市刷新成功", Snackbar.LENGTH_SHORT);
        SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
        snackbar.show();
        // 取消抽屉的下拉刷新显示
        stopRefreshing(mLeftSwipe);
        // 更新list集合
        updateList();
        // 通知适配器更新数据
        mLocationAdapter.notifyDataSetChanged();
        // 更新主界面的数据
        showData(mainWeatherId);
    }

    /**
     * 主城市刷新成功时调用
     */
    private void mainAreaRefreshSuccess() {
        stopRefreshing(mHomeSwipe);
        Snackbar snackbar = Snackbar.make(tv_location, "刷新成功", Snackbar.LENGTH_SHORT);
        SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
        snackbar.show();
        showData(mainWeatherId);
    }


    /**
     * 自定义的listview适配器
     */
    private class MyLocationBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAreasNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            LocationViewHolder locationViewHolder = new LocationViewHolder();
            if (convertView != null) {
                view = convertView;
                locationViewHolder = (LocationViewHolder) view.getTag();
            } else {
                view = View.inflate(HomeActivity.this, R.layout.item_drawer_location, null);
                locationViewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                locationViewHolder.iv_yes = (ImageView) view.findViewById(R.id.iv_yes);
                locationViewHolder.circleTempView = (CircleTempView) view.findViewById(R.id.circle_temp_view);
                view.setTag(locationViewHolder);
            }
            locationViewHolder.circleTempView.setTempValue(mAreaRTTempList.get(position) + "°");
            locationViewHolder.circleTempView.setTempIcon(mTempIconList.get(position));
            String name = mAreasNameList.get(position);
            locationViewHolder.tv_title.setText(name);
            if (dao.getMainAreaName().equals(name)) {
                locationViewHolder.iv_yes.setImageResource(R.mipmap.ic_yes);
                locationViewHolder.iv_yes.setVisibility(View.VISIBLE);
            } else {
                locationViewHolder.iv_yes.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    static class LocationViewHolder {
        TextView tv_title;
        ImageView iv_yes;
        CircleTempView circleTempView;
    }

    private class MyForecastBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mForecastList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ForecastViewHolder forecastViewHolder;
            if (convertView == null) {
                view = View.inflate(HomeActivity.this, R.layout.item_forecast, null);
                forecastViewHolder = new ForecastViewHolder();
                forecastViewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
                forecastViewHolder.iv_dayType = (ImageView) view.findViewById(R.id.iv_dayType);
                forecastViewHolder.tv_dayType = (TextView) view.findViewById(R.id.tv_dayType);
                forecastViewHolder.tv_tempRange = (TextView) view.findViewById(R.id.tv_tempRange);
                forecastViewHolder.iv_rightArrow = (ImageView) view.findViewById(R.id.iv_rightArrow);
                forecastViewHolder.iv_rightArrow.setImageResource(R.mipmap.ic_right_arrow_white);
                view.setTag(forecastViewHolder);
            } else {
                view = convertView;
                forecastViewHolder = (ForecastViewHolder) view.getTag();
            }
            if (position == 0) {
                forecastViewHolder.tv_date.setText("今天");
            } else if (position == 1) {
                forecastViewHolder.tv_date.setText("明天");
            } else if (position == 2) {
                forecastViewHolder.tv_date.setText("后天");
            } else {
                forecastViewHolder.tv_date.setText(mForecastList.get(position).getWeek());
            }
            String dayType = mForecastList.get(position).getWeatherStart();
            forecastViewHolder.tv_dayType.setText(dayType);
            int iconResId = WeatherUtils.getWhiteIconIdByTypeName(dayType);
            if (iconResId != 0) {
                forecastViewHolder.iv_dayType.setImageResource(iconResId);
                forecastViewHolder.iv_dayType.setVisibility(View.VISIBLE);
            } else {
                forecastViewHolder.iv_dayType.setVisibility(View.INVISIBLE);
            }
            String tempMin = mForecastList.get(position).getTempMin();
            String tempMax = mForecastList.get(position).getTempMax();
            forecastViewHolder.tv_tempRange.setText(tempMin + " ° ~ " + tempMax + " °C");
            return view;
        }
    }

    static class ForecastViewHolder {
        TextView tv_date;
        ImageView iv_dayType;
        TextView tv_dayType;
        TextView tv_tempRange;
        ImageView iv_rightArrow;
    }

    private class MyLivingIndexBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mZhishuList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            LivingIndexViewHolder livingIndexViewHolder;
            if (convertView == null) {
                view = View.inflate(HomeActivity.this, R.layout.item_living_index_simple, null);
                livingIndexViewHolder = new LivingIndexViewHolder();
                livingIndexViewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                livingIndexViewHolder.tv_value = (TextView) view.findViewById(R.id.tv_value);
                livingIndexViewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                view.setTag(livingIndexViewHolder);
            } else {
                view = convertView;
                livingIndexViewHolder = (LivingIndexViewHolder) view.getTag();
            }
            livingIndexViewHolder.tv_name.setText(mZhishuList.get(position).getName());
            livingIndexViewHolder.tv_value.setText(mZhishuList.get(position).getValue());
            livingIndexViewHolder.iv_icon.setImageResource(resIdList.get(position));
            return view;
        }
    }

    static class LivingIndexViewHolder {
        TextView tv_name;
        TextView tv_value;
        ImageView iv_icon;
    }

    @Override
    public void initConfiguration() {
        setEnableTranslucentBar(true);
        setTranslucentBarColor(R.color.notification_transparent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 实例化控件
        findViews();
        // 更换背景
        checkBg();
        sp = getSharedPreferences(getString(R.string.config), MODE_PRIVATE);
        dao = new WeatherDao(this);
        mTempIconList = new ArrayList<Integer>();

        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawers();
                } else {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            }
        });
        ll_alarmInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入预警详细页面
                enterAlarmInfoActivity();
            }
        });

        updateList();
        // 刷新按钮设置点击监听
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mainWeatherId)) {
                    Snackbar snackbar = Snackbar.make(tv_location, "请添加一个城市", Snackbar.LENGTH_SHORT);
                    SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                    snackbar.show();
                } else {
                    RotateAnimation ra = new RotateAnimation(0, 720, iv_refresh.getWidth() / 2, iv_refresh.getHeight() / 2);
                    ra.setDuration(2000);
                    iv_refresh.startAnimation(ra);
                    refreshMainAreaData();
                }

            }
        });
        // 初始化toolbar
        initToolbar();
        // 初始化抽屉
        initDrawer();
        // 初始化ViewPager
        initViewPager();
        // 初始化下拉刷新
        initSwipeLayout();
        // 创建适配器
        mForecastAdapter = new MyForecastBaseAdapter();
        mLivingIndexAdapter = new MyLivingIndexBaseAdapter();
        // 显示新版更新提示
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showVersionChangeLogDialog();
                    }
                });
            }
        }).start();
    }

    private void enterAlarmInfoActivity() {
        Intent intent = new Intent(HomeActivity.this, AlarmInfoActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(HomeActivity.this, intent);
    }

    /**
     * 显示版本更新说明Dialog
     */
    private void showVersionChangeLogDialog() {
        boolean isRead = sp.getBoolean(getString(R.string.version_1_4_3_note_read), false);
        if (!isRead) {
            DialogUtils.showAlertDialog(HomeActivity.this, VersionUtils.getVersionName(HomeActivity.this) + " 版本更新日志:", getString(R.string.version_change_log), "已阅", "", "", new DialogUtils.DialogCallBack() {
                @Override
                public void onPositiveButton(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(getString(R.string.version_1_4_3_note_read), true);
                    editor.commit();
                    dialog.dismiss();
                    DialogUtils.showUseAssistantDialog(HomeActivity.this);
                }

                @Override
                public void onNegativeButton(DialogInterface dialog, int which) {

                }

                @Override
                public void onNeutralButton(DialogInterface dialog, int which) {

                }
            });
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkBg() {
        File customImage = new File(Environment.getExternalStorageDirectory(), "purebg.png");
        if (customImage.exists()) {
            home_bg.setBackground(Drawable.createFromPath(customImage.getAbsolutePath()));
        }
    }

    /**
     * 初始化相关设置
     */
    private void initSettings() {
        if (sp.getBoolean(getString(R.string.notification_service), SettingsActivity.NOTIFICATION_DEFAULT) && !ServiceUtils.isServiceRunning(this, "hanjie.app.pureweather.services.NotificationService")) {
            Intent notificationIntent = new Intent(HomeActivity.this, NotificationService.class);
            startService(notificationIntent);
        }
        if (sp.getBoolean(getString(R.string.auto_update_weather), SettingsActivity.AUTO_UPDATE_DEFAULT) && !ServiceUtils.isServiceRunning(this, "hanjie.app.pureweather.services.AutoUpdateService")) {
            Intent autoUpdateWeatherIntent = new Intent(HomeActivity.this, AutoUpdateService.class);
            startService(autoUpdateWeatherIntent);
        }
    }

    @Override
    public int createContentView() {
        return R.layout.activity_home;
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        final LayoutInflater inflater = getLayoutInflater();
        viewRealTime = inflater.inflate(R.layout.pager_real_time, null);
        viewForecast = inflater.inflate(R.layout.pager_forecast, null);
        viewLivingIndex = inflater.inflate(R.layout.pager_living_index, null);
        viewYesterday = inflater.inflate(R.layout.pager_yesterday, null);
        initPagerViews();
        mViewList = new ArrayList<View>();
        mViewList.add(viewRealTime);
        mViewList.add(viewForecast);
        mViewList.add(viewYesterday);
        mViewList.add(viewLivingIndex);
        mViewPager.setAdapter(new MyPagerAdapter(mViewList));
        mViewPager.setCurrentItem(sp.getInt(getString(R.string.default_page), SettingsActivity.DEFAULT_PAGE_DEFAULT));
        mViewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {

                TextView textView = (TextView) inflater.inflate(R.layout.tab_textview, container, false);

                switch (position) {
                    case 0: {
                        textView.setText("实时天气");
                        textView.setTextColor(getResources().getColor(R.color.home_text_color));
                        break;
                    }
                    case 1: {
                        textView.setText("未来天气");
                        break;
                    }
                    case 2: {
                        textView.setText("昨日天气");
                        break;
                    }
                    case 3: {
                        textView.setText("生活指数");
                        break;
                    }
                }
                return textView;
            }
        });
        mViewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mViewList.size(); i++) {
                    TextView textView = (TextView) mViewPagerTab.getTabAt(i);
                    if (i == position) {
                        textView.setTextColor(getResources().getColor(R.color.home_text_color));
                    } else {
                        textView.setTextColor(getResources().getColor(R.color.home_text_color_dark));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPagerTab.setViewPager(mViewPager);
    }

    /**
     * 实例化ViewPager中的控件
     */
    private void initPagerViews() {
        lv_forecast = (ListView) viewForecast.findViewById(R.id.lv_forecast);
        lv_forecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enterForecastActivity(position);
            }
        });
        tv_noForecastData = (TextView) viewForecast.findViewById(R.id.tv_noForecastData);
        lv_livingIndex = (ListView) viewLivingIndex.findViewById(R.id.lv_livingIndex);
        // 实例化昨天天气控件
        tv_noLivingIndexData = (TextView) viewLivingIndex.findViewById(R.id.tv_noLivingIndexData);
        tv_noYesterdayData = (TextView) viewYesterday.findViewById(R.id.tv_noYesterdayData);
        iv_yesdayDayType = (ImageView) viewYesterday.findViewById(R.id.iv_yesdayDayType);
        tv_yesdayDayType = (TextView) viewYesterday.findViewById(R.id.tv_yesdayDayType);
        iv_yesdayNightType = (ImageView) viewYesterday.findViewById(R.id.iv_yesdayNightType);
        tv_yesdayNightType = (TextView) viewYesterday.findViewById(R.id.tv_yesdayNightType);
        ll_yesterday = (LinearLayout) viewYesterday.findViewById(R.id.ll_yesterday);
        tv_yesdayTempRange = (TextView) viewYesterday.findViewById(R.id.tv_yesdayTempRange);
        // 实例化实时天气天气控件
        tv_noRealTimeData = (TextView) viewRealTime.findViewById(R.id.tv_noRealTimeData);
        vddv_shidu = (VerticalDataDisplayView) viewRealTime.findViewById(R.id.vddv_shidu);
        tddv_feng = (TriangleDataDisplayView) viewRealTime.findViewById(R.id.tddv_feng);
        tddv_sunriseset = (TriangleDataDisplayView) viewRealTime.findViewById(R.id.tddv_sunriseset);
        ll_realTime = (LinearLayout) viewRealTime.findViewById(R.id.ll_realTime);
        vddv_aqi = (VerticalDataDisplayView) viewRealTime.findViewById(R.id.vddv_aqi);
        rl_noAQIData = (RelativeLayout) viewRealTime.findViewById(R.id.rl_noAQIData);
    }

    private void enterForecastActivity(int position) {
        Intent intent = new Intent(HomeActivity.this, ForecastDetailsActivity.class);
        intent.putExtra("position", position);
        AnimationUtils.startActivityWithExplodeAnimation(HomeActivity.this, intent);
    }

    private void enterAQIActivity() {
        Intent intent = new Intent(HomeActivity.this, AQIActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(HomeActivity.this, intent);
    }

    /**
     * 自定义ViewPager适配器
     */
    private class MyPagerAdapter extends PagerAdapter {

        public List<View> listViews;

        public MyPagerAdapter(ArrayList<View> mListViews) {
            this.listViews = mListViews;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(listViews.get(position));
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listViews.get(position));
        }
    }

    /**
     * 更新area集合、area rt temp集合、area temp icon集合
     */
    private void updateList() {
        mAreasNameList = dao.getAreasNameList();
        mAreaRTTempList = dao.getAreaRTTempList();
        mTempIconList = WeatherUtils.getTempIconIdList(mAreaRTTempList);
    }

    /**
     * 初始化下拉刷新控件
     */
    private void initSwipeLayout() {
        // --------初始化home界面的下拉刷新
        mHomeSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_home);
        // 设置下拉监听
        mHomeSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新主area天气数据
                if (TextUtils.isEmpty(mainWeatherId)) {
                    stopRefreshing(mHomeSwipe);
                    Snackbar snackbar = Snackbar.make(tv_location, "请添加一个城市", Snackbar.LENGTH_SHORT);
                    SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                    snackbar.show();
                } else {
                    refreshMainAreaData();
                }
            }
        });
        // 设置旋转颜色渐变
        mHomeSwipe.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_red_light);
        // ----------初始化左侧抽屉的下拉刷新
        mLeftSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_left);
        // 设置下拉监听
        mLeftSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dao.getAreasIdList().size() == 0) {
                    stopRefreshing(mLeftSwipe);
                    Snackbar snackbar = Snackbar.make(tv_location, "请添加一个城市", Snackbar.LENGTH_SHORT);
                    SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                    snackbar.show();
                } else {
                    // 刷新所有已选择的area天气数据
                    refreshAllAreaData();
                }
            }
        });
        // 设置旋转颜色渐变
        mLeftSwipe.setColorSchemeResources(android.R.color.holo_orange_light);
    }

    /**
     * 停止显示下拉刷新
     *
     * @param swipe 需要停止显示的下拉刷新控件
     */
    private void stopRefreshing(SwipeRefreshLayout swipe) {
        if (swipe.isRefreshing()) {
            swipe.setRefreshing(false);
        }
    }


    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        // 抽屉开关切换监听器
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawer.setDrawerListener(mDrawerToggle);
        //设置抽屉内的ListView
        mLocationAdapter = new MyLocationBaseAdapter();
        lv_drawerMenu.setAdapter(mLocationAdapter);
        // listview点击事件
        lv_drawerMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取点击的area的id
                mainWeatherId = CityQueryDao.getWeatherIdByAreaName((String) mAreasNameList.get(position));
                // 获取点击的area的名称
                final String name = (String) mAreasNameList.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(500);
                        if (dao.haveCache(mainWeatherId)) {
                            // 点击的主area有本地缓存，显示数据
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showData(mainWeatherId);
                                }
                            });
                            if (checkMainAreaNeedUpdate()) {
                                refreshMainAreaData();
                            }
                        } else {
                            // 否则请求网络数据
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showNA();
                                    tv_location.setText(name);
                                }
                            });
                            refreshMainAreaData();
                        }
                    }
                }).start();
                // 获取之前已经设置的主area id
                String oldMainAreaId = dao.getMainAreaId();
                // 获取之前已经设置的主area名称
                String oldMainAreaName = dao.getMainAreaName();
                // 将之前的主area的标记设为非主area
                dao.setMainArea(oldMainAreaId, 0);
                // 将新点击的area设置为主area
                dao.setMainArea(mainWeatherId, 1);
                // 通知适配器更新数据
                mLocationAdapter.notifyDataSetChanged();
                // 关闭抽屉
                mDrawer.closeDrawers();
            }
        });

        ll_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入city管理页面
                enterCityManage();
            }
        });

        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入设置界面
                enterSettings();
            }
        });


    }

    /**
     * 进入城市管理界面
     */
    private void enterCityManage() {
        Intent intent = new Intent(this, CityManageActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(this, intent);
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        getSupportActionBar().setTitle("");
        // 为Toolbar设置菜单点击监听
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_exit: {
                        finish();
                        break;
                    }
                    case R.id.menu_refreshAllArea: {
                        if (dao.getAreasIdList().size() == 0) {
                            Snackbar snackbar = Snackbar.make(tv_location, "请添加一个城市", Snackbar.LENGTH_SHORT);
                            SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                            snackbar.show();
                        } else {
                            // 刷新所有已选择的area天气数据
                            refreshAllAreaData();
                        }
                        break;
                    }
                    case R.id.menu_cityManage: {
                        enterCityManage();
                        break;
                    }
                    case R.id.menu_settings: {
                        enterSettings();
                        break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 实例化控件
     */
    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_location = (TextView) findViewById(R.id.tv_location);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        lv_drawerMenu = (ListView) findViewById(R.id.lv_drawerMenu);
        tv_updateTime = (TextView) findViewById(R.id.tv_updateTime);
        tv_RTTemp = (TextView) findViewById(R.id.tv_RTTemp);
        tv_RTType = (TextView) findViewById(R.id.tv_RTType);
        iv_type = (ImageView) findViewById(R.id.iv_type);
        ll_alarmInfo = (LinearLayout) findViewById(R.id.ll_alarmInfo);
        tv_alarmInfo = (TextView) findViewById(R.id.tv_alarmInfo);
        ll_manage = (LinearLayout) findViewById(R.id.ll_manage);
        tv_degree = (TextView) findViewById(R.id.tv_degree);
        iv_settings = (ImageView) findViewById(R.id.iv_settings);
        home_bg = (LinearLayout) findViewById(R.id.home_bg);
        ll_updateTimeArea = (LinearLayout) findViewById(R.id.ll_updateTimeArea);
    }

    /**
     * 进入选择城市页面
     */
    private void enterSelectCity() {
        Intent intent = new Intent(HomeActivity.this, ChooseCityActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(this, intent);
    }

    /**
     * 进入设置页面
     */
    private void enterSettings() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(this, intent);
    }

    /**
     * 刷新主area天气数据
     */
    private void refreshMainAreaData() {

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    String areaId = params[0];
                    String s = HttpUtils.requestData(getString(R.string.base_api_url) + areaId);
                    WeatherDataParser.parserToDB(HomeActivity.this, s, areaId);
                    dao.setCache(mainWeatherId, 1);
                    dao.setLastUpdateTime(mainWeatherId, String.valueOf(System.currentTimeMillis()));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                if (isSuccess) {
                    mainAreaRefreshSuccess();
                } else {
                    networkError();
                }
            }
        }.execute(mainWeatherId);

    }

    /**
     * 刷新所有area天气数据
     */
    private void refreshAllAreaData() {

        new AsyncTask<ArrayList, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(ArrayList... params) {
                ArrayList<String> areaIdList = params[0];
                for (String id : areaIdList) {
                    try {
                        String s = HttpUtils.requestData(getString(R.string.base_api_url) + id);
                        WeatherDataParser.parserToDB(HomeActivity.this, s, id);
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
                    allAreaRefreshSuccess();
                } else {
                    networkError();
                }
            }

        }.execute(dao.getAreasIdList());

    }

    /**
     * 显示数据在界面上
     *
     * @param weatherId 要显示的areaid
     */
    private void showData(String weatherId) {
        // ---------------显示Notification部分--------------
        BroadcastUtils.sendNotificationBroadcast(this);
        // ---------------显示桌面插件Widget部分--------------
        BroadcastUtils.sendUpdateWidgetWeatherBroadcast(this);
        if (dao.haveCache(weatherId)) {
            // ----------显示实时天气部分(ViewPager上方部分)-----------
            WeatherRealtime realtime = dao.getDataFromRealtime(weatherId);
            tv_degree.setVisibility(View.VISIBLE);
            String updateTime = realtime.getUpdatetime();
            String wendu = realtime.getWendu();
            tv_RTTemp.setText(wendu);
            tv_updateTime.setText(updateTime + " 发布");
            ll_updateTimeArea.setVisibility(View.VISIBLE);
            mForecastList = dao.getDataListFromForecast(weatherId);
            String realTimeTypeName = realtime.getWeather();
            tv_RTType.setText(realTimeTypeName);
            int resId = WeatherUtils.getWhiteIconIdByTypeName(realTimeTypeName);
            if (resId != 0) {
                iv_type.setImageResource(resId);
                iv_type.setVisibility(View.VISIBLE);
            } else {
                iv_type.setVisibility(View.GONE);
            }
            // ----------根据是否有预警信息显示预警信息--------
            if (dao.haveAlarm(weatherId)) {
                ll_alarmInfo.setVisibility(View.VISIBLE);
                tv_alarmInfo.setText(dao.getSimpleAlarmDesc(weatherId));
            } else {
                ll_alarmInfo.setVisibility(View.GONE);
            }
            // -----------显示天气预报部分-------------
            if (lv_forecast.getAdapter() == null) {
                lv_forecast.setAdapter(mForecastAdapter);
            } else {
                mForecastAdapter.notifyDataSetChanged();
            }
            tv_noForecastData.setVisibility(View.INVISIBLE);
            lv_forecast.setVisibility(View.VISIBLE);
            // -----------显示生活指数部分--------------
            mZhishuList = dao.getDataListFromZhishu(weatherId);
            resIdList = WeatherUtils.getWhiteLivingIndexIconList(mZhishuList);
            if (lv_livingIndex.getAdapter() == null) {
                lv_livingIndex.setAdapter(mLivingIndexAdapter);
            } else {
                mLivingIndexAdapter.notifyDataSetChanged();
            }
            tv_noLivingIndexData.setVisibility(View.INVISIBLE);
            lv_livingIndex.setVisibility(View.VISIBLE);
            // -----------显示昨日天气部分--------------
            ll_yesterday.setVisibility(View.VISIBLE);
            tv_noYesterdayData.setVisibility(View.INVISIBLE);
            WeatherYesterday yesterday = dao.getDataFromYesterday(weatherId);
            String yesdayLow = yesterday.getTempMin();
            String yesdayHigh = yesterday.getTempMax();
            tv_yesdayTempRange.setText(yesdayLow + " ° ~ " + yesdayHigh + " °C");
            String yesdayDayType = yesterday.getWeatherStart();
            iv_yesdayDayType.setImageResource(WeatherUtils.getWhiteIconIdByTypeName(yesdayDayType));
            tv_yesdayDayType.setText(yesdayDayType);
            String yesdayNightType = yesterday.getWeatherEnd();
            iv_yesdayNightType.setImageResource(WeatherUtils.getWhiteIconIdByTypeName(yesdayNightType));
            tv_yesdayNightType.setText(yesdayNightType);
            // ----------------显示实时天气ViewPager部分-----------------
            ll_realTime.setVisibility(View.VISIBLE);
            tv_noRealTimeData.setVisibility(View.INVISIBLE);
            vddv_shidu.setValueText(realtime.getShidu());
            tddv_feng.setLeftValue(realtime.getFengxiang());
            tddv_feng.setRightValue(realtime.getFengli());
            tddv_sunriseset.setLeftValue(realtime.getSunrise());
            tddv_sunriseset.setRightValue(realtime.getSunset());
            WeatherAQI aqi = dao.getDataFromAQI(weatherId);
            if (dao.haveAQI(weatherId)) {
                vddv_aqi.setVisibility(View.VISIBLE);
                rl_noAQIData.setVisibility(View.INVISIBLE);
                vddv_aqi.setTitleText(aqi.getQuality());
                vddv_aqi.setValueText(aqi.getAqi());
                vddv_aqi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterAQIActivity();
                    }
                });
            } else {
                vddv_aqi.setVisibility(View.INVISIBLE);
                rl_noAQIData.setVisibility(View.VISIBLE);
            }

        } else {
            showNA();
        }
        // whatever,都要显示主area的位置在主界面上
        tv_location.setText(CityQueryDao.getAreaNameByWeatherId(mainWeatherId));
        updateList();
        mLocationAdapter.notifyDataSetChanged();
    }

    /**
     * 显示N/A界面
     */
    public void showNA() {
        // 判断当前是否已经至少选择一个城市
        if (!sp.getBoolean("area_selected", false)) {
            tv_location.setText("N/A");
        }
        tv_degree.setVisibility(View.INVISIBLE);
        tv_RTTemp.setText("");
        ll_updateTimeArea.setVisibility(View.INVISIBLE);
        tv_RTType.setText("");
        iv_type.setVisibility(View.GONE);
        updateList();
        mLocationAdapter.notifyDataSetChanged();
        lv_forecast.setVisibility(View.INVISIBLE);
        tv_noForecastData.setVisibility(View.VISIBLE);
        lv_livingIndex.setVisibility(View.INVISIBLE);
        tv_noLivingIndexData.setVisibility(View.VISIBLE);
        ll_alarmInfo.setVisibility(View.GONE);
        tv_noYesterdayData.setVisibility(View.VISIBLE);
        ll_yesterday.setVisibility(View.INVISIBLE);
        ll_realTime.setVisibility(View.INVISIBLE);
        tv_noRealTimeData.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册showdata Receiver
        if (showDataReceiver == null) {
            showDataReceiver = new ShowDataReceiver();
        }
        registerReceiver(showDataReceiver, new IntentFilter(ACTION_SHOWDATA));
        // 初始化相关设置
        initSettings();
        // ---------------显示Notification部分--------------
        BroadcastUtils.sendNotificationBroadcast(this);
        // ---------------显示桌面插件Widget部分--------------
        BroadcastUtils.sendUpdateWidgetWeatherBroadcast(this);
        // 判断当前是否已经至少选择一个城市
        if (!sp.getBoolean("area_selected", false)) {
            Snackbar snackbar = Snackbar.make(tv_location, "空空如也~", Snackbar.LENGTH_INDEFINITE);
            SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), Color.YELLOW);
            snackbar.setAction("立即添加城市", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterSelectCity();
                }
            });
            snackbar.show();
            showNA();
        }
        // 通知适配器更新数据
        mLocationAdapter.notifyDataSetChanged();
        // 获取主areaId
        mainWeatherId = dao.getMainAreaId();
        if (!TextUtils.isEmpty(mainWeatherId)) {
            if (dao.haveCache(mainWeatherId)) {
                showData(mainWeatherId);
                if (checkMainAreaNeedUpdate()) {
                    refreshMainAreaData();
                }
            } else {
                tv_location.setText(dao.getMainAreaName());
                showNA();
                refreshMainAreaData();
            }
        }
    }

    /**
     * 检查主area是否需要更新
     *
     * @return 需要返回true，否则返回false
     */
    public boolean checkMainAreaNeedUpdate() {
        boolean need = false;
        if (System.currentTimeMillis() - Long.valueOf(dao.getLastUpdateTime(mainWeatherId)) < 3600000) {
        } else {
            need = true;
        }
        return need;
    }

    /**
     * 用于更新主页面数据的Receiver
     */
    private class ShowDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showData(mainWeatherId);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (showDataReceiver != null) {
            unregisterReceiver(showDataReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        // 双击退出应用
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= SystemClock.uptimeMillis() - 1000) {
            // 双击事件逻辑处理
            finish();
        } else {
            Snackbar snackbar = Snackbar.make(tv_location, "再按一次退出", Snackbar.LENGTH_SHORT);
            SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

}
