package top.niunaijun.blackboxa.view.apps

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * ItemTouchHelper.Callback implementation enabling drag-and-drop reordering of app items
 * in a RecyclerView. Supports movement in all four directions (up, down, left, right)
 * and delegates the move event to the provided callback.
 *
 * @property onMoveBlock callback invoked with the source and target adapter positions when an item is moved.
 */
class AppsTouchCallBack(private val onMoveBlock: (from: Int, to: Int) -> Unit) :
    ItemTouchHelper.Callback() {

    /**
     * Returns the movement flags allowing drag in all directions with no swipe support.
     *
     * @param recyclerView the RecyclerView to which the touch helper is attached.
     * @param viewHolder the ViewHolder being checked for movement capabilities.
     * @return movement flags with up/down/left/right drag enabled and swipe disabled.
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)

    }


    /**
     * Called when an item is moved to a new position. Invokes [onMoveBlock] with the
     * source and target positions.
     *
     * @param recyclerView the RecyclerView containing the items.
     * @param viewHolder the ViewHolder of the item being moved.
     * @param target the ViewHolder of the item at the target position.
     * @return always returns true to indicate the move was handled.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        onMoveBlock(fromPosition, toPosition)
        return true
    }

    /**
     * Called when an item is swiped. No action is taken as swipe gestures are not supported.
     *
     * @param viewHolder the ViewHolder of the swiped item.
     * @param direction the direction of the swipe.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}