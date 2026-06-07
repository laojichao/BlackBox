package top.niunaijun.blackbox.utils.compat;

import android.accounts.AccountManager;

/**
 * Compatibility wrapper for {@link android.accounts.AccountManager} constants.
 * <p>
 * Provides access to account manager bundle keys and error codes that may not be
 * available as public constants on all API levels, including authentication failure
 * notification flags, caller package name keys, custom token expiry, and error codes
 * for restricted or disabled account management.
 */
public class AccountManagerCompat {

    /**
     * Boolean, if set and 'customTokens' the authenticator is responsible for
     * notifications.
     */
    public static final String KEY_NOTIFY_ON_FAILURE = "notifyOnAuthFailure";

    /**
     * The Android package of the caller will be set in the options bundle by the
     * {@link AccountManager} and will be passed to the AccountManagerService and
     * to the AccountAuthenticators. The vuid of the caller will be known by the
     * AccountManagerService as well as the AccountAuthenticators so they will be able to
     * verify that the package is consistent with the vuid (a vuid might be shared by many
     * packages).
     */
    public static final String KEY_ANDROID_PACKAGE_NAME = "androidPackageName";

    /** Error code indicating the user is restricted from performing account operations. */
    public static final int ERROR_CODE_USER_RESTRICTED = 100;

    /** Error code indicating account management is disabled for the given account type. */
    public static final int ERROR_CODE_MANAGEMENT_DISABLED_FOR_ACCOUNT_TYPE = 101;

    /**
     * Bundle key used for the {@code long} expiration time (in millis from the unix epoch) of the
     * associated auth token.
     *
     */
    public static final String KEY_CUSTOM_TOKEN_EXPIRY = "android.accounts.expiry";

    /**
     * Bundle key used to supply the last time the credentials of the account
     * were authenticated successfully. Time is specified in milliseconds since
     * epoch. Associated time is updated on successful authentication of account
     * on adding account, confirming credentials, or updating credentials.
     */
    public static final String KEY_LAST_AUTHENTICATED_TIME = "lastAuthenticatedTime";


}
