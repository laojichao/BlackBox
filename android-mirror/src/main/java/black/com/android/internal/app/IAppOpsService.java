package black.com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.app.IAppOpsService.
 * AIDL interface for the application operations (AppOps) system service.
 */
@BClassName("com.android.internal.app.IAppOpsService")
public interface IAppOpsService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.app.IAppOpsService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IAppOpsService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
