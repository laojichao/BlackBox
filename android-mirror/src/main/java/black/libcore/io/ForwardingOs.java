package black.libcore.io;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden libcore.io.ForwardingOs.
 * Provides access to the delegate Os instance wrapped by ForwardingOs.
 */
@BClassName("libcore.io.ForwardingOs")
public interface ForwardingOs {
    /** The underlying Os implementation being delegated to. */
    @BField
    Object os();
}
