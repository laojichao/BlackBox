package black.java.lang;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden java.lang.ThreadGroup fields for pre-Nougat API levels.
 * Provides access to internal thread group arrays and count.
 */
@BClassName("java.lang.ThreadGroup")
public interface ThreadGroupN {
    /** The array of child thread groups. */
    @BField
    java.lang.ThreadGroup[] groups();

    /** The number of active child thread groups. */
    @BField
    Integer ngroups();

    /** The parent thread group. */
    @BField
    java.lang.ThreadGroup parent();
}
