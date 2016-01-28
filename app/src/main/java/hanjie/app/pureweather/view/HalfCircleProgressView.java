package hanjie.app.pureweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.utils.DensityUtils;

public class HalfCircleProgressView extends View {

    // 绘制Arc的画笔
    private Paint mArcPaint;

    private Paint mBGArcPaint;

    private RectF mRectf;

    // 绘制Text的画笔
    private Paint mTextPaint;

    private float mValue = 0;
    private float mMaxValue = 0;
    private int mProgressValue = 0;
    private float mProgressSweepAngle = 0;

    private int level = 0;

    private Rect mTextBounds;

    private int mMeasureWidth;
    private int mMeasureHeight;

    private Context mContext;

    public HalfCircleProgressView(Context context) {
        super(context);
    }

    public HalfCircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HalfCircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(DensityUtils.dp2px(mContext, 45));
        mBGArcPaint = new Paint();
        mBGArcPaint.setStyle(Paint.Style.STROKE);
        mBGArcPaint.setAntiAlias(true);
        mBGArcPaint.setColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureWidth = 0;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            mMeasureWidth = widthSpecSize;
        } else {
            mMeasureWidth = DensityUtils.dp2px(mContext, 100);
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                mMeasureWidth = Math.min(mMeasureWidth, widthSpecSize);
            }
        }

        mMeasureHeight = 0;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mMeasureHeight = heightSpecSize;
        } else {
            mMeasureHeight = mMeasureWidth / 2;
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                mMeasureHeight = Math.min(mMeasureHeight, heightSpecSize);
            }
        }
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
        initViews();
    }

    private void initViews() {
        mRectf = new RectF(mMeasureWidth * 0.1f, mMeasureHeight * 0.1f, mMeasureWidth * 0.9f, mMeasureHeight * 2 * 0.9f);
        mArcPaint.setStrokeWidth(mMeasureWidth * 0.07f);
        mBGArcPaint.setStrokeWidth(mMeasureWidth * 0.07f);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 画背景圆弧
        canvas.drawArc(mRectf, 180, 180, false, mBGArcPaint);

        mProgressSweepAngle = mProgressValue / mMaxValue * 180;
        if (mProgressSweepAngle <= 180) {
            canvas.drawArc(mRectf, 180, mProgressSweepAngle, false, mArcPaint);
        } else {
            canvas.drawArc(mRectf, 180, 180, false, mArcPaint);
        }

        // 画Text
        String textValue = String.valueOf(mProgressValue);
        mTextBounds = new Rect();
        mTextPaint.getTextBounds(textValue, 0, textValue.length(), mTextBounds);
        canvas.drawText(textValue, 0, textValue.length(), mMeasureWidth / 2 - mTextBounds.width() / 2, mMeasureHeight - mTextBounds.height() / 2, mTextPaint);

        if (mProgressValue < mValue) {
            mProgressValue += 5;
            if (mProgressValue > mValue) {
                mProgressValue = (int) mValue;
            }
            postInvalidate();
        }
    }

    /**
     * 设置要显示的值，在setMaxValue()之后调用
     *
     * @param value
     */
    public void setValue(float value) {
        if (value < 0) {
            mValue = 0;
        } else {
            mValue = value;
        }

        if (mValue <= 50) {
            mArcPaint.setColor(getResources().getColor(R.color.level_one));
            mTextPaint.setColor(getResources().getColor(R.color.level_one));
            level = 1;
        } else if (mValue <= 100) {
            mArcPaint.setColor(getResources().getColor(R.color.level_two));
            mTextPaint.setColor(getResources().getColor(R.color.level_two));
            level = 2;
        } else if (mValue <= 150) {
            mArcPaint.setColor(getResources().getColor(R.color.level_three));
            mTextPaint.setColor(getResources().getColor(R.color.level_three));
            level = 3;
        } else if (mValue <= 200) {
            mArcPaint.setColor(getResources().getColor(R.color.level_four));
            mTextPaint.setColor(getResources().getColor(R.color.level_four));
            level = 4;
        } else if (mValue <= 300) {
            mArcPaint.setColor(getResources().getColor(R.color.level_five));
            mTextPaint.setColor(getResources().getColor(R.color.level_five));
            level = 5;
        } else {
            mArcPaint.setColor(getResources().getColor(R.color.level_six));
            mTextPaint.setColor(getResources().getColor(R.color.level_six));
            level = 6;
        }
    }

    /**
     * 获取等级
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置最大值，在setValue()之前调用
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

}
