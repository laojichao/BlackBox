package black.android.content.pm;


import android.content.pm.PackageParser;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.pm.PackageParser for Pie (API 28).
 * Provides static collectCertificates with a skipVerify boolean parameter.
 */
@BClassName("android.content.pm.PackageParser")
public interface PackageParserPie {
    /** Collects certificates for the given package, optionally skipping verification. */
    @BStaticMethod
    void collectCertificates(PackageParser.Package p, boolean skipVerify);
}
