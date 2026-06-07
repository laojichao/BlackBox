package top.niunaijun.blackboxa.util

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Extension functions for lazy ViewBinding inflation in Activity, Fragment, and Dialog classes.
 *
 * Uses reified generics and reflection to invoke the generated `inflate` method
 * of any [ViewBinding] implementation without manual casting.
 */

/**
 * Lazily inflates a [ViewBinding] for this [Activity].
 *
 * @param T The specific [ViewBinding] implementation type.
 * @return A [Lazy] delegate that provides the inflated [ViewBinding] on first access.
 */
inline fun <reified T : ViewBinding> Activity.inflate(): Lazy<T> = lazy {
    inflateBinding(layoutInflater)
}

/**
 * Lazily inflates a [ViewBinding] for this [Fragment].
 *
 * @param T The specific [ViewBinding] implementation type.
 * @return A [Lazy] delegate that provides the inflated [ViewBinding] on first access.
 */
inline fun <reified T : ViewBinding> Fragment.inflate(): Lazy<T> = lazy {
    inflateBinding(layoutInflater)
}

/**
 * Lazily inflates a [ViewBinding] for this [Dialog].
 *
 * @param T The specific [ViewBinding] implementation type.
 * @return A [Lazy] delegate that provides the inflated [ViewBinding] on first access.
 */
inline fun <reified T : ViewBinding> Dialog.inflate(): Lazy<T> = lazy {
    inflateBinding(layoutInflater)
}


/**
 * Inflates a [ViewBinding] by reflectively invoking its static `inflate(LayoutInflater)` method.
 *
 * @param T The specific [ViewBinding] implementation type.
 * @param layoutInflater The [LayoutInflater] to use for inflation.
 * @return The inflated [ViewBinding] instance.
 */
inline fun <reified T : ViewBinding> inflateBinding(layoutInflater: LayoutInflater): T {
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java)
    return method.invoke(null, layoutInflater) as T
}

/**
 * Inflates a [ViewBinding] with a parent [ViewGroup] by reflectively invoking
 * its static `inflate(LayoutInflater, ViewGroup, Boolean)` method.
 *
 * @param T The specific [ViewBinding] implementation type.
 * @param viewGroup The parent [ViewGroup] for layout parameters.
 * @param attachToParent Whether to attach the inflated view to the parent immediately.
 * @return The inflated [ViewBinding] instance.
 */
inline fun <reified T : ViewBinding> newBindingViewHolder(viewGroup: ViewGroup, attachToParent: Boolean = false): T {
    val method = T::class.java.getMethod("inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java)
    return method.invoke(null, LayoutInflater.from(viewGroup.context), viewGroup, attachToParent) as T
}
