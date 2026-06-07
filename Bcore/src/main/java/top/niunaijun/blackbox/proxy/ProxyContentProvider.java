package top.niunaijun.blackbox.proxy;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.utils.compat.BundleCompat;

/**
 * Proxy ContentProvider declared in the host manifest to receive process initialization
 * calls for virtual apps. When the special method {@code "_Black_|_init_process_"} is
 * called, it initializes the virtual app's process via {@link BActivityThread} and returns
 * a binder for IPC communication. Multiple static inner classes (P0-P49) provide distinct
 * authority entries for concurrent virtual app processes.
 *
 * @author Milk
 */
public class ProxyContentProvider extends ContentProvider {
    /**
     * Called when the content provider is created. Returns {@code false} as the actual
     * initialization is deferred to the {@link #call} method.
     *
     * @return always {@code false}
     */
    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     * Handles provider calls. When the special initialization method is invoked, extracts
     * the {@link AppConfig} and initializes the virtual process, returning a binder
     * for subsequent IPC communication.
     *
     * @param method the method name to call
     * @param arg    optional argument string
     * @param extras optional Bundle of additional arguments
     * @return a {@link Bundle} containing the client binder on init, or delegates to super
     */
    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (method.equals("_Black_|_init_process_")) {
            assert extras != null;
            extras.setClassLoader(AppConfig.class.getClassLoader());
            AppConfig appConfig = extras.getParcelable(AppConfig.KEY);
            BActivityThread.currentActivityThread().initProcess(appConfig);

            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "_Black_|_client_", BActivityThread.currentActivityThread());
            return bundle;
        }
        return super.call(method, arg, extras);
    }

    /**
     * Not implemented. Returns {@code null}.
     *
     * @param uri           the URI to query
     * @param projection    the columns to return
     * @param selection     the selection clause
     * @param selectionArgs the selection arguments
     * @param sortOrder     the sort order
     * @return always {@code null}
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    /**
     * Not implemented. Returns {@code null}.
     *
     * @param uri the URI to query
     * @return always {@code null}
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Not implemented. Returns {@code null}.
     *
     * @param uri    the content URI
     * @param values the values to insert
     * @return always {@code null}
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    /**
     * Not implemented. Returns 0.
     *
     * @param uri           the URI to query
     * @param selection     the selection clause
     * @param selectionArgs the selection arguments
     * @return always 0
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     * Not implemented. Returns 0.
     *
     * @param uri           the URI to query
     * @param values        the values to update
     * @param selection     the selection clause
     * @param selectionArgs the selection arguments
     * @return always 0
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static class P0 extends ProxyContentProvider {

    }

    public static class P1 extends ProxyContentProvider {

    }

    public static class P2 extends ProxyContentProvider {

    }

    public static class P3 extends ProxyContentProvider {

    }

    public static class P4 extends ProxyContentProvider {

    }

    public static class P5 extends ProxyContentProvider {

    }

    public static class P6 extends ProxyContentProvider {

    }

    public static class P7 extends ProxyContentProvider {

    }

    public static class P8 extends ProxyContentProvider {

    }

    public static class P9 extends ProxyContentProvider {

    }

    public static class P10 extends ProxyContentProvider {

    }

    public static class P11 extends ProxyContentProvider {

    }

    public static class P12 extends ProxyContentProvider {

    }

    public static class P13 extends ProxyContentProvider {

    }

    public static class P14 extends ProxyContentProvider {

    }

    public static class P15 extends ProxyContentProvider {

    }

    public static class P16 extends ProxyContentProvider {

    }

    public static class P17 extends ProxyContentProvider {

    }

    public static class P18 extends ProxyContentProvider {

    }

    public static class P19 extends ProxyContentProvider {

    }

    public static class P20 extends ProxyContentProvider {

    }

    public static class P21 extends ProxyContentProvider {

    }

    public static class P22 extends ProxyContentProvider {

    }

    public static class P23 extends ProxyContentProvider {

    }

    public static class P24 extends ProxyContentProvider {

    }

    public static class P25 extends ProxyContentProvider {

    }

    public static class P26 extends ProxyContentProvider {

    }

    public static class P27 extends ProxyContentProvider {

    }

    public static class P28 extends ProxyContentProvider {

    }

    public static class P29 extends ProxyContentProvider {

    }

    public static class P30 extends ProxyContentProvider {

    }

    public static class P31 extends ProxyContentProvider {

    }

    public static class P32 extends ProxyContentProvider {

    }

    public static class P33 extends ProxyContentProvider {

    }

    public static class P34 extends ProxyContentProvider {

    }

    public static class P35 extends ProxyContentProvider {

    }

    public static class P36 extends ProxyContentProvider {

    }

    public static class P37 extends ProxyContentProvider {

    }

    public static class P38 extends ProxyContentProvider {

    }

    public static class P39 extends ProxyContentProvider {

    }

    public static class P40 extends ProxyContentProvider {

    }

    public static class P41 extends ProxyContentProvider {

    }

    public static class P42 extends ProxyContentProvider {

    }

    public static class P43 extends ProxyContentProvider {

    }

    public static class P44 extends ProxyContentProvider {

    }

    public static class P45 extends ProxyContentProvider {

    }

    public static class P46 extends ProxyContentProvider {

    }

    public static class P47 extends ProxyContentProvider {

    }

    public static class P48 extends ProxyContentProvider {

    }

    public static class P49 extends ProxyContentProvider {

    }
}
