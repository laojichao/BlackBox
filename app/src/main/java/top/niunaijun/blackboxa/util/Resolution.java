package top.niunaijun.blackboxa.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * Utility class for screen resolution and display metric conversions.
 *
 * Provides methods for querying screen dimensions, converting between dp/sp/px units,
 * managing soft keyboard visibility, and detecting status/navigation bar sizes.
 */
public class Resolution {
    private static final String TAG = "UtilsScreen";

    /**
     * Gets the width of the display, in pixels.
     * <p>
     * Note that this value should not be used for computing layouts, since a
     * device will typically have screen decoration (such as a status bar) along
     * the edges of the display that reduce the amount of application space
     * available from the size returned here. Layouts should instead use the
     * window size.
     * <p>
     * The size is adjusted based on the current rotation of the display.
     * <p>
     * The size returned by this method does not necessarily represent the
     * actual raw size (native resolution) of the display. The returned size may
     * be adjusted to exclude certain system decoration elements that are always
     * visible. It may also be scaled to provide compatibility with older
     * applications that were originally designed for smaller displays.
     *
     * @return Screen width in pixels.
     */
    public static int getScreenWidth(Context context) {
        return getScreenSize(context, null).x;
    }

    /**
     * Gets the height of the display, in pixels.
     * <p>
     * Note that this value should not be used for computing layouts, since a
     * device will typically have screen decoration (such as a status bar) along
     * the edges of the display that reduce the amount of application space
     * available from the size returned here. Layouts should instead use the
     * window size.
     * <p>
     * The size is adjusted based on the current rotation of the display.
     * <p>
     * The size returned by this method does not necessarily represent the
     * actual raw size (native resolution) of the display. The returned size may
     * be adjusted to exclude certain system decoration elements that are always
     * visible. It may also be scaled to provide compatibility with older
     * applications that were originally designed for smaller displays.
     *
     * @return Screen height in pixels.
     */
    public static int getScreenHeight(Context context) {
        return getScreenSize(context, null).y;
    }

    /**
     * Gets the size of the display, in pixels.
     * <p>
     * Note that this value should not be used for computing layouts, since a
     * device will typically have screen decoration (such as a status bar) along
     * the edges of the display that reduce the amount of application space
     * available from the size returned here. Layouts should instead use the
     * window size.
     * <p>
     * The size is adjusted based on the current rotation of the display.
     * <p>
     * The size returned by this method does not necessarily represent the
     * actual raw size (native resolution) of the display. The returned size may
     * be adjusted to exclude certain system decoration elements that are always
     * visible. It may also be scaled to provide compatibility with older
     * applications that were originally designed for smaller displays.
     *
     * @param outSize null-ok. If it is null, will create a Point instance inside,
     *                otherwise use it to fill the output. NOTE if it is not null,
     *                it will be the returned value.
     * @return Screen size in pixels, the x is the width, the y is the height.
     */
    @SuppressLint("NewApi")
    public static Point getScreenSize(Context context, Point outSize) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point ret = outSize == null ? new Point() : outSize;
        final Display defaultDisplay = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(ret);
        } else {
            ret.x = defaultDisplay.getWidth();
            ret.y = defaultDisplay.getHeight();
        }
        return ret;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * Returns the screen density scale factor.
     *
     * @param context the context to retrieve display metrics from
     * @return the screen density, or 0 if context is null
     */
    public static float getDensity(Context context) {
        float density = 0f;
        if (context== null) {
            return density;
        }
        try {
            density = context.getResources().getDisplayMetrics().density;
        } catch (Exception e) {

        }
        return density;
    }

    /**
     * Checks whether the given resolution matches the device's native screen resolution.
     *
     * @param context the activity context
     * @param width   the expected width in pixels
     * @param height  the expected height in pixels
     * @return true if the given dimensions match the device screen
     */
    public static boolean checkPix(Activity context, int width, int height) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            DisplayMetrics metrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            return metrics.widthPixels == width && metrics.heightPixels == height;
        } else {
            return getScreenPixWidth(context) == width && getScreenPixHeight(context) == height;
        }
    }

    /**
     * Returns the screen width in pixels from display metrics.
     *
     * @param context the context to retrieve display metrics from
     * @return the screen width in pixels
     */
    public static int getScreenPixWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Returns the screen height in pixels from display metrics.
     *
     * @param context the context to retrieve display metrics from
     * @return the screen height in pixels
     */
    public static int getScreenPixHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px).
     *
     * @param context the context to retrieve display metrics from
     * @param dip     the value in dp to convert
     * @return the equivalent value in pixels
     */
    public static int dipToPx(Context context, int dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Converts pixels (px) to density-independent pixels (dp).
     *
     * @param context the context to retrieve display metrics from
     * @param pxValue the value in pixels to convert
     * @return the equivalent value in dp
     */
    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Converts scale-independent pixels (sp) to pixels (px) for consistent text sizing.
     *
     * @param context the context to retrieve display metrics from
     * @param spValue the value in sp to convert
     * @return the equivalent value in pixels
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * Hides the soft keyboard from the given view.
     *
     * @param view the view whose window token is used to hide the keyboard
     */
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard for the given view.
     *
     * @param view the view to receive keyboard input
     */
    public static void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Shows the soft keyboard for the given view after a specified delay.
     *
     * @param view       the view to receive keyboard input
     * @param delayMillis the delay in milliseconds before showing the keyboard
     */
    public static void showInputMethod(final View view, long delayMillis) {
        // 显示输入法
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Resolution.showInputMethod(view);

            }
        }, delayMillis);
    }

    /**
     * Checks whether the device screen is currently unlocked.
     *
     * @param c the context to retrieve the keyguard service from
     * @return true if the screen is unlocked, false if locked
     */
    public static boolean isScreenLocked(Context c) {
        KeyguardManager mKeyguardManager = (KeyguardManager) c
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean bResult = !mKeyguardManager.inKeyguardRestrictedInputMode();

        return bResult;
    }

    /**
     * Returns the height of the system status bar in pixels.
     *
     * @param context the context to retrieve resources from
     * @return the status bar height in pixels, or 38 as a fallback default
     */
    public static int getBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    //http://stackoverflow.com/questions/20264268/how-to-get-height-and-width-of-navigation-bar-programmatically
    //获取屏幕下方导航栏高度
    /**
     * Returns the size of the system navigation bar at the bottom of the screen.
     *
     * @param context the context to retrieve display information from
     * @return a Point where x is the navigation bar width and y is its height;
     *         returns an empty Point if no navigation bar is present
     */
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getScreenSize(context, null);
        Point realScreenSize = getRealScreenSize(context);

//        // navigation bar on the right
//        if (appUsableSize.x < realScreenSize.x) {
//            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
//        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }


    /**
     * Returns the real physical screen size in pixels, including any system decorations.
     *
     * @param context the context to retrieve the window manager from
     * @return a Point representing the full screen resolution (x = width, y = height)
     */
    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
            }
        }

        return size;
    }
}
