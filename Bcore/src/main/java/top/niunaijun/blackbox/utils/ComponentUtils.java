package top.niunaijun.blackbox.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ProviderInfo;

import java.util.Objects;

import top.niunaijun.blackbox.app.BActivityThread;

import static android.content.pm.ActivityInfo.LAUNCH_SINGLE_INSTANCE;

/**
 * Utility class for Android component-related operations within the virtual environment.
 * Provides methods for inspecting {@link Intent} targets, resolving task affinities,
 * comparing {@link ComponentInfo} objects, and extracting provider authorities.
 */
public class ComponentUtils {

    /**
     * Checks whether the given intent is a request to install an APK package,
     * identified by the MIME type {@code application/vnd.android.package-archive}.
     *
     * @param intent the intent to check
     * @return {@code true} if the intent's data type indicates an APK install request
     */
    public static boolean isRequestInstall(Intent intent) {
        return "application/vnd.android.package-archive".equals(intent.getType());
    }

    /**
     * Checks whether the given intent targets the currently running virtual application package.
     *
     * @param intent the intent to check
     * @return {@code true} if the intent's component package matches the current virtual app's package name
     */
    public static boolean isSelf(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component == null || BActivityThread.getAppPackageName() == null) return false;
        return component.getPackageName().equals(BActivityThread.getAppPackageName());
    }

    /**
     * Checks whether all intents in the given array target the currently running virtual application.
     *
     * @param intent the array of intents to check
     * @return {@code true} if every intent in the array is a self-targeting intent
     */
    public static boolean isSelf(Intent[] intent) {
        for (Intent intent1 : intent) {
            if (!isSelf(intent1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Resolves the task affinity for the given activity. For single-instance activities,
     * returns a unique affinity string. Otherwise falls back to the activity's task affinity,
     * the application's task affinity, or the package name.
     *
     * @param info the activity info to resolve the task affinity for
     * @return the resolved task affinity string
     */
    public static String getTaskAffinity(ActivityInfo info) {
        if (info.launchMode == LAUNCH_SINGLE_INSTANCE) {
            return "-SingleInstance-" + info.packageName + "/" + info.name;
        } else if (info.taskAffinity == null && info.applicationInfo.taskAffinity == null) {
            return info.packageName;
        } else if (info.taskAffinity != null) {
            return info.taskAffinity;
        }
        return info.applicationInfo.taskAffinity;
    }

    /**
     * Returns the first authority string from a content provider's authority list.
     * If the provider info contains multiple semicolon-separated authorities,
     * only the first one is returned.
     *
     * @param info the provider info whose authority to extract
     * @return the first authority string, or {@code null} if the info is {@code null}
     */
    public static String getFirstAuthority(ProviderInfo info) {
        if (info == null) {
            return null;
        }
        String[] authorities = info.authority.split(";");
        return authorities.length == 0 ? info.authority : authorities[0];
    }

    /**
     * Compares two intents for filter equality, checking action, data, type, package,
     * component, and categories. This mirrors the platform's
     * {@code Intent.filterEquals()} behavior.
     *
     * @param a the first intent to compare
     * @param b the second intent to compare
     * @return {@code true} if both intents match on all filter criteria, or if both are {@code null}
     */
    public static boolean intentFilterEquals(Intent a, Intent b) {
        if (a != null && b != null) {
            if (!Objects.equals(a.getAction(), b.getAction())) {
                return false;
            }
            if (!Objects.equals(a.getData(), b.getData())) {
                return false;
            }
            if (!Objects.equals(a.getType(), b.getType())) {
                return false;
            }
            Object pkgA = a.getPackage();
            if (pkgA == null && a.getComponent() != null) {
                pkgA = a.getComponent().getPackageName();
            }
            String pkgB = b.getPackage();
            if (pkgB == null && b.getComponent() != null) {
                pkgB = b.getComponent().getPackageName();
            }
            if (!Objects.equals(pkgA, pkgB)) {
                return false;
            }
            if (!Objects.equals(a.getComponent(), b.getComponent())) {
                return false;
            }
            if (!Objects.equals(a.getCategories(), b.getCategories())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the process name for the given component. If the component does not
     * declare an explicit process name, the package name is used as a fallback.
     *
     * @param componentInfo the component info to resolve the process name for
     * @return the resolved process name
     */
    public static String getProcessName(ComponentInfo componentInfo) {
        String processName = componentInfo.processName;
        if (processName == null) {
            processName = componentInfo.packageName;
            componentInfo.processName = processName;
        }
        return processName;
    }

    /**
     * Checks whether two component infos refer to the same component by comparing
     * both their package names and class names.
     *
     * @param first the first component info
     * @param second the second component info
     * @return {@code true} if both components share the same package name and class name
     */
    public static boolean isSameComponent(ComponentInfo first, ComponentInfo second) {

        if (first != null && second != null) {
            String pkg1 = first.packageName + "";
            String pkg2 = second.packageName + "";
            String name1 = first.name + "";
            String name2 = second.name + "";
            return pkg1.equals(pkg2) && name1.equals(name2);
        }
        return false;
    }

    /**
     * Converts a {@link ComponentInfo} into a {@link ComponentName} using its
     * package name and class name.
     *
     * @param componentInfo the component info to convert
     * @return a new {@link ComponentName} representing the component
     */
    public static ComponentName toComponentName(ComponentInfo componentInfo) {
        return new ComponentName(componentInfo.packageName, componentInfo.name);
    }
}
