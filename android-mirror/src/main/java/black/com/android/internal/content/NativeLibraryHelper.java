package black.com.android.internal.content;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BParamClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.content.NativeLibraryHelper.
 * Provides access to native library extraction and ABI detection methods.
 */
@BClassName("com.android.internal.content.NativeLibraryHelper")
public interface NativeLibraryHelper {
    /**
     * Copy native binaries from the APK to the target directory.
     */
    @BStaticMethod
    Integer copyNativeBinaries(Handle Handle0, File File1, String String2);

    /**
     * Find the best supported ABI from the given list.
     */
    @BStaticMethod
    Integer findSupportedAbi(Handle Handle0, @BParamClassName("[Ljava.lang.String;") String[] strings);

    /**
     * Mirror of NativeLibraryHelper.Handle for managing APK native lib access.
     */
    @BClassName("com.android.internal.content.NativeLibraryHelper$Handle")
    interface Handle {
        /** Whether native libs should be extracted (vs. loaded directly from APK). */
        @BField
        boolean extractNativeLibs();

        /**
         * Create a Handle for the given APK file.
         */
        @BStaticMethod
        Object create(File File0);
    }
}
