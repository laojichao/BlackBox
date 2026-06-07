package top.niunaijun.blackbox.fake.hook;

import android.text.TextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Abstract base class for intercepting method calls on arbitrary objects using Java's
 * dynamic proxy mechanism. Implements {@link InvocationHandler} to route method calls
 * through registered {@link MethodHook} instances. Supports annotation-based hook
 * registration via {@link ProxyMethod}, {@link ProxyMethods}, and {@link ScanClass}.
 */
public abstract class ClassInvocationStub implements InvocationHandler, IInjectHook {
    public static final String TAG = ClassInvocationStub.class.getSimpleName();

    private final Map<String, MethodHook> mMethodHookMap = new HashMap<>();
    private Object mBase;
    private Object mProxyInvocation;
    private boolean onlyProxy;

    /**
     * Returns the original (un-proxied) object being wrapped.
     *
     * @return the target object to intercept calls on
     */
    protected abstract Object getWho();

    /**
     * Called to inject the proxy into the target system, replacing the original
     * object reference with the proxy instance.
     *
     * @param baseInvocation  the original object
     * @param proxyInvocation the dynamic proxy instance
     */
    protected abstract void inject(Object baseInvocation, Object proxyInvocation);

    /**
     * Called after injection to allow subclasses to manually register method hooks.
     */
    protected void onBindMethod() {

    }

    /**
     * Returns the proxy invocation object.
     *
     * @return the proxy instance
     */
    protected Object getProxyInvocation() {
        return mProxyInvocation;
    }

    /**
     * Returns the original base object.
     *
     * @return the base object
     */
    protected Object getBase() {
        return mBase;
    }

    /**
     * Sets whether this stub should only create a proxy without injecting it.
     *
     * @param o true to only proxy without injection
     */
    protected void onlyProxy(boolean o) {
        onlyProxy = o;
    }

    /**
     * Initializes the hook by creating a dynamic proxy, injecting it, and scanning
     * for annotated method hooks in declared inner classes and scanned classes.
     */
    @Override
    public void injectHook() {
        mBase = getWho();
        mProxyInvocation = Proxy.newProxyInstance(mBase.getClass().getClassLoader(), MethodParameterUtils.getAllInterface(mBase.getClass()), this);
        if (!onlyProxy) {
            inject(mBase, mProxyInvocation);
        }

        onBindMethod();
        Class<?>[] declaredClasses = this.getClass().getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            initAnnotation(declaredClass);
        }
        ScanClass scanClass = this.getClass().getAnnotation(ScanClass.class);
        if (scanClass != null) {
            for (Class<?> aClass : scanClass.value()) {
                for (Class<?> declaredClass : aClass.getDeclaredClasses()) {
                    initAnnotation(declaredClass);
                }
            }
        }
    }

    /**
     * Scans a class for {@link ProxyMethod} and {@link ProxyMethods} annotations
     * and registers the corresponding MethodHook instances.
     *
     * @param clazz the class to scan for proxy method annotations
     */
    protected void initAnnotation(Class<?> clazz) {
        ProxyMethod proxyMethod = clazz.getAnnotation(ProxyMethod.class);
        if (proxyMethod != null) {
            final String name = proxyMethod.value();
            if (!TextUtils.isEmpty(name)) {
                try {
                    addMethodHook(name, (MethodHook) clazz.newInstance());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        ProxyMethods proxyMethods = clazz.getAnnotation(ProxyMethods.class);
        if (proxyMethods != null) {
            String[] value = proxyMethods.value();
            for (String name : value) {
                try {
                    addMethodHook(name, (MethodHook) clazz.newInstance());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Registers a MethodHook using its own method name.
     *
     * @param methodHook the MethodHook to register
     */
    protected void addMethodHook(MethodHook methodHook) {
        mMethodHookMap.put(methodHook.getMethodName(), methodHook);
    }

    /**
     * Registers a MethodHook for a specific method name.
     *
     * @param name       the method name to hook
     * @param methodHook the MethodHook to register
     */
    protected void addMethodHook(String name, MethodHook methodHook) {
        mMethodHookMap.put(name, methodHook);
    }

    /**
     * Handles proxied method invocations. If a matching MethodHook is registered and
     * enabled, executes the hook chain (beforeHook -> hook -> afterHook). Otherwise,
     * delegates directly to the original method.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the method result
     * @throws Throwable if the method invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHook methodHook = mMethodHookMap.get(method.getName());
        if (methodHook == null || !methodHook.isEnable()) {
            try {
                return method.invoke(mBase, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
        }

        Object result = methodHook.beforeHook(mBase, method, args);
        if (result != null) {
            return result;
        }
        result = methodHook.hook(mBase, method, args);
        result = methodHook.afterHook(result);
        return result;
    }
}
