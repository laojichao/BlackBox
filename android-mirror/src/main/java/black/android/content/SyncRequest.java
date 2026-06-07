package black.android.content;

import android.accounts.Account;
import android.os.Bundle;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.SyncRequest internals.
 * Provides access to the private fields governing sync request configuration.
 */
@BClassName("android.content.SyncRequest")
public interface SyncRequest {
    /** The account to synchronize. */
    @BField
    Account mAccountToSync();

    /** The content provider authority to sync. */
    @BField
    String mAuthority();

    /** Extra parameters for the sync operation. */
    @BField
    Bundle mExtras();

    /** Whether this is a periodic sync request. */
    @BField
    boolean mIsPeriodic();

    /** The flex time in seconds for periodic sync. */
    @BField
    long mSyncFlexTimeSecs();

    /** The target run time in seconds for periodic sync. */
    @BField
    long mSyncRunTimeSecs();
}
