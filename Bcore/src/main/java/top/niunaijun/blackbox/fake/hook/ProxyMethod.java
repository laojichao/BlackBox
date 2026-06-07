package top.niunaijun.blackbox.fake.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used on inner classes of a {@link ClassInvocationStub} to indicate
 * that the annotated class is a {@link MethodHook} targeting a specific method name.
 * The value specifies the method name to intercept.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyMethod {
    /**
     * The name of the method to hook.
     *
     * @return the target method name
     */
    String value();
}
