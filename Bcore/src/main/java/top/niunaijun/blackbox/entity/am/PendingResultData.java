package top.niunaijun.blackbox.entity.am;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import black.android.content.BRBroadcastReceiverPendingResult;
import black.android.content.BRBroadcastReceiverPendingResultM;
import black.android.content.BroadcastReceiverPendingResultContext;
import black.android.content.BroadcastReceiverPendingResultMContext;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Parcelable snapshot of a {@link BroadcastReceiver.PendingResult} within the BlackBox
 * virtual environment.
 * <p>
 * This class captures the internal state of a broadcast receiver's pending result so it
 * can be serialized across process boundaries. It supports both pre-Marshmallow and
 * Marshmallow+ Android versions by extracting fields via reflection wrappers. The
 * captured data can later be used to reconstruct an equivalent {@code PendingResult}
 * via {@link #build()}.
 * </p>
 */
public class PendingResultData implements Parcelable {
    /** The type of the pending result (e.g., ordered, parallel, sticky). */
    public int mType;

    /** Whether this broadcast was sent as an ordered broadcast. */
    public boolean mOrderedHint;

    /** Whether this is the initial sticky broadcast delivery. */
    public boolean mInitialStickyHint;

    /** The IPC binder token associated with this pending result. */
    public IBinder mToken;

    /** The user ID of the sender that initiated the broadcast. */
    public int mSendingUser;

    /** Additional flags for the pending result (available on Marshmallow+). */
    public int mFlags;

    /** The result code set by the receiver (e.g., {@code Activity.RESULT_OK}). */
    public int mResultCode;

    /** The result data string set by the receiver. */
    public String mResultData;

    /** The result extras bundle set by the receiver. */
    public Bundle mResultExtras;

    /** Whether the receiver called {@code abortBroadcast()}. */
    public boolean mAbortBroadcast;

    /** Whether the pending result has been finished via {@code setResultCode} or similar. */
    public boolean mFinished;

    /** A unique UUID token identifying this pending result within BlackBox. */
    public String mBToken;

    /**
     * Constructs a {@link PendingResultData} by extracting the internal state from
     * a live {@link BroadcastReceiver.PendingResult}.
     *
     * @param pendingResult the pending result whose state should be captured
     */
    public PendingResultData(BroadcastReceiver.PendingResult pendingResult) {
        mBToken = UUID.randomUUID().toString();
        if (BuildCompat.isM()) {
            BroadcastReceiverPendingResultMContext resultMContext = BRBroadcastReceiverPendingResultM.get(pendingResult);
            mType = resultMContext.mType();
            mOrderedHint = resultMContext.mOrderedHint();
            mInitialStickyHint = resultMContext.mInitialStickyHint();
            mToken = resultMContext.mToken();
            mSendingUser = resultMContext.mSendingUser();
            mFlags = resultMContext.mFlags();
            mResultData = resultMContext.mResultData();
            mResultExtras = resultMContext.mResultExtras();
            mAbortBroadcast = resultMContext.mAbortBroadcast();
            mFinished = resultMContext.mFinished();
        } else {
            BroadcastReceiverPendingResultContext resultContext = BRBroadcastReceiverPendingResult.get(pendingResult);
            mType = resultContext.mType();
            mOrderedHint = resultContext.mOrderedHint();
            mInitialStickyHint = resultContext.mInitialStickyHint();
            mToken = resultContext.mToken();
            mSendingUser = resultContext.mSendingUser();
            mResultData = resultContext.mResultData();
            mResultExtras = resultContext.mResultExtras();
            mAbortBroadcast = resultContext.mAbortBroadcast();
            mFinished = resultContext.mFinished();
        }
    }

    /**
     * Reconstructs a {@link BroadcastReceiver.PendingResult} from the captured state.
     * <p>
     * Handles version differences between pre-Marshmallow and Marshmallow+ APIs.
     * </p>
     *
     * @return a new {@code PendingResult} populated with this data's fields
     */
    public BroadcastReceiver.PendingResult build() {
        if (BuildCompat.isM()) {
            return BRBroadcastReceiverPendingResultM.get()._new(mResultCode, mResultData, mResultExtras, mType, mOrderedHint, mInitialStickyHint, mToken, mSendingUser, mFlags);
        } else {
            return BRBroadcastReceiverPendingResult.get()._new(mResultCode, mResultData, mResultExtras, mType, mOrderedHint, mInitialStickyHint, mToken, mSendingUser);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeByte(this.mOrderedHint ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mInitialStickyHint ? (byte) 1 : (byte) 0);
        dest.writeStrongBinder(this.mToken);
        dest.writeInt(this.mSendingUser);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mResultCode);
        dest.writeString(this.mResultData);
        dest.writeBundle(this.mResultExtras);
        dest.writeByte(this.mAbortBroadcast ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mFinished ? (byte) 1 : (byte) 0);
        dest.writeString(this.mBToken);
    }

    /**
     * Populates this instance's fields from the given {@link Parcel}.
     * <p>
     * This method can be called on an already-constructed instance to reinitialize
     * its state from a new parcel.
     * </p>
     *
     * @param source the Parcel to read from
     */
    public void readFromParcel(Parcel source) {
        this.mType = source.readInt();
        this.mOrderedHint = source.readByte() != 0;
        this.mInitialStickyHint = source.readByte() != 0;
        this.mToken = source.readStrongBinder();
        this.mSendingUser = source.readInt();
        this.mFlags = source.readInt();
        this.mResultCode = source.readInt();
        this.mResultData = source.readString();
        this.mResultExtras = source.readBundle();
        this.mAbortBroadcast = source.readByte() != 0;
        this.mFinished = source.readByte() != 0;
        this.mBToken = source.readString();
    }

    /**
     * Constructs a {@link PendingResultData} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected PendingResultData(Parcel in) {
        this.mType = in.readInt();
        this.mOrderedHint = in.readByte() != 0;
        this.mInitialStickyHint = in.readByte() != 0;
        this.mToken = in.readStrongBinder();
        this.mSendingUser = in.readInt();
        this.mFlags = in.readInt();
        this.mResultCode = in.readInt();
        this.mResultData = in.readString();
        this.mResultExtras = in.readBundle();
        this.mAbortBroadcast = in.readByte() != 0;
        this.mFinished = in.readByte() != 0;
        this.mBToken = in.readString();
    }

    public static final Parcelable.Creator<PendingResultData> CREATOR = new Parcelable.Creator<PendingResultData>() {
        @Override
        public PendingResultData createFromParcel(Parcel source) {
            return new PendingResultData(source);
        }

        @Override
        public PendingResultData[] newArray(int size) {
            return new PendingResultData[size];
        }
    };

    /**
     * Returns a string representation of this pending result's state for debugging.
     *
     * @return a debug string containing all field values
     */
    @Override
    public String toString() {
        return "PendingResultData{" +
                "mType=" + mType +
                ", mOrderedHint=" + mOrderedHint +
                ", mInitialStickyHint=" + mInitialStickyHint +
                ", mToken=" + mToken +
                ", mSendingUser=" + mSendingUser +
                ", mFlags=" + mFlags +
                ", mResultCode=" + mResultCode +
                ", mResultData='" + mResultData + '\'' +
                ", mResultExtras=" + mResultExtras +
                ", mAbortBroadcast=" + mAbortBroadcast +
                ", mFinished=" + mFinished +
                '}';
    }
}
