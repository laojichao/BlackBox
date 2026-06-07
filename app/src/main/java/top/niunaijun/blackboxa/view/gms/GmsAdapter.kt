package top.niunaijun.blackboxa.view.gms

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.databinding.ItemGmsBinding

/**
 * RecyclerView adapter factory for displaying Google Mobile Services (GMS) installation status per user.
 *
 * Creates [GmsVH] view holders that show each virtual user's name and a toggle checkbox
 * indicating whether GMS is currently installed for that user. The checkbox change triggers
 * a click on the root view to delegate install/uninstall logic to the parent activity.
 */
class GmsAdapter : RVHolderFactory() {

    /**
     * Creates a new [GmsVH] instance inflated from the item_gms layout.
     *
     * @param parent the parent ViewGroup for inflation context.
     * @param viewType the view type identifier (unused, single type).
     * @param item the data object to be bound to the view holder.
     * @return a new [GmsVH] instance ready to bind [GmsBean] data.
     */
    override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
        return GmsVH(inflate(R.layout.item_gms,parent))
    }

    /**
     * ViewHolder that binds a [GmsBean] item to the item_gms layout.
     *
     * Displays the user name and a checkbox reflecting GMS installation status.
     * Programmatic checkbox changes (from list updates) do not trigger the click handler;
     * only user-pressed interactions propagate the click.
     */
    class GmsVH(itemView:View):RVHolder<GmsBean>(itemView){

        private val binding = ItemGmsBinding.bind(itemView)

        /**
         * Binds the [GmsBean] data to the view elements.
         *
         * @param item the [GmsBean] containing user name and GMS installation status.
         * @param isSelected whether the item is currently selected (unused).
         * @param payload optional partial update payload (unused).
         */
        override fun setContent(item: GmsBean, isSelected: Boolean, payload: Any?) {
            binding.tvTitle.text = item.userName
            binding.checkbox.isChecked = item.isInstalledGms
            binding.checkbox.setOnCheckedChangeListener  { buttonView, _ ->
                if(buttonView.isPressed){
                    binding.root.performClick()
                }
            }
        }
    }
}