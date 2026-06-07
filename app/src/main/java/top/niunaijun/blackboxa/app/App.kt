package top.niunaijun.blackboxa.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Application class that initializes the BlackBox host environment.
 *
 * Delegates lifecycle callbacks to [AppManager] for BlackBox core initialization
 * and third-party service setup. Provides a static application context via [getContext].
 */
class App : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private lateinit var mContext: Context

        /**
         * Returns the global application context.
         *
         * @return the application [Context]
         */
        @JvmStatic
        fun getContext(): Context {
            return mContext
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        mContext = base!!
        AppManager.doAttachBaseContext(base)

    }

    override fun onCreate() {
        super.onCreate()
        AppManager.doOnCreate(mContext)
    }
}