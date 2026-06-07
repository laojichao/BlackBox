package black.android.content;

import android.accounts.Account;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Mirror of hidden android.content.SyncInfo constructor.
 * Allows constructing SyncInfo objects representing an active sync operation.
 */
@BClassName("android.content.SyncInfo")
public interface SyncInfo {
    /** Creates a new SyncInfo with authority ID, account, authority name, and start time. */
    @BConstructor
    SyncInfo _new(int int0, Account Account1, String String2, long long3);
}
