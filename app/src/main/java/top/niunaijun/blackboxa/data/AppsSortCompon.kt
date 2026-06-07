package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo

/**
 * Comparator that orders [ApplicationInfo] objects based on a predefined sort order list.
 *
 * Applications whose package names appear earlier in [sortedList] are placed first.
 * Applications not found in the sort list are sorted to the end.
 *
 * @property sortedList The ordered list of package names that defines the desired sort order.
 */
class AppsSortComparator(private val sortedList: List<String>) : Comparator<ApplicationInfo> {
    /**
     * Compares two [ApplicationInfo] objects by their position in the [sortedList].
     *
     * @param o1 The first application info to compare, or null.
     * @param o2 The second application info to compare, or null.
     * @return A negative integer if o1 precedes o2, a positive integer if o1 follows o2,
     *         or zero if they are equal or either is null.
     */
    override fun compare(o1: ApplicationInfo?, o2: ApplicationInfo?): Int {
        if (o1 == null || o2 == null) {
            return 0
        }

        val first = sortedList.indexOf(o1.packageName)
        val second = sortedList.indexOf(o2.packageName)
        return first - second

    }
}