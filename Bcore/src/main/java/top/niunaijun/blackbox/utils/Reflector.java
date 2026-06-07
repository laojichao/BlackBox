package top.niunaijun.blackbox.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Fluent reflection utility that provides a chainable API for accessing constructors,
 * fields, and methods of classes at runtime. Supports binding to a caller instance for
 * non-static member access and includes {@link QuietReflector} for exception-suppressing
 * variant that silently swallows reflection errors.
 *
 * <p>Usage examples:
 * <pre>
 *     // Access a static field
 *     Object value = Reflector.on("com.example.MyClass").field("myField").get();
 *
 *     // Call a method on an instance
 *     Reflector.with(instance).method("doSomething", String.class).call("arg");
 * </pre>
 */
public class Reflector {
    public static final String LOG_TAG = "Reflector";

    protected Class<?> mType;
    protected Object mCaller;
    protected Constructor mConstructor;
    protected Field mField;
    protected Method mMethod;


    /**
     * Creates a {@link Reflector} targeting the class identified by the given name,
     * using the default class loader and initializing the class.
     *
     * @param name the fully qualified class name
     * @return a new {@link Reflector} instance targeting the specified class
     * @throws Exception if the class cannot be found
     */
    public static Reflector on(String name) throws Exception {
        return on(name, true, Reflector.class.getClassLoader());
    }

    /**
     * Creates a {@link Reflector} targeting the class identified by the given name,
     * using the default class loader with explicit control over static initialization.
     *
     * @param name the fully qualified class name
     * @param initialize whether to initialize the class (run static initializers)
     * @return a new {@link Reflector} instance targeting the specified class
     * @throws Exception if the class cannot be found
     */
    public static Reflector on(String name, boolean initialize) throws Exception {
        return on(name, initialize, Reflector.class.getClassLoader());
    }

    /**
     * Creates a {@link Reflector} targeting the class identified by the given name,
     * with explicit control over class loader and static initialization.
     *
     * @param name the fully qualified class name
     * @param initialize whether to initialize the class (run static initializers)
     * @param loader the class loader to use for loading the class
     * @return a new {@link Reflector} instance targeting the specified class
     * @throws Exception if the class cannot be found
     */
    public static Reflector on(String name, boolean initialize, ClassLoader loader) throws Exception {
        try {
            return on(Class.forName(name, initialize, loader));
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    /**
     * Creates a {@link Reflector} targeting the given class object directly.
     *
     * @param type the class to target for reflection operations
     * @return a new {@link Reflector} instance targeting the specified class
     */
    public static Reflector on(Class<?> type) {
        Reflector reflector = new Reflector();
        reflector.mType = type;
        return reflector;
    }

    /**
     * Creates a {@link Reflector} bound to the given caller object. The reflector
     * will use the caller's class for reflection and the caller as the instance
     * for non-static member access.
     *
     * @param caller the object instance to bind for method/field access
     * @return a new {@link Reflector} bound to the given object
     * @throws Exception if the caller cannot be validated against its class
     */
    public static Reflector with(Object caller) throws Exception {
        return on(caller.getClass()).bind(caller);
    }

    protected Reflector() {

    }

    /**
     * Selects a constructor by its parameter types for subsequent instantiation.
     * Clears any previously selected field or method.
     *
     * @param parameterTypes the parameter types of the constructor to find
     * @return this {@link Reflector} for chaining
     * @throws Exception if no matching constructor is found
     */
    public Reflector constructor(Class<?>... parameterTypes) throws Exception {
        try {
            mConstructor = mType.getDeclaredConstructor(parameterTypes);
            mConstructor.setAccessible(true);
            mField = null;
            mMethod = null;
            return this;
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    /**
     * Creates a new instance of the target class using the previously selected constructor.
     *
     * @param <R> the expected return type
     * @param initargs the arguments to pass to the constructor
     * @return the newly created instance
     * @throws Exception if no constructor has been selected or instantiation fails
     */
    @SuppressWarnings("unchecked")
    public <R> R newInstance(Object... initargs) throws Exception {
        if (mConstructor == null) {
            throw new Exception("Constructor was null!");
        }
        try {
            return (R) mConstructor.newInstance(initargs);
        } catch (InvocationTargetException e) {
            throw new Exception("Oops!", e.getTargetException());
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    protected Object checked(Object caller) throws Exception {
        if (caller == null || mType.isInstance(caller)) {
            return caller;
        }
        throw new Exception("Caller [" + caller + "] is not a instance of type [" + mType + "]!");
    }

    protected void check(Object caller, Member member, String name) throws Exception {
        if (member == null) {
            throw new Exception(name + " was null!");
        }
        if (caller == null && !Modifier.isStatic(member.getModifiers())) {
            throw new Exception("Need a caller!");
        }
        checked(caller);
    }

    /**
     * Binds this reflector to the given caller instance for subsequent non-static
     * field and method access operations.
     *
     * @param caller the object instance to bind
     * @return this {@link Reflector} for chaining
     * @throws Exception if the caller is not an instance of the target type
     */
    public Reflector bind(Object caller) throws Exception {
        mCaller = checked(caller);
        return this;
    }

    /**
     * Unbinds the current caller, allowing only static field and method access.
     *
     * @return this {@link Reflector} for chaining
     */
    public Reflector unbind() {
        mCaller = null;
        return this;
    }

    /**
     * Selects a field by name for subsequent get/set operations. Searches the entire
     * class hierarchy if the field is not declared directly on the target class.
     * Clears any previously selected constructor or method.
     *
     * @param name the name of the field to find
     * @return this {@link Reflector} for chaining
     * @throws Exception if the field cannot be found
     */
    public Reflector field(String name) throws Exception {
        try {
            mField = findField(name);
            mField.setAccessible(true);
            mConstructor = null;
            mMethod = null;
            return this;
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    protected Field findField(String name) throws NoSuchFieldException {
        try {
            return mType.getField(name);
        } catch (NoSuchFieldException e) {
            for (Class<?> cls = mType; cls != null; cls = cls.getSuperclass()) {
                try {
                    return cls.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    // Ignored
                }
            }
            throw e;
        }
    }

    /**
     * Gets the value of the previously selected field using the bound caller.
     *
     * @param <R> the expected return type
     * @return the field value cast to type {@code R}
     * @throws Exception if no field has been selected, no caller is bound for non-static access,
     *         or the field read fails
     */
    @SuppressWarnings("unchecked")
    public <R> R get() throws Exception {
        return get(mCaller);
    }

    /**
     * Gets the value of the previously selected field on the specified caller object.
     *
     * @param <R> the expected return type
     * @param caller the object instance whose field value to read
     * @return the field value cast to type {@code R}
     * @throws Exception if no field has been selected, the caller type is invalid,
     *         or the field read fails
     */
    @SuppressWarnings("unchecked")
    public <R> R get(Object caller) throws Exception {
        check(caller, mField, "Field");
        try {
            return (R) mField.get(caller);
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    /**
     * Sets the value of the previously selected field using the bound caller.
     *
     * @param value the value to set on the field
     * @return this {@link Reflector} for chaining
     * @throws Exception if no field has been selected or the field write fails
     */
    public Reflector set(Object value) throws Exception {
        return set(mCaller, value);
    }

    /**
     * Sets the value of the previously selected field on the specified caller object.
     *
     * @param caller the object instance whose field to modify
     * @param value the value to set on the field
     * @return this {@link Reflector} for chaining
     * @throws Exception if no field has been selected, the caller type is invalid,
     *         or the field write fails
     */
    public Reflector set(Object caller, Object value) throws Exception {
        check(caller, mField, "Field");
        try {
            mField.set(caller, value);
            return this;
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    /**
     * Selects a method by name and parameter types for subsequent invocation.
     * Searches the entire class hierarchy if the method is not declared directly
     * on the target class. Clears any previously selected constructor or field.
     *
     * @param name the name of the method to find
     * @param parameterTypes the parameter types of the method
     * @return this {@link Reflector} for chaining
     * @throws Exception if the method cannot be found
     */
    public Reflector method(String name, Class<?>... parameterTypes) throws Exception {
        try {
            mMethod = findMethod(name, parameterTypes);
            mMethod.setAccessible(true);
            mConstructor = null;
            mField = null;
            return this;
        } catch (NoSuchMethodException e) {
            throw new Exception("Oops!", e);
        }
    }

    protected Method findMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        try {
            return mType.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            for (Class<?> cls = mType; cls != null; cls = cls.getSuperclass()) {
                try {
                    return cls.getDeclaredMethod(name, parameterTypes);
                } catch (NoSuchMethodException ex) {
                    // Ignored
                }
            }
            throw e;
        }
    }

    /**
     * Invokes the previously selected method using the bound caller and the given arguments.
     *
     * @param <R> the expected return type
     * @param args the arguments to pass to the method
     * @return the method's return value cast to type {@code R}
     * @throws Exception if no method has been selected, no caller is bound for non-static access,
     *         or the method invocation fails
     */
    public <R> R call(Object... args) throws Exception {
        return callByCaller(mCaller, args);
    }

    /**
     * Invokes the previously selected method on the specified caller object with the given arguments.
     *
     * @param <R> the expected return type
     * @param caller the object instance on which to invoke the method
     * @param args the arguments to pass to the method
     * @return the method's return value cast to type {@code R}
     * @throws Exception if no method has been selected, the caller type is invalid,
     *         or the method invocation fails
     */
    @SuppressWarnings("unchecked")
    public <R> R callByCaller(Object caller, Object... args) throws Exception {
        check(caller, mMethod, "Method");
        try {
            return (R) mMethod.invoke(caller, args);
        } catch (InvocationTargetException e) {
            throw new Exception("Oops!", e.getTargetException());
        } catch (Throwable e) {
            throw new Exception("Oops!", e);
        }
    }

    /**
     * A variant of {@link Reflector} that silently suppresses all reflection exceptions.
     * When a reflection operation fails, the error is stored internally and {@code null}
     * is returned instead of throwing. This is useful for optional reflection where failure
     * is acceptable (e.g., hooking into classes that may not exist on all devices).
     */
    public static class QuietReflector extends Reflector {

        protected Throwable mIgnored;

        /**
         * Creates a {@link QuietReflector} targeting the class identified by the given name,
         * using the default class loader and initializing the class.
         *
         * @param name the fully qualified class name
         * @return a new {@link QuietReflector} instance; if the class is not found, the
         *         error is stored internally rather than thrown
         */
        public static QuietReflector on(String name) {
            return on(name, true, QuietReflector.class.getClassLoader());
        }

        /**
         * Creates a {@link QuietReflector} targeting the given class name with control
         * over static initialization.
         *
         * @param name the fully qualified class name
         * @param initialize whether to initialize the class
         * @return a new {@link QuietReflector} instance
         */
        public static QuietReflector on(String name, boolean initialize) {
            return on(name, initialize, QuietReflector.class.getClassLoader());
        }

        /**
         * Creates a {@link QuietReflector} targeting the given class name with full control
         * over class loader and initialization.
         *
         * @param name the fully qualified class name
         * @param initialize whether to initialize the class
         * @param loader the class loader to use
         * @return a new {@link QuietReflector} instance
         */
        public static QuietReflector on(String name, boolean initialize, ClassLoader loader) {
            Class<?> cls = null;
            try {
                cls = Class.forName(name, initialize, loader);
                return on(cls, null);
            } catch (Throwable e) {
//                Log.w(LOG_TAG, "Oops!", e);
                return on(cls, e);
            }
        }

        /**
         * Creates a {@link QuietReflector} targeting the given class object.
         *
         * @param type the class to target; if {@code null}, subsequent operations will be no-ops
         * @return a new {@link QuietReflector} instance
         */
        public static QuietReflector on(Class<?> type) {
            return on(type, (type == null) ? new Exception("Type was null!") : null);
        }

        private static QuietReflector on(Class<?> type, Throwable ignored) {
            QuietReflector reflector = new QuietReflector();
            reflector.mType = type;
            reflector.mIgnored = ignored;
            return reflector;
        }

        /**
         * Creates a {@link QuietReflector} bound to the given caller object.
         *
         * @param caller the object instance to bind; if {@code null}, returns a reflector
         *               targeting a null type
         * @return a new {@link QuietReflector} bound to the given object
         */
        public static QuietReflector with(Object caller) {
            if (caller == null) {
                return on((Class<?>) null);
            }
            return on(caller.getClass()).bind(caller);
        }

        protected QuietReflector() {

        }

        /**
         * Returns the last exception that was caught and stored, or {@code null} if
         * no error has occurred since the last successful operation.
         *
         * @return the last caught {@link Throwable}, or {@code null}
         */
        public Throwable getIgnored() {
            return mIgnored;
        }

        protected boolean skip() {
            return skipAlways() || mIgnored != null;
        }

        protected boolean skipAlways() {
            return mType == null;
        }

        @Override
        public QuietReflector constructor(Class<?>... parameterTypes) {
            if (skipAlways()) {
                return this;
            }
            try {
                mIgnored = null;
                super.constructor(parameterTypes);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return this;
        }

        @Override
        public <R> R newInstance(Object... initargs) {
            if (skip()) {
                return null;
            }
            try {
                mIgnored = null;
                return super.newInstance(initargs);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return null;
        }

        @Override
        public QuietReflector bind(Object obj) {
            if (skipAlways()) {
                return this;
            }
            try {
                mIgnored = null;
                super.bind(obj);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return this;
        }

        @Override
        public QuietReflector unbind() {
            super.unbind();
            return this;
        }

        @Override
        public QuietReflector field(String name) {
            if (skipAlways()) {
                return this;
            }
            try {
                mIgnored = null;
                super.field(name);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return this;
        }

        @Override
        public <R> R get() {
            if (skip()) {
                return null;
            }
            try {
                mIgnored = null;
                return super.get();
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return null;
        }

        @Override
        public <R> R get(Object caller) {
            if (skip()) {
                return null;
            }
            try {
                mIgnored = null;
                return super.get(caller);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return null;
        }

        @Override
        public QuietReflector set(Object value) {
            if (skip()) {
                return this;
            }
            try {
                mIgnored = null;
                super.set(value);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return this;
        }

        @Override
        public QuietReflector set(Object caller, Object value) {
            if (skip()) {
                return this;
            }
            try {
                mIgnored = null;
                super.set(caller, value);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return this;
        }

        @Override
        public QuietReflector method(String name, Class<?>... parameterTypes) {
            if (skipAlways()) {
                return this;
            }
            try {
                mIgnored = null;
                super.method(name, parameterTypes);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return this;
        }

        @Override
        public <R> R call(Object... args) {
            if (skip()) {
                return null;
            }
            try {
                mIgnored = null;
                return super.call(args);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return null;
        }

        @Override
        public <R> R callByCaller(Object caller, Object... args) {
            if (skip()) {
                return null;
            }
            try {
                mIgnored = null;
                return super.callByCaller(caller, args);
            } catch (Throwable e) {
                mIgnored = e;
//                Log.w(LOG_TAG, "Oops!", e);
            }
            return null;
        }
    }

    /**
     * Finds the first declared method in the given class that matches the specified name.
     * Does not search the superclass hierarchy.
     *
     * @param clazz the class to search for the method
     * @param methodName the name of the method to find
     * @return the matching {@link Method}, or {@code null} if not found
     */
    public static Method findMethodByFirstName(Class<?> clazz, String methodName) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (methodName.equals(declaredMethod.getName())) {
                return declaredMethod;
            }
        }
        return null;
    }
}
