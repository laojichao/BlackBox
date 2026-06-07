package black.android.media;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.media.AudioManager fields and methods.
 * Provides access to the internal IAudioService binder.
 */
@BClassName("android.media.AudioManager")
public interface AudioManager {
    /** Static reference to the IAudioService. */
    @BStaticField
    IInterface sService();

    /** Initialize and retrieve the audio service binder. */
    @BStaticMethod
    void getService();
}
