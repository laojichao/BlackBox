package black.android.webkit;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.webkit.IWebViewUpdateService methods.
 * Provides access to the WebView update service for querying provider info.
 */
@BClassName("android.webkit.IWebViewUpdateService")
public interface IWebViewUpdateService {
    /** Get the package name of the currently active WebView implementation. */
    @BMethod
    String getCurrentWebViewPackageName();

    /** Wait for and return the current WebView provider. */
    @BMethod
    Object waitForAndGetProvider();
}
