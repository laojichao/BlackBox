package top.niunaijun.blackboxa.view.xp

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.databinding.ItemXpBinding


/**
 * RecyclerView adapter factory for rendering Xposed module items.
 *
 * Creates [XpVH] view holders that display each module's icon, name, description,
 * and enable/disable toggle switch. The toggle switch propagates click events to
 * the parent RecyclerView item click listener for state management.
 */
class XpAdapter : RVHolderFactory() {

    override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
        return XpVH(inflate(R.layout.item_xp, parent))
    }

    /**
     * ViewHolder for rendering individual Xposed module items.
     *
     * Displays the module's icon, name, description, and an enable/disable switch.
     * Toggle changes propagate as item click events for upstream state management.
     *
     * @param itemView the inflated item view
     */
    class XpVH(itemView: View) : RVHolder<XpModuleInfo>(itemView) {

        private val binding = ItemXpBinding.bind(itemView)

        override fun setContent(item: XpModuleInfo, isSelected: Boolean, payload: Any?) {
            binding.icon.setImageDrawable(item.icon)
            binding.name.text = item.name
            binding.desc.text = item.desc
            binding.enable.isChecked = item.enable
            binding.enable.setOnCheckedChangeListener { buttonView, _ ->
                if (buttonView.isPressed) {
                    binding.root.performClick()
                }

            }
        }
    }

}