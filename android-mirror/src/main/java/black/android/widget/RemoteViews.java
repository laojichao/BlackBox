package black.android.widget;

import android.content.pm.ApplicationInfo;

import java.util.ArrayList;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.widget.RemoteViews fields.
 * Provides access to internal action list and application info.
 */
@BClassName("android.widget.RemoteViews")
public interface RemoteViews {
    /** The list of RemoteViews actions to apply. */
    @BField
    ArrayList<Object> mActions();

    /** The ApplicationInfo of the package that owns this RemoteViews. */
    @BField
    ApplicationInfo mApplication();

    /** The package name associated with this RemoteViews. */
    @BField
    String mPackage();
}
