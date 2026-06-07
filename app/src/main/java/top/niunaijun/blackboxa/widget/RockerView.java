package top.niunaijun.blackboxa.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import top.niunaijun.blackboxa.util.MathUtil;


/**
 * A custom SurfaceView-based virtual joystick (rocker) control.
 *
 * Renders a circular active area and a draggable rocker knob. The rocker tracks
 * touch input, computes angle and distance from center, and delivers periodic
 * callbacks via [RockerListener]. Supports both color-filled and bitmap-based rendering.
 *
 * Author: GcsSloop
 * Created Date: 16/5/24
 * Copyright (C) 2016 GcsSloop.
 * GitHub: https://github.com/GcsSloop
 */
public class RockerView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private static final int DEFAULT_AREA_RADIUS = 100;
    private static final int DEFAULT_ROCKER_RADIUS = 35;

    private static final int DEFAULT_AREA_COLOR = Color.argb(128,0,0,0);
    private static final int DEFAULT_ROCKER_COLOR = Color.argb(128,0,0,0);

    private static final int DEFAULT_REFRESH_CYCLE = 30;
    private static final int DEFAULT_CALLBACK_CYCLE = 300;

    private SurfaceHolder mHolder;
    private static Thread mDrawThread;
    private static Thread mCallbackThread;
    private static boolean mDrawOk = true;
    private static boolean mCallbackOk = true;

    private Paint mPaint;

    /**
     * The rocker active area center position.
     * usually, it is the center of this view.
     */
    private Point mAreaPosition;

    /**
     * The Rocker position.
     * usually, it as same asmAreaPosition .
     * if this view touched, it will follow the touch position.
     * <p/>
     * we get position information from this.
     */
    private Point mRockerPosition;


    private int mAreaRadius = -1;
    private int mRockerRadius = -1;

    private int mAreaColor;
    private int mRockerColor;
    private Bitmap mAreaBitmap;
    private Bitmap mRockerBitmap;

    private boolean canMove = true;


    private RockerListener mListener;
    /** Event type indicating an immediate touch action response. */
    public static final int EVENT_ACTION = 1;
    /** Event type indicating a periodic clock-based callback tick. */
    public static final int EVENT_CLOCK = 2;

    private int mRefreshCycle = DEFAULT_REFRESH_CYCLE;
    private int mCallbackCycle = DEFAULT_CALLBACK_CYCLE;


    /*Life Cycle***********************************************************************************/

    /**
     * Constructs a RockerView with default attributes.
     *
     * @param context the application context
     */
    public RockerView(Context context) {
        this(context, null);
    }

    /**
     * Constructs a RockerView with XML attributes.
     *
     * @param context the application context
     * @param attrs   the XML attribute set
     */
    public RockerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructs a RockerView with XML attributes and a default style.
     *
     * @param context      the application context
     * @param attrs        the XML attribute set
     * @param defStyleAttr  the default style attribute resource
     */
    public RockerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // init attrs
        initAttrs(context, attrs);

        // set paint
        setPaint();

        if (isInEditMode()) {
            return;
        }

        // config surfaceView
        configSurfaceView();

        // config surfaceHolder
        configSurfaceHolder();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mAreaColor = DEFAULT_AREA_COLOR;
        mRockerColor = DEFAULT_ROCKER_COLOR;
        mAreaRadius = DEFAULT_AREA_RADIUS;
        mRockerRadius = DEFAULT_ROCKER_RADIUS;

    }

    private void setPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    private void configSurfaceView() {
        setKeepScreenOn(true);          // do not lock screen when surfaceView is running.
        setFocusable(true);             // make sure this surfaceView can get focus from keyboard.
        setFocusableInTouchMode(true);  // make sure this surfaceView can get focus from touch.
        setZOrderOnTop(true);           // make sure this surface is placed on top of the window
    }

    private void configSurfaceHolder() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT); //设置背景透明
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = 0, measureHeight = 0;
        int defaultWidth = (mAreaRadius + mRockerRadius) * 2;
        int defalutHeight = defaultWidth;

        int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式

        if (widthmode == MeasureSpec.AT_MOST || widthmode == MeasureSpec.UNSPECIFIED || widthsize < 0) {
            measureWidth = defaultWidth;
        } else {
            measureWidth = widthsize;
        }


        if (heightmode == MeasureSpec.AT_MOST || heightmode == MeasureSpec.UNSPECIFIED || heightsize < 0) {
            measureHeight = defalutHeight;
        } else {
            measureHeight = heightsize;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mAreaPosition = new Point(w / 2, h / 2);
        mRockerPosition = new Point(mAreaPosition);

        // this need subtract the view padding
        int tempRadius = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom());
        tempRadius /= 2;
        if (mAreaRadius == -1)
            mAreaRadius = (int) (tempRadius * 0.75);
        if (mRockerRadius == -1)
            mRockerRadius = (int) (tempRadius * 0.25);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mDrawThread = new Thread(this);
            mDrawThread.start();

            mCallbackThread = new Thread(() -> {
                while (mCallbackOk) {

                    // listener callback
                    listenerCallback();

                    try {
                        Thread.sleep(mCallbackCycle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mCallbackThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawOk = false;
        mCallbackOk = false;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mDrawOk = true;
            mCallbackOk = true;
        } else {
            mDrawOk = false;
            mCallbackOk = false;
        }
    }

    /*Event Response*******************************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int len = MathUtil.getDistance(mAreaPosition.x, mAreaPosition.y, event.getX(), event.getY());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //如果屏幕接触点不在摇杆挥动范围内,则不处理
                if (len > mAreaRadius) {
                    return true;
                }
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (len <= mAreaRadius) {
                    //如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                    mRockerPosition.set((int) event.getX(), (int) event.getY());

                } else {
                    //设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
                    mRockerPosition = MathUtil.getPointByCutLength(mAreaPosition,
                            new Point((int) event.getX(), (int) event.getY()), mAreaRadius);
                }
                if (mListener != null) {
                    float radian = MathUtil.getRadian(mAreaPosition, new Point((int) event.getX(), (int) event.getY()));
                    float angle = RockerView.this.getAngleConvert(radian);
                    float distance = MathUtil.getDistance(mAreaPosition.x, mAreaPosition.y, event.getX(), event.getY());
                    mListener.callback(EVENT_ACTION, angle, distance);
                }
            }
            //如果手指离开屏幕，则摇杆返回初始位置
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mRockerPosition = new Point(mAreaPosition);
                if (mListener != null) {
                    mListener.callback(EVENT_ACTION, -1, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /*Thread - draw view***************************************************************************/

    @Override
    public void run() {
        if (isInEditMode()) {
            return;
        }

        Canvas canvas = null;

        while (mDrawOk) {
            boolean canMove = this.canMove;
            try {
                if (canMove) {
                    canvas = mHolder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    drawArea(canvas);
                    drawRocker(canvas);
                }
                Thread.sleep(mRefreshCycle);    // 休眠

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null && canMove) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void drawArea(Canvas canvas) {

        if (null != mAreaBitmap) {
            mPaint.setColor(Color.BLACK);
            Rect src = new Rect(0, 0, mAreaBitmap.getWidth(), mAreaBitmap.getHeight());
            Rect dst = new Rect(
                    mAreaPosition.x - mAreaRadius,
                    mAreaPosition.y - mAreaRadius,
                    mAreaPosition.x + mAreaRadius,
                    mAreaPosition.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, src, dst, mPaint);
        } else {
            mPaint.setColor(mAreaColor);
            canvas.drawCircle(mAreaPosition.x, mAreaPosition.y, mAreaRadius, mPaint);
        }
    }

    private void drawRocker(Canvas canvas) {
        if (null != mRockerBitmap) {
            mPaint.setColor(Color.BLACK);
            Rect src = new Rect(0, 0, mRockerBitmap.getWidth(), mRockerBitmap.getHeight());
            Rect dst = new Rect(
                    mRockerPosition.x - mRockerRadius,
                    mRockerPosition.y - mRockerRadius,
                    mRockerPosition.x + mRockerRadius,
                    mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, src, dst, mPaint);
        } else {
            mPaint.setColor(mRockerColor);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mPaint);
        }
    }

    private void listenerCallback() {
        if (mListener != null) {
            if (mRockerPosition.x == mAreaPosition.x && mRockerPosition.y == mAreaPosition.y) {
                mListener.callback(EVENT_CLOCK, -1, 0);
            } else {
                float radian = MathUtil.getRadian(mAreaPosition, new Point(mRockerPosition.x, mRockerPosition.y));
                float angle = RockerView.this.getAngleConvert(radian);
                float distance = MathUtil.getDistance(mAreaPosition.x, mAreaPosition.y, mRockerPosition.x, mRockerPosition.y);
                mListener.callback(EVENT_CLOCK, angle, distance);
            }
        }
    }

    //获取摇杆偏移角度 上方中间为0，左为负，右为正
    private float getAngleConvert(float radian) {
        return 90 + Math.round(radian / Math.PI * 180);
    }

    // for preview
    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            canvas.drawColor(Color.WHITE);
            drawArea(canvas);
            drawRocker(canvas);
        }
    }

    /*Getter Setter********************************************************************************/

    /**
     * Enables or disables the rendering update loop. When disabled, the surface is not redrawn.
     *
     * @param isMove true to allow drawing updates, false to pause rendering
     */
    public void setCanMove(boolean isMove) {
        this.canMove = isMove;
    }

    /**
     * Returns the radius of the active area circle in pixels.
     *
     * @return the area radius
     */
    public int getAreaRadius() {
        return mAreaRadius;
    }

    /**
     * Sets the radius of the active area circle in pixels.
     *
     * @param areaRadius the area radius in pixels
     */
    public void setAreaRadius(int areaRadius) {
        mAreaRadius = areaRadius;
    }

    /**
     * Returns the radius of the draggable rocker knob in pixels.
     *
     * @return the rocker radius
     */
    public int getRockerRadius() {
        return mRockerRadius;
    }

    /**
     * Sets the radius of the draggable rocker knob in pixels.
     *
     * @param rockerRadius the rocker radius in pixels
     */
    public void setRockerRadius(int rockerRadius) {
        mRockerRadius = rockerRadius;
    }

    /**
     * Returns the custom bitmap used for the active area, or null if using color fill.
     *
     * @return the area bitmap, or null
     */
    public Bitmap getAreaBitmap() {
        return mAreaBitmap;
    }

    /**
     * Sets a custom bitmap for rendering the active area instead of a solid color circle.
     *
     * @param areaBitmap the bitmap to use, or null to revert to color fill
     */
    public void setAreaBitmap(Bitmap areaBitmap) {
        mAreaBitmap = areaBitmap;
    }

    /**
     * Returns the custom bitmap used for the rocker knob, or null if using color fill.
     *
     * @return the rocker bitmap, or null
     */
    public Bitmap getRockerBitmap() {
        return mRockerBitmap;
    }

    /**
     * Sets a custom bitmap for rendering the rocker knob instead of a solid color circle.
     *
     * @param rockerBitmap the bitmap to use, or null to revert to color fill
     */
    public void setRockerBitmap(Bitmap rockerBitmap) {
        mRockerBitmap = rockerBitmap;
    }

    /**
     * Returns the surface drawing refresh cycle in milliseconds.
     *
     * @return the refresh cycle in ms
     */
    public int getRefreshCycle() {
        return mRefreshCycle;
    }

    /**
     * Sets the surface drawing refresh cycle in milliseconds.
     *
     * @param refreshCycle the refresh interval in ms
     */
    public void setRefreshCycle(int refreshCycle) {
        mRefreshCycle = refreshCycle;
    }

    /**
     * Returns the listener callback cycle in milliseconds.
     *
     * @return the callback cycle in ms
     */
    public int getCallbackCycle() {
        return mCallbackCycle;
    }

    /**
     * Sets the interval at which the [RockerListener] is invoked with current angle and distance.
     *
     * @param callbackCycle the callback interval in ms
     */
    public void setCallbackCycle(int callbackCycle) {
        mCallbackCycle = callbackCycle;
    }

    /**
     * Returns the active area fill color.
     *
     * @return the area color as an ARGB integer
     */
    public int getAreaColor() {
        return mAreaColor;
    }

    /**
     * Sets the active area fill color and clears any custom area bitmap.
     *
     * @param areaColor the ARGB color value
     */
    public void setAreaColor(int areaColor) {
        mAreaColor = areaColor;
        mAreaBitmap = null;
    }

    /**
     * Returns the rocker knob fill color.
     *
     * @return the rocker color as an ARGB integer
     */
    public int getRockerColor() {
        return mRockerColor;
    }

    /**
     * Sets the rocker knob fill color and clears any custom rocker bitmap.
     *
     * @param rockerColor the ARGB color value
     */
    public void setRockerColor(int rockerColor) {
        mRockerColor = rockerColor;
        mRockerBitmap = null;
    }

    /**
     * Sets the listener that receives periodic rocker angle and distance callbacks.
     *
     * @param listener the [RockerListener] to receive events
     */
    public void setListener(@NonNull RockerListener listener) {
        mListener = listener;
    }

    /*Rocker Listener******************************************************************************/

    /**
     * Listener interface for receiving rocker position change events.
     */
    public interface RockerListener {

        /**
         * Called when the rocker position changes or on periodic callback ticks.
         *
         * @param eventType       the event type: [EVENT_ACTION] for touch events, [EVENT_CLOCK] for periodic ticks
         * @param currentAngle    the current angle in degrees (0-360, where 0 is up), or -1 if centered
         * @param currentDistance the current distance from center in pixels
         */
        void callback(int eventType, float currentAngle, float currentDistance);
    }

}