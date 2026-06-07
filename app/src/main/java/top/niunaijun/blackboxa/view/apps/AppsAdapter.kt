package top.niunaijun.blackboxa.view.apps

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.ItemAppBinding

/**
 * RecyclerView adapter factory for displaying installed application items in a grid layout.
 *
 * Creates [AppsVH] view holders that display each app's icon, name, and an XP module corner label.
 * Uses [RVHolderFactory] as the base factory pattern for view holder creation.
 */
class AppsAdapter : RVHolderFactory() {

    /**
     * Creates a new [AppsVH] instance inflated from the item_app layout.
     *
     * @param parent the parent ViewGroup for inflation context.
     * @param viewType the view type identifier (unused, single type).
     * @param item the data object to be bound to the view holder.
     * @return a new [AppsVH] instance ready to bind [AppInfo] data.
     */
    override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
        return AppsVH(inflate(R.layout.item_app,parent))
    }

    /**
     * ViewHolder that binds an [AppInfo] item to the item_app layout.
     *
     * Displays the app icon, app name, and shows a corner label when the app is an Xposed module.
     */
    class AppsVH(itemView:View):RVHolder<AppInfo>(itemView){

        val binding = ItemAppBinding.bind(itemView)

        /**
         * Binds the [AppInfo] data to the view elements.
         *
         * @param item the [AppInfo] containing app icon, name, and module flag.
         * @param isSelected whether the item is currently selected (unused).
         * @param payload optional partial update payload (unused).
         */
        override fun setContent(item: AppInfo, isSelected: Boolean, payload: Any?) {
            binding.icon.setImageDrawable(item.icon)
            binding.name.text = item.name
            if(item.isXpModule){
                binding.cornerLabel.visibility = View.VISIBLE
            }else{
                binding.cornerLabel.visibility = View.INVISIBLE
            }
        }

    }
}