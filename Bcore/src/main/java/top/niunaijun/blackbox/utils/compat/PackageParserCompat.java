package top.niunaijun.blackbox.utils.compat;

import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;

import black.android.content.pm.BRPackageParser;
import black.android.content.pm.BRPackageParserLollipop;
import black.android.content.pm.BRPackageParserLollipop22;
import black.android.content.pm.BRPackageParserMarshmallow;
import black.android.content.pm.BRPackageParserNougat;
import black.android.content.pm.BRPackageParserPie;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;

/**
 * Compatibility wrapper for {@link android.content.pm.PackageParser} across Android versions.
 * <p>
 * The PackageParser API changed significantly between Android releases:
 * <ul>
 *   <li>Pre-Lollipop: constructor takes no arguments; parsePackage uses four parameters.</li>
 *   <li>Lollipop (API 21): constructor and parsePackage signature changed.</li>
 *   <li>Lollipop MR1 (API 22): further signature adjustments.</li>
 *   <li>Marshmallow (API 23): simplified two-parameter parsePackage.</li>
 *   <li>Nougat (API 24): collectCertificates moved to a static method.</li>
 *   <li>Pie (API 28): collectCertificates gained a skipVerify flag.</li>
 * </ul>
 * This class abstracts all these differences behind unified static methods.
 */
public class PackageParserCompat {

    /** Empty GID array used as a default parameter. */
    public static final int[] GIDS = new int[]{};
    private static final int API_LEVEL = Build.VERSION.SDK_INT;
    private static final int myUserId = 0;

    /**
     * Creates a new {@link PackageParser} instance appropriate for the current API level.
     *
     * @param packageFile the APK file (used for context on some API levels; may be ignored)
     * @return a new PackageParser instance, or null if the API level is unsupported
     */
    public static PackageParser createParser(File packageFile) {
        if (API_LEVEL >= M) {
            return BRPackageParserMarshmallow.get()._new();
        } else if (API_LEVEL >= LOLLIPOP_MR1) {
            return BRPackageParserLollipop22.get()._new();
        } else if (API_LEVEL >= LOLLIPOP) {
            return BRPackageParserLollipop.get()._new();
        }
        return null;
    }

    /**
     * Parses an APK file into a {@link PackageParser.Package} using the appropriate
     * method signature for the current API level.
     *
     * @param parser      the PackageParser instance created via {@link #createParser(File)}
     * @param packageFile the APK file to parse
     * @param flags       parsing flags (e.g. {@code PackageParser.PARSE_COLLECT_CERTIFICATES})
     * @return the parsed Package object
     * @throws Throwable if parsing fails
     */
    public static Package parsePackage(PackageParser parser, File packageFile, int flags) throws Throwable {
        if (API_LEVEL >= M) {
            return BRPackageParserMarshmallow.getWithException(parser).parsePackage(packageFile, flags);
        } else if (API_LEVEL >= LOLLIPOP_MR1) {
            return BRPackageParserLollipop22.getWithException(parser).parsePackage(packageFile, flags);
        } else if (API_LEVEL >= LOLLIPOP) {
            return BRPackageParserLollipop.getWithException(parser).parsePackage(packageFile, flags);
        } else {
            return BRPackageParser.getWithException(parser).parsePackage(packageFile, null,
                    new DisplayMetrics(), flags);
        }
    }

    /**
     * Collects certificates for the given package using the correct API for the current
     * Android version.
     * <p>
     * On Pie+ uses the static {@code collectCertificates} with skipVerify; on Nougat
     * uses the static variant; on older versions uses the instance method on the parser.
     *
     * @param parser the PackageParser instance (used on pre-Nougat)
     * @param p      the parsed Package to collect certificates for
     * @param flags  certificate collection flags
     * @throws Throwable if certificate collection fails
     */
    public static void collectCertificates(PackageParser parser, Package p, int flags) throws Throwable {
        if (BuildCompat.isPie()) {
            BRPackageParserPie.getWithException().collectCertificates(p, true/*skipVerify*/);
        } else if (API_LEVEL >= N) {
            BRPackageParserNougat.getWithException().collectCertificates(p, flags);
        } else if (API_LEVEL >= M) {
            BRPackageParserMarshmallow.getWithException(parser).collectCertificates(p, flags);
        } else if (API_LEVEL >= LOLLIPOP_MR1) {
            BRPackageParserLollipop22.getWithException(parser).collectCertificates(p, flags);
        } else if (API_LEVEL >= LOLLIPOP) {
            BRPackageParserLollipop.getWithException(parser).collectCertificates(p, flags);
        } else {
            BRPackageParser.get(parser).collectCertificates(p, flags);
        }
    }
}
