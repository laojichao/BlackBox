package top.niunaijun.blackbox.utils.compat;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.WindowManager;

import black.android.app.BRActivity;
import black.com.android.internal.BRRstyleable;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.utils.DrawableUtils;

/**
 * Compatibility utility for fixing {@link android.app.Activity} state within the virtual environment.
 * <p>
 * Handles activity initialization differences across Android versions by applying the correct
 * window attributes (wallpaper background, fullscreen flags), setting up the task description
 * with the appropriate label and icon for the virtual user on Android 5.0+ (Lollipop), and
 * resolving the correct content resolver for the virtual environment.
 */
public class ActivityCompat {

    /**
     * Fixes the given activity's internal state for the virtual environment.
     * <p>
     * This method resolves the activity's content resolver, applies window theme attributes
     * such as wallpaper background and fullscreen flags, and on Android 5.0+ (Lollipop)
     * sets the task description with a user-prefixed label and the activity icon.
     *
     * @param activity the activity instance to fix
     */
    public static void fix(Activity activity) {
        // mContentResolver
        BRActivity.get(activity).mActivityInfo();

        Context baseContext = activity.getBaseContext();
        try {
            TypedArray typedArray = activity.obtainStyledAttributes((BRRstyleable.get().Window()));
            if (typedArray != null) {
                boolean showWallpaper = typedArray.getBoolean(BRRstyleable.get().Window_windowShowWallpaper(),
                        false);
                if (showWallpaper) {
                    activity.getWindow().setBackgroundDrawable(WallpaperManager.getInstance(activity).getDrawable());
                }
                boolean fullscreen = typedArray.getBoolean(BRRstyleable.get().Window_windowFullscreen(), false);
                if (fullscreen) {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                typedArray.recycle();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = activity.getIntent();
            ApplicationInfo applicationInfo = baseContext.getApplicationInfo();
            PackageManager pm = activity.getPackageManager();
            if (intent != null && activity.isTaskRoot()) {
                try {
                    String label = TaskDescriptionCompat.getTaskDescriptionLabel(
                            BActivityThread.getUserId(), applicationInfo.loadLabel(pm));

                    Bitmap icon = null;
                    Drawable drawable = getActivityIcon(activity);
                    if (drawable != null) {
                        ActivityManager am = (ActivityManager) baseContext.getSystemService(Context.ACTIVITY_SERVICE);
                        int iconSize = am.getLauncherLargeIconSize();
                        icon = DrawableUtils.drawableToBitmap(drawable, iconSize, iconSize);
                    }

                    activity.setTaskDescription(new ActivityManager.TaskDescription(label, icon));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Drawable getActivityIcon(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            Drawable icon = pm.getActivityIcon(activity.getComponentName());
            if (icon != null)
                return icon;
        } catch (PackageManager.NameNotFoundException ignore) {
        }

        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        return applicationInfo.loadIcon(pm);
    }
}