package black.android.app;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Reflection mirror for {@code android.app.SharedPreferencesImpl}.
 * Provides access to the hidden constructor for creating a SharedPreferencesImpl
 * backed by the given file.
 */
@BClassName("android.app.SharedPreferencesImpl")
public interface SharedPreferencesImpl {
    /** Creates a new SharedPreferencesImpl backed by the specified file and mode. */
    @BConstructor
    SharedPreferencesImpl _new(File File0, int int1);
}
