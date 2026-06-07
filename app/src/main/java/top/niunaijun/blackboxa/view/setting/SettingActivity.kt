package top.niunaijun.blackboxa.view.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivitySettingBinding
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.view.base.BaseActivity

/**
 * Settings screen that hosts the [SettingFragment] preference UI.
 *
 * Displays application configuration options including Xposed module settings,
 * root/Xposed hiding toggles, daemon service control, and GMS management.
 */
class SettingActivity : BaseActivity() {

    private val viewBinding: ActivitySettingBinding by inflate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.setting, true)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, SettingFragment())
                .commit()
    }

    companion object{
        /**
         * Launches the [SettingActivity] from the given context.
         *
         * @param context the context used to start the activity
         */
        fun start(context: Context){
            val intent = Intent(context,SettingActivity::class.java)
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            context.startActivity(intent)
        }
    }

}