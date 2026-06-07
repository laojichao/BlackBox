package top.niunaijun.blackbox.fake.frameworks;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.accounts.IAccountManagerResponse;
import android.os.Bundle;
import android.os.RemoteException;

import java.util.Map;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.accounts.IBAccountManagerService;

/**
 * Client-side manager for account operations within the virtual environment. Provides
 * a facade over {@link IBAccountManagerService} for managing accounts, auth tokens,
 * passwords, and account visibility scoped to the current virtual user.
 */
public class BAccountManager extends BlackManager<IBAccountManagerService> {
    private static final BAccountManager sBAccountManager = new BAccountManager();

    /**
     * Returns the singleton instance of {@link BAccountManager}.
     *
     * @return the singleton BAccountManager instance
     */
    public static BAccountManager get() {
        return sBAccountManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.ACCOUNT_MANAGER;
    }

    /**
     * Returns the password for the given account.
     *
     * @param account the account to query
     * @return the password string, or null on failure
     */
    public String getPassword(Account account) {
        try {
            return getService().getPassword(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Returns user data associated with the given account and key.
     *
     * @param account the account to query
     * @param key     the user data key
     * @return the user data string, or null on failure
     */
    public String getUserData(Account account, String key) {
        try {
            return getService().getUserData(account, key, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all registered authenticator types for the current virtual user.
     *
     * @return an array of AuthenticatorDescription, or null on failure
     */
    public AuthenticatorDescription[] getAuthenticatorTypes() {
        try {
            return getService().getAuthenticatorTypes(BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns accounts accessible to the given package and UID.
     *
     * @param packageName the package name to query
     * @param uid         the UID of the package
     * @return an array of Account objects, or null on failure
     */
    public Account[] getAccountsForPackage(String packageName, int uid) {
        try {
            return getService().getAccountsForPackage(packageName, uid, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns accounts of the given type that are visible to the specified package.
     *
     * @param type        the account type to filter by
     * @param packageName the package name to check visibility for
     * @return an array of Account objects, or null on failure
     */
    public Account[] getAccountsByTypeForPackage(String type, String packageName) {
        try {
            return getService().getAccountsByTypeForPackage(type, packageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all accounts of the specified type for the current virtual user.
     *
     * @param type the account type to filter by, or null for all accounts
     * @return an array of Account objects, or null on failure
     */
    public Account[] getAccountsAsUser(String type) {
        try {
            return getService().getAccountsAsUser(type, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Asynchronously retrieves an account by type and features.
     *
     * @param response     the callback to receive the result
     * @param accountType  the account type to search for
     * @param features     required account features, or null
     */
    public void getAccountByTypeAndFeatures(IAccountManagerResponse response, String accountType,
                                            String[] features) {
        try {
            getService().getAccountByTypeAndFeatures(response, accountType, features, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Asynchronously retrieves accounts by type that have the specified features.
     *
     * @param response     the callback to receive the result
     * @param accountType  the account type to filter by
     * @param features     required account features
     */
    public void getAccountsByFeatures(IAccountManagerResponse response, String accountType,
                               String[] features) {
        try {
            getService().getAccountsByFeatures(response, accountType, features, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an account explicitly with the given password and extras.
     *
     * @param account  the account to add
     * @param password the account password
     * @param extras   additional data to associate with the account
     * @return true if the account was added successfully, false otherwise
     */
    public boolean addAccountExplicitly(Account account, String password, Bundle extras) {
        try {
            return getService().addAccountExplicitly(account, password, extras, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Asynchronously removes an account for the current virtual user.
     *
     * @param response              the callback to receive the result
     * @param account               the account to remove
     * @param expectActivityLaunch  whether an activity launch is expected
     */
    public void removeAccountAsUser(IAccountManagerResponse response, Account account,
                             boolean expectActivityLaunch) {
        try {
            getService().removeAccountAsUser(response, account, expectActivityLaunch, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an account explicitly.
     *
     * @param account the account to remove
     * @return true if the account was removed successfully, false otherwise
     */
    public boolean removeAccountExplicitly(Account account) {
        try {
            return getService().removeAccountExplicitly(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copies an account from one user to another.
     *
     * @param response  the callback to receive the result
     * @param account   the account to copy
     * @param userFrom  the source user ID
     * @param userTo    the destination user ID
     */
    public void copyAccountToUser(IAccountManagerResponse response, Account account,
                           int userFrom, int userTo) {
        try {
            getService().copyAccountToUser(response, account, userFrom, userTo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invalidates an auth token for the given account type.
     *
     * @param accountType the account type
     * @param authToken   the auth token to invalidate
     */
    public void invalidateAuthToken(String accountType, String authToken) {
        try {
            getService().invalidateAuthToken(accountType, authToken, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a cached auth token for the given account and token type.
     *
     * @param account       the account to query
     * @param authTokenType the auth token type
     * @return the cached auth token string, or null on failure
     */
    public String peekAuthToken(Account account, String authTokenType) {
        try {
            return getService().peekAuthToken(account, authTokenType, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets an auth token for the given account and token type.
     *
     * @param account       the account to update
     * @param authTokenType the auth token type
     * @param authToken     the auth token value to set
     */
    public void setAuthToken(Account account, String authTokenType, String authToken) {
        try {
            getService().setAuthToken(account, authTokenType, authToken, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets the password for the given account.
     *
     * @param account  the account to update
     * @param password the new password
     */
    public void setPassword(Account account, String password) {
        try {
            getService().setPassword(account, password, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the password for the given account.
     *
     * @param account the account whose password should be cleared
     */
    public void clearPassword(Account account) {
        try {
            getService().clearPassword(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets user data for the given account and key.
     *
     * @param account the account to update
     * @param key     the user data key
     * @param value   the user data value
     */
    public void setUserData(Account account, String key, String value) {
        try {
            getService().setUserData(account, key, value, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates app permission for an account's auth token type.
     *
     * @param account       the account
     * @param authTokenType the auth token type
     * @param uid           the app UID
     * @param value         whether to grant or revoke the permission
     */
    public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) {
        try {
            getService().updateAppPermission(account, authTokenType, uid, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously retrieves an auth token for the given account.
     *
     * @param response             the callback to receive the result
     * @param account              the account to get the token for
     * @param authTokenType        the auth token type
     * @param notifyOnAuthFailure   whether to notify on authentication failure
     * @param expectActivityLaunch  whether an activity launch is expected
     * @param options              additional options bundle
     */
    public void getAuthToken(IAccountManagerResponse response, Account account,
                      String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch,
                      Bundle options) {
        try {
            getService().getAuthToken(response, account, authTokenType, notifyOnAuthFailure, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously adds an account of the given type.
     *
     * @param response             the callback to receive the result
     * @param accountType          the account type to add
     * @param authTokenType        the auth token type, or null
     * @param requiredFeatures     required account features, or null
     * @param expectActivityLaunch  whether an activity launch is expected
     * @param options              additional options bundle
     */
    public void addAccount(IAccountManagerResponse response, String accountType,
                    String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch,
                    Bundle options) {
        try {
            getService().addAccount(response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously adds an account as a specific user.
     *
     * @param response             the callback to receive the result
     * @param accountType          the account type to add
     * @param authTokenType        the auth token type, or null
     * @param requiredFeatures     required account features, or null
     * @param expectActivityLaunch  whether an activity launch is expected
     * @param options              additional options bundle
     */
    public void addAccountAsUser(IAccountManagerResponse response, String accountType,
                          String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch,
                          Bundle options) {
        try {
            getService().addAccountAsUser(response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously updates credentials for the given account.
     *
     * @param response             the callback to receive the result
     * @param account              the account to update
     * @param authTokenType        the auth token type
     * @param expectActivityLaunch  whether an activity launch is expected
     * @param options              additional options bundle
     */
    public void updateCredentials(IAccountManagerResponse response, Account account,
                           String authTokenType, boolean expectActivityLaunch, Bundle options) {
        try {
            getService().updateCredentials(response, account, authTokenType, expectActivityLaunch, options, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously edits properties for the given account type.
     *
     * @param response             the callback to receive the result
     * @param accountType          the account type to edit
     * @param expectActivityLaunch  whether an activity launch is expected
     */
    public void editProperties(IAccountManagerResponse response, String accountType,
                        boolean expectActivityLaunch) {
        try {
            getService().editProperties(response, accountType, expectActivityLaunch, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously confirms credentials for an account as a specific user.
     *
     * @param response             the callback to receive the result
     * @param account              the account to confirm
     * @param options              additional options bundle
     * @param expectActivityLaunch  whether an activity launch is expected
     */
    public void confirmCredentialsAsUser(IAccountManagerResponse response, Account account,
                                  Bundle options, boolean expectActivityLaunch) {
        try {
            getService().confirmCredentialsAsUser(response, account, options, expectActivityLaunch, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Marks the given account as authenticated.
     *
     * @param account the account to mark as authenticated
     * @return true if the operation succeeded, false otherwise
     */
    public boolean accountAuthenticated(Account account) {
        try {
            return getService().accountAuthenticated(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Asynchronously retrieves the label for an auth token type.
     *
     * @param response      the callback to receive the result
     * @param accountType   the account type
     * @param authTokenType the auth token type to get the label for
     */
    public void getAuthTokenLabel(IAccountManagerResponse response, String accountType,
                           String authTokenType) {
        try {
            getService().getAuthTokenLabel(response, accountType, authTokenType, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a map of package names to visibility values for the given account.
     *
     * @param account the account to query
     * @return a Map of package name to visibility Integer, or null on failure
     */
    public Map getPackagesAndVisibilityForAccount(Account account) {
        try {
            return getService().getPackagesAndVisibilityForAccount(account, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds an account explicitly with visibility settings.
     *
     * @param account    the account to add
     * @param password   the account password
     * @param extras     additional data to associate with the account
     * @param visibility a Map of package name to visibility Integer
     * @return true if the account was added successfully, false otherwise
     */
    public boolean addAccountExplicitlyWithVisibility(Account account, String password, Bundle extras,
                                               Map visibility) {
        try {
            return getService().addAccountExplicitlyWithVisibility(account, password, extras, visibility, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets the visibility of the given account for a specific package.
     *
     * @param account       the account to update
     * @param packageName   the package name
     * @param newVisibility the new visibility level
     * @return true if the operation succeeded, false otherwise
     */
    public boolean setAccountVisibility(Account account, String packageName, int newVisibility) {
        try {
            return getService().setAccountVisibility(account, packageName, newVisibility, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the visibility level of the given account for a specific package.
     *
     * @param account     the account to query
     * @param packageName the package name
     * @return the visibility level (default 3 = VISIBILITY_NOT_VISIBLE)
     */
    public int getAccountVisibility(Account account, String packageName) {
        try {
            return getService().getAccountVisibility(account, packageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // AccountManager.VISIBILITY_NOT_VISIBLE
        return 3;
    }

    /**
     * Returns accounts and their visibility for the given package and optional account type.
     *
     * @param packageName the package name
     * @param accountType the account type to filter by, or null for all types
     * @return a Map of Account to visibility Integer, or null on failure
     */
    public Map getAccountsAndVisibilityForPackage(String packageName, String accountType) {
        try {
            return getService().getAccountsAndVisibilityForPackage(packageName, accountType, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers a listener for account changes of the specified types.
     *
     * @param accountTypes  the account types to listen for
     * @param opPackageName the package name of the listener
     */
    public void registerAccountListener(String[] accountTypes, String opPackageName) {
        try {
            getService().registerAccountListener(accountTypes, opPackageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregisters a listener for account changes of the specified types.
     *
     * @param accountTypes  the account types to stop listening for
     * @param opPackageName the package name of the listener
     */
    public void unregisterAccountListener(String[] accountTypes, String opPackageName) {
        try {
            getService().unregisterAccountListener(accountTypes, opPackageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
