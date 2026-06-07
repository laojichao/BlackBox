package top.niunaijun.blackbox.utils.compat;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexFile;
import top.niunaijun.blackbox.utils.Reflector;

/**
 * Compatibility utility for accessing {@link dalvik.system.DexFile} internal structures.
 * <p>
 * Uses reflection to extract native DEX file cookie values from a ClassLoader's internal
 * {@code DexPathList}. On Android 6.0 (Marshmallow) and above, the {@code mCookie} field
 * is a {@code long[]} array (supporting multi-dex); on older versions it is a single
 * {@code long}. These cookies are used internally by the virtual environment to manage
 * loaded DEX files.
 */
public class DexFileCompat {

    /**
     * Retrieves all DEX file cookies from the given ClassLoader.
     * <p>
     * Extracts the {@code DexFile} objects from the ClassLoader's {@code pathList.dexElements}
     * and collects their native cookie values.
     *
     * @param classLoader the ClassLoader to extract cookies from
     * @return a list of native DEX file cookie values (long)
     */
    public static List<Long> getCookies(ClassLoader classLoader) {
        List<Long> cookies = new ArrayList<>();
        List<DexFile> dexFiles = getDexFiles(classLoader);
        for (DexFile dexFile : dexFiles) {
            cookies.addAll(getCookies(dexFile));
        }
        return cookies;
    }

    /**
     * Retrieves the native cookie(s) from a single {@link DexFile} instance.
     * <p>
     * On Marshmallow+ the {@code mCookie} field is a {@code long[]} (one entry per DEX);
     * on older versions it is a single {@code long} value.
     *
     * @param dexFile the DexFile to extract the cookie from (may be null)
     * @return a list of cookie values; empty if the DexFile is null or extraction fails
     */
    public static List<Long> getCookies(DexFile dexFile) {
        List<Long> cookies = new ArrayList<>();
        if (dexFile == null)
            return cookies;
        try {
            Object object = Reflector.with(dexFile)
                    .field("mCookie")
                    .get();
            if (BuildCompat.isM()) {
                for (long l : (long[]) object) {
                    cookies.add(l);
                }
            } else {
                cookies.add((long) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookies;
    }

    private static List<DexFile> getDexFiles(ClassLoader classLoader) {
        List<DexFile> dexFiles = new ArrayList<>();
        Object[] dexElements = getDexElements(classLoader);
        for (Object dexElement : dexElements) {
            try {
                dexFiles.add(Reflector.with(dexElement)
                        .field("dexFile")
                        .<DexFile>get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dexFiles;
    }

    private static Object[] getDexElements(ClassLoader classLoader) {
        Object dexPathList = getDexPathList(classLoader);
        if (dexPathList == null) {
            return new Object[]{};
        }
        try {
            return Reflector.with(dexPathList)
                    .field("dexElements")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[]{};
    }

    private static Object getDexPathList(ClassLoader classLoader) {
        try {
            return Reflector.on("dalvik.system.BaseDexClassLoader")
                    .field("pathList")
                    .get(classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
