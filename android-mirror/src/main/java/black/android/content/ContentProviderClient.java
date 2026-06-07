package black.android.content;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.ContentProviderClient internals.
 * Provides access to the underlying IContentProvider binder proxy.
 */
@BClassName("android.content.ContentProviderClient")
public interface ContentProviderClient {
    /** The underlying IContentProvider binder interface. */
    @BField
    IInterface mContentProvider();
}
