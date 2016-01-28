package hanjie.app.pureweather.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.services.AutoUpdateService;
import hanjie.app.pureweather.services.DeskWidget41Service;
import hanjie.app.pureweather.services.NotificationService;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.utils.BroadcastUtils;
import hanjie.app.pureweather.utils.DialogUtils;
import hanjie.app.pureweather.utils.VersionUtils;
import hanjie.app.pureweather.view.CheckBoxItemView;
import hanjie.app.pureweather.view.SimpleSelectItemView;

public class SettingsActivity extends BaseToolbarActivity implements View.OnClickListener {


    // 设置的默认值区域
    public static final boolean NOTIFICATION_DEFAULT = true;
    public static final int NOTIFICATION_COLOR_DEFAULT = 2;
    public static final boolean AUTO_UPDATE_DEFAULT = true;
    public static final int AUTO_UPDATE_MODE_DEFAULT = 1;
    public static final int INTERVAL_DEFAULT = 0;
    public static final int WIDGET_TEXT_COLOR_DEFAULT = 0;
    public static final int NOTIFICATION_ICON_COLOR_DEFAULT = 0;
    public static final boolean TRANSITION_ANIMATION_DEFAULT = true;
    public static final int DEFAULT_PAGE_DEFAULT = 0;
    // 设置的值区域
    public static final int MODE_UPDATE_CURRENT_AREA = 0;
    public static final int MODE_UPDATE_ALL_AREA = 1;
    private String[] mUpdateModes = new String[]{"只更新当前城市", "更新所有已添加的城市"};
    private String[] mUpdateInterval = new String[]{"30分钟", "1小时", "2小时", "5小时"};
    public static long[] mUpdateIntervalValues = {1800000, 3600000, 7200000, 18000000};
    private String[] mNotificationColors = new String[]{"系统底色", "黑色", "白色"};
    public static int[] mNotificationBGColorIds = new int[]{R.color.notification_transparent, R.color.notification_black, R.color.notification_white};
    public static int[] mNotificationTextColorIds = new int[]{R.color.notification_text_white, R.color.notification_text_white, R.color.notification_text_black};
    private String[] mWidgetTextColors = new String[]{"白色", "黑色"};
    public static int[] mWidgetTextColorIds = new int[]{R.color.widget_text_white, R.color.widget_text_black};
    private String[] mNotificationIconColors = new String[]{"白色", "黑色 (部分ROM不支持)"};
    private String[] mDefalutPage = new String[]{"实时天气", "未来天气", "昨日天气", "生活指数"};

    private SharedPreferences mSP;
    private SharedPreferences.Editor mEditor;
    private CheckBoxItemView mNotificationItem;
    private CheckBoxItemView mAutoUpdateItem;
    private SimpleSelectItemView mAutoUpdateModeItem;
    private SimpleSelectItemView mAutoUpdateIntervalItem;
    private RelativeLayout mAboutInfoItem;
    private SimpleSelectItemView mNotificationColorItem;
    private SimpleSelectItemView mWidgetTextColorItem;
    private RelativeLayout mVersionChangeLogItem;
    private CheckBoxItemView mTransitionAnimationItem;
    private SimpleSelectItemView mDefaultPageItem;
    private RelativeLayout mUseAssistantItem;

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("设置");
        mSP = getSharedPreferences(getString(R.string.config), MODE_PRIVATE);
        mEditor = mSP.edit();
        initViews();
        setListener();
    }

    private void initViews() {

        // 实例化

        mNotificationItem = (CheckBoxItemView) findViewById(R.id.item_notification);
        mAutoUpdateItem = (CheckBoxItemView) findViewById(R.id.item_autoUpdate);
        mAutoUpdateModeItem = (SimpleSelectItemView) findViewById(R.id.item_autoUpdateMode);
        mAutoUpdateIntervalItem = (SimpleSelectItemView) findViewById(R.id.item_autoUpdateInterval);
        mAboutInfoItem = (RelativeLayout) findViewById(R.id.rl_aboutInfo);
        mNotificationColorItem = (SimpleSelectItemView) findViewById(R.id.item_notificationColor);
        mWidgetTextColorItem = (SimpleSelectItemView) findViewById(R.id.item_widgetTextColor);
        mVersionChangeLogItem = (RelativeLayout) findViewById(R.id.rl_versionChangeLog);
        mTransitionAnimationItem = (CheckBoxItemView) findViewById(R.id.item_transitionAnimation);
        mDefaultPageItem = (SimpleSelectItemView) findViewById(R.id.item_defaultPage);
        mUseAssistantItem = (RelativeLayout) findViewById(R.id.rl_useAssistant);

        // 相关设置
        boolean notificationIsChecked = mSP.getBoolean(getString(R.string.notification_service), NOTIFICATION_DEFAULT);
        mNotificationItem.setChecked(notificationIsChecked);
        if (!notificationIsChecked) {
            //　如果通知栏为关闭状态，则隐藏相关item
            mNotificationColorItem.setVisibility(View.GONE);
        }

        boolean autoUpdate = mSP.getBoolean(getString(R.string.auto_update_weather), AUTO_UPDATE_DEFAULT);
        mAutoUpdateItem.setChecked(autoUpdate);
        if (!autoUpdate) {
            //　如果自动更新为关闭状态，则隐藏相关item
            mAutoUpdateModeItem.setVisibility(View.GONE);
            mAutoUpdateIntervalItem.setVisibility(View.GONE);
        }

        int mode = mSP.getInt(getString(R.string.auto_update_weather_mode), AUTO_UPDATE_MODE_DEFAULT);
        mAutoUpdateModeItem.setDesc(mUpdateModes[mode]);

        int interval = mSP.getInt(getString(R.string.auto_update_weather_interval), INTERVAL_DEFAULT);
        mAutoUpdateIntervalItem.setDesc(mUpdateInterval[interval]);

        int notificationColor = mSP.getInt(getString(R.string.notification_color), NOTIFICATION_COLOR_DEFAULT);
        mNotificationColorItem.setDesc(mNotificationColors[notificationColor]);

        int widgetTextColor = mSP.getInt(getString(R.string.widget_text_color), WIDGET_TEXT_COLOR_DEFAULT);
        mWidgetTextColorItem.setDesc(mWidgetTextColors[widgetTextColor]);

        boolean transitionAnimation = mSP.getBoolean(getString(R.string.transition_animation), TRANSITION_ANIMATION_DEFAULT);
        mTransitionAnimationItem.setChecked(transitionAnimation);

        int defaultPage = mSP.getInt(getString(R.string.default_page), DEFAULT_PAGE_DEFAULT);
        mDefaultPageItem.setDesc(mDefalutPage[defaultPage]);
    }

    private void setListener() {
        mAutoUpdateItem.setOnClickListener(this);
        mNotificationItem.setOnClickListener(this);
        mAutoUpdateModeItem.setOnClickListener(this);
        mAutoUpdateIntervalItem.setOnClickListener(this);
        mAboutInfoItem.setOnClickListener(this);
        mNotificationColorItem.setOnClickListener(this);
        mWidgetTextColorItem.setOnClickListener(this);
        mVersionChangeLogItem.setOnClickListener(this);
        mTransitionAnimationItem.setOnClickListener(this);
        mDefaultPageItem.setOnClickListener(this);
        mUseAssistantItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_autoUpdate: {
                boolean isCheck = mAutoUpdateItem.isChecked();
                Intent autoUpdateWeatherIntent = new Intent(SettingsActivity.this, AutoUpdateService.class);
                if (isCheck) {
                    // 当前已经选中,设为未选中,并关闭服务
                    stopService(autoUpdateWeatherIntent);
                    // 并隐藏相关item
                    mAutoUpdateModeItem.setVisibility(View.GONE);
                    mAutoUpdateIntervalItem.setVisibility(View.GONE);
                } else {
                    // 当前未选中,设为选中,并开启服务
                    startService(autoUpdateWeatherIntent);
                    //　并显示相关item
                    mAutoUpdateModeItem.setVisibility(View.VISIBLE);
                    mAutoUpdateIntervalItem.setVisibility(View.VISIBLE);
                }
                mAutoUpdateItem.setChecked(!isCheck);
                mEditor.putBoolean(getString(R.string.auto_update_weather), !isCheck);
                mEditor.commit();
                break;
            }
            case R.id.item_notification: {
                boolean isCheck = mNotificationItem.isChecked();
                Intent notificationIntent = new Intent(SettingsActivity.this, NotificationService.class);
                if (isCheck) {
                    // 当前已经选中,设为未选中,并关闭服务
                    stopService(notificationIntent);
                    // 并隐藏相关item
                    mNotificationColorItem.setVisibility(View.GONE);
                } else {
                    // 当前未选中,设为选中,并开启服务
                    startService(notificationIntent);
                    //　并显示相关item
                    mNotificationColorItem.setVisibility(View.VISIBLE);
                }
                mNotificationItem.setChecked(!isCheck);
                mEditor.putBoolean(getString(R.string.notification_service), !isCheck);
                mEditor.commit();
                break;
            }
            case R.id.item_autoUpdateMode: {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MySingleChoiceAlertDialogStyle);
                builder.setTitle("更新模式");
                builder.setSingleChoiceItems(mUpdateModes, mSP.getInt(getString(R.string.auto_update_weather_mode), AUTO_UPDATE_MODE_DEFAULT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt(getString(R.string.auto_update_weather_mode), which);
                        mEditor.commit();
                        mAutoUpdateModeItem.setDesc(mUpdateModes[which]);
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }
            case R.id.item_autoUpdateInterval: {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MySingleChoiceAlertDialogStyle);
                builder.setTitle("更新频率");
                builder.setSingleChoiceItems(mUpdateInterval, mSP.getInt(getString(R.string.auto_update_weather_interval), INTERVAL_DEFAULT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt(getString(R.string.auto_update_weather_interval), which);
                        mEditor.commit();
                        mAutoUpdateIntervalItem.setDesc(mUpdateInterval[which]);
                        Intent intent = new Intent(AutoUpdateService.ACTION_BACKGROUND_UPDATE);
                        intent.putExtra(AutoUpdateService.EXTRA_INTERVAL_CHANGED, true);
                        sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }
            case R.id.rl_aboutInfo: {
                enterAboutInfoActivity();
                break;
            }
            case R.id.item_notificationColor: {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MySingleChoiceAlertDialogStyle);
                builder.setTitle("通知栏底色");
                builder.setSingleChoiceItems(mNotificationColors, mSP.getInt(getString(R.string.notification_color), NOTIFICATION_COLOR_DEFAULT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt(getString(R.string.notification_color), which);
                        mEditor.commit();
                        mNotificationColorItem.setDesc(mNotificationColors[which]);
                        BroadcastUtils.sendNotificationBroadcast(SettingsActivity.this);
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }
            case R.id.item_widgetTextColor: {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MySingleChoiceAlertDialogStyle);
                builder.setTitle("桌面部件颜色");
                builder.setSingleChoiceItems(mWidgetTextColors, mSP.getInt(getString(R.string.widget_text_color), WIDGET_TEXT_COLOR_DEFAULT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt(getString(R.string.widget_text_color), which);
                        mEditor.commit();
                        mWidgetTextColorItem.setDesc(mWidgetTextColors[which]);
                        BroadcastUtils.sendUpdateWidgetTextColorBroadcast(SettingsActivity.this);
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }
            case R.id.rl_versionChangeLog: {
                showVersionChangeLogDialog();
                break;
            }
            case R.id.item_transitionAnimation: {
                boolean isCheck = mTransitionAnimationItem.isChecked();
                mTransitionAnimationItem.setChecked(!isCheck);
                mEditor.putBoolean(getString(R.string.transition_animation), !isCheck);
                mEditor.commit();
                break;
            }
            case R.id.item_defaultPage: {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MySingleChoiceAlertDialogStyle);
                builder.setTitle("首页默认显示页");
                builder.setSingleChoiceItems(mDefalutPage, mSP.getInt(getString(R.string.default_page), DEFAULT_PAGE_DEFAULT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt(getString(R.string.default_page), which);
                        mEditor.commit();
                        mDefaultPageItem.setDesc(mDefalutPage[which]);
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }
            case R.id.rl_useAssistant: {
                DialogUtils.showUseAssistantDialog(SettingsActivity.this);
                break;
            }
        }
    }

    private void enterAboutInfoActivity() {
        Intent intent = new Intent(SettingsActivity.this, AboutInfoActivity.class);
        AnimationUtils.startActivityWithExplodeAnimation(this, intent);
    }


    /**
     * 显示版本更新说明
     */
    private void showVersionChangeLogDialog() {
        DialogUtils.showAlertDialog(SettingsActivity.this, VersionUtils.getVersionName(SettingsActivity.this) + " 版本更新日志:", getString(R.string.version_change_log), "已阅", "", "", new DialogUtils.DialogCallBack() {
            @Override
            public void onPositiveButton(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeButton(DialogInterface dialog, int which) {

            }

            @Override
            public void onNeutralButton(DialogInterface dialog, int which) {

            }
        });
    }

    @Override
    public int createContentView() {
        return R.layout.activity_settings;
    }

}
