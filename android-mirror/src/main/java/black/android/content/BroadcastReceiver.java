package black.android.content;

import android.os.Bundle;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClassName;

/**
 * Mirror of hidden android.content.BroadcastReceiver internals.
 * Provides access to PendingResult fields for manipulating broadcast results.
 */
@BClassName("android.content.BroadcastReceiver")
public interface BroadcastReceiver {
    /** Returns the PendingResult associated with this broadcast receiver. */
    @BMethod
    android.content.BroadcastReceiver.PendingResult getPendingResult();

    /** Sets the PendingResult for this broadcast receiver. */
    @BMethod
    void setPendingResult(@BParamClassName("android.content.BroadcastReceiver$PendingResult") Object pendingResult);

    /**
     * Mirror of hidden BroadcastReceiver.PendingResult (API 26+, with flags field).
     */
    @BClassName("android.content.BroadcastReceiver$PendingResult")
    interface PendingResultM {
        /** Constructs a PendingResult with full parameters including flags. */
        @BConstructor
        android.content.BroadcastReceiver.PendingResult _new(int resultCode, String resultData, Bundle resultExtras, int type,
                              boolean ordered, boolean sticky, IBinder token, int userId, int flags);

        /** Whether the broadcast has been aborted. */
        @BField
        boolean mAbortBroadcast();

        /** Whether finish() has been called on this result. */
        @BField
        boolean mFinished();

        /** Flags associated with the pending result. */
        @BField
        int mFlags();

        /** Whether this is an initial sticky broadcast hint. */
        @BField
        boolean mInitialStickyHint();

        /** Whether this is an ordered broadcast hint. */
        @BField
        boolean mOrderedHint();

        /** The result code to return to the broadcaster. */
        @BField
        int mResultCode();

        /** The result data string to return to the broadcaster. */
        @BField
        String mResultData();

        /** The result extras Bundle to return to the broadcaster. */
        @BField
        Bundle mResultExtras();

        /** The user ID that sent the broadcast. */
        @BField
        int mSendingUser();

        /** The binder token for this pending result. */
        @BField
        IBinder mToken();

        /** The type of pending result (ordered, sticky, etc.). */
        @BField
        int mType();
    }

    /**
     * Mirror of hidden BroadcastReceiver.PendingResult (pre-API 26, without flags field).
     */
    @BClassName("android.content.BroadcastReceiver$PendingResult")
    interface PendingResult {
        /** Constructs a PendingResult with full parameters. */
        @BConstructor
        android.content.BroadcastReceiver.PendingResult _new(int resultCode, String resultData, Bundle resultExtras, int type,
                                                             boolean ordered, boolean sticky, IBinder token, int userId);

        /** Whether the broadcast has been aborted. */
        @BField
        boolean mAbortBroadcast();

        /** Whether finish() has been called on this result. */
        @BField
        boolean mFinished();

        /** Whether this is an initial sticky broadcast hint. */
        @BField
        boolean mInitialStickyHint();

        /** Whether this is an ordered broadcast hint. */
        @BField
        boolean mOrderedHint();

        /** The result code to return to the broadcaster. */
        @BField
        int mResultCode();

        /** The result data string to return to the broadcaster. */
        @BField
        String mResultData();

        /** The result extras Bundle to return to the broadcaster. */
        @BField
        Bundle mResultExtras();

        /** The user ID that sent the broadcast. */
        @BField
        int mSendingUser();

        /** The binder token for this pending result. */
        @BField
        IBinder mToken();

        /** The type of pending result (ordered, sticky, etc.). */
        @BField
        int mType();
    }
}
