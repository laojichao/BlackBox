package top.niunaijun.blackbox.core.env;

import android.content.ComponentName;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Maintains the lists of special system packages, root-related packages,
 * Xposed packages, and pre-installed packages used by the BlackBox
 * virtual environment.
 *
 * <p>System (or "open") packages are those that the virtual engine
 * delegates directly to the host rather than sandboxing -- for example
 * the WebView implementation, the camera, and the host app itself.
 * "Black" packages are root or Xposed packages that should be hidden
 * from the virtual app when the corresponding concealment options are
 * enabled.</p>
 */
public class AppSystemEnv {
    /** Packages delegated to the host system and not sandboxed. */
    private static final List<String> sSystemPackages = new ArrayList<>();
    /** Well-known su / superuser packages (hidden when root-hiding is on). */
    private static final List<String> sSuPackages = new ArrayList<>();
    /** Known Xposed installer packages (hidden when Xposed-hiding is on). */
    private static final List<String> sXposedPackages = new ArrayList<>();
    /** Packages pre-installed into every virtual user. */
    private static final List<String> sPreInstallPackages = new ArrayList<>();

    static {
        sSystemPackages.add("android");
        sSystemPackages.add("com.google.android.webview");
        sSystemPackages.add("com.google.android.webview.dev");
        sSystemPackages.add("com.google.android.webview.beta");
        sSystemPackages.add("com.google.android.webview.canary");
        sSystemPackages.add("com.android.webview");
        sSystemPackages.add("com.android.camera");

        // google Gboard
        sSystemPackages.add("com.google.android.inputmethod.latin");
        sSystemPackages.add(BlackBoxCore.getHostPkg());

        // 华为
        sSystemPackages.add("com.huawei.webview");

        // oppo
        sSystemPackages.add("com.coloros.safecenter");

        // su
        sSuPackages.add("com.noshufou.android.su");
        sSuPackages.add("com.noshufou.android.su.elite");
        sSuPackages.add("eu.chainfire.supersu");
        sSuPackages.add("com.koushikdutta.superuser");
        sSuPackages.add("com.thirdparty.superuser");
        sSuPackages.add("com.yellowes.su");

        sXposedPackages.add("de.robv.android.xposed.installer");

        sPreInstallPackages.add("com.huawei.hwid");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < 29){
            //解决Android 9三星浏览器闪退问题
        }else{

        }
    }

    /**
     * Checks whether the given package is a system (open) package that
     * should be delegated to the host rather than sandboxed.
     *
     * @param packageName the package name to test
     * @return {@code true} if the package is in the system package list
     */
    public static boolean isOpenPackage(String packageName) {
        return sSystemPackages.contains(packageName);
    }

    /**
     * Checks whether the package of the given {@link ComponentName} is a
     * system (open) package.
     *
     * @param componentName the component whose package is tested; may be {@code null}
     * @return {@code true} if the component's package is in the system package list
     */
    public static boolean isOpenPackage(ComponentName componentName) {
        return componentName != null && isOpenPackage(componentName.getPackageName());
    }

    /**
     * Checks whether the given package should be blocked (hidden) from
     * the virtual app.  A package is blocked if it is a root package and
     * root-hiding is active, or an Xposed package and Xposed-hiding is
     * active.
     *
     * @param packageName the package name to test
     * @return {@code true} if the package should be concealed
     */
    public static boolean isBlackPackage(String packageName) {
        if (BlackBoxCore.get().isHideRoot() && sSuPackages.contains(packageName)) {
            return true;
        } else if (BlackBoxCore.get().isHideXposed() && sXposedPackages.contains(packageName)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the list of packages that are pre-installed into every
     * newly-created virtual user.
     *
     * @return list of pre-install package names
     */
    public static List<String> getPreInstallPackages() {
        return sPreInstallPackages;
    }
}
