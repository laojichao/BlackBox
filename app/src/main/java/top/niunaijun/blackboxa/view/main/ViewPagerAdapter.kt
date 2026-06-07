package top.niunaijun.blackboxa.view.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import top.niunaijun.blackboxa.view.apps.AppsFragment

/**
 * ViewPager2 adapter that manages [AppsFragment] instances for each virtual user profile.
 *
 * Each page in the ViewPager represents a separate user space where the user can view
 * and manage installed virtual apps. The adapter supports dynamic data replacement
 * through [replaceData] to accommodate changes in the number of virtual users.
 */

class ViewPagerAdapter(appCompatActivity: AppCompatActivity) : FragmentStateAdapter(appCompatActivity) {

    private var fragmentList = mutableListOf<AppsFragment>()

    /**
     * Replaces the entire fragment list and notifies the adapter of dataset changes.
     *
     * @param list the new list of [AppsFragment] instances to display
     */
    fun replaceData(list: MutableList<AppsFragment>){
        this.fragmentList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}