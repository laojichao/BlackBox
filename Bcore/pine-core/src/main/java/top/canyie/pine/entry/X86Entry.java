package top.canyie.pine.entry;

import top.canyie.pine.Pine;
import top.canyie.pine.utils.Primitives;

/**
 * Entry point bridge for x86 hooked methods.
 * <p>
 * When a method is hooked on x86, its entry point is redirected to one of the typed bridge
 * methods (e.g. {@code voidBridge}, {@code intBridge}) in this class. The bridge extracts
 * arguments from x86 registers and the stack according to the cdecl calling convention,
 * reconstructs them as Java objects, and delegates to
 * {@link Pine#handleCall(Pine.HookRecord, Object, Object[])} for callback invocation.
 * </p>
 *
 * @author canyie
 */
public final class X86Entry {
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private X86Entry() {
    }

    private static void voidBridge(int artMethod, int extras, int ebx) throws Throwable {
        handleBridge(artMethod, extras, ebx);
    }

    private static int intBridge(int artMethod, int extras, int ebx) throws Throwable {
        return (int) handleBridge(artMethod, extras, ebx);
    }

    private static long longBridge(int artMethod,int extras, int ebx) throws Throwable {
        return (long) handleBridge(artMethod, extras, ebx);
    }

    private static double doubleBridge(int artMethod,int extras, int ebx) throws Throwable {
        return (double) handleBridge(artMethod, extras, ebx);
    }

    private static float floatBridge(int artMethod, int extras, int ebx) throws Throwable {
        return (float) handleBridge(artMethod, extras, ebx);
    }

    private static boolean booleanBridge(int artMethod, int extras, int ebx) throws Throwable {
        return (boolean) handleBridge(artMethod, extras, ebx);
    }

    private static byte byteBridge(int artMethod, int extras, int ebx) throws Throwable {
        return (byte) handleBridge(artMethod, extras, ebx);
    }

    private static char charBridge(int artMethod, int extras, int ebx) throws Throwable {
        return (char) handleBridge(artMethod, extras, ebx);
    }

    private static short shortBridge(int artMethod, int extras, int ebx) throws Throwable {
        return (short) handleBridge(artMethod, extras, ebx);
    }

    private static Object objectBridge(int artMethod, int extras, int ebx) throws Throwable {
        return handleBridge(artMethod, extras, ebx);
    }

    /**
     * Bridge handler for x86. Extracts arguments from the stack (cdecl convention),
     * converts them to Java objects, and delegates to {@link Pine#handleCall}.
     * <p>
     * x86 calling convention (cdecl): all arguments are passed on the stack. The first
     * argument is at [ebp+8] for instance methods (this pointer). Arguments are read as
     * 32-bit integers and reinterpreted based on the parameter type.
     * </p>
     */
    private static Object handleBridge(int artMethod, int extras, int ebx) throws Throwable {
        Pine.log("handleBridge: artMethod=%#x extras=%#x ebx=%#x", artMethod, extras, ebx);
        Pine.HookRecord hookRecord = Pine.getHookRecord(artMethod);
        int[] argsAsInts = getArgsAsInts(hookRecord, extras, ebx);
        long thread = Pine.currentArtThread0();

        Object receiver;
        Object[] args;

        int index = 0;

        if (hookRecord.isStatic) {
            receiver = null;
        } else {
            receiver = Pine.getObject(thread, argsAsInts[0]);
            index = 1;
        }

        if (hookRecord.paramNumber > 0) {
            args = new Object[hookRecord.paramNumber];
            for (int i = 0;i < hookRecord.paramNumber;i++) {
                Class<?> paramType = hookRecord.paramTypes[i];
                Object value;
                if (paramType.isPrimitive()) {
                    if (paramType == int.class) {
                        value = argsAsInts[index];
                    } else if (paramType == long.class) {
                        value = Primitives.ints2Long(argsAsInts[index++], argsAsInts[index]);
                    } else if (paramType == double.class) {
                        value = Primitives.ints2Double(argsAsInts[index++], argsAsInts[index]);
                    } else if (paramType == float.class) {
                        value = Float.intBitsToFloat(argsAsInts[index]);
                    } else if (paramType == boolean.class) {
                        value = argsAsInts[index] != 0;
                    } else if (paramType == short.class) {
                        value = (short) argsAsInts[index];
                    } else if (paramType == char.class) {
                        value = (char) argsAsInts[index];
                    } else if (paramType == byte.class) {
                        value = (byte) argsAsInts[index];
                    } else {
                        throw new AssertionError("Unknown primitive type: " + paramType);
                    }
                } else {
                    value = Pine.getObject(thread, argsAsInts[index]);
                }
                args[i] = value;
                index++;
            }
        } else {
            args = Pine.EMPTY_OBJECT_ARRAY;
        }

        return Pine.handleCall(hookRecord, receiver, args);
    }

    /**
     * Calculates the required array size and calls the native method to extract all arguments
     * as 32-bit int values from the x86 stack.
     *
     * @param hookRecord the hook record containing parameter type information.
     * @param extras     the native extras pointer with saved register state.
     * @param ebx        the ebx register value.
     * @return an array of argument values represented as ints.
     */
    private static int[] getArgsAsInts(Pine.HookRecord hookRecord, int extras, int ebx) {
        int len = hookRecord.isStatic ? 0 : 1/*this*/;
        Class<?>[] paramTypes = hookRecord.paramTypes;
        for (Class<?> paramType : paramTypes) {
            len += paramType == long.class || paramType == double.class ? 2 : 1;
        }
        int[] array = len != 0 ? new int[len] : EMPTY_INT_ARRAY;
        Pine.getArgsX86(extras, array, ebx);
        return array;
    }
}
