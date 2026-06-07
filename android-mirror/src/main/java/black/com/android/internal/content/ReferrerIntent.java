package black.com.android.internal.content;

import android.content.Intent;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Mirror of hidden com.android.internal.content.ReferrerIntent.
 * An Intent subclass that carries a referring package name.
 */
@BClassName("com.android.internal.content.ReferrerIntent")
public interface ReferrerIntent {
    /**
     * Construct a ReferrerIntent with the given intent and referrer package.
     */
    @BConstructor
    Intent _new(Intent Intent0, String String1);
}
