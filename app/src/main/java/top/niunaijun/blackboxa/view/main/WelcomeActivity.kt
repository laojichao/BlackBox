package top.niunaijun.blackboxa.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.view.list.ListViewModel

/**
 * Splash/welcome screen that serves as the application entry point.
 *
 * On creation, this activity triggers a preview scan of installed virtual apps via
 * [ListViewModel] and immediately redirects to [MainActivity]. It also handles
 * re-entry via [onNewIntent] for single-top launch scenarios, ensuring the user
 * always lands on the main screen.
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        jump()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewInstalledAppList()
        jump()
    }

    private fun jump() {
        MainActivity.start(this)
        finish()
    }

    private fun previewInstalledAppList(){
        val viewModel = ViewModelProvider(this,InjectionUtil.getListFactory()).get(ListViewModel::class.java)
        viewModel.previewInstalledList()
    }
}