package top.niunaijun.blackboxa.view.fake

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackbox.fake.frameworks.BLocationManager
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.databinding.ItemFakeBinding
import top.niunaijun.blackboxa.util.getString

/**
 * RecyclerView adapter factory for displaying fake location settings per installed application.
 *
 * Creates [FakeLocationVH] view holders that show each app's icon, name, and the configured
 * fake location coordinates (or "real location" if none is set). Displays a corner label
 * indicating the app is configurable.
 */
class FakeLocationAdapter : RVHolderFactory() {

    /**
     * Creates a new [FakeLocationVH] instance inflated from the item_fake layout.
     *
     * @param parent the parent ViewGroup for inflation context.
     * @param viewType the view type identifier (unused, single type).
     * @param item the data object to be bound to the view holder.
     * @return a new [FakeLocationVH] instance ready to bind [FakeLocationBean] data.
     */
    override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
        return FakeLocationVH(inflate(R.layout.item_fake,parent))
    }

    /**
     * ViewHolder that binds a [FakeLocationBean] item to the item_fake layout.
     *
     * Displays the app icon, name, and either the configured latitude/longitude or
     * a "real location" label depending on the app's fake location configuration.
     */
    class FakeLocationVH(itemView:View):RVHolder<FakeLocationBean>(itemView){

        private val binding = ItemFakeBinding.bind(itemView)

        /**
         * Binds the [FakeLocationBean] data to the view elements.
         *
         * @param item the [FakeLocationBean] containing app info and location data.
         * @param isSelected whether the item is currently selected (unused).
         * @param payload optional partial update payload (unused).
         */
        override fun setContent(item: FakeLocationBean, isSelected: Boolean, payload: Any?) {
            binding.icon.setImageDrawable(item.icon)
            binding.name.text = item.name
            if (item.fakeLocation == null || item.fakeLocationPattern == BLocationManager.CLOSE_MODE) {
                binding.fakeLocation.text = getString(R.string.real_location)
            } else {
                binding.fakeLocation.text =
                    String.format("%f, %f", item.fakeLocation!!.latitude, item.fakeLocation!!.longitude)
            }
            binding.cornerLabel.visibility = View.VISIBLE

        }
    }
}