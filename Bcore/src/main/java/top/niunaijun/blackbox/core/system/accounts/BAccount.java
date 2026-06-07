package top.niunaijun.blackbox.core.system.accounts;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Represents a single account entry within the virtual environment.
 * Virtualizes the Android {@link android.accounts.Account} system by storing account credentials,
 * user data, auth tokens, and per-package visibility settings.
 *
 * <p>Used by {@link BAccountManagerService} to persist account state across virtual user sessions.
 * Implements {@link Parcelable} for IPC transport.</p>
 */
public class BAccount implements Parcelable {
    /** The underlying Android account (name + type). */
    public Account account;
    /** The password associated with this account, or null if not set. */
    public String password;
    /** Key-value pairs of user-defined data attached to this account. */
    public HashMap<String, String> accountUserData = new LinkedHashMap<>();
    /** Per-package visibility settings for this account. */
    public HashMap<String, Integer> visibility = new LinkedHashMap<>();
    /** Cached auth tokens keyed by token type. */
    public HashMap<String, String> authTokens = new LinkedHashMap<>();
    /** Timestamp (millis) of the last successful authentication. */
    public long updateLastAuthenticatedTime;

    /**
     * Checks whether the given account matches this record.
     *
     * @param account the account to compare against
     * @return true if the account is non-null and equals this record's account
     */
    public boolean isMatch(Account account) {
        if (account == null) return false;
        return account.equals(this.account);
    }

    /**
     * Inserts a key-value pair into this account's user data.
     *
     * @param key   the data key
     * @param value the data value
     */
    public void insertExtra(String key, String value) {
        this.accountUserData.put(key, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.account, flags);
        dest.writeString(this.password);
        dest.writeSerializable(this.accountUserData);
        dest.writeSerializable(this.visibility);
        dest.writeSerializable(this.authTokens);
        dest.writeLong(this.updateLastAuthenticatedTime);
    }

    /**
     * Reads this account's fields from a Parcel.
     *
     * @param source the Parcel to read from
     */
    public void readFromParcel(Parcel source) {
        this.account = source.readParcelable(Account.class.getClassLoader());
        this.password = source.readString();
        this.accountUserData = (HashMap<String, String>) source.readSerializable();
        this.visibility = (HashMap<String, Integer>) source.readSerializable();
        this.authTokens = (HashMap<String, String>) source.readSerializable();
        this.updateLastAuthenticatedTime = source.readLong();
    }

    /** Creates an empty BAccount. */
    public BAccount() {
    }

    protected BAccount(Parcel in) {
        this.account = in.readParcelable(Account.class.getClassLoader());
        this.password = in.readString();
        this.accountUserData = (HashMap<String, String>) in.readSerializable();
        this.visibility = (HashMap<String, Integer>) in.readSerializable();
        this.authTokens = (HashMap<String, String>) in.readSerializable();
        this.updateLastAuthenticatedTime = in.readLong();
    }

    public static final Creator<BAccount> CREATOR = new Creator<BAccount>() {
        @Override
        public BAccount createFromParcel(Parcel source) {
            return new BAccount(source);
        }

        @Override
        public BAccount[] newArray(int size) {
            return new BAccount[size];
        }
    };
}
