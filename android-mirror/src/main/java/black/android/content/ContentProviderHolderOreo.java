package black.android.content;

import android.content.pm.ProviderInfo;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.app.ContentProviderHolder (API 26+).
 * Holds a reference to a content provider along with its metadata.
 */
@BClassName("android.app.ContentProviderHolder")
public interface ContentProviderHolderOreo {
    /** The ProviderInfo metadata for this content provider. */
    @BField
    ProviderInfo info();

    /** Whether no release is needed when done with this provider. */
    @BField
    boolean noReleaseNeeded();

    /** The IContentProvider binder interface. */
    @BField
    IInterface provider();
}
