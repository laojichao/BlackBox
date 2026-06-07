package black.android.content.pm;

import android.content.pm.PackageParser.SigningDetails;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.pm.SigningInfo.
 * Contains the signing details of an installed package (API 28+).
 */
@BClassName("android.content.pm.SigningInfo")
public interface SigningInfo {
    /** Creates a new SigningInfo from the given SigningDetails. */
    @BConstructor
    android.content.pm.SigningInfo _new(SigningDetails SigningDetails0);

    /** The signing details containing certificates and key rotation history. */
    @BField
    SigningDetails mSigningDetails();
}
