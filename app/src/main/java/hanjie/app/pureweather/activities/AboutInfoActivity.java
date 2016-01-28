package hanjie.app.pureweather.activities;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.utils.AnimationUtils;
import hanjie.app.pureweather.utils.SnackBarUtils;
import hanjie.app.pureweather.utils.VersionUtils;

public class AboutInfoActivity extends BaseToolbarActivity implements View.OnClickListener {

    private TextView tv_appVersion;
    private TextView tv_email;
    private TextView tv_qqGroup;
    private TextView tv_sinaWeibo;

    @Override
    public void initConfiguration() {
        AnimationUtils.setExplodeEnterTransition(this);
        setEnableTranslucentBar(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("关于");
        findViews();
        initViews();
        showData();
    }

    private void initViews() {
        tv_email.setOnClickListener(this);
        tv_qqGroup.setOnClickListener(this);
        tv_sinaWeibo.setOnClickListener(this);
    }

    private void showData() {
        tv_appVersion.setText("版本 " + VersionUtils.getVersionName(AboutInfoActivity.this));
    }

    private void findViews() {
        tv_appVersion = (TextView) findViewById(R.id.tv_appVersion);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_qqGroup = (TextView) findViewById(R.id.tv_qqGroup);
        tv_sinaWeibo = (TextView) findViewById(R.id.tv_sinaWeibo);
    }

    @Override
    public int createContentView() {
        return R.layout.activity_aboutinfo;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_email:
            case R.id.tv_qqGroup:
            case R.id.tv_sinaWeibo: {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(((TextView) v).getText());
                Snackbar snackbar = Snackbar.make(tv_email, "复制成功", Snackbar.LENGTH_SHORT);
                SnackBarUtils.customSnackBar(snackbar, getResources().getColor(R.color.home_snack_bar_background), getResources().getColor(R.color.home_snack_bar_text), 0);
                snackbar.show();
                break;
            }
        }
    }
}
