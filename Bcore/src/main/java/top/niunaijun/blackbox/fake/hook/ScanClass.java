package top.niunaijun.blackbox.fake.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation placed on a {@link ClassInvocationStub} subclass to specify additional
 * classes that should be scanned for {@link ProxyMethod} and {@link ProxyMethods}
 * annotations during hook initialization.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScanClass {
    /**
     * The classes to scan for proxy method annotations.
     *
     * @return the array of classes to scan
     */
    Class<?>[] value() default {};
}