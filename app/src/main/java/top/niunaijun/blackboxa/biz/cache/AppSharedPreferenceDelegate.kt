package top.niunaijun.blackboxa.biz.cache

import android.content.Context
import android.text.TextUtils
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property delegate that persists values in Android [SharedPreferences].
 *
 * Supports the five basic SharedPreferences types: [Int], [Long], [Float], [String],
 * and [Boolean]. To support additional types (e.g., objects via serialization),
 * subclass this delegate and override [findData] and [putData].
 *
 * The property name is used as the SharedPreferences key automatically.
 *
 * @param Data The type of the delegated property value.
 * @param context The Android context used to access SharedPreferences.
 * @param default The default value returned when no stored value exists for the key.
 * @param spName The SharedPreferences file name. Defaults to the class simple name if null.
 */
open class AppSharedPreferenceDelegate<Data>(context: Context, private val default: Data, spName: String? = null) : ReadWriteProperty<Any, Data?> {

    private val mSharedPreferences by lazy {
        val tmpCacheName = if (TextUtils.isEmpty(spName)) {
            AppSharedPreferenceDelegate::class.java.simpleName
        } else {
            spName
        }
        return@lazy context.getSharedPreferences(tmpCacheName, Context.MODE_PRIVATE)
    }

    /**
     * Reads the value for the delegated property from SharedPreferences.
     *
     * @param thisRef The object that owns the property.
     * @param property The metadata of the delegated property.
     * @return The stored value, or [default] if no value exists.
     */
    override fun getValue(thisRef: Any, property: KProperty<*>): Data {
        return findData(property.name, default)
    }

    /**
     * Writes a new value for the delegated property to SharedPreferences.
     *
     * @param thisRef The object that owns the property.
     * @param property The metadata of the delegated property.
     * @param value The new value to store, or null to remove the key.
     */
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Data?) {
        putData(property.name, value)
    }

    /**
     * Reads a value from SharedPreferences by key.
     *
     * @param key The SharedPreferences key to look up.
     * @param default The default value to return if the key does not exist.
     * @return The stored value cast to [Data], or [default] if not found.
     * @throws IllegalArgumentException If [Data] is not one of the five supported types.
     */
    protected fun findData(key: String, default: Data): Data {
        with(mSharedPreferences) {
            val result: Any = when (default) {
                is Int -> getInt(key, default)
                is Long -> getLong(key, default)
                is Float -> getFloat(key, default)
                is String -> getString(key, default)!!
                is Boolean -> getBoolean(key, default)
                else -> throw IllegalArgumentException("This type $default can not be saved into sharedPreferences")
            }
            return result as? Data ?: default
        }
    }

    /**
     * Writes a value to SharedPreferences by key. Removes the key if the value is null.
     *
     * @param key The SharedPreferences key to write to.
     * @param value The value to store, or null to remove the key.
     * @throws IllegalArgumentException If [Data] is not one of the five supported types.
     */
    protected fun putData(key: String, value: Data?) {
        mSharedPreferences.edit {
            if (value == null) {
                remove(key)
            } else {
                when (value) {
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> throw IllegalArgumentException("This type $default can not be saved into Preferences")
                }
            }
        }
    }
}