package black.android.content;

import android.content.Intent;
import android.os.Bundle;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.content.IIntentReceiver.
 * AIDL callback interface for receiving broadcast intents from the system.
 */
@BClassName("android.content.IIntentReceiver")
public interface IIntentReceiver {
    /** Callback invoked when a broadcast intent is received. */
    @BMethod
    void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser);
}
