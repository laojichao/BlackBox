package black.android.webkit;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.webkit.WebViewFactory fields and methods.
 * Provides access to WebView provider detection and update service.
 */
@BClassName("android.webkit.WebViewFactory")
public interface WebViewFactory {
    /** Whether WebView is supported on this device. */
    @BStaticField
    Boolean sWebViewSupported();

    /** Get the IWebViewUpdateService for checking WebView provider status. */
    @BStaticMethod
    Object getUpdateService();
}
