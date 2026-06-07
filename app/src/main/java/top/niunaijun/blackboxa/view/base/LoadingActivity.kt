package top.niunaijun.blackboxa.view.base

import android.view.KeyEvent
import com.roger.catloadinglibrary.CatLoadingView
import top.niunaijun.blackboxa.R

/**
 * Abstract activity that provides a loading dialog overlay for long-running operations.
 *
 * Displays a non-cancelable [CatLoadingView] dialog that blocks user interaction and
 * prevents back-press dismissal while an asynchronous operation is in progress.
 * Subclasses should call [showLoading] before starting work and [hideLoading] when complete.
 */
abstract class LoadingActivity : BaseActivity() {

    private lateinit var loadingView: CatLoadingView

    /**
     * Displays the loading dialog if it is not already showing.
     * The dialog is non-cancelable and intercepts back/escape key presses.
     */
    fun showLoading() {
        if (!this::loadingView.isInitialized) {
            loadingView = CatLoadingView()
        }

        if (!loadingView.isAdded) {
            loadingView.setBackgroundColor(R.color.primary)
            loadingView.show(supportFragmentManager, "")
            supportFragmentManager.executePendingTransactions()
            loadingView.setClickCancelAble(false)
            loadingView.dialog?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    return@setOnKeyListener true
                }
                false
            }
        }
    }


    /**
     * Dismisses the loading dialog if it has been initialized and is currently showing.
     */
    fun hideLoading() {
        if (this::loadingView.isInitialized) {
            loadingView.dismiss()
        }
    }
}