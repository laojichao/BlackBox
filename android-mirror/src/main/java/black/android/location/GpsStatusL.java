package black.android.location;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.location.GpsStatus methods for Lollipop (API 21+).
 * Uses array parameters instead of scalar values for additional satellite info.
 */
@BClassName("android.location.GpsStatus")
public interface GpsStatusL {
    /**
     * Set GPS satellite status with array-based additional flags (Lollipop variant).
     */
    @BMethod
    void setStatus(int int0, int[] ints1, float[] floats2, float[] floats3, float[] floats4, int[] ints5, int[] ints6, int[] ints7);
}
