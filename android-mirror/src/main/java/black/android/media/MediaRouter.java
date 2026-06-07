package black.android.media;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.media.MediaRouter fields and inner classes.
 * Provides access to the internal static MediaRouter state and service binders.
 */
@BClassName("android.media.MediaRouter")
public interface MediaRouter {
    /** The static MediaRouter singleton holder. */
    @BStaticField
    Object sStatic();

    /**
     * Mirror of MediaRouter$Static for Kitkat (API 19).
     * Contains the IMediaRouterService binder.
     */
    @BClassName("android.media.MediaRouter$Static")
    interface StaticKitkat {
        /** The IMediaRouterService binder (Kitkat). */
        @BField
        IInterface mMediaRouterService();
    }

    /**
     * Mirror of MediaRouter$Static for newer API levels.
     * Contains the IAudioService binder.
     */
    @BClassName("android.media.MediaRouter$Static")
    interface Static {
        /** The IAudioService binder. */
        @BField
        IInterface mAudioService();
    }
}
