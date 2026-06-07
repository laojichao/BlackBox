package black.android.location;

import android.util.ArrayMap;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.location.LocationManager fields for Android Q (API 29+).
 * Uses ArrayMap instead of HashMap for listener storage.
 */
@BClassName("android.location.LocationManager")
public interface LocationManagerQ {
    /** ArrayMap of registered GNSS NMEA listeners (Q+). */
    @BField
    ArrayMap mGnssNmeaListeners();

    /** ArrayMap of registered GNSS status listeners (Q+). */
    @BField
    ArrayMap mGnssStatusListeners();

    /** ArrayMap of registered GPS NMEA listeners (Q+). */
    @BField
    ArrayMap mGpsNmeaListeners();

    /** ArrayMap of registered GPS status listeners (Q+). */
    @BField
    ArrayMap mGpsStatusListeners();

    /** ArrayMap of registered location update listeners (Q+). */
    @BField
    ArrayMap mListeners();
}
