package top.niunaijun.blackboxa.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import top.niunaijun.blackbox.BlackBoxCore

/**
 * Transparent activity that handles app shortcut launches.
 *
 * Receives a package name and user ID via intent extras, launches the corresponding
 * virtual app through [BlackBoxCore], and immediately finishes itself. This activity
 * serves as a trampoline for home screen shortcuts that directly open virtual apps.
 */
class ShortcutActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pkg = intent.getStringExtra("pkg")
        val userID = intent.getIntExtra("userId",0)

        lifecycleScope.launch {
            BlackBoxCore.get().launchApk(pkg,userID)
            finish()
        }
    }
}