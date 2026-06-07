package top.niunaijun.blackbox.core.system;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.BundleCompat;

/**
 * ContentProvider that serves as the IPC entry point into the BlackBox
 * virtual engine from the client (virtual app) process.
 *
 * <p>Registered in the host app's manifest, this provider boots the
 * entire virtual system on {@link #onCreate()} and handles binder
 * lookup requests from client code.  When a client calls
 * {@link #call(String, String, Bundle)} with method {@code "VM"}, it
 * extracts the requested service name from the bundle extras and
 * returns the corresponding system service binder wrapped in a reply
 * bundle.</p>
 *
 * <p>All standard CRUD operations ({@code query}, {@code insert},
 * {@code update}, {@code delete}) are no-ops and return empty
 * results.</p>
 */
public class SystemCallProvider extends ContentProvider {
    public static final String TAG = "SystemCallProvider";

    @Override
    public boolean onCreate() {
        return initSystem();
    }

    /**
     * Bootstraps the virtual engine by calling
     * {@link BlackBoxSystem#getSystem()#startup()}.
     *
     * @return always {@code true}
     */
    private boolean initSystem() {
        BlackBoxSystem.getSystem().startup();
        return true;
    }

    /**
     * Handles IPC calls from virtual app processes.
     *
     * <p>When {@code method} is {@code "VM"}, reads the service name
     * from {@code extras} (key {@code "_B_|_server_name_"}), resolves
     * it via {@link ServiceManager#getService(String)}, and returns the
     * binder in a reply bundle under key {@code "_B_|_server_"}.</p>
     *
     * @param method the call method identifier
     * @param arg    optional string argument (unused)
     * @param extras optional bundle of input parameters
     * @return a {@link Bundle} containing the requested binder, or the
     *         result of the default implementation
     */
    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Slog.d(TAG, "call: " + method + ", " + extras);
        if ("VM".equals(method)) {
            Bundle bundle = new Bundle();
            if (extras != null) {
                String name = extras.getString("_B_|_server_name_");
                BundleCompat.putBinder(bundle, "_B_|_server_", ServiceManager.getService(name));
            }
            return bundle;
        }
        return super.call(method, arg, extras);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
