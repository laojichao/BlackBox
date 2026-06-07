package black.android.content;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.content.AttributionSource.
 * Represents the source of an operation for attribution and permission checks (API 31+).
 */
@BClassName("android.content.AttributionSource")
public interface AttributionSource {
    /** Returns the AttributionSourceState containing UID and package name. */
    @BField
    Object mAttributionSourceState();

    /** Returns the next AttributionSource in the chain. */
    @BMethod
    Object getNext();
}
