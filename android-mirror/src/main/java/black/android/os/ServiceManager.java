package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import java.util.Map;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.ServiceManager.
 * Provides access to the Android service registry for obtaining system service binders.
 */
@BClassName("android.os.ServiceManager")
public interface ServiceManager {
    /**
     * Cache of service name to IBinder mappings.
     */
    @BStaticField
    Map<String, IBinder> sCache();

    /**
     * The raw IServiceManager binder proxy.
     */
    @BStaticField
    IInterface sServiceManager();

    /**
     * Register a binder service under the given name.
     */
    @BStaticMethod
    void addService(String String0, IBinder IBinder1);

    /**
     * Look up a cached service by name.
     */
    @BStaticMethod
    IBinder checkService();

    /**
     * Get the raw IServiceManager binder interface.
     */
    @BStaticMethod
    IInterface getIServiceManager();

    /**
     * Look up a system service by name, blocking if needed.
     */
    @BStaticMethod
    IBinder getService(String name);

    /**
     * List all registered system service names.
     */
    @BStaticMethod
    String[] listServices();
}
