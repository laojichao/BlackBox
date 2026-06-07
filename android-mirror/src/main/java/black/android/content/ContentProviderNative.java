package black.android.content;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.ContentProviderNative.
 * Provides static utility to convert an IBinder to an IContentProvider proxy.
 */
@BClassName("android.content.ContentProviderNative")
public interface ContentProviderNative {
    /** Converts a raw IBinder to an IContentProvider proxy interface. */
    @BStaticMethod
    IInterface asInterface(IBinder IBinder0);
}
