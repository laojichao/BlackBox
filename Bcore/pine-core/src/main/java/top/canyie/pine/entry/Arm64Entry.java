package top.canyie.pine.entry;

import top.canyie.pine.Pine;
import top.canyie.pine.utils.Three;

/**
 * Entry point bridge for ARM64 (aarch64) hooked methods.
 * <p>
 * When a method is hooked on ARM64, its entry point is redirected to one of the typed bridge
 * methods (e.g. {@code voidBridge}, {@code intBridge}) in this class. The bridge extracts
 * arguments from ARM64 registers (x0-x7 for general-purpose, d0-d7 for floating-point) and
 * the stack according to the AArch64 calling convention, reconstructs them as Java objects,
 * and delegates to {@link Pine#handleCall(Pine.HookRecord, Object, Object[])} for callback invocation.
 * </p>
 *
 * @author canyie
 */
public final class Arm64Entry {
    private static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    private static final long[] EMPTY_LONG_ARRAY = new long[0];
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    private static final int CR_SIZE = 7; // x1~x7, x0 is used as callee
    private static final int FPR_SIZE = 8; // d0~d8
    private static final long INT_BITS = 0xffffffffL;
    private static final long SHORT_BITS = 0xffffL;
    private static final long BYTE_BITS = 0xffL;
    private Arm64Entry() {
    }

    private static void voidBridge(long artMethod, long extras, long sp,
                                   long x4, long x5, long x6, long x7) throws Throwable {
        handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static int intBridge(long artMethod, long extras, long sp,
                                 long x4, long x5, long x6, long x7) throws Throwable {
        return (int) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static long longBridge(long artMethod, long extras, long sp,
                                   long x4, long x5, long x6, long x7) throws Throwable {
        return (long) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static double doubleBridge(long artMethod, long extras, long sp,
                                       long x4, long x5, long x6, long x7) throws Throwable {
        return (double) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static float floatBridge(long artMethod, long extras, long sp,
                                     long x4, long x5, long x6, long x7) throws Throwable {
        return (float) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static boolean booleanBridge(long artMethod, long extras, long sp,
                                         long x4, long x5, long x6, long x7) throws Throwable {
        return (boolean) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static char charBridge(long artMethod, long extras, long sp,
                                   long x4, long x5, long x6, long x7) throws Throwable {
        return (char) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static byte byteBridge(long artMethod, long extras, long sp,
                                   long x4, long x5, long x6, long x7) throws Throwable {
        return (byte) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static short shortBridge(long artMethod, long extras, long sp,
                                     long x4, long x5, long x6, long x7) throws Throwable {
        return (short) handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    private static Object objectBridge(long artMethod, long extras, long sp,
                                       long x4, long x5, long x6, long x7) throws Throwable {
        return handleBridge(artMethod, extras, sp, x4, x5, x6, x7);
    }

    /**
     * Bridge handler for ARM64. Extracts arguments from registers and stack, converts them
     * to Java objects, and delegates to {@link Pine#handleCall}.
     * <p>
     * ARM64 calling convention (AAPCS64): first 8 integer/pointer args in x0-x7 (x0 = this for
     * instance methods), first 8 floating-point args in d0-d7, additional args on stack.
     * Note: object references are 32-bit in ART even on 64-bit platforms.
     * </p>
     * Note: This method must never be inlined into the typed bridge methods to avoid a crash
     * when hooking proxy methods (known bug with lr register corruption).
     */
    private static Object handleBridge(long artMethod, long originExtras, long sp,
                                       long x4, long x5, long x6, long x7) throws Throwable {
        // Clone the extras and unlock to minimize the time we hold the lock
        long extras = Pine.cloneExtras(originExtras);
        Pine.log("handleBridge: artMethod=%#x originExtras=%#x extras=%#x sp=%#x", artMethod, originExtras, extras, sp);
        Pine.HookRecord hookRecord = Pine.getHookRecord(artMethod);
        // Extract x1-x3 from extras, x4-x7 passed directly, stack and fp registers via native
        Three<long[], long[], double[]> three = getArgs(hookRecord, extras, sp, x4, x5, x6, x7);
        long[] coreRegisters = three.a;
        long[] stack = three.b;
        double[] fpRegisters = three.c;

        Object receiver;
        Object[] args;

        // Track position in core registers, stack, and fp registers independently
        int crIndex = 0, stackIndex = 0, fprIndex = 0;
        long thread = Pine.currentArtThread0();

        if (hookRecord.isStatic) {
            receiver = null;
        } else {
            // For instance methods, x0 (index 0) holds the receiver; ART object refs are 32-bit
            receiver = Pine.getObject(thread, coreRegisters[0]);
            crIndex = 1;
            stackIndex = 1;
        }

        if (hookRecord.paramNumber > 0) {
            args = new Object[hookRecord.paramNumber];
            for (int i = 0; i < hookRecord.paramNumber; i++) {
                Class<?> paramType = hookRecord.paramTypes[i];
                Object value;
                if (paramType == double.class) {
                    if (fprIndex < fpRegisters.length)
                        value = fpRegisters[fprIndex++];
                    else
                        value = Double.longBitsToDouble(stack[stackIndex]);
                } else if (paramType == float.class) {
                    long asLong;
                    if (fprIndex < fpRegisters.length)
                        asLong = Double.doubleToLongBits(fpRegisters[fprIndex++]);
                    else
                        asLong = stack[stackIndex];
                    value = Float.intBitsToFloat((int) (asLong & INT_BITS));
                } else {
                    long asLong;
                    if (crIndex < coreRegisters.length)
                        asLong = coreRegisters[crIndex++];
                    else
                        asLong = stack[stackIndex];

                    if (paramType.isPrimitive()) {
                        if (paramType == int.class) {
                            value = (int) (asLong & INT_BITS);
                        } else if (paramType == long.class) {
                            value = asLong;
                        } else if (paramType == boolean.class) {
                            value = asLong != 0;
                        } else if (paramType == short.class) {
                            value = (short) (asLong & SHORT_BITS);
                        } else if (paramType == char.class) {
                            value = (char) (asLong & SHORT_BITS);
                        } else if (paramType == byte.class) {
                            value = (byte) (asLong & BYTE_BITS);
                        } else {
                            throw new AssertionError("Unknown primitive type: " + paramType);
                        }
                    } else {
                        // In art, object address is actually 32 bits
                        value = Pine.getObject(thread, asLong & INT_BITS);
                    }
                }
                args[i] = value;
                stackIndex++;
            }
        } else {
            args = Pine.EMPTY_OBJECT_ARRAY;
        }

        return Pine.handleCall(hookRecord, receiver, args);
    }

    /**
     * Calculates the required array sizes for core registers, stack, and floating-point registers,
     * calls the native method to fill x1-x3 and stack/fp values, then manually assigns x4-x7
     * which are passed as direct method parameters.
     *
     * @param hookRecord the hook record containing parameter type information.
     * @param extras     the native extras pointer with saved register state.
     * @param sp         the stack pointer at call time.
     * @param x4         the x4 register value (5th general-purpose arg or callee).
     * @param x5         the x5 register value.
     * @param x6         the x6 register value.
     * @param x7         the x7 register value.
     * @return a {@link Three} containing core registers, stack values, and fp register values.
     */
    private static Three<long[], long[], double[]> getArgs(Pine.HookRecord hookRecord, long extras, long sp,
                                                         long x4, long x5, long x6, long x7) {
        // TODO: Cache these values
        int crLength = 0;
        int stackLength = 0;
        int fprLength = 0;
        boolean[] typeWides;

        int paramTotal = hookRecord.paramNumber;
        if (!hookRecord.isStatic) {
            crLength = 1;
            stackLength = 1;
            paramTotal++;
        }
        if (paramTotal != 0) {
            typeWides = new boolean[paramTotal];
            if (!hookRecord.isStatic) {
                typeWides[0] = false; // this object is a reference, always 32-bit
            }
            for (int i = 0;i < hookRecord.paramNumber;i++) {
                Class<?> paramType = hookRecord.paramTypes[i];
                boolean fp;
                boolean wide;
                if (paramType == double.class) {
                    fp = true;
                    wide = true;
                } else if (paramType == float.class) {
                    fp = true;
                    wide = false;
                } else if (paramType == long.class) {
                    fp = false;
                    wide = true;
                } else {
                    fp = false;
                    wide = false;
                }

                if (fp) { // floating point
                    if (fprLength < FPR_SIZE)
                        fprLength++;
                } else {
                    if (crLength < CR_SIZE)
                        crLength++;
                }
                stackLength += wide ? 8 : 4;

                if (hookRecord.isStatic)
                    typeWides[i] = wide;
                else
                    typeWides[i + 1] = wide;
            }
        } else {
            typeWides = EMPTY_BOOLEAN_ARRAY;
        }

        long[] coreRegisters = crLength != 0 ? new long[crLength] : EMPTY_LONG_ARRAY;
        long[] stack = stackLength != 0 ? new long[stackLength] : EMPTY_LONG_ARRAY;
        double[] fpRegisters = fprLength != 0 ? new double[fprLength] : EMPTY_DOUBLE_ARRAY;
        Pine.getArgsArm64(extras, sp, typeWides, coreRegisters, stack, fpRegisters);

        // Manually assign x4-x7 (passed as direct parameters) into the coreRegisters array.
        // x1-x3 are already filled by the native getArgsArm64 call above.
        // Using do-while(false) as a labeled break pattern for sequential assignment.
        do {
            if (crLength < 4) break;
            coreRegisters[3] = x4;
            if (crLength == 4) break;
            coreRegisters[4] = x5;
            if (crLength == 5) break;
            coreRegisters[5] = x6;
            if (crLength == 6) break;
            coreRegisters[6] = x7;
        } while(false);

        return new Three<>(coreRegisters, stack, fpRegisters);
    }
}
