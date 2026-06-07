package top.niunaijun.blackboxa.view.list

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.databinding.ItemPackageBinding

/**
 * RecyclerView adapter factory for displaying installed applications or modules in a list.
 *
 * Creates [ListVH] view holders that display each app's icon, name, package name,
 * and an "installed" corner label when the app is already present in the virtual environment.
 */
class ListAdapter : RVHolderFactory() {

    /**
     * Creates a new [ListVH] instance inflated from the item_package layout.
     *
     * @param parent the parent ViewGroup for inflation context.
     * @param viewType the view type identifier (unused, single type).
     * @param item the data object to be bound to the view holder.
     * @return a new [ListVH] instance ready to bind [InstalledAppBean] data.
     */
    override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
        return ListVH(inflate(R.layout.item_package,parent))
    }

    /**
     * ViewHolder that binds an [InstalledAppBean] item to the item_package layout.
     *
     * Displays the app icon, name, package name, and a corner label visible only when
     * the app is already installed in the virtual environment.
     */
    class ListVH(itemView:View) :RVHolder<InstalledAppBean>(itemView){

        val binding = ItemPackageBinding.bind(itemView)

        /**
         * Binds the [InstalledAppBean] data to the view elements.
         *
         * @param item the [InstalledAppBean] containing app icon, name, package name, and install status.
         * @param isSelected whether the item is currently selected (unused).
         * @param payload optional partial update payload (unused).
         */
        override fun setContent(item: InstalledAppBean, isSelected: Boolean, payload: Any?) {
            binding.icon.setImageDrawable(item.icon)
            binding.name.text = item.name
            binding.packageName.text = item.packageName
            binding.cornerLabel.visibility = if (item.isInstall) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}