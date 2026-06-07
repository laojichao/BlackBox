package black.android.content;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.content.ContentResolver internals.
 * Provides access to the static IContentService and instance package name.
 */
@BClassName("android.content.ContentResolver")
public interface ContentResolver {
    /** Static reference to the IContentService system service. */
    @BStaticField
    IInterface sContentService();

    /** The package name of this content resolver's owning application. */
    @BField
    String mPackageName();
}
