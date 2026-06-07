package top.niunaijun.blackbox.fake.service.context.providers;

import android.os.IInterface;

/**
 * Interface for content provider wrappers in the virtual environment.
 * <p>
 * Implementations wrap an existing {@link IInterface} content provider proxy,
 * intercepting calls to inject the correct application package name and
 * attribution source information.
 */
public interface BContentProvider {
    /**
     * Wraps the given content provider proxy with virtual environment interception.
     *
     * @param contentProviderProxy the real content provider proxy interface
     * @param appPkg               the application package name to inject into calls
     * @return the wrapped content provider proxy
     */
    IInterface wrapper(final IInterface contentProviderProxy, final String appPkg);
}
