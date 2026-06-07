package black.android.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.Activity}.
 * Provides access to hidden fields and methods of the Activity class,
 * such as activity info, result handling, and token references.
 */
@BClassName("android.app.Activity")
public interface Activity {
    /** Returns the hidden {@code mActivityInfo} field containing the activity's metadata. */
    @BField
    ActivityInfo mActivityInfo();

    /** Returns the hidden {@code mEmbeddedID} field identifying this embedded activity. */
    @BField
    String mEmbeddedID();

    /** Returns the hidden {@code mFinished} field indicating whether the activity has finished. */
    @BField
    boolean mFinished();

    /** Returns the hidden {@code mParent} field referencing the parent activity. */
    @BField
    android.app.Activity mParent();

    /** Returns the hidden {@code mResultCode} field for the activity result. */
    @BField
    int mResultCode();

    /** Returns the hidden {@code mResultData} field containing the result intent. */
    @BField
    Intent mResultData();

    /** Returns the hidden {@code mToken} IBinder field identifying this activity. */
    @BField
    IBinder mToken();

    /** Invokes the hidden {@code onActivityResult} callback with the given request/result codes and data. */
    @BMethod
    void onActivityResult(int int0, int int1, Intent Intent2);
}
