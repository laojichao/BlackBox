package black.com.android.internal.appwidget;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.appwidget.IAppWidgetService.
 * AIDL interface for the app widget management system service.
 */
@BClassName("com.android.internal.appwidget.IAppWidgetService")
public interface IAppWidgetService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.appwidget.IAppWidgetService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IAppWidgetService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
