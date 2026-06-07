package top.niunaijun.blackbox.utils.provider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.utils.compat.ContentProviderCompat;

/**
 * Utility class for calling content providers within the BlackBox virtual environment.
 * <p>
 * Provides static convenience methods and a fluent {@link Builder} for invoking
 * content provider methods by authority name. Delegates to {@link ContentProviderCompat}
 * for cross-version-compatible provider access with configurable retry logic.
 */
public class ProviderCall {

    /**
     * Calls a content provider method by authority name using the application context.
     * <p>
     * Wraps {@link #call(String, Context, String, String, Bundle, int)} with the default
     * application context and a retry count of 5. Returns null instead of throwing if the
     * provider cannot be acquired.
     *
     * @param authority  the authority name of the content provider
     * @param methodName the method name to invoke on the provider
     * @param arg        an optional argument string for the method
     * @param bundle     optional Bundle of additional arguments
     * @return the Bundle result from the provider call, or null on failure
     */
    public static Bundle callSafely(String authority, String methodName, String arg, Bundle bundle) {
        try {
            return call(authority, BlackBoxCore.getContext(), methodName, arg, bundle, 5);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calls a content provider method by authority name with retry support.
     * <p>
     * Constructs a content URI from the authority and delegates to
     * {@link ContentProviderCompat#call(Context, Uri, String, String, Bundle, int)}.
     *
     * @param authority   the authority name of the content provider
     * @param context     the context used to access the content resolver
     * @param method      the method name to invoke on the provider
     * @param arg         an optional argument string for the method
     * @param bundle      optional Bundle of additional arguments
     * @param retryCount  the maximum number of retries if the provider is not yet available
     * @return the Bundle result from the provider call
     * @throws IllegalAccessException if the provider client could not be acquired after retries
     */
    public static Bundle call(String authority, Context context, String method, String arg, Bundle bundle, int retryCount) throws IllegalAccessException {
        Uri uri = Uri.parse("content://" + authority);
        return ContentProviderCompat.call(context, uri, method, arg, bundle, retryCount);
    }

    /**
     * Fluent builder for constructing and executing content provider calls.
     * <p>
     * Allows setting the method name, argument string, typed key-value parameters,
     * and retry count before executing the call. Supports common value types including
     * Boolean, Integer, String, Serializable, Bundle, Parcelable, and int arrays.
     *
     * <pre>{@code
     * Bundle result = new ProviderCall.Builder(context, "com.example.provider")
     *     .methodName("getConfig")
     *     .arg("theme")
     *     .addArg("userId", 42)
     *     .retry(3)
     *     .call();
     * }</pre>
     */
    public static final class Builder {

        private Context context;

        private Bundle bundle = new Bundle();

        private String method;
        private String auth;
        private String arg;
        private int retryCount = 5;

        /**
         * Creates a new Builder for calling a content provider.
         *
         * @param context the context used to access the content resolver
         * @param auth    the authority name of the target content provider
         */
        public Builder(Context context, String auth) {
            this.context = context;
            this.auth = auth;
        }

        /**
         * Sets the method name to invoke on the content provider.
         *
         * @param name the method name
         * @return this Builder for chaining
         */
        public Builder methodName(String name) {
            this.method = name;
            return this;
        }

        /**
         * Sets the optional argument string for the provider call.
         *
         * @param arg the argument string
         * @return this Builder for chaining
         */
        public Builder arg(String arg) {
            this.arg = arg;
            return this;
        }

        /**
         * Adds a typed key-value argument to the call Bundle.
         * <p>
         * Supported types: Boolean, Integer, String, Serializable, Bundle, Parcelable, int[].
         * Null values are silently ignored. Unsupported types will cause an
         * {@link IllegalArgumentException}.
         *
         * @param key   the argument key
         * @param value the argument value (Boolean, Integer, String, Serializable, Bundle,
         *              Parcelable, or int[])
         * @return this Builder for chaining
         * @throws IllegalArgumentException if the value type is not supported
         */
        public Builder addArg(String key, Object value) {
            if (value != null) {
                if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Serializable) {
                    bundle.putSerializable(key, (Serializable) value);
                } else if (value instanceof Bundle) {
                    bundle.putBundle(key, (Bundle) value);
                } else if (value instanceof Parcelable) {
                    bundle.putParcelable(key, (Parcelable) value);
                } else if (value instanceof int[]) {
                    bundle.putIntArray(key, (int[]) value);
                } else {
                    throw new IllegalArgumentException("Unknown type " + value.getClass() + " in Bundle.");
                }
            }
            return this;
        }

        /**
         * Sets the maximum number of retry attempts when the provider is not yet available.
         * <p>
         * Each retry waits 400ms before the next attempt. Defaults to 5.
         *
         * @param retryCount the maximum number of retries
         * @return this Builder for chaining
         */
        public Builder retry(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        /**
         * Executes the content provider call.
         *
         * @return the Bundle result from the provider
         * @throws IllegalAccessException if the provider client could not be acquired after retries
         */
        public Bundle call() throws IllegalAccessException {
            return ProviderCall.call(auth, context, method, arg, bundle, retryCount);
        }

        /**
         * Executes the content provider call, swallowing any {@link IllegalAccessException}.
         * <p>
         * Returns null instead of throwing if the provider cannot be acquired.
         *
         * @return the Bundle result from the provider, or null on failure
         */
        public Bundle callSafely() {
            try {
                return call();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
