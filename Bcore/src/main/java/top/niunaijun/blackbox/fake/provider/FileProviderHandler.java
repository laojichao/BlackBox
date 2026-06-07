package top.niunaijun.blackbox.fake.provider;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;

import java.io.File;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Utility class for handling FileProvider URI conversion within the virtual environment.
 * Converts content URIs to their underlying file paths and re-generates them using
 * the virtual environment's storage manager for correct file access.
 */
public class FileProviderHandler {

    /**
     * Converts a content URI to a virtual-environment-aware URI. On Android N and above,
     * extracts the file path and re-generates the URI via the virtual storage manager.
     *
     * @param context the current context
     * @param uri     the content URI to convert
     * @return the converted URI, or null if conversion fails
     */
    public static Uri convertFileUri(Context context, Uri uri) {
        if (BuildCompat.isN()) {
            File file = convertFile(context, uri);
            if (file == null)
                return null;
            return BlackBoxCore.getBStorageManager().getUriForFile(file.getAbsolutePath());
        }
        return uri;
    }

    /**
     * Converts a content URI to its corresponding File by checking all registered
     * content providers in the current activity thread.
     *
     * @param context the current context
     * @param uri     the content URI to resolve
     * @return the File if found and exists, or null
     */
    public static File convertFile(Context context, Uri uri) {
        List<ProviderInfo> providers = BActivityThread.getProviders();
        for (ProviderInfo provider : providers) {
            try {
                File fileForUri = FileProvider.getFileForUri(context, provider.authority, uri);
                if (fileForUri != null && fileForUri.exists()) {
                    return fileForUri;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
