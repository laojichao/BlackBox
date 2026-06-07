package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.Account;

import java.util.Objects;

/**
 * Caches an authentication token for a specific account, token type, and calling package
 * within the virtual environment. Used by {@link BAccountManagerService} to avoid repeated
 * authenticator service calls when a valid cached token exists.
 *
 * <p>Tokens have an expiry time ({@link #expiryEpochMillis}) after which they are considered
 * stale and removed from the cache.</p>
 */
public class TokenCache {
    /** The virtual user ID this token belongs to. */
    public int userId;
    /** The account (name + type) this token was issued for. */
    public Account account;
    /** Epoch millis timestamp after which this token is considered expired. */
    public long expiryEpochMillis;
    /** The cached authentication token value. */
    public String authToken;
    /** The type of the auth token (e.g., "access_token"). */
    public String authTokenType;
    /** The package name that requested this token. */
    public String packageName;

    /**
     * Creates a new token cache entry with full token data.
     *
     * @param userId       the virtual user ID
     * @param account      the account this token belongs to
     * @param callerPkg    the requesting package name
     * @param tokenType    the type of the auth token
     * @param token        the token value
     * @param expiryMillis the epoch millis when this token expires
     */
    public TokenCache(int userId,Account account,
                      String callerPkg,
                      String tokenType,
                      String token,
                      long expiryMillis) {
        this.userId = userId;
        this.account = account;
        this.expiryEpochMillis = expiryMillis;
        this.authToken = token;
        this.authTokenType = tokenType;
        this.packageName = callerPkg;
    }

    /**
     * Creates a token cache entry without a token value, used for lookup keys.
     *
     * @param userId       the virtual user ID
     * @param account      the account
     * @param authTokenType the type of the auth token
     * @param packageName  the requesting package name
     */
    public TokenCache(int userId, Account account, String authTokenType, String packageName) {
        this.userId = userId;
        this.account = account;
        this.authToken = authToken;
        this.authTokenType = authTokenType;
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenCache)) return false;
        TokenCache that = (TokenCache) o;
        return userId == that.userId &&
                expiryEpochMillis == that.expiryEpochMillis &&
                Objects.equals(account, that.account) &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(authTokenType, that.authTokenType) &&
                Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, account, expiryEpochMillis, authToken, authTokenType, packageName);
    }
}
