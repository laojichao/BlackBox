package top.niunaijun.blackbox.utils;

import java.util.Arrays;
import java.util.HashSet;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;

/**
 * Utility class for inspecting and manipulating method parameter arrays within the
 * virtual environment. Provides methods to extract typed parameters, replace virtual
 * application package names and UIDs with their host equivalents, and discover
 * interfaces implemented by a class hierarchy.
 */
public class MethodParameterUtils {

    /**
     * Finds and returns the first parameter in the given argument array whose class
     * exactly matches the specified type.
     *
     * @param <T> the expected return type
     * @param args the method argument array to search
     * @param tClass the exact class type to match
     * @return the first matching argument cast to type {@code T}, or {@code null} if not found
     */
    public static <T> T getFirstParam(Object[] args, Class<T> tClass) {
        if (args == null) {
            return null;
        }
        int index = ArrayUtils.indexOfFirst(args, tClass);
        if (index != -1) {
            return (T) args[index];
        }
        return null;
    }

    /**
     * Finds the first String parameter in the argument array that matches a virtual
     * application package name and replaces it with the host application's package name.
     * This is used to mask the virtual app's identity when making system calls.
     *
     * @param args the method argument array to search and modify in-place
     * @return the original virtual package name that was replaced, or {@code null} if none found
     */
    public static String replaceFirstAppPkg(Object[] args) {
        if (args == null) {
            return null;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                String value = (String) args[i];
                if (BlackBoxCore.get().isInstalled(value, BActivityThread.getUserId())) {
                    args[i] = BlackBoxCore.getHostPkg();
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Replaces all String parameters in the argument array that match virtual application
     * package names with the host application's package name.
     *
     * @param args the method argument array to search and modify in-place
     */
    public static void replaceAllAppPkg(Object[] args) {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                continue;
            if (args[i] instanceof String) {
                String value = (String) args[i];
                if (BlackBoxCore.get().isInstalled(value, BActivityThread.getUserId())) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
        }
    }

    /**
     * Finds the first Integer parameter in the argument array that matches the virtual
     * environment's UID and replaces it with the host application's UID.
     *
     * @param args the method argument array to search and modify in-place
     */
    public static void replaceFirstUid(Object[] args) {
        if (args == null)
            return;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Integer) {
                int uid = (int) args[i];
                if (uid == BActivityThread.getBUid()) {
                    args[i] = BlackBoxCore.getHostUid();
                }
            }
        }
    }

    /**
     * Finds the last Integer parameter in the argument array that matches the virtual
     * environment's UID and replaces it with the host application's UID.
     *
     * @param args the method argument array to search and modify in-place
     */
    public static void replaceLastUid(Object[] args) {
        int index = ArrayUtils.indexOfLast(args, Integer.class);
        if (index != -1) {
            int uid = (int) args[index];
            if (uid == BActivityThread.getBUid()) {
                args[index] = BlackBoxCore.getHostUid();
            }
        }
    }

    /**
     * Finds the last String parameter in the argument array that matches a virtual
     * application package name and replaces it with the host application's package name.
     *
     * @param args the method argument array to search and modify in-place
     * @return the original virtual package name that was replaced, or {@code null} if none found
     */
    public static String replaceLastAppPkg(Object[] args) {
        int index = ArrayUtils.indexOfLast(args, String.class);
        if (index != -1) {
            String pkg = (String) args[index];
            if (BlackBoxCore.get().isInstalled(pkg, BActivityThread.getUserId())) {
                args[index] = BlackBoxCore.getHostPkg();
            }
            return pkg;
        }
        return null;
    }

    /**
     * Finds the Nth String parameter (specified by {@code sequence}) in the argument array
     * that matches a virtual application package name and replaces it with the host
     * application's package name.
     *
     * @param args the method argument array to search and modify in-place
     * @param sequence the occurrence number to find (1-based; e.g., 2 finds the second String match)
     * @return the original virtual package name that was replaced, or {@code null} if not enough matches
     */
    public static String replaceSequenceAppPkg(Object[] args, int sequence) {
        int index = ArrayUtils.indexOf(args, String.class, sequence);
        if (index != -1) {
            String pkg = (String) args[index];
            if (BlackBoxCore.get().isInstalled(pkg, BActivityThread.getUserId())) {
                args[index] = BlackBoxCore.getHostPkg();
            }
            return pkg;
        }
        return null;
    }

    /**
     * Finds the index of the first class in the given class array that equals the specified type.
     *
     * @param args the class array to search
     * @param type the class type to find
     * @return the index of the first matching class, or {@code -1} if not found
     */
    public static int getParamsIndex(Class[] args, Class<?> type) {
        for (int i = 0; i < args.length; i++) {
            Class obj = args[i];
            if (obj.equals(type)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the index of the first object in the argument array whose class matches
     * the specified type (by identity or instanceof check), starting from index 0.
     *
     * @param args the object array to search
     * @param type the class type to match
     * @return the index of the first matching element, or {@code -1} if not found
     */
    public static int getIndex(Object[] args, Class<?> type) {
        return getIndex(args, type, 0);
    }

    /**
     * Finds the index of the first object in the argument array whose class matches
     * the specified type (by identity or instanceof check), starting from the given index.
     *
     * @param args the object array to search
     * @param type the class type to match
     * @param start the starting index for the search
     * @return the index of the first matching element at or after {@code start}, or {@code -1} if not found
     */
    public static int getIndex(Object[] args, Class<?> type, int start) {
        for (int i = start; i < args.length; i++) {
            Object obj = args[i];
            if (obj != null && obj.getClass() == type) {
                return i;
            }
            if (type.isInstance(obj)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retrieves all interfaces implemented by the given class and its entire superclass
     * hierarchy, including transitive interface inheritance.
     *
     * @param clazz the class to inspect for implemented interfaces
     * @return an array of all interfaces found in the class hierarchy
     */
    public static Class<?>[] getAllInterface(Class clazz) {
        HashSet<Class<?>> classes = new HashSet<>();
        getAllInterfaces(clazz, classes);
        Class<?>[] result = new Class[classes.size()];
        classes.toArray(result);
        return result;
    }


    /**
     * Recursively collects all interfaces implemented by the given class and its
     * superclass chain into the provided collection.
     *
     * @param clazz the class to inspect
     * @param interfaceCollection the set to accumulate discovered interfaces into
     */
    public static void getAllInterfaces(Class clazz, HashSet<Class<?>> interfaceCollection) {
        Class<?>[] classes = clazz.getInterfaces();
        if (classes.length != 0) {
            interfaceCollection.addAll(Arrays.asList(classes));
        }
        if (clazz.getSuperclass() != Object.class) {
            getAllInterfaces(clazz.getSuperclass(), interfaceCollection);
        }
    }


}
