package top.niunaijun.blackbox.utils.compat;

import java.lang.reflect.Method;
import java.util.List;

import black.android.content.pm.BRParceledListSlice;

/**
 * Compatibility utility for working with {@code android.content.pm.ParceledListSlice}.
 * <p>
 * ParceledListSlice is a hidden system class used to efficiently transfer large lists of
 * Parcelable objects across IPC boundaries. This class provides methods to check whether
 * a method return type or runtime object is a ParceledListSlice, and to create instances
 * via reflection regardless of constructor availability.
 */
public class ParceledListSliceCompat {

    /**
     * Checks whether the given method's return type is {@code ParceledListSlice}.
     *
     * @param method the method to check (may be null)
     * @return true if the method is non-null and its return type is ParceledListSlice
     */
	public static boolean isReturnParceledListSlice(Method method) {
		return method != null && method.getReturnType() == BRParceledListSlice.getRealClass();
	}

	/**
	 * Checks whether the given object is an instance of {@code ParceledListSlice}.
	 *
	 * @param obj the object to check (may be null)
	 * @return true if the object is non-null and its runtime class is ParceledListSlice
	 */
	public static boolean isParceledListSlice(Object obj) {
		return obj != null && obj.getClass() == BRParceledListSlice.getRealClass();
	}

	/**
	 * Creates a {@code ParceledListSlice} wrapping the given list via reflection.
	 * <p>
	 * First attempts to use the single-argument constructor; if unavailable, falls back
	 * to the no-arg constructor and appends items individually.
	 *
	 * @param list the list of Parcelable items to wrap
	 * @return the created ParceledListSlice object
	 */
	public static Object create(List<?> list) {
		Object slice = BRParceledListSlice.get()._new(list);
		if (slice != null) {
			return slice;
		} else {
			slice = BRParceledListSlice.get()._new();
		}
		for (Object item : list) {
			BRParceledListSlice.get(slice).append(item);
		}
		BRParceledListSlice.get(slice).setLastSlice(true);
		return slice;
	}
}
