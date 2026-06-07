package black.android.app;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.AlarmManager}.
 * Provides access to hidden fields of the AlarmManager, including
 * the underlying IAlarmManager binder service and target SDK version.
 */
@BClassName("android.app.AlarmManager")
public interface AlarmManager {
    /** Returns the hidden {@code mService} field referencing the IAlarmManager binder. */
    @BField
    IInterface mService();

    /** Returns the hidden {@code mTargetSdkVersion} field for the application's target SDK. */
    @BField
    int mTargetSdkVersion();
}
