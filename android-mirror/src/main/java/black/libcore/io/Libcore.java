package black.libcore.io;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden libcore.io.Libcore.
 * Provides access to the static Os implementation used by the libcore I/O layer.
 */
@BClassName("libcore.io.Libcore")
public interface Libcore {
    /** The static Os implementation instance for low-level I/O operations. */
    @BStaticField
    Object os();
}
