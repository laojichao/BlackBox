package black.android.location;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.location.GpsStatus methods.
 * Provides access to the internal setStatus method for injecting GPS satellite data.
 */
@BClassName("android.location.GpsStatus")
public interface GpsStatus {
    /**
     * Set the GPS satellite status with PRN, SNR, elevation, and azimuth arrays.
     */
    @BMethod
    void setStatus(int int0, int[] ints1, float[] floats2, float[] floats3, float[] floats4, int int5, int int6, int int7);
}
