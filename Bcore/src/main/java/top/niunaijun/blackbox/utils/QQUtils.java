package top.niunaijun.blackbox.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Utility class for applying runtime modifications to the QQ (Tencent Mobile QQ) application
 * within the virtual environment. Uses reflection to modify QQ's internal logging configuration
 * to enable verbose log output.
 */
public class QQUtils {
    /**
     * Enables verbose log output for the QQ application by modifying its internal
     * {@code UIN_REPORTLOG_LEVEL} field via reflection. Only takes effect when the
     * application loaded by the given context is QQ.
     *
     * @param context the application context whose class loader has access to QQ classes
     */
    public static void hackLog(Context context) {
        try {
            Class<?> aClass = context.getClassLoader().loadClass("com.tencent.qphone.base.util.QLog");
            Field uin_reportlog_level = aClass.getDeclaredField("UIN_REPORTLOG_LEVEL");
            uin_reportlog_level.setAccessible(true);
            uin_reportlog_level.set(null, 1000);
            Log.d("QQUtils", "hackLog: success");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
