package black.android.content.pm;

import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.content.pm.PackageParser for Marshmallow (API 23).
 * Uses the standard no-arg constructor with collectCertificates and parsePackage.
 */
@BClassName("android.content.pm.PackageParser")
public interface PackageParserMarshmallow {
    /** Creates a new PackageParser instance. */
    @BConstructor
    PackageParser _new();

    /** Collects certificates for the given package. */
    @BMethod
    void collectCertificates(Package p, int flags);

    /** Parses an APK file with the given flags and returns the Package object. */
    @BMethod
    Package parsePackage(File File0, int int1);
}
