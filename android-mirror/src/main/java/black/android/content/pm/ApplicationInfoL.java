package black.android.content.pm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.pm.ApplicationInfo fields introduced in Lollipop (API 21).
 * Exposes CPU ABI, source directories, and private flags.
 */
@BClassName("android.content.pm.ApplicationInfo")
public interface ApplicationInfoL {
    /** The primary CPU ABI for this application. */
    @BField
    String primaryCpuAbi();

    /** The private flags bitmask for internal configuration. */
    @BField
    Integer privateFlags();

    /** The scanned public source directory path. */
    @BField
    String scanPublicSourceDir();

    /** The scanned source directory path. */
    @BField
    String scanSourceDir();

    /** The secondary CPU ABI for multi-arch applications. */
    @BField
    String secondaryCpuAbi();

    /** The secondary native library directory path. */
    @BField
    String secondaryNativeLibraryDir();

    /** Public source directories for split APKs. */
    @BField
    String[] splitPublicSourceDirs();

    /** Source directories for split APKs. */
    @BField
    String[] splitSourceDirs();
}
