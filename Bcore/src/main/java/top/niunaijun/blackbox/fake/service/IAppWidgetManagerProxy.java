package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.com.android.internal.appwidget.BRIAppWidgetServiceStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android App Widget Manager system service (IAppWidgetService).
 * Intercepts widget management operations to replace package names with the host
 * package and return safe default values for all widget-related methods. Prevents
 * virtual apps from directly managing host device widgets.
 *
 * @author Milk
 */
public class IAppWidgetManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the App Widget Manager binder service.
     */
    public IAppWidgetManagerProxy() {
        super(BRServiceManager.get().getService(Context.APPWIDGET_SERVICE));
    }

    /**
     * Returns the IAppWidgetService interface instance from the system service.
     *
     * @return the original IAppWidgetService binder interface
     */
    @Override
    protected Object getWho() {
        return BRIAppWidgetServiceStub.get().asInterface(BRServiceManager.get().getService(Context.APPWIDGET_SERVICE));
    }

    /**
     * Replaces the system App Widget Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.APPWIDGET_SERVICE);
    }

    /**
     * Checks if the environment has been corrupted by another proxy.
     *
     * @return always returns false
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Intercepts all method calls to replace app package names in arguments
     * with the host package name before delegation.
     *
     * @param proxy the proxy object
     * @param method the method being invoked
     * @param args the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method call fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    /**
     * Binds default value method proxies for all widget management operations.
     * Each method returns a safe default value (0, false, null, or empty array)
     * to prevent virtual apps from interacting with real host widgets.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("startListening", new int[0]));
        addMethodHook(new ValueMethodProxy("stopListening", 0));
        addMethodHook(new ValueMethodProxy("allocateAppWidgetId", 0));
        addMethodHook(new ValueMethodProxy("deleteAppWidgetId", 0));
        addMethodHook(new ValueMethodProxy("deleteHost", 0));
        addMethodHook(new ValueMethodProxy("deleteAllHosts", 0));
        addMethodHook(new ValueMethodProxy("getAppWidgetViews", null));
        addMethodHook(new ValueMethodProxy("getAppWidgetIdsForHost", null));
        addMethodHook(new ValueMethodProxy("createAppWidgetConfigIntentSender", null));
        addMethodHook(new ValueMethodProxy("updateAppWidgetIds", 0));
        addMethodHook(new ValueMethodProxy("updateAppWidgetOptions", 0));
        addMethodHook(new ValueMethodProxy("getAppWidgetOptions", null));
        addMethodHook(new ValueMethodProxy("partiallyUpdateAppWidgetIds", 0));
        addMethodHook(new ValueMethodProxy("updateAppWidgetProvider", 0));
        addMethodHook(new ValueMethodProxy("notifyAppWidgetViewDataChanged", 0));
        addMethodHook(new ValueMethodProxy("getInstalledProvidersForProfile", null));
        addMethodHook(new ValueMethodProxy("getAppWidgetInfo", null));
        addMethodHook(new ValueMethodProxy("hasBindAppWidgetPermission", false));
        addMethodHook(new ValueMethodProxy("setBindAppWidgetPermission", 0));
        addMethodHook(new ValueMethodProxy("bindAppWidgetId", false));
        addMethodHook(new ValueMethodProxy("bindRemoteViewsService", 0));
        addMethodHook(new ValueMethodProxy("unbindRemoteViewsService", 0));
        addMethodHook(new ValueMethodProxy("getAppWidgetIds", new int[0]));
        addMethodHook(new ValueMethodProxy("isBoundWidgetPackage", false));
    }
}
