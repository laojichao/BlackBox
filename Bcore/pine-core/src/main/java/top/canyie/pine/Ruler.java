package top.canyie.pine;

/**
 * Internal utility class used for calculating ART method size and member offsets at runtime.
 * <p>
 * This class contains two native methods and an interface whose ART method structures are
 * measured by the native code to determine the size of an {@code ArtMethod} and the offsets
 * of its fields. This information is critical for the hooking mechanism to correctly
 * manipulate ART method entries.
 * </p>
 *
 * @author canyie
 */
@SuppressWarnings("unused")
final class Ruler {
    /** First native method used for ArtMethod size measurement. */
    private static native void m1();
    /** Second native method used for ArtMethod size measurement. */
    private static native void m2();

    /** Interface used for measuring the ArtMethod size of interface methods. */
    private interface I {
         void m();
    }
}