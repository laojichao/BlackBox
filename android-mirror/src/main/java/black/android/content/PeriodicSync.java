package black.android.content;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.PeriodicSync internals.
 * Provides access to the flexible time field for periodic sync scheduling.
 */
@BClassName("android.content.PeriodicSync")
public interface PeriodicSync {
    /** The flex time in seconds allowed for periodic sync scheduling. */
    @BField
    long flexTime();
}
