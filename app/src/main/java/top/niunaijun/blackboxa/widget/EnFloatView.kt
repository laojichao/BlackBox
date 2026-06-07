package top.niunaijun.blackboxa.widget

import android.content.Context
import android.view.MotionEvent
import com.imuxuan.floatingview.FloatingMagnetView
import top.niunaijun.blackboxa.R

/**
 * Floating view containing a [RockerView] for virtual joystick-based location manipulation.
 *
 * Extends [FloatingMagnetView] to provide a draggable overlay that captures angle and distance
 * input from the rocker and forwards it to a [LocationListener] callback.
 *
 * @param mContext the application context
 */
class EnFloatView(mContext: Context) : FloatingMagnetView(mContext) {

    private val TAG = "RockerManager"

    private var rockerView: RockerView? = null

    private var mListener: LocationListener? = null

    init {
        inflate(mContext, R.layout.view_float_rocker, this)
        initRockerView()
    }

    private fun initRockerView() {

        rockerView = findViewById(R.id.rocker)
        rockerView?.setListener { type, currentAngle, currentDistance ->
            if (type == RockerView.EVENT_CLOCK && currentAngle != -1F) {
                val realAngle = currentAngle
                val realDistance = currentDistance * 0.001F
                //拉满的话，大概就是一秒五米

                mListener?.invoke(realAngle, realDistance)

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            rockerView?.setCanMove(false)
        } else if (event?.action == MotionEvent.ACTION_UP) {
            rockerView?.setCanMove(true)
        }
        return super.onTouchEvent(event)
    }

    /**
     * Sets the location listener that receives angle and distance values from the rocker.
     *
     * @param listener callback invoked with angle (degrees) and distance (scaled units)
     */
    fun setListener(listener: LocationListener) {
        this.mListener = listener
    }

}

/** Callback type for receiving rocker angle and distance values. */
typealias LocationListener = (angle: Float, distance: Float) -> Unit