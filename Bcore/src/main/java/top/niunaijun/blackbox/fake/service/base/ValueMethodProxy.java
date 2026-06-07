package top.niunaijun.blackbox.fake.service.base;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * A reusable method hook that intercepts a named method and returns a
 * predetermined static value instead of executing the real implementation.
 * <p>
 * This proxy is used to stub out service methods that should return safe
 * default values (e.g., 0, false, null, empty arrays) without forwarding
 * the call to the real system service.
 */
public class ValueMethodProxy extends MethodHook {

	Object mValue;
	String mName;

	/**
	 * Constructs a new value method proxy.
	 *
	 * @param name  the name of the method to hook
	 * @param value the static value to return when the method is called
	 */
	public ValueMethodProxy(String name, Object value) {
		mValue = value;
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
	 * Returns the predetermined static value without invoking the real method.
	 *
	 * @param who    the target object (unused)
	 * @param method the original method (unused)
	 * @param args   the method arguments (unused)
	 * @return the static value configured at construction time
	 * @throws Throwable never thrown
	 */
	@Override
	protected Object hook(Object who, Method method, Object[] args) throws Throwable {
		return mValue;
	}
}
