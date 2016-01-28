package hanjie.app.pureweather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hanjie.app.pureweather.R;

/**
 * 自定义组合控件，两个TextView以及一个右箭头
 */
public class SimpleSelectItemView extends RelativeLayout {

    /**
     * 选项的标题
     */
    private TextView tv_title;
    /**
     * 选项的描述信息
     */
    private TextView tv_desc;
    /**
     * 从自定义组合控件的属性文件attrs.xml中获取的标题
     */
    private String mTitle;
    /**
     * 从自定义组合控件的属性文件attrs.xml中获取到的默认的desc信息
     */
    private String mDefaultDesc;

    public SimpleSelectItemView(Context context) {
        super(context);
    }

    public SimpleSelectItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleSelectItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 加载布局文件
        View v = View.inflate(context, R.layout.item_simple_select, SimpleSelectItemView.this);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        tv_desc = (TextView) v.findViewById(R.id.tv_desc);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, defStyleAttr, 0);
        mTitle = (String) a.getText(R.styleable.CustomView_pure_title);
        tv_title.setText(mTitle);
        mDefaultDesc = (String) a.getText(R.styleable.CustomView_pure_default_desc);
        tv_desc.setText(mDefaultDesc);
    }

    /**
     * 设置子标题描述信息
     *
     * @param desc 描述信息
     */
    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }

    /**
     * 设置enabled的同时，改变item title的文本颜色
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            tv_title.setTextColor(Color.BLACK);
        } else {
            tv_title.setTextColor(Color.GRAY);
        }
    }

}
