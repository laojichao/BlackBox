package top.niunaijun.blackbox.utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utility class providing common array manipulation operations such as trimming,
 * appending, searching by type or value, and bounds checking. Supports both
 * object arrays and primitive int arrays.
 */
public class ArrayUtils {

	/**
	 * Returns a copy of the given array truncated to the specified size, or {@code null}
	 * if the array is {@code null} or the requested size is zero.
	 *
	 * @param <T> the component type of the array
	 * @param array the source array to trim
	 * @param size the desired length of the returned array
	 * @return a new array of length {@code size} containing the first {@code size} elements,
	 *         the original array if its length already equals {@code size}, or {@code null}
	 *         if the input is {@code null} or {@code size} is zero
	 */
	public static<T> T[] trimToSize(T[] array, int size) {
		if (array == null || size == 0) {
			return null;
		} else if (array.length == size) {
			return array;
		} else {
			return Arrays.copyOf(array, size);
		}
	}

	/**
	 * Appends a single item to the end of the given array, returning a new array
	 * with length equal to the original plus one.
	 *
	 * @param array the source array to append to
	 * @param item the object to append
	 * @return a new {@code Object[]} containing all elements of {@code array} followed by {@code item}
	 */
	public static Object[] push(Object[] array, Object item)
	{
		Object[] longer = new Object[array.length + 1];
		System.arraycopy(array, 0, longer, 0, array.length);
		longer[array.length] = item;
		return longer;
	}

	/**
	 * Checks whether the given object array contains the specified value.
	 *
	 * @param <T> the component type of the array
	 * @param array the array to search
	 * @param value the value to look for
	 * @return {@code true} if the array contains the value, {@code false} otherwise
	 */
	public static <T> boolean contains(T[] array, T value) {
		return indexOf(array, value) != -1;
	}

	/**
	 * Checks whether the given int array contains the specified value.
	 *
	 * @param array the int array to search
	 * @param value the int value to look for
	 * @return {@code true} if the array contains the value, {@code false} otherwise
	 */
	public static boolean contains(int[] array, int value) {
		if (array == null) return false;
		for (int element : array) {
			if (element == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the first index of the given value in the array using {@link Objects#equals}.
	 *
	 * @param <T> the component type of the array
	 * @param array the array to search
	 * @param value the value to locate
	 * @return the index of the first occurrence, or {@code -1} if not found
	 */
	public static <T> int indexOf(T[] array, T value) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (Objects.equals(array[i], value)) return i;
		}
		return -1;
	}

	/**
	 * Finds the first index of the given type in a class array using identity comparison (==).
	 *
	 * @param array the class array to search
	 * @param type the class to match by identity
	 * @return the index of the first matching element, or {@code -1} if not found
	 */
	public static int protoIndexOf(Class<?>[] array, Class<?> type) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == type) return i;
		}
		return -1;
	}

	/**
	 * Finds the index of the first element in the array whose class exactly matches
	 * the specified type using identity comparison (==).
	 *
	 * @param array the array to search
	 * @param type the exact class type to match
	 * @return the index of the first matching element, or {@code -1} if not found
	 */
	public static int indexOfFirst(Object[] array, Class<?> type) {
		if (!isEmpty(array)) {
			int N = -1;
			for (Object one : array) {
				N++;
				if (one != null && type == one.getClass()) {
					return N;
				}
			}
		}
		return -1;
	}

	/**
	 * Finds the index of the first element in the class array that is identical to
	 * the specified type, starting the search at the given sequence number.
	 *
	 * @param array the class array to search
	 * @param type the class to match by identity
	 * @param sequence the starting position for the search (0-based)
	 * @return the index of the first matching element at or after {@code sequence},
	 *         or {@code -1} if not found
	 */
	public static int protoIndexOf(Class<?>[] array, Class<?> type, int sequence) {
		if (array == null) {
			return -1;
		}
		while (sequence < array.length) {
			if (type == array[sequence]) {
				return sequence;
			}
			sequence++;
		}
		return -1;
	}


	/**
	 * Finds the index of the first element in the object array that is an instance of
	 * the specified type, starting the search at the given sequence number.
	 *
	 * @param array the object array to search
	 * @param type the class type to check instances against
	 * @param sequence the starting position for the search (0-based)
	 * @return the index of the first element that is an instance of {@code type} at or after
	 *         {@code sequence}, or {@code -1} if not found
	 */
	public static int indexOfObject(Object[] array, Class<?> type, int sequence) {
		if (array == null) {
			return -1;
		}
		while (sequence < array.length) {
			if (type.isInstance(array[sequence])) {
				return sequence;
			}
			sequence++;
		}
		return -1;
	}


	/**
	 * Finds the index of the Nth element in the object array whose class exactly matches
	 * the specified type, where N is determined by the {@code sequence} parameter.
	 *
	 * @param array the object array to search
	 * @param type the exact class type to match using identity comparison
	 * @param sequence the occurrence number to find (1-based; e.g., 2 finds the second match)
	 * @return the index of the Nth matching element, or {@code -1} if not enough matches exist
	 */
	public static int indexOf(Object[] array, Class<?> type, int sequence) {
		if (!isEmpty(array)) {
			int N = -1;
			for (Object one : array) {
				N++;
				if (one != null && one.getClass() == type) {
					if (--sequence <= 0) {
						return N;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Finds the index of the last element in the array whose class exactly matches
	 * the specified type.
	 *
	 * @param array the object array to search (traversed from end to start)
	 * @param type the exact class type to match using identity comparison
	 * @return the index of the last matching element, or {@code -1} if not found
	 */
	public static int indexOfLast(Object[] array, Class<?> type) {
		if (!isEmpty(array)) {
			for (int N = array.length; N > 0; N--) {
				Object one = array[N - 1];
				if (one != null && one.getClass() == type) {
					return N - 1;
				}
			}
		}
		return -1;
	}

	/**
	 * Checks whether the given array is {@code null} or has zero length.
	 *
	 * @param <T> the component type of the array
	 * @param array the array to check
	 * @return {@code true} if the array is {@code null} or empty, {@code false} otherwise
	 */
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Returns the first element in the array whose class matches the specified type,
	 * cast to the target type.
	 *
	 * @param <T> the expected return type
	 * @param args the object array to search
	 * @param clazz the exact class type to match
	 * @return the first matching element cast to type {@code T}, or {@code null} if not found
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFirst(Object[] args, Class<?> clazz) {
		int index = indexOfFirst(args, clazz);
		if (index != -1) {
			return (T) args[index];
		}
		return null;
	}


	/**
	 * Validates that the given offset and count describe a valid range within an array
	 * of the specified length. Throws an exception if the range is out of bounds.
	 *
	 * @param arrayLength the total length of the array
	 * @param offset the starting index
	 * @param count the number of elements in the range
	 * @throws ArrayIndexOutOfBoundsException if offset or count is negative, or if the range
	 *         extends beyond the array length
	 */
	public static void checkOffsetAndCount(int arrayLength, int offset, int count) throws ArrayIndexOutOfBoundsException {
		if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
			throw new ArrayIndexOutOfBoundsException(offset);
		}
	}
}
