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

public class TipTitleView extends RelativeLayout {

    private ImageView iv_icon;
    private TextView tv_title;
    private View view_bottomLine;

    private void initView(Context context) {
        View.inflate(context, R.layout.custom_tip_title, TipTitleView.this);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_title = (TextView) findViewById(R.id.tv_title);
        view_bottomLine = findViewById(R.id.view_bottomLine);
    }

    public TipTitleView(Context context) {
        super(context);
    }

    public TipTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, defStyleAttr, 0);
        iv_icon.setImageResource(a.getResourceId(R.styleable.CustomView_pure_icon, 0));
        tv_title.setText(a.getString(R.styleable.CustomView_pure_title));
        tv_title.setTextColor(a.getColor(R.styleable.CustomView_pure_titleTextColor, Color.BLACK));
        if (a.getBoolean(R.styleable.CustomView_pure_bottomLine, false)) {
            view_bottomLine.setVisibility(VISIBLE);
            view_bottomLine.setBackgroundColor(a.getColor(R.styleable.CustomView_pure_lineColor, Color.BLACK));
        }
        a.recycle();
    }

}
