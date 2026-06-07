package black.android.content.pm;

import android.os.Parcelable;
import android.os.Parcelable.Creator;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.content.pm.ParceledListSlice.
 * A Parcelable wrapper for transferring large lists in chunks via IPC.
 */
@BClassName("android.content.pm.ParceledListSlice")
public interface ParceledListSlice {
    /** Creates an empty ParceledListSlice. */
    @BConstructor
    Object _new();

    /** Creates a ParceledListSlice wrapping the given list. */
    @BConstructor
    Object _new(List<?> List0);

    /** Parcelable creator for deserialization. */
    @BStaticField
    Creator CREATOR();

    /** Appends an item to the list. */
    @BMethod
    Boolean append(Object item);

    /** Returns the underlying list. */
    @BMethod
    List<?> getList();

    /** Returns whether this is the last slice of the list. */
    @BMethod
    Boolean isLastSlice();

    /** Populates the list from the parcel in chunks. */
    @BMethod
    Parcelable populateList();

    /** Sets whether this is the last slice. */
    @BMethod
    void setLastSlice(boolean b);
}
