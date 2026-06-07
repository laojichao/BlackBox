package top.niunaijun.blackbox.utils;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Utility class for detecting and checking the ABI (Application Binary Interface) architecture
 * of native libraries contained within an APK file. Parses the ZIP entries of an APK to determine
 * whether it ships with 32-bit (armeabi/armeabi-v7a) or 64-bit (arm64-v8a) native libraries,
 * and checks compatibility with the host process bitness.
 */
public class AbiUtils {
    private final Set<String> mLibs = new HashSet<>();
    private static final Map<File, AbiUtils> sAbiUtilsMap = new HashMap<>();

    /**
     * Checks whether the given APK file is ABI-compatible with the current host process.
     * Returns {@code true} if the APK contains no native libraries (pure Java/Kotlin app),
     * or if its native library architecture matches the host process bitness.
     *
     * @param apkFile the APK file to check for ABI compatibility
     * @return {@code true} if the APK is compatible with the host process, {@code false} otherwise
     */
    public static boolean isSupport(File apkFile) {
        AbiUtils abiUtils = sAbiUtilsMap.get(apkFile);
        if (abiUtils == null) {
            abiUtils = new AbiUtils(apkFile);
            sAbiUtilsMap.put(apkFile, abiUtils);
        }
        if (abiUtils.isEmptyAib()) {
            return true;
        }

        if (BlackBoxCore.is64Bit()) {
            return abiUtils.is64Bit();
        } else {
            return abiUtils.is32Bit();
        }
    }

    /**
     * Constructs an {@code AbiUtils} instance by scanning the entries of the given APK file
     * to discover which native library architectures are bundled inside.
     *
     * @param apkFile the APK file whose native library directories will be scanned
     */
    public AbiUtils(File apkFile) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(apkFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String name = zipEntry.getName();
                if (name.startsWith("lib/arm64-v8a")) {
                    mLibs.add("arm64-v8a");
                } else if (name.startsWith("lib/armeabi")) {
                    mLibs.add("armeabi");
                } else if (name.startsWith("lib/armeabi-v7a")) {
                    mLibs.add("armeabi-v7a");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.close(zipFile);
        }
    }

    /**
     * Checks whether this APK contains 64-bit (arm64-v8a) native libraries.
     *
     * @return {@code true} if the APK includes arm64-v8a libraries, {@code false} otherwise
     */
    public boolean is64Bit() {
        return mLibs.contains("arm64-v8a");
    }

    /**
     * Checks whether this APK contains 32-bit (armeabi or armeabi-v7a) native libraries.
     *
     * @return {@code true} if the APK includes armeabi or armeabi-v7a libraries, {@code false} otherwise
     */
    public boolean is32Bit() {
        return mLibs.contains("armeabi") || mLibs.contains("armeabi-v7a");
    }

    /**
     * Checks whether this APK has no native libraries at all (i.e., it is a pure Java/Kotlin application).
     *
     * @return {@code true} if no native library entries were found in the APK, {@code false} otherwise
     */
    public boolean isEmptyAib() {
        return mLibs.isEmpty();
    }
}
