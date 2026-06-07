package top.niunaijun.blackbox.utils.compat;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;

/**
 * Compatibility wrapper for {@link android.content.ContentProviderClient} operations.
 * <p>
 * Handles API differences in content provider access across Android versions:
 * <ul>
 *   <li>Pre-API 17 (Jelly Bean MR1): uses the simpler {@code ContentResolver.call()} directly.</li>
 *   <li>API 16+ (Jelly Bean): acquires unstable content provider clients to avoid killing
 *       the calling process on provider crashes.</li>
 *   <li>API 24+ (Nougat): uses {@code ContentProviderClient.close()} instead of the deprecated
 *       {@code release()} for cleanup.</li>
 * </ul>
 * Also provides retry logic for acquiring provider clients that may not yet be registered.
 */
public class ContentProviderCompat {

    /**
     * Calls a content provider method with retry support.
     * <p>
     * On pre-API 17 devices, delegates directly to {@code ContentResolver.call()}. On API 17+,
     * acquires a {@link ContentProviderClient} with retry logic and calls the method through it.
     *
     * @param context    the context used to access the content resolver
     * @param uri        the URI of the content provider to call
     * @param method     the method name to invoke on the provider
     * @param arg        an optional argument string for the method
     * @param extras     optional Bundle of additional arguments
     * @param retryCount the maximum number of retries if the provider is not yet available
     * @return the Bundle result from the provider call
     * @throws IllegalAccessException if the provider client could not be acquired after retries
     */
    public static Bundle call(Context context, Uri uri, String method, String arg, Bundle extras, int retryCount) throws IllegalAccessException {
        if (VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.getContentResolver().call(uri, method, arg, extras);
        }
        ContentProviderClient client = acquireContentProviderClientRetry(context, uri, retryCount);
        try {
            if (client == null) {
                throw new IllegalAccessException();
            }
            return client.call(method, arg, extras);
        } catch (RemoteException e) {
            throw new IllegalAccessException(e.getMessage());
        } finally {
            releaseQuietly(client);
        }
    }


    private static ContentProviderClient acquireContentProviderClient(Context context, Uri uri) {
        try {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return context.getContentResolver().acquireUnstableContentProviderClient(uri);
            }
            return context.getContentResolver().acquireContentProviderClient(uri);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Acquires a content provider client by URI with retry logic.
     * <p>
     * Retries up to {@code retryCount} times with a 400ms delay between attempts if the
     * provider is not yet available.
     *
     * @param context    the context used to access the content resolver
     * @param uri        the URI of the content provider
     * @param retryCount the maximum number of acquisition attempts
     * @return the acquired {@link ContentProviderClient}, or null if all attempts fail
     */
    public static ContentProviderClient acquireContentProviderClientRetry(Context context, Uri uri, int retryCount) {
        ContentProviderClient client = acquireContentProviderClient(context, uri);
        if (client == null) {
            int retry = 0;
            while (retry < retryCount && client == null) {
                SystemClock.sleep(400);
                retry++;
                client = acquireContentProviderClient(context, uri);
            }
        }
        return client;
    }

    /**
     * Acquires a content provider client by authority name with retry logic.
     * <p>
     * Retries up to {@code retryCount} times with a 400ms delay between attempts if the
     * provider is not yet available.
     *
     * @param context    the context used to access the content resolver
     * @param name       the authority name of the content provider
     * @param retryCount the maximum number of acquisition attempts
     * @return the acquired {@link ContentProviderClient}, or null if all attempts fail
     */
    public static ContentProviderClient acquireContentProviderClientRetry(Context context, String name, int retryCount) {
        ContentProviderClient client = acquireContentProviderClient(context, name);
        if (client == null) {
            int retry = 0;
            while (retry < retryCount && client == null) {
                SystemClock.sleep(400);
                retry++;
                client = acquireContentProviderClient(context, name);
            }
        }
        return client;
    }

    private static ContentProviderClient acquireContentProviderClient(Context context, String name) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return context.getContentResolver().acquireUnstableContentProviderClient(name);
        }
        return context.getContentResolver().acquireContentProviderClient(name);
    }

    private static void releaseQuietly(ContentProviderClient client) {
        if (client != null) {
            try {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    client.close();
                } else {
                    client.release();
                }
            } catch (Exception ignored) {
            }
        }
    }
}