package top.niunaijun.blackbox.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.R;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Transparent launcher Activity that serves as a bridge for starting virtual applications.
 * <p>
 * This activity displays the target application's icon while the actual virtual app
 * starts in the background via {@link top.niunaijun.blackbox.core.system.am.BActivityManagerService}.
 * Once the virtual activity becomes visible, this activity automatically finishes itself
 * on resume to avoid stacking on top of the launched app.
 *
 * @author BlackBox
 */
public class LauncherActivity extends Activity {
    public static final String TAG = "SplashScreen";

    /** Extra key for the target Intent to launch inside the virtual environment. */
    public static final String KEY_INTENT = "launch_intent";
    /** Extra key for the target virtual application's package name. */
    public static final String KEY_PKG = "launch_pkg";
    /** Extra key for the virtual user ID under which the app should run. */
    public static final String KEY_USER_ID = "launch_user_id";
    private boolean isRunning = false;

    /**
     * Convenience method to launch a virtual application through this activity.
     * <p>
     * Creates a new intent targeting {@code LauncherActivity}, attaches the real
     * launch intent and user ID as extras, and starts the activity in a new task.
     *
     * @param intent  the intent describing the virtual activity to launch (must have a package set)
     * @param userId  the virtual user ID to run the application under
     */
    public static void launch(Intent intent, int userId) {
        Intent splash = new Intent();
        splash.setClass(BlackBoxCore.getContext(), LauncherActivity.class);
        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        splash.putExtra(LauncherActivity.KEY_INTENT, intent);
        splash.putExtra(LauncherActivity.KEY_PKG, intent.getPackage());
        splash.putExtra(LauncherActivity.KEY_USER_ID, userId);
        BlackBoxCore.getContext().startActivity(splash);
    }

    /**
     * Initializes the launcher activity: extracts the target intent and package info,
     * displays the application icon, and starts the virtual activity on a background thread.
     * Finishes immediately if the intent is missing or the package is not installed.
     *
     * @param savedInstanceState the saved instance state bundle, or {@code null} if not restoring
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Intent launchIntent = intent.getParcelableExtra(KEY_INTENT);
        String packageName = intent.getStringExtra(KEY_PKG);
        int userId = intent.getIntExtra(KEY_USER_ID, 0);

        PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, 0, userId);
        if (packageInfo == null) {
            Slog.e(TAG, packageName + " not installed!");
            finish();
            return;
        }
        Drawable drawable = packageInfo.applicationInfo.loadIcon(BlackBoxCore.getPackageManager());
        setContentView(R.layout.activity_launcher);
        findViewById(R.id.iv_icon).setBackgroundDrawable(drawable);
        new Thread(() -> BlackBoxCore.getBActivityManager().startActivity(launchIntent, userId)).start();
    }

    /**
     * Marks that the launcher has moved to the background, indicating the virtual
     * activity has likely started and taken focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        isRunning = true;
    }

    /**
     * Automatically finishes this activity when it resumes after having been paused,
     * since this indicates the virtual application has already taken over the foreground.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning) {
            finish();
        }
    }
}
