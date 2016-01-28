package hanjie.app.pureweather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hanjie.app.pureweather.R;

public class VerticalDataDisplayView extends RelativeLayout {

    private ImageView iv_icon;
    private TextView tv_title;
    private TextView tv_value;
    private View view_topLine;
    private View view_bottomLine;
    private View view_leftLine;
    private View view_rightLine;

    /**
     * 初始化视图(加载布局，实例化各控件)
     *
     * @param context 用于来加载布局文件使用的context
     */
    private void initView(Context context) {
        // 加载布局文件
        View.inflate(context, R.layout.custom_vertical_data_display, VerticalDataDisplayView.this);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_value = (TextView) findViewById(R.id.tv_value);
        view_topLine = findViewById(R.id.view_topLine);
        view_bottomLine = findViewById(R.id.view_bottomLine);
        view_leftLine = findViewById(R.id.view_leftLine);
        view_rightLine = findViewById(R.id.view_rightLine);
    }

    public VerticalDataDisplayView(Context context) {
        super(context);
        initView(context);
    }

    public VerticalDataDisplayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalDataDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, defStyleAttr, 0);
        int iconResId = a.getResourceId(R.styleable.CustomView_pure_icon, 0);
        iv_icon.setImageResource(iconResId);
        String title = a.getString(R.styleable.CustomView_pure_title);
        tv_title.setText(title);
        tv_title.setTextColor(a.getColor(R.styleable.CustomView_pure_titleTextColor, Color.BLACK));
        tv_value.setTextColor(a.getColor(R.styleable.CustomView_pure_valueTextColor, Color.BLACK));
        int lineColor = a.getColor(R.styleable.CustomView_pure_lineColor, Color.BLACK);
        if (a.getBoolean(R.styleable.CustomView_pure_topLine, false)) {
            view_topLine.setVisibility(VISIBLE);
            view_topLine.setBackgroundColor(lineColor);
        }
        if (a.getBoolean(R.styleable.CustomView_pure_bottomLine, false)) {
            view_bottomLine.setVisibility(VISIBLE);
            view_bottomLine.setBackgroundColor(lineColor);
        }
        if (a.getBoolean(R.styleable.CustomView_pure_leftLine, false)) {
            view_leftLine.setVisibility(VISIBLE);
            view_leftLine.setBackgroundColor(lineColor);
        }
        if (a.getBoolean(R.styleable.CustomView_pure_rightLine, false)) {
            view_rightLine.setVisibility(VISIBLE);
            view_rightLine.setBackgroundColor(lineColor);
        }
        a.recycle();
    }

    public void setValueText(String value) {
        tv_value.setText(value);
    }

    public void setTitleText(String title) {
        tv_title.setText(title);
    }

}
