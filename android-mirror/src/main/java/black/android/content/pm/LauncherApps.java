package black.android.content.pm;

import android.content.pm.PackageManager;
import android.os.IInterface;
import android.os.UserManager;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.pm.LauncherApps internals.
 * Provides access to the PackageManager, service binder, and UserManager.
 */
@BClassName("android.content.pm.LauncherApps")
public interface LauncherApps {
    /** The PackageManager instance used for package queries. */
    @BField
    PackageManager mPm();

    /** The ILauncherApps binder service interface. */
    @BField
    IInterface mService();

    /** The UserManager instance for multi-user support. */
    @BField
    UserManager mUserManager();
}
