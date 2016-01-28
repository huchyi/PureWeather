package hanjie.app.pureweather.activities;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.db.dao.CityQueryDao;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.utils.KeyBoardUtils;

public class ChooseCityActivity extends BaseToolbarActivity {

    /**
     * 显示设置listview的适配器消息
     */
    private static final int MESSAGE_SHOW_LIST = 0;
    /**
     * 顶部的显示省/市的文本
     */
    private TextView tv_location;
    /**
     * 主界面的listview
     */
    private ListView lv_location;
    /**
     * 用于存放当前层级的位置的集合
     */
    private ArrayList<String> mLocationList;
    /**
     * 自定义的BaseAdapter
     */
    private LocationAdapter mAdapter;
    /**
     * 省级
     */
    private final int LEVEL_PROVINCE = 0;
    /**
     * 市级
     */
    private final int LEVEL_CITY = 1;
    /**
     * 县级
     */
    private final int LEVEL_AREA = 2;
    /**
     * 选择城市页面
     */
    private final int VIEW_SELECT_CITY = 3;
    /**
     * 搜索城市页面
     */
    private final int VIEW_SEARCH_CITY = 4;
    /**
     * 保存当前listview显示的层级，默认为省级
     */
    private int currentLevel = LEVEL_PROVINCE;
    /**
     * 保存选择的当前省份
     */
    private String currentProvince;
    /**
     * 保存选择的当前市
     */
    private String currentCity;
    /**
     * 存取配置文件的SharedPreferences
     */
    private SharedPreferences sp;
    /**
     * 搜索城市区域
     */
    private LinearLayout ll_search;
    /**
     * 搜索城市输入框
     */
    private EditText et_search;
    /**
     * 显示搜索城市和热门城市(默认)的listview
     */
    private ListView lv_location_search;
    /**
     * 当前是搜索城市结果还是热门城市得显示状态
     */
    private TextView tv_listState;
    /**
     * 点击可根据省市选择城市的问TextView控件
     */
    private TextView tv_allCity;
    /**
     * 显示搜索城市和热门城市(默认)的listview的适配器adapter
     */
    private SearchLocationAdapter mSearchAdapter;
    /**
     * 保存热门城市(默认)或者搜索城市结果的集合
     */
    private ArrayList<String> mSearchLocationList = new ArrayList<String>();
    /**
     * 当前的视图状态(搜索界面还是根据省市选择界面)
     */
    private int currentView = VIEW_SEARCH_CITY;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_LIST: {
                    mLocationList = CityQueryDao.getProvinceList();
                    lv_location.setAdapter(mAdapter);
                    break;
                }
            }
        }
    };

    /**
     * 自定义的选择城市 adapter
     */
    private class LocationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLocationList.size();
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
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(ChooseCityActivity.this, R.layout.item_location, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_name.setText(mLocationList.get(position));
            return view;
        }
    }

    /**
     * 自定义的搜索城市 adapter
     */
    private class SearchLocationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSearchLocationList.size();
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
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(ChooseCityActivity.this, R.layout.item_location, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_name.setText(mSearchLocationList.get(position));
            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
    }

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        findViews();
        getSupportActionBar().setTitle("");
        mAdapter = new LocationAdapter();
        lv_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    currentProvince = mLocationList.get(position);
                    tv_location.setText(currentProvince);
                    mLocationList = CityQueryDao.getCityListByProvince(currentProvince);
                    currentLevel = LEVEL_CITY;
                } else if (currentLevel == LEVEL_CITY) {
                    currentCity = mLocationList.get(position);
                    tv_location.setText(currentCity);
                    mLocationList = CityQueryDao.getAreaListByCity(currentCity);
                    currentLevel = LEVEL_AREA;
                } else {
                    addArea(mLocationList, position);
                }
                mAdapter.notifyDataSetChanged();
                lv_location.setSelection(0);
            }
        });
        tv_allCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_location.setText(R.string.add_city);
                lv_location.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.INVISIBLE);
                currentView = VIEW_SELECT_CITY;
                KeyBoardUtils.hintKeyBoard(ChooseCityActivity.this);
            }
        });
        mSearchAdapter = new SearchLocationAdapter();
        // 初始化热门城市
        initHotCity();
        lv_location_search.setAdapter(mSearchAdapter);
        lv_location_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addArea(mSearchLocationList, position);
            }
        });
        lv_location_search.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // 隐藏软键盘
                    KeyBoardUtils.hintKeyBoard(ChooseCityActivity.this);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyWord = s.toString().trim();
                if (!TextUtils.isEmpty(keyWord)) {
                    tv_listState.setText("搜索结果");
                    mSearchLocationList = CityQueryDao.searchByKeyWord(keyWord);
                } else {
                    tv_listState.setText("热门城市");
                    initHotCity();
                }
                mSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        initDB();
    }

    @Override
    public int createContentView() {
        return R.layout.activity_choose_city;
    }

    /**
     * 保存将选择的area
     *
     * @param list     当前保存listview的集合
     * @param position listview中点击的位置
     */
    private void addArea(ArrayList<String> list, int position) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("area_selected", true);
        editor.commit();
        WeatherDao dao = new WeatherDao(ChooseCityActivity.this);
        // 获取当前设置的的主areaID
        String mainAreaId = dao.getMainAreaId();
        // 获取当前设置的的主area的名称
        String mainAreaName = dao.getMainAreaName();
        // 获取当前选择的area的名称
        String chooseAreaName = list.get(position);
        // 获取当前选择的area的ID
        String chooseWeatherId = CityQueryDao.getWeatherIdByAreaName(chooseAreaName);

        // 当前有无设置过的主area？
        if (!TextUtils.isEmpty(mainAreaId)) {
            // 有
            // 当前选择的是否就是主area？
            if (chooseWeatherId.equals(mainAreaId)) {
                // 是
            } else {
                // 不是
                // 将选择的添加至数据库
                dao.addToAreas(chooseWeatherId, chooseAreaName);
                // 并设置成主area
                dao.setMainArea(chooseWeatherId, 1);
                // 再见已设置的主area设置为非主area
                dao.setMainArea(mainAreaId, 0);
            }
        } else {
            // 无
            // 将选择的添加至数据库
            dao.addToAreas(chooseWeatherId, chooseAreaName);
            // 并设置成主area
            dao.setMainArea(chooseWeatherId, 1);
        }
        finish();
    }

    /**
     * 初始化热门城市
     */
    private void initHotCity() {
        mSearchLocationList.clear();
        mSearchLocationList.add("北京");
        mSearchLocationList.add("上海");
        mSearchLocationList.add("天津");
        mSearchLocationList.add("合肥");
        mSearchLocationList.add("重庆");
        mSearchLocationList.add("南京");
        mSearchLocationList.add("苏州");
        mSearchLocationList.add("西安");
        mSearchLocationList.add("武汉");
        mSearchLocationList.add("杭州");
        mSearchLocationList.add("成都");
        mSearchLocationList.add("广州");
        mSearchLocationList.add("深圳");
    }

    private void initDB() {
        final File file = new File(getFilesDir(), "location.db");
        if (file.exists() && file.length() > 0) {
            mLocationList = CityQueryDao.getProvinceList();
            lv_location.setAdapter(mAdapter);
        } else {
            // 开启一个线程去检查省市数据库是否存在
            new Thread(new Runnable() {
                @Override
                public void run() {
                    copyLocationDB(file);
                }
            }).start();
        }
    }

    /**
     * 实例化控件
     */
    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_location = (TextView) findViewById(R.id.tv_location);
        lv_location = (ListView) findViewById(R.id.lv_location);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        et_search = (EditText) findViewById(R.id.et_search);
        lv_location_search = (ListView) findViewById(R.id.lv_location_search);
        tv_listState = (TextView) findViewById(R.id.tv_listState);
        tv_allCity = (TextView) findViewById(R.id.tv_allCity);
    }

    /**
     * 拷贝省市数据库
     */
    private void copyLocationDB(File file) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getAssets().open("location.db");
            fos = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(MESSAGE_SHOW_LIST);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentView == VIEW_SELECT_CITY) {
            if (currentLevel == LEVEL_AREA) {
                mLocationList = CityQueryDao.getCityListByProvince(currentProvince);
                tv_location.setText(currentProvince);
                currentLevel = LEVEL_CITY;
            } else if (currentLevel == LEVEL_CITY) {
                mLocationList = CityQueryDao.getProvinceList();
                tv_location.setText(R.string.add_city);
                currentLevel = LEVEL_PROVINCE;
            } else {
                tv_location.setText(R.string.search_city);
                lv_location.setVisibility(View.INVISIBLE);
                ll_search.setVisibility(View.VISIBLE);
                currentView = VIEW_SEARCH_CITY;
                return;
            }
            mAdapter.notifyDataSetChanged();
            lv_location.setSelection(0);
        } else {
            finish();
        }
    }

}
