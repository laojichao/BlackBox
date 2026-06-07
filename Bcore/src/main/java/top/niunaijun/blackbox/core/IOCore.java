package top.niunaijun.blackbox.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.TrieTree;

/**
 * I/O path redirection engine for the BlackBox virtual environment.
 *
 * <p>Intercepts filesystem paths and transparently redirects them from
 * their original locations (e.g. {@code /data/data/com.example}) to
 * sandboxed virtual directories so that each virtual user's data is
 * isolated.  Internally uses a {@link TrieTree} for efficient prefix
 * matching and also supports a "black-list" tree that forces certain
 * paths (e.g. Pictures) to remain on the real filesystem.</p>
 *
 * <p>Optionally hides root-related paths and spoofs {@code /proc/self}
 * entries when root-hiding mode is active.</p>
 */
@SuppressLint("SdCardPath")
public class IOCore {
    public static final String TAG = "IOCore";

    private static final IOCore sIOCore = new IOCore();

    /** Trie for matching original paths to their redirect targets. */
    private static final TrieTree mTrieTree = new TrieTree();
    /** Trie for paths that should bypass redirection (black-list). */
    private static final TrieTree sBlackTree = new TrieTree();

    /** Ordered map of original path prefix to redirect destination. */
    private final Map<String, String> mRedirectMap = new LinkedHashMap<>();

    private static final Map<String, Map<String, String>> sCachePackageRedirect = new HashMap<>();

    /**
     * Returns the singleton {@code IOCore} instance.
     *
     * @return the global {@code IOCore}
     */
    public static IOCore get() {
        return sIOCore;
    }

    /**
     * Registers a filesystem path redirection rule.
     *
     * <p>All future accesses under {@code origPath} will be transparently
     * remapped to the corresponding location under {@code redirectPath}.
     * The redirect directory is created if it does not already exist, and
     * the rule is also registered with the native I/O layer.</p>
     *
     * @param origPath     the original filesystem prefix (e.g. {@code /data/data/com.example})
     * @param redirectPath the target filesystem prefix (e.g. {@code /data/data/com.virtual/data/com.example})
     */
    public void addRedirect(String origPath, String redirectPath) {
        if (TextUtils.isEmpty(origPath) || TextUtils.isEmpty(redirectPath) || mRedirectMap.get(origPath) != null)
            return;
        //Add the key to TrieTree
        mTrieTree.add(origPath);
        mRedirectMap.put(origPath, redirectPath);
        File redirectFile = new File(redirectPath);
        if (!redirectFile.exists()) {
            FileUtils.mkdirs(redirectPath);
        }
        NativeCore.addIORule(origPath, redirectPath);
    }

    /**
     * Adds a path to the black-list trie so that it will never be
     * redirected, even if it matches a registered redirect rule.
     *
     * @param path the path prefix to black-list
     */
    public void addBlackRedirect(String path) {
        if (TextUtils.isEmpty(path))
            return;
        sBlackTree.add(path);
    }

    /**
     * Resolves a path through the redirect rules.
     *
     * <p>Paths containing {@code /blackbox/} are returned as-is.
     * Black-listed paths are also returned unchanged.  Otherwise the
     * longest-prefix trie is consulted and the matching prefix is
     * replaced with its redirect target.</p>
     *
     * @param path the original filesystem path
     * @return the redirected path, or the original if no rule matched
     */
    public String redirectPath(String path) {
        if (TextUtils.isEmpty(path))
            return path;
        if (path.contains("/blackbox/")) {
            return path;
        }
        String search = sBlackTree.search(path);
        if (!TextUtils.isEmpty(search))
            return search;

        //Search the key from TrieTree
        String key = mTrieTree.search(path);
        if (!TextUtils.isEmpty(key))
            path = path.replace(key, Objects.requireNonNull(mRedirectMap.get(key)));

        return path;
    }

    /**
     * File-based overload of {@link #redirectPath(String)}.
     *
     * @param path the original file path
     * @return the redirected file, or {@code null} if the input is null
     */
    public File redirectPath(File path) {
        if (path == null)
            return null;
        String pathStr = path.getAbsolutePath();
        return new File(redirectPath(pathStr));
    }

    /**
     * Resolves a path using a caller-supplied redirect rule map instead
     * of the global trie.
     *
     * @param path the original path
     * @param rule redirect rule map (prefix -> target)
     * @return the redirected path
     */
    public String redirectPath(String path, Map<String, String> rule) {
        if (TextUtils.isEmpty(path))
            return path;

        //Search the key from TrieTree
        String key = mTrieTree.search(path);
        if (!TextUtils.isEmpty(key))
            path = path.replace(key, Objects.requireNonNull(rule.get(key)));

        return path;
    }

    /**
     * File-based overload of {@link #redirectPath(String, Map)}.
     *
     * @param path the original file path
     * @param rule redirect rule map (prefix -> target)
     * @return the redirected file, or {@code null} if the input is null
     */
    public File redirectPath(File path, Map<String, String> rule) {
        if (path == null)
            return null;
        String pathStr = path.getAbsolutePath();
        return new File(redirectPath(pathStr, rule));
    }

    /**
     * Enables the I/O redirection layer for the given application context.
     *
     * <p>Builds a complete set of redirect rules covering the app's data
     * directory, native library directory, external storage, and
     * optionally root-related paths and {@code /proc} entries.  After
     * all rules are registered the native I/O interceptor is activated.</p>
     *
     * @param context the application context whose paths will be redirected
     */
    public void enableRedirect(Context context) {
        Map<String, String> rule = new LinkedHashMap<>();
        Set<String> blackRule = new HashSet<>();
        String packageName = context.getPackageName();

        try {
            ApplicationInfo packageInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA, BActivityThread.getUserId());
            int systemUserId = BlackBoxCore.getHostUserId();
            rule.put(String.format("/data/data/%s/lib", packageName), packageInfo.nativeLibraryDir);
            rule.put(String.format("/data/user/%d/%s/lib", systemUserId, packageName), packageInfo.nativeLibraryDir);

            rule.put(String.format("/data/data/%s", packageName), packageInfo.dataDir);
            rule.put(String.format("/data/user/%d/%s", systemUserId, packageName), packageInfo.dataDir);

            if (BlackBoxCore.getContext().getExternalCacheDir() != null && context.getExternalCacheDir() != null) {
                File external = BEnvironment.getExternalUserDir(BActivityThread.getUserId());

                // sdcard
                rule.put("/sdcard", external.getAbsolutePath());
                rule.put(String.format("/storage/emulated/%d", systemUserId), external.getAbsolutePath());

                blackRule.add("/sdcard/Pictures");
                blackRule.add(String.format("/storage/emulated/%d/Pictures", systemUserId));
            }
            if (BlackBoxCore.get().isHideRoot()) {
                hideRoot(rule);
            }
            proc(rule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String key : rule.keySet()) {
            get().addRedirect(key, rule.get(key));
        }
        for (String s : blackRule) {
            get().addBlackRedirect(s);
        }
        NativeCore.enableIO();
    }

    /**
     * Adds rules that redirect common su binary and Superuser.apk paths
     * to non-existent targets, effectively hiding root from the virtual app.
     *
     * @param rule the rule map to append root-hiding entries to
     */
    private void hideRoot(Map<String, String> rule) {
        rule.put("/system/app/Superuser.apk", "/system/app/Superuser.apk-fake");
        rule.put("/sbin/su", "/sbin/su-fake");
        rule.put("/system/bin/su", "/system/bin/su-fake");
        rule.put("/system/xbin/su", "/system/xbin/su-fake");
        rule.put("/data/local/xbin/su", "/data/local/xbin/su-fake");
        rule.put("/data/local/bin/su", "/data/local/bin/su-fake");
        rule.put("/system/sd/xbin/su", "/system/sd/xbin/su-fake");
        rule.put("/system/bin/failsafe/su", "/system/bin/failsafe/su-fake");
        rule.put("/data/local/su", "/data/local/su-fake");
        rule.put("/su/bin/su", "/su/bin/su-fake");
    }

    /**
     * Adds redirect rules so that {@code /proc/self/cmdline} and
     * {@code /proc/<hostPid>/cmdline} point to the virtual process's
     * cmdline file, spoofing the process identity.
     *
     * @param rule the rule map to append proc entries to
     */
    private void proc(Map<String, String> rule) {
        int appPid = BActivityThread.getAppPid();
        int pid = Process.myPid();
        String selfProc = "/proc/self/";
        String proc = "/proc/" + pid + "/";

        String cmdline = new File(BEnvironment.getProcDir(appPid), "cmdline").getAbsolutePath();
        rule.put(proc + "cmdline", cmdline);
        rule.put(selfProc + "cmdline", cmdline);
    }
}
