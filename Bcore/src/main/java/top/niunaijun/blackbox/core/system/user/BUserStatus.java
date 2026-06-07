package top.niunaijun.blackbox.core.system.user;

/**
 * Enumeration of lifecycle states for a virtual user managed by {@link BUserManagerService}.
 */
public enum BUserStatus {
    /** The virtual user is active and can run applications. */
    ENABLE,
    /** The virtual user is suspended and cannot run applications. */
    DISABLE
}
