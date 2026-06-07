package black.android.util;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.util.Singleton.
 * Provides access to the lazy singleton pattern used internally by Android.
 */
@BClassName("android.util.Singleton")
public interface Singleton {
    /** The cached singleton instance. */
    @BField
    Object mInstance();

    /** Get the singleton instance, creating it if needed. */
    @BMethod
    Object get();
}
