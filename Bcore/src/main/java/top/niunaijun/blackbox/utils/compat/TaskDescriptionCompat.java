package top.niunaijun.blackbox.utils.compat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.Locale;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.utils.DrawableUtils;

/**
 * Compatibility utility for fixing {@link android.app.ActivityManager.TaskDescription} in the
 * virtual environment.
 * <p>
 * Inside a virtual (multi-user) space, the recents task label and icon may not reflect the
 * actual application identity. This class ensures the task description shows the correct
 * app label (prefixed with the virtual user ID) and icon bitmap so that users can
 * distinguish between virtual-space instances of the same application.
 */
public class TaskDescriptionCompat {
    /**
     * Fixes a {@link TaskDescription} by ensuring it has a valid label and icon.
     * <p>
     * If the given task description already contains both a label and an icon it is returned
     * unchanged. Otherwise, the application label (prepended with the virtual user ID) and
     * icon are fetched from the package manager and a new TaskDescription is built.
     *
     * @param td the original TaskDescription to fix; may have null label or icon
     * @return a TaskDescription guaranteed to have a non-null label and icon
     */
    public static ActivityManager.TaskDescription fix(ActivityManager.TaskDescription td) {
        String label = td.getLabel();
        Bitmap icon = td.getIcon();

        if (label != null && icon != null)
            return td;

        label = getTaskDescriptionLabel(BActivityThread.getUserId(), getApplicationLabel());
        Drawable drawable = getApplicationIcon();
        if (drawable == null)
            return td;

        ActivityManager am = (ActivityManager) BlackBoxCore.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int iconSize = am.getLauncherLargeIconSize();
        icon = DrawableUtils.drawableToBitmap(drawable, iconSize, iconSize);
        td = new ActivityManager.TaskDescription(label, icon, td.getPrimaryColor());
        return td;
    }

    /**
     * Formats a task description label with the virtual user ID prefix.
     * <p>
     * The resulting label follows the pattern {@code [B<userId>]<label>}, for example
     * {@code [B0]MyApp} or {@code [B999]MyApp}.
     *
     * @param userId the virtual user ID (e.g. 0 for the default virtual user)
     * @param label  the application label to prefix
     * @return the formatted label string in the form {@code [B<userId>]<label>}
     */
    public static String getTaskDescriptionLabel(int userId, CharSequence label) {
        return String.format(Locale.CHINA, "[B%d]%s", userId, label);
    }

    private static CharSequence getApplicationLabel() {
        try {
            PackageManager pm = BlackBoxCore.getPackageManager();
            return pm.getApplicationLabel(pm.getApplicationInfo(BActivityThread.getAppPackageName(), 0));
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static Drawable getApplicationIcon() {
        try {
            return BlackBoxCore.getPackageManager().getApplicationIcon(BActivityThread.getAppPackageName());
        } catch (PackageManager.NameNotFoundException ignore) {
            return null;
        }
    }
}
