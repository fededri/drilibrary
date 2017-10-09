package com.fededri.utils.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.fededri.utils.R;


/**
 * Created by Federico Torres on 5/10/2017.
 */

public class CounterView extends View {

    private final int SUM_OPERATION = 1;
    private final int SUBSTRACTION_OPERATION = 2;


    private int count = 0;
    private int mOperation;
    private float mDiameter;
    private int mBackgroundColor, mOnPressEdgeColor, mOnPressCenterColor;
    private float operationXDrawOffsetPercentage, operationYDrawOffsetPercentage;
    private float cx, cy; //center of the circle
    private Paint mCirclePaint, mOpPaint, mOnPressPaint;
    GestureDetector mDetector;
    private SumListener sumListener;
    private  SubstractionListener substractionListener;
    private RadialGradient radialGradient;
    private Matrix matrix;

    public CounterView(Context context) {
        super(context);
        init();
    }


    public CounterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CounterView, 0, 0);


        try {
            mOperation = a.getInt(R.styleable.CounterView_operation, 0);
            mBackgroundColor = a.getColor(R.styleable.CounterView_backgroundColor, getContext().getResources().getColor(R.color.light_gray_2));
            operationXDrawOffsetPercentage = a.getFloat(R.styleable.CounterView_operationXOffsetPercentage, (float) 0.7);
            operationYDrawOffsetPercentage = a.getFloat(R.styleable.CounterView_operationYOffsetPercentage, (float) 0.9);
            mOnPressCenterColor =  a.getColor(R.styleable.CounterView_onPressCenterColor, getContext().getResources().getColor(R.color.counter_center_press_color));
            mOnPressEdgeColor = a.getColor(R.styleable.CounterView_onPressEdgeColor, getContext().getResources().getColor(R.color.counter_edge_press_color));
        } finally {
            a.recycle();
        }

        init();

    }



    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mBackgroundColor);


        mOpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOpPaint.setStyle(Paint.Style.FILL);
        mOpPaint.setColor(getContext().getResources().getColor(R.color.white));


        mOnPressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOnPressPaint.setStyle(Paint.Style.FILL);
        mOnPressPaint.setColor(mOnPressEdgeColor);


        matrix = new Matrix();
        mDetector = new GestureDetector(getContext(), new mListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
         boolean result =  mDetector.onTouchEvent(event);
         pressed = result;
         invalidate();
         return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        mDiameter = Math.min(ww, hh);
        cx = ww / 2;
        cy = hh / 2;
        radialGradient = new RadialGradient(cx, cy, mDiameter/2, new int[] {mOnPressCenterColor, mOnPressEdgeColor}, null, Shader.TileMode.MIRROR);
        mOnPressPaint.setShader(radialGradient);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(pressed){
            float scale = incScale(0.01f);
            matrix.setScale(scale, scale, mDiameter/2, mDiameter/2);
            radialGradient.setLocalMatrix(matrix);
            canvas.drawCircle(cx, cy, mDiameter / 2, mOnPressPaint);
        }else {
            canvas.drawCircle(cx, cy, mDiameter / 2, mCirclePaint);
        }

        if (mOperation != 0) {
            float offsetX, offsetY; //padding between the rectangle and the background circle
            switch (mOperation) {
                case SUM_OPERATION:
                    offsetX = ((mDiameter / 2) - ((mDiameter / 2) * operationXDrawOffsetPercentage));
                    offsetY = ((mDiameter / 2) - ((mDiameter / 2) * operationYDrawOffsetPercentage));
                    canvas.drawRect(cx - offsetX, cy - offsetY, cx + offsetX, cy + offsetY, mOpPaint);
                    //inverse
                    offsetY = ((mDiameter / 2) - ((mDiameter / 2) * operationXDrawOffsetPercentage));
                    offsetX = ((mDiameter / 2) - ((mDiameter / 2) * operationYDrawOffsetPercentage));
                    canvas.drawRect(cx - offsetX, cy - offsetY, cx + offsetX, cy + offsetY, mOpPaint);
                    break;

                case SUBSTRACTION_OPERATION:
                    offsetX = ((mDiameter / 2) - ((mDiameter / 2) * operationXDrawOffsetPercentage));
                    offsetY = ((mDiameter / 2) - ((mDiameter / 2) * operationYDrawOffsetPercentage));
                    canvas.drawRect(cx - offsetX, cy - offsetY, cx + offsetX, cy + offsetY, mOpPaint);
                    break;
            }
        }

        postInvalidateOnAnimation();
    }

    private float radialScaleDirection;
    private float radialScale;

    private float incScale(float delta) {
        radialScale = (radialScale + delta * radialScaleDirection);
        if (radialScale <= 0.2f) {
            radialScaleDirection = 1;
            radialScale = 0.2f;
        } else if (radialScale >= 1) {
            radialScaleDirection = -1;
            radialScale = 1;
        }

        return radialScale;
    }


    public interface SumListener{
        void sumClick();
    }

    public interface SubstractionListener{
        void substractionClick();
    }


    public void setSumListener(SumListener sumListener) {
        this.sumListener = sumListener;
    }

    public void setSubstractionListener(SubstractionListener substractionListener) {
        this.substractionListener = substractionListener;
    }


    boolean pressed = false;

    class mListener extends GestureDetector.SimpleOnGestureListener {

        private final String TAG = "CounterViewListener";
        @Override
        public boolean onDown(MotionEvent e) {
            if(mOperation == SUM_OPERATION  && sumListener != null ) sumListener.sumClick();
            else if(mOperation == SUBSTRACTION_OPERATION && substractionListener != null) substractionListener.substractionClick();
            return true;
        }



    }
}
