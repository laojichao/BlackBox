package black.android.os;

import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.os.Bundle methods.
 * Provides access to hidden IBinder get/put operations.
 */
@BClassName("android.os.Bundle")
public interface Bundle {
    /**
     * Retrieve an IBinder value from the bundle by key.
     */
    @BMethod
    IBinder getIBinder(String String0);

    /**
     * Store an IBinder value in the bundle by key.
     */
    @BMethod
    void putIBinder(String String0, IBinder IBinder1);
}
