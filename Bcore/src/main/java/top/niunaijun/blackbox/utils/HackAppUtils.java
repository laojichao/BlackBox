package top.niunaijun.blackbox.utils;

/**
 * Utility class for applying application-specific hacks within the virtual environment.
 * Currently provides methods to enable verbose logging output for specific applications
 * by modifying their internal configuration via reflection.
 */
public class HackAppUtils {

    /**
     * Enable the Log output of QQ.
     *
     * @param packageName package name
     * @param classLoader class loader
     */
    public static void enableQQLogOutput(String packageName, ClassLoader classLoader) {
        if ("com.tencent.mobileqq".equals(packageName)) {
            try {
                Reflector.on("com.tencent.qphone.base.util.QLog", true, classLoader)
                        .field("UIN_REPORTLOG_LEVEL")
                        .set(100);
            } catch (Exception e) {
                e.printStackTrace();
                // ignore
            }
        }
    }
}
