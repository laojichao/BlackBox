package black.java.lang;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden java.lang.ThreadGroup fields for newer API levels (Nougat+).
 * Provides access to internal thread group lists as a List.
 */
@BClassName("java.lang.ThreadGroup")
public interface ThreadGroup {
    /** The list of child thread groups. */
    @BField
    List<java.lang.ThreadGroup> groups();

    /** The parent thread group. */
    @BField
    java.lang.ThreadGroup parent();
}
