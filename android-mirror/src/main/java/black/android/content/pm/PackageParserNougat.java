package black.android.content.pm;


import android.content.pm.PackageParser;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.pm.PackageParser for Nougat (API 24-25).
 * Provides static collectCertificates method (no instance needed).
 */
@BClassName("android.content.pm.PackageParser")
public interface PackageParserNougat {
    /** Collects certificates for the given package statically. */
    @BStaticMethod
    void collectCertificates(PackageParser.Package p, int flags);
}
