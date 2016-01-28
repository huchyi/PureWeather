package hanjie.app.pureweather.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.db.dao.CityQueryDao;
import hanjie.app.pureweather.db.dao.WeatherDao;
import hanjie.app.pureweather.services.DeskWidget41Service;
import hanjie.app.pureweather.services.NotificationService;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.utils.BroadcastUtils;
import hanjie.app.pureweather.utils.DialogUtils;
import hanjie.app.pureweather.utils.SnackBarUtils;
import hanjie.app.pureweather.utils.WeatherUtils;
import hanjie.app.pureweather.view.CircleTempView;

public class CityManageActivity extends BaseToolbarActivity {

    private ListView lv_location;
    private TextView tv_noLocationData;
    private WeatherDao dao;
    private ArrayList<String> mAreaIdList;
    private ArrayList<String> mAreaNameList;
    private ArrayList<String> mAreaRTTempList;
    private ArrayList<Integer> mTempIconIdList;
    private LocationBaseAdapter mLocationAdapter;
    private SharedPreferences sp;
    private LinearLayout ll_addCity;

    private class LocationBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAreaNameList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            LocationViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(CityManageActivity.this, R.layout.item_location_manage, null);
                viewHolder = new LocationViewHolder();
                viewHolder.circle_temp_view = (CircleTempView) view.findViewById(R.id.circle_temp_view);
                viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                viewHolder.iv_deleteCity = (ImageView) view.findViewById(R.id.iv_deleteCity);
                viewHolder.iv_deleteCity.setImageResource(R.mipmap.ic_delete_gray);
                viewHolder.rl_deleteCity = (RelativeLayout) view.findViewById(R.id.rl_deleteCity);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (LocationViewHolder) view.getTag();
            }
            viewHolder.circle_temp_view.setTempValue(mAreaRTTempList.get(position) + "°");
            viewHolder.circle_temp_view.setTempIcon(mTempIconIdList.get(position));
            final String name = mAreaNameList.get(position);
            viewHolder.tv_title.setText(name);
            viewHolder.rl_deleteCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 弹出delete dialog
                    showDeleteDialog(name);
                }
            });
            return view;
        }
    }

    /**
     * 根据传进来的城市名称，显示对应的delete对话框
     *
     * @param name 要delete的城市名
     */
    private void showDeleteDialog(final String name) {
        DialogUtils.showAlertDialog(this, "提示", "确定删除 \"" + name + "\" ?", "确定", "取消", null, new DialogUtils.DialogCallBack() {
            @Override
            public void onPositiveButton(DialogInterface dialog, int which) {
                // 首先获取当前的主area ID
                String currentMainAreaId = dao.getMainAreaId();
                // 根据城市名从数据库和集合中删除其记录
                String deleteId = CityQueryDao.getWeatherIdByAreaName(name);
                if (dao.deleteFromDB(deleteId)) {
                    updateList();
                    if (mAreaIdList.size() == 0) {
                        // 删除后，一个area也没有了
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("area_selected", false);
                        editor.commit();
                        refreshUI();
                    } else if (deleteId.equals(currentMainAreaId)) {
                        // 刚刚删除的area为主area,将集合中的第一个area作为主area
                        dao.setMainArea(mAreaIdList.get(0), 1);
                    }
                    Snackbar snackbar = Snackbar.make(lv_location, "删除成功", Snackbar.LENGTH_SHORT);
                    SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(lv_location, "删除失败", Snackbar.LENGTH_SHORT);
                    SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                    snackbar.show();
                }
                mLocationAdapter.notifyDataSetChanged();
                // 更新通知栏和桌面Widget
                BroadcastUtils.sendNotificationBroadcast(CityManageActivity.this);
                BroadcastUtils.sendUpdateWidgetWeatherBroadcast(CityManageActivity.this);
            }

            @Override
            public void onNegativeButton(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

            @Override
            public void onNeutralButton(DialogInterface dialog, int which) {

            }
        });
    }

    static class LocationViewHolder {
        CircleTempView circle_temp_view;
        TextView tv_title;
        ImageView iv_deleteCity;
        RelativeLayout rl_deleteCity;
    }

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("城市管理");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        dao = new WeatherDao(this);
        findViews();
        lv_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentMainAreaId = dao.getMainAreaId();
                String clickAreaId = mAreaIdList.get(position);
                if (!currentMainAreaId.equals(clickAreaId)) {
                    dao.setMainArea(clickAreaId, 1);
                    dao.setMainArea(currentMainAreaId, 0);
                }
                finish();
            }
        });
        ll_addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterChooseCity();
            }
        });
    }

    /**
     * 进入选择城市页面
     */
    private void enterChooseCity() {
        Intent intent = new Intent(this, ChooseCityActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(this, intent);
    }

    /**
     * 根据集合的大小刷新主界面显示情况
     */
    private void refreshUI() {
        updateList();
        if (mAreaNameList.size() == 0) {
            tv_noLocationData.setVisibility(View.VISIBLE);
            lv_location.setVisibility(View.INVISIBLE);
        } else {
            tv_noLocationData.setVisibility(View.INVISIBLE);
            lv_location.setVisibility(View.VISIBLE);
            if (lv_location.getAdapter() == null) {
                mLocationAdapter = new LocationBaseAdapter();
            }
            mLocationAdapter.notifyDataSetChanged();
            lv_location.setAdapter(mLocationAdapter);
        }
        // 更新通知栏和桌面Widget
        BroadcastUtils.sendNotificationBroadcast(this);
        BroadcastUtils.sendUpdateWidgetWeatherBroadcast(this);
    }

    private void updateList() {
        mAreaNameList = dao.getAreasNameList();
        mAreaIdList = dao.getAreasIdList();
        mAreaRTTempList = dao.getAreaRTTempList();
        mTempIconIdList = WeatherUtils.getTempIconIdList(mAreaRTTempList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void findViews() {
        lv_location = (ListView) findViewById(R.id.lv_location);
        tv_noLocationData = (TextView) findViewById(R.id.tv_noLocationData);
        ll_addCity = (LinearLayout) findViewById(R.id.ll_addCity);
    }

    @Override
    public int createContentView() {
        return R.layout.activity_city_manage;
    }

}
