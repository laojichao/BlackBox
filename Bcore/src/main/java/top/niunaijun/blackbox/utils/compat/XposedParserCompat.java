package top.niunaijun.blackbox.utils.compat;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.utils.CloseUtils;

/**
 * Compatibility utility for detecting and parsing Xposed/LSPosed modules.
 * <p>
 * Xposed modules declare their entry point class in an {@code assets/xposed_init} file
 * inside the APK. This class reads that file to determine whether an APK is an Xposed
 * module and, if so, extracts the module metadata (name, description, main class) from
 * the {@link ApplicationInfo}.
 */
public class XposedParserCompat {

    /**
     * Parses an installed Xposed module into an {@link InstalledModule} descriptor.
     * <p>
     * Reads the module name, description (from {@code xposeddescription} metadata),
     * and main entry class (from {@code assets/xposed_init}).
     *
     * @param applicationInfo the {@link ApplicationInfo} of the candidate module package
     * @return an {@link InstalledModule} with the parsed metadata, or {@code null} if the
     *         package is not a valid Xposed module (missing {@code xposed_init} or metadata)
     */
    public static InstalledModule parseModule(ApplicationInfo applicationInfo) {
        try {
            PackageManager packageManager = BlackBoxCore.getPackageManager();
            InstalledModule module = new InstalledModule();
            module.packageName = applicationInfo.packageName;
            module.enable = false;
            module.desc = applicationInfo.metaData.getString("xposeddescription");
            module.name = applicationInfo.loadLabel(packageManager).toString();
            module.main = readMain(applicationInfo.sourceDir);
            return module;
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Checks whether the given APK file is an Xposed module by looking for
     * {@code assets/xposed_init} inside the archive.
     *
     * @param file the absolute path to the APK file to check
     * @return {@code true} if the APK contains a valid {@code xposed_init} asset, {@code false}
     *         otherwise (including on I/O errors)
     */
    public static boolean isXPModule(String file) {
        try {
            String s = readMain(file);
            return s != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String readMain(String apk) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(new File(apk));
            ZipEntry entry = zipFile.getEntry("assets/xposed_init");
            if (entry == null) {
                throw new RuntimeException();
            }
            return getInputStreamContent(zipFile.getInputStream(entry)).trim();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.close(zipFile);
        }
        return null;
    }

    private static String getInputStreamContent(InputStream stream) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.close(reader);
        }
        return builder.toString();
    }
}
