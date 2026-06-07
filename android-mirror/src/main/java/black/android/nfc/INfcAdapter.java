package black.android.nfc;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.nfc.INfcAdapter.
 * AIDL interface for the NFC adapter system service.
 */
@BClassName("android.nfc.INfcAdapter")
public interface INfcAdapter {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.nfc.INfcAdapter$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the INfcAdapter proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
