package black.android.content;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.IntentFilter internals.
 * Provides access to the private action and category lists.
 */
@BClassName("android.content.IntentFilter")
public interface IntentFilter {
    /** The list of action strings this filter matches. */
    @BField
    List<String> mActions();

    /** The list of category strings this filter matches. */
    @BField
    List<String> mCategories();
}
