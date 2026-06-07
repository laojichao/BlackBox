package top.niunaijun.blackboxa.view.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Base Activity class providing common toolbar initialization and user ID retrieval.
 *
 * All activities in the application should extend this class to inherit consistent
 * toolbar setup with optional back navigation and the ability to read the current
 * virtual user ID from the launching intent.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Initializes the given toolbar as the action bar with the specified title and optional back navigation.
     *
     * @param toolbar the [Toolbar] widget to configure as the action bar.
     * @param title the string resource ID for the toolbar title.
     * @param showBack whether to display a back/up navigation arrow. Defaults to false.
     * @param onBack optional callback invoked when the back arrow is pressed before finishing the activity.
     */
    protected fun initToolbar(toolbar: Toolbar,title:Int, showBack: Boolean = false, onBack: (() -> Unit)? = null) {
        setSupportActionBar(toolbar)
        toolbar.setTitle(title)
        if (showBack) {
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                toolbar.setNavigationOnClickListener {
                    if (onBack != null) {
                        onBack()
                    }
                    finish()
                }
            }
        }
    }

    /**
     * Retrieves the virtual user ID passed via the launching intent's "userID" extra.
     *
     * @return the virtual user ID, or 0 if not specified.
     */
    protected fun currentUserID():Int{
        return intent.getIntExtra("userID", 0)
    }
}