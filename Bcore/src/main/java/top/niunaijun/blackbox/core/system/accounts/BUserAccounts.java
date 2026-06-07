package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the set of accounts belonging to a single virtual user.
 * Virtualizes the per-user account storage that the real Android {@link android.accounts.AccountManager}
 * maintains internally. Each virtual user ID maps to one {@link BUserAccounts} instance containing
 * a list of {@link BAccount} entries with their credentials, tokens, user data, and visibility settings.
 *
 * <p>Used by {@link BAccountManagerService} as the per-user data container.
 * Implements {@link Parcelable} for IPC transport and disk persistence.</p>
 */
public class BUserAccounts implements Parcelable {
    /** Lock object used to synchronize access to this user's account data. */
    public final Object lock = new Object();

    /** The virtual user ID that owns these accounts. */
    public int userId;
    /** The list of all accounts registered for this virtual user. */
    public List<BAccount> accounts = new ArrayList<>();

    /**
     * Converts this user's account list to a plain array of {@link Account} objects.
     *
     * @return array of Android {@link Account} instances (name + type pairs)
     */
    public Account[] toAccounts() {
        List<Account> local = new ArrayList<>();
        for (BAccount account : accounts) {
            local.add(account.account);
        }
        return local.toArray(new Account[]{});
    }

    /**
     * Creates a new empty account entry and adds it to this user's account list.
     *
     * @param account the Android {@link Account} (name + type) to register
     * @return the newly created {@link BAccount} wrapper
     */
    public BAccount addAccount(Account account) {
        BAccount bAccount = new BAccount();
        bAccount.account = account;
        accounts.add(bAccount);
        return bAccount;
    }

    /**
     * Looks up an account by matching its name and type.
     *
     * @param account the account to search for
     * @return the matching {@link BAccount}, or null if not found
     */
    public BAccount getAccount(Account account) {
        for (BAccount bAccount : accounts) {
            if (bAccount.isMatch(account))
                return bAccount;
        }
        return null;
    }

    /**
     * Removes an account from this user's account list.
     *
     * @param account the account to remove
     * @return true if the account was found and removed, false otherwise
     */
    public boolean delAccount(Account account) {
        BAccount bAccount = getAccount(account);
        return accounts.remove(bAccount);
    }


    /**
     * Returns the per-package visibility map for the given account.
     *
     * @param account the account to query
     * @return map of package name to visibility level, or an empty map if the account is not found
     */
    public Map<String, Integer> getVisibility(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount == null)
            return new HashMap<>();
        return bAccount.visibility;
    }

    /**
     * Returns the user-defined key-value data for the given account.
     *
     * @param account the account to query
     * @return map of user data keys to values, or an empty map if the account is not found
     */
    public Map<String, String> getAccountUserData(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount == null)
            return new HashMap<>();
        return bAccount.accountUserData;
    }

    /**
     * Returns the cached auth tokens for the given account, keyed by token type.
     *
     * @param account the account to query
     * @return map of auth token type to token value, or an empty map if the account is not found
     */
    public Map<String, String> getAuthToken(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount == null)
            return new HashMap<>();
        return bAccount.authTokens;
    }

    /**
     * Returns all accounts of the specified type for this user.
     *
     * @param type the account type to filter by (e.g., "com.google")
     * @return array of matching {@link Account} instances
     */
    public Account[] getAccountsByType(String type) {
        List<Account> local = new ArrayList<>();
        for (BAccount account : accounts) {
            if (account.account.type.equals(type)) {
                local.add(account.account);
            }
        }
        return local.toArray(new Account[]{});
    }

    /**
     * Updates the last-authenticated timestamp for the given account to the current time.
     *
     * @param account the account whose timestamp to update
     */
    public void updateLastAuthenticatedTime(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount != null) {
            bAccount.updateLastAuthenticatedTime = System.currentTimeMillis();
        }
    }

    /**
     * Retrieves the last-authenticated timestamp for the given account.
     *
     * @param account the account to query
     * @return timestamp in milliseconds, or -1 if the account is not found
     */
    public long findAccountLastAuthenticatedTime(Account account) {
        BAccount bAccount = getAccount(account);
        if (bAccount != null) {
            return bAccount.updateLastAuthenticatedTime;
        }
        return -1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeTypedList(this.accounts);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readInt();
        this.accounts = source.createTypedArrayList(BAccount.CREATOR);
    }

    public BUserAccounts() {
    }

    protected BUserAccounts(Parcel in) {
        this.userId = in.readInt();
        this.accounts = in.createTypedArrayList(BAccount.CREATOR);
    }

    public static final Creator<BUserAccounts> CREATOR = new Creator<BUserAccounts>() {
        @Override
        public BUserAccounts createFromParcel(Parcel source) {
            return new BUserAccounts(source);
        }

        @Override
        public BUserAccounts[] newArray(int size) {
            return new BUserAccounts[size];
        }
    };
}
