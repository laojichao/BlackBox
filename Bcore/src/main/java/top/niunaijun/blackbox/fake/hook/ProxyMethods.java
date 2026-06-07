package top.niunaijun.blackbox.fake.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used on inner classes of a {@link ClassInvocationStub} to indicate
 * that the annotated class is a {@link MethodHook} targeting multiple method names.
 * The value is an array of method names to intercept.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyMethods {
    /**
     * The names of the methods to hook.
     *
     * @return the array of target method names
     */
    String[] value() default {};
}
