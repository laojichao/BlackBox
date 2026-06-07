package black.java.io;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden java.io.File fields.
 * Provides access to the static FileSystem instance used by File operations.
 */
@BClassName("java.io.File")
public interface File {
    /** The platform-specific FileSystem implementation used by all File instances. */
    @BStaticField
    Object fs();
}
