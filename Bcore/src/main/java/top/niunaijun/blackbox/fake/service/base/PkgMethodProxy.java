package top.niunaijun.blackbox.fake.service.base;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * A reusable method hook that replaces the first application package name
 * argument in method calls before delegating to the real implementation.
 * <p>
 * This proxy is used by service proxies that need to transparently replace
 * the virtual app's package name with the host or caller package in
 * service method arguments.
 */
public class PkgMethodProxy extends MethodHook {

	String mName;

	/**
	 * Constructs a new package method proxy for the given method name.
	 *
	 * @param name the name of the method to hook
	 */
	public PkgMethodProxy(String name) {
		mName = name;
	}

	/**
	 * Returns the method name this proxy intercepts.
	 *
	 * @return the target method name
	 */
	@Override
	protected String getMethodName() {
		return mName;
	}

	/**
	 * Replaces the first application package name argument and delegates to the real method.
	 *
	 * @param who    the target object
	 * @param method the original method
	 * @param args   the method arguments
	 * @return the result of the delegated method call
	 * @throws Throwable if invocation fails
	 */
	@Override
	protected Object hook(Object who, Method method, Object[] args) throws Throwable {
		MethodParameterUtils.replaceFirstAppPkg(args);
		return method.invoke(who, args);
	}
}
