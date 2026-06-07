package top.niunaijun.blackbox.utils.compat;

import android.os.Build;

/**
 * Compatibility utility for Android SDK version detection and ROM identification.
 * <p>
 * Provides helper methods to safely check the current Android version, including
 * preview SDK detection that is not available on pre-Marshmallow devices. Also
 * identifies the device's custom ROM type (EMUI, MIUI, Flyme, ColorOS, 360UI,
 * Letv, Vivo, Samsung) through system properties and build fingerprints.
 */
public class BuildCompat {

    /**
     * Returns the preview SDK integer, safely handling devices below Marshmallow
     * where {@code Build.VERSION.PREVIEW_SDK_INT} does not exist.
     *
     * @return the preview SDK integer, or 0 if not running a preview or below API 23
     */
    public static int getPreviewSDKInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                return Build.VERSION.PREVIEW_SDK_INT;
            } catch (Throwable e) {
                // ignore
            }
        }
        return 0;
    }

    /**
     * Checks if the device is running Android 12 (S / API 31) or higher.
     *
     * @return true if SDK_INT >= 31, or SDK_INT == 30 with preview SDK == 1
     */
    // 12
    public static boolean isS() {
        return Build.VERSION.SDK_INT >= 31 || (Build.VERSION.SDK_INT >= 30 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 11 (R / API 30) or higher.
     *
     * @return true if SDK_INT >= 30, or SDK_INT == 29 with preview SDK == 1
     */
    // 11
    public static boolean isR() {
        return Build.VERSION.SDK_INT >= 30 || (Build.VERSION.SDK_INT >= 29 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 10 (Q / API 29) or higher.
     *
     * @return true if SDK_INT >= 29, or SDK_INT == 28 with preview SDK == 1
     */
    // 10
    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= 29 || (Build.VERSION.SDK_INT >= 28 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 9 (Pie / API 28) or higher.
     *
     * @return true if SDK_INT >= 28, or SDK_INT == 27 with preview SDK == 1
     */
    // 9
    public static boolean isPie() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P || (Build.VERSION.SDK_INT >= 27 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 8.0 (Oreo / API 26) or higher.
     *
     * @return true if SDK_INT >= 26, or SDK_INT == 25 with preview SDK == 1
     */
    // 8
    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || (Build.VERSION.SDK_INT >= 25 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 7.1 (Nougat MR1 / API 25) or higher.
     *
     * @return true if SDK_INT >= 25, or SDK_INT == 24 with preview SDK == 1
     */
    // 7.1
    public static boolean isN_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 || (Build.VERSION.SDK_INT >= 24 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 7.0 (Nougat / API 24) or higher.
     *
     * @return true if SDK_INT >= 24, or SDK_INT == 23 with preview SDK == 1
     */
    // 7
    public static boolean isN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || (Build.VERSION.SDK_INT >= 23 && Build.VERSION.PREVIEW_SDK_INT == 1);
    }

    /**
     * Checks if the device is running Android 6.0 (Marshmallow / API 23) or higher.
     *
     * @return true if SDK_INT >= 23
     */
    // 6
    public static boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Checks if the device is running Android 5.0 (Lollipop / API 21) or higher.
     *
     * @return true if SDK_INT >= 21
     */
    // 5
    public static boolean isL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Checks if the device is manufactured by Samsung.
     *
     * @return true if the brand or manufacturer is "samsung" (case-insensitive)
     */
    public static boolean isSamsung() {
        return "samsung".equalsIgnoreCase(Build.BRAND) || "samsung".equalsIgnoreCase(Build.MANUFACTURER);
    }

    /**
     * Checks if the device is running Huawei EMUI.
     *
     * @return true if the build display starts with "EMUI" or the
     *         {@code ro.build.version.emui} property contains "EmotionUI"
     */
    public static boolean isEMUI() {
        if (Build.DISPLAY.toUpperCase().startsWith("EMUI")) {
            return true;
        }
        String property = SystemPropertiesCompat.get("ro.build.version.emui");
        return property != null && property.contains("EmotionUI");
    }

    /**
     * Checks if the device is running Xiaomi MIUI.
     *
     * @return true if the {@code ro.miui.ui.version.code} property is greater than 0
     */
    public static boolean isMIUI() {
        return SystemPropertiesCompat.getInt("ro.miui.ui.version.code", 0) > 0;
    }

    /**
     * Checks if the device is running Meizu Flyme OS.
     *
     * @return true if the build display string contains "flyme" (case-insensitive)
     */
    public static boolean isFlyme() {
        return Build.DISPLAY.toLowerCase().contains("flyme");
    }

    /**
     * Checks if the device is running OPPO ColorOS.
     *
     * @return true if either {@code ro.build.version.opporom} or
     *         {@code ro.rom.different.version} system property exists
     */
    public static boolean isColorOS() {
        return SystemPropertiesCompat.isExist("ro.build.version.opporom")
                || SystemPropertiesCompat.isExist("ro.rom.different.version");
    }

    /**
     * Checks if the device is running 360 UI.
     *
     * @return true if the {@code ro.build.uiversion} property contains "360UI" (case-insensitive)
     */
    public static boolean is360UI() {
        String property = SystemPropertiesCompat.get("ro.build.uiversion");
        return property != null && property.toUpperCase().contains("360UI");
    }

    /**
     * Checks if the device is manufactured by Letv (LeEco).
     *
     * @return true if the manufacturer is "Letv" (case-insensitive)
     */
    public static boolean isLetv() {
        return Build.MANUFACTURER.equalsIgnoreCase("Letv");
    }

    /**
     * Checks if the device is manufactured by Vivo.
     *
     * @return true if the {@code ro.vivo.os.build.display.id} system property exists
     */
    public static boolean isVivo() {
        return SystemPropertiesCompat.isExist("ro.vivo.os.build.display.id");
    }


    private static ROMType sRomType;

    /**
     * Returns the detected ROM type of the current device.
     * <p>
     * The result is cached after the first call. Detection order: EMUI, MIUI, Flyme,
     * ColorOS, 360UI, Letv, Vivo, Samsung, then OTHER as fallback.
     *
     * @return the {@link ROMType} enum value representing the device's ROM
     */
    public static ROMType getROMType() {
        if (sRomType == null) {
            if (isEMUI()) {
                sRomType = ROMType.EMUI;
            } else if (isMIUI()) {
                sRomType = ROMType.MIUI;
            } else if (isFlyme()) {
                sRomType = ROMType.FLYME;
            } else if (isColorOS()) {
                sRomType = ROMType.COLOR_OS;
            } else if (is360UI()) {
                sRomType = ROMType._360;
            } else if (isLetv()) {
                sRomType = ROMType.LETV;
            } else if (isVivo()) {
                sRomType = ROMType.VIVO;
            } else if (isSamsung()) {
                sRomType = ROMType.SAMSUNG;
            } else {
                sRomType = ROMType.OTHER;
            }
        }
        return sRomType;
    }

    /**
     * Enumeration of known custom ROM types detected on the device.
     */
    public enum ROMType {
        EMUI,
        MIUI,
        FLYME,
        COLOR_OS,
        LETV,
        VIVO,
        _360,
        SAMSUNG,
        OTHER
    }
}