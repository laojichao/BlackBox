package black.android.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IInterface;

import java.util.HashMap;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.location.LocationManager fields and inner classes.
 * Provides access to internal listener maps and transport classes for GPS/GNSS spoofing.
 */
@BClassName("android.location.LocationManager")
public interface LocationManager {
    /** Map of registered GNSS NMEA listeners. */
    @BField
    HashMap mGnssNmeaListeners();

    /** Map of registered GNSS status listeners. */
    @BField
    HashMap mGnssStatusListeners();

    /** Map of registered GPS NMEA listeners (legacy). */
    @BField
    HashMap mGpsNmeaListeners();

    /** Map of registered GPS status listeners (legacy). */
    @BField
    HashMap mGpsStatusListeners();

    /** Map of registered location update listeners. */
    @BField
    HashMap mListeners();

    /** Map of registered NMEA listeners. */
    @BField
    HashMap mNmeaListeners();

    /** The underlying ILocationManager service binder. */
    @BField
    IInterface mService();

    /**
     * Mirror of GnssStatusListenerTransport inner class.
     * Bridges GNSS status callbacks to LocationListener.
     */
    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface ListenerTransport {
        /** The wrapped LocationListener. */
        @BField
        LocationListener mListener();

        /** Reference to the enclosing LocationManager instance. */
        @BField
        Object this$0();

        /** Forward a location change event. */
        @BMethod
        void onLocationChanged(Location Location0);

        /** Forward a provider disabled event. */
        @BMethod
        void onProviderDisabled(String String0);

        /** Forward a provider enabled event. */
        @BMethod
        void onProviderEnabled(String String0);

        /** Forward a provider status change event. */
        @BMethod
        void onStatusChanged(String String0, int int1, Bundle Bundle2);
    }

    /**
     * Mirror of VIVO-specific GnssStatusListenerTransport.
     * Handles VIVO device-specific SV status callback signature.
     */
    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface GpsStatusListenerTransportVIVO {
        /**
         * VIVO-specific satellite status changed callback with extra long array.
         */
        @BMethod
        void onSvStatusChanged(int int0, int[] ints1, float[] floats2, float[] floats3, float[] floats4, int int5, int int6, int int7,  long[] longs8);
    }

    /**
     * Mirror of Samsung S5-specific GpsStatusListenerTransport (empty placeholder).
     */
    @BClassName("android.location.LocationManager$GpsStatusListenerTransport")
    interface GpsStatusListenerTransportSumsungS5 {
//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, int int5, int int6, int int7, [I int[]8);
    }

    /**
     * Mirror of OPPO R815T-specific GpsStatusListenerTransport (empty placeholder).
     */
    @BClassName("android.location.LocationManager$GpsStatusListenerTransport")
    interface GpsStatusListenerTransportOPPO_R815T {
//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, [I int[]5, [I int[]6, [I int[]7, int int8);
    }

    /**
     * Mirror of GpsStatusListenerTransport inner class.
     * Bridges GPS status callbacks for older API levels.
     */
    @BClassName("android.location.LocationManager$GpsStatusListenerTransport")
    interface GpsStatusListenerTransport {
        /** The wrapped GPS status listener. */
        @BField
        Object mListener();

        /** The wrapped NMEA listener. */
        @BField
        Object mNmeaListener();

        /** Reference to the enclosing LocationManager instance. */
        @BField
        Object this$0();

        /** Called when the first GPS fix is obtained. */
        @BMethod
        void onFirstFix(int int0);

        /** Called when GPS engine starts. */
        @BMethod
        void onGpsStarted();

        /** Called when an NMEA sentence is received. */
        @BMethod
        void onNmeaReceived(long long0, String String1);

//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, int int5, int int6, int int7);
    }

    /**
     * Mirror of Oreo-specific GnssStatusListenerTransport (empty placeholder).
     */
    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface GnssStatusListenerTransportO {
//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, [F float[]5);
    }

    /**
     * Mirror of GnssStatusListenerTransport inner class (Nougat+).
     * Bridges GNSS status callbacks to GpsStatus.Listener.
     */
    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface GnssStatusListenerTransport {
        /** The wrapped GPS status listener. */
        @BField
        Object mGpsListener();

        /** The wrapped GPS NMEA listener. */
        @BField
        Object mGpsNmeaListener();

        /** Reference to the enclosing LocationManager instance. */
        @BField
        Object this$0();

        /** Called when the first GNSS fix is obtained. */
        @BMethod
        void onFirstFix(int int0);

        /** Called when GNSS engine starts. */
        @BMethod
        void onGnssStarted();

        /** Called when an NMEA sentence is received from GNSS. */
        @BMethod
        void onNmeaReceived(long long0, String String1);

//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4);
    }
}
