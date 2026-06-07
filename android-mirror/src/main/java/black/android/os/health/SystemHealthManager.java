package black.android.os.health;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.os.health.SystemHealthManager.
 * Provides access to the internal IBatteryStats service binder.
 */
@BClassName("android.os.health.SystemHealthManager")
public interface SystemHealthManager {
    /**
     * The underlying IBatteryStats service interface.
     */
    @BField
    IInterface mBatteryStats();
}
