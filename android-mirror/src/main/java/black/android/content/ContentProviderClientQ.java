package black.android.content;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Mirror of hidden android.content.ContentProviderClient constructor for Android Q (API 29+).
 * Allows direct instantiation via reflection.
 */
@BClassName("android.content.ContentProviderClient")
public interface ContentProviderClientQ {
    /** Creates a new ContentProviderClient instance via reflection. */
    @BConstructor
    ContentProviderClientQ _new();
}
