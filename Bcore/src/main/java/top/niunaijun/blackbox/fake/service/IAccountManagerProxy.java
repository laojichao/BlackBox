package top.niunaijun.blackbox.fake.service;

import android.accounts.Account;
import android.accounts.IAccountManagerResponse;
import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.Map;

import black.android.accounts.BRIAccountManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.frameworks.BAccountManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Proxy for the Android Account Manager system service (IAccountManager).
 * Intercepts account-related operations such as adding, removing, and querying
 * accounts, managing auth tokens, and handling account visibility. Delegates
 * all operations to the virtual environment's {@link BAccountManager} to ensure
 * proper account isolation within the virtual sandbox.
 *
 * @author Milk
 */
public class IAccountManagerProxy extends BinderInvocationStub {
    /** Tag for logging. */
    public static final String TAG = "IAccountManagerProxy";

    /**
     * Constructs a new proxy by obtaining the Account Manager binder service.
     */
    public IAccountManagerProxy() {
        super(BRServiceManager.get().getService(Context.ACCOUNT_SERVICE));
    }

    /**
     * Returns the IAccountManager interface instance from the system service.
     *
     * @return the original IAccountManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIAccountManagerStub.get().asInterface(BRServiceManager.get().getService(Context.ACCOUNT_SERVICE));
    }

    /**
     * Replaces the system Account Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ACCOUNT_SERVICE);
    }

    /**
     * Called after method hooks are bound. No additional setup required.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
    }

    /**
     * Checks if the environment has been corrupted by another proxy.
     *
     * @return always returns false
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Intercepts all method calls and logs them before delegating.
     *
     * @param proxy the proxy object
     * @param method the method being invoked
     * @param args the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method call fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Slog.d(TAG, "call " + method.getName());
        return super.invoke(proxy, method, args);
    }

    /**
     * Hook that intercepts {@code getPassword} to retrieve the password
     * for the given account from the virtual account manager.
     */
    @ProxyMethod("getPassword")
    public static class getPassword extends MethodHook {

        /**
         * Retrieves the password for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account to query
         * @return the password string for the account
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getPassword((Account) args[0]);
        }
    }

    /**
     * Hook that intercepts {@code getUserData} to retrieve user data
     * for the given account from the virtual account manager.
     */
    @ProxyMethod("getUserData")
    public static class getUserData extends MethodHook {

        /**
         * Retrieves user data for the specified account and key.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the key string
         * @return the user data string, or null if not found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getUserData((Account) args[0], (String) args[1]);
        }
    }

    /**
     * Hook that intercepts {@code getAuthenticatorTypes} to return
     * authenticator types registered in the virtual account manager.
     */
    @ProxyMethod("getAuthenticatorTypes")
    public static class getAuthenticatorTypes extends MethodHook {

        /**
         * Returns the list of registered authenticator types.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return array of AuthenticatorDescription for the virtual environment
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAuthenticatorTypes();
        }
    }

    /**
     * Hook that intercepts {@code getAccountsForPackage} to return
     * accounts associated with the given package in the virtual environment.
     */
    @ProxyMethod("getAccountsForPackage")
    public static class getAccountsForPackage extends MethodHook {

        /**
         * Retrieves accounts for the specified package name and UID.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the package name, args[1] is the UID
         * @return array of Account objects for the package
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsForPackage((String) args[0], (int) args[1]);
        }
    }

    /**
     * Hook that intercepts {@code getAccountsByTypeForPackage} to return
     * accounts filtered by type and package in the virtual environment.
     */
    @ProxyMethod("getAccountsByTypeForPackage")
    public static class getAccountsByTypeForPackage extends MethodHook {

        /**
         * Retrieves accounts filtered by type for the specified package.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the account type, args[1] is the package name
         * @return array of Account objects matching the criteria
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsByTypeForPackage((String) args[0], (String) args[1]);
        }
    }

    /**
     * Hook that intercepts {@code getAccountByTypeAndFeatures} to find accounts
     * matching the specified type and features in the virtual environment.
     */
    @ProxyMethod("getAccountByTypeAndFeatures")
    public static class getAccountByTypeAndFeatures extends MethodHook {

        /**
         * Finds accounts by type and features via the virtual account manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the account type, args[2] is the features array
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAccountByTypeAndFeatures((IAccountManagerResponse) args[0], (String) args[1], (String[]) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getAccountsByFeatures} to query accounts
     * by features in the virtual environment.
     */
    @ProxyMethod("getAccountsByFeatures")
    public static class getAccountsByFeatures extends MethodHook {

        /**
         * Queries accounts by features via the virtual account manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the account type, args[2] is the features array
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAccountsByFeatures((IAccountManagerResponse) args[0], (String) args[1], (String[]) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getAccountsAsUser} to return accounts
     * for the virtual user, ignoring the system user parameter.
     */
    @ProxyMethod("getAccountsAsUser")
    public static class getAccountsAsUser extends MethodHook {

        /**
         * Retrieves accounts for the virtual user by type.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the account type
         * @return array of Account objects for the virtual user
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsAsUser((String) args[0]);
        }
    }

    /**
     * Hook that intercepts {@code addAccountExplicitly} to add an account
     * to the virtual account manager.
     */
    @ProxyMethod("addAccountExplicitly")
    public static class addAccountExplicitly extends MethodHook {

        /**
         * Adds an account explicitly to the virtual account manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the password, args[2] is the user data Bundle
         * @return true if the account was added successfully
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().addAccountExplicitly((Account) args[0], (String) args[1], (Bundle) args[2]);
        }
    }

    /**
     * Hook that intercepts {@code removeAccountAsUser} to remove an account
     * from the virtual account manager asynchronously.
     */
    @ProxyMethod("removeAccountAsUser")
    public static class removeAccountAsUser extends MethodHook {

        /**
         * Removes an account asynchronously from the virtual account manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the Account, args[2] is the requireDeletion boolean
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().removeAccountAsUser((IAccountManagerResponse) args[0], (Account) args[1], (boolean) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code removeAccountExplicitly} to remove an account
     * directly from the virtual account manager.
     */
    @ProxyMethod("removeAccountExplicitly")
    public static class removeAccountExplicitly extends MethodHook {

        /**
         * Removes an account explicitly from the virtual account manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account to remove
         * @return true if the account was removed successfully
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().removeAccountExplicitly((Account) args[0]);
        }
    }

    /**
     * Hook that intercepts {@code copyAccountToUser} to copy an account
     * between users in the virtual account manager.
     */
    @ProxyMethod("copyAccountToUser")
    public static class copyAccountToUser extends MethodHook {

        /**
         * Copies an account to another user in the virtual environment.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the Account, args[2] is source user, args[3] is dest user
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().copyAccountToUser((IAccountManagerResponse) args[0], (Account) args[1], (int) args[2], (int) args[3]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code invalidateAuthToken} to invalidate
     * an auth token in the virtual account manager.
     */
    @ProxyMethod("invalidateAuthToken")
    public static class invalidateAuthToken extends MethodHook {

        /**
         * Invalidates the specified auth token.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the account type, args[1] is the auth token
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().invalidateAuthToken((String) args[0], (String) args[1]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code peekAuthToken} to peek at a cached
     * auth token for the given account.
     */
    @ProxyMethod("peekAuthToken")
    public static class peekAuthToken extends MethodHook {

        /**
         * Peeks at the cached auth token for the account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the auth token type
         * @return the cached auth token string, or null
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().peekAuthToken((Account) args[0], (String) args[1]);
        }
    }

    /**
     * Hook that intercepts {@code setAuthToken} to store an auth token
     * for the given account in the virtual account manager.
     */
    @ProxyMethod("setAuthToken")
    public static class setAuthToken extends MethodHook {

        /**
         * Sets the auth token for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the token type, args[2] is the auth token
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().setAuthToken((Account) args[0], (String) args[1], (String) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code setPassword} to set the password
     * for the given account in the virtual account manager.
     */
    @ProxyMethod("setPassword")
    public static class setPassword extends MethodHook {

        /**
         * Sets the password for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the password
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().setPassword((Account) args[0], (String) args[1]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code clearPassword} to clear the password
     * for the given account in the virtual account manager.
     */
    @ProxyMethod("clearPassword")
    public static class clearPassword extends MethodHook {

        /**
         * Clears the password for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().clearPassword((Account) args[0]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code setUserData} to set user data
     * for the given account in the virtual account manager.
     */
    @ProxyMethod("setUserData")
    public static class setUserData extends MethodHook {

        /**
         * Sets user data for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the key, args[2] is the value
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().setUserData((Account) args[0], (String) args[1], (String) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code updateAppPermission} to update
     * app permissions for an account in the virtual account manager.
     */
    @ProxyMethod("updateAppPermission")
    public static class updateAppPermission extends MethodHook {

        /**
         * Updates app permission for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the auth type, args[2] is the UID, args[3] is the grant boolean
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().updateAppPermission((Account) args[0], (String) args[1], (int) args[2], (boolean) args[3]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getAuthToken} to retrieve an auth token
     * for the given account from the virtual account manager.
     */
    @ProxyMethod("getAuthToken")
    public static class getAuthToken extends MethodHook {

        /**
         * Retrieves an auth token for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the Account, args[2] is the auth type,
         *             args[3] is notifyOnFailure, args[4] is isUpdate, args[5] is the options Bundle
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAuthToken((IAccountManagerResponse) args[0],
                    (Account) args[1],
                    (String) args[2],
                    (boolean) args[3],
                    (boolean) args[4],
                    (Bundle) args[5]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code addAccount} to add a new account
     * to the virtual account manager.
     */
    @ProxyMethod("addAccount")
    public static class addAccount extends MethodHook {

        /**
         * Adds a new account with the specified type and options.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the account type,
         *             args[2] is the auth type, args[3] is required features, args[4] is addActivity,
         *             args[5] is the options Bundle
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().addAccount((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (String) args[2],
                    (String[]) args[3],
                    (boolean) args[4],
                    (Bundle) args[5]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code addAccountAsUser} to add a new account
     * for a specific user in the virtual account manager.
     */
    @ProxyMethod("addAccountAsUser")
    public static class addAccountAsUser extends MethodHook {

        /**
         * Adds a new account as a specific user with the given options.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the account type,
         *             args[2] is the auth type, args[3] is required features, args[4] is addActivity,
         *             args[5] is the options Bundle
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().addAccountAsUser((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (String) args[2],
                    (String[]) args[3],
                    (boolean) args[4],
                    (Bundle) args[5]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code updateCredentials} to update account
     * credentials in the virtual account manager.
     */
    @ProxyMethod("updateCredentials")
    public static class updateCredentials extends MethodHook {

        /**
         * Updates credentials for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the Account, args[2] is the auth type,
         *             args[3] is requireActivity, args[4] is the options Bundle
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().updateCredentials((IAccountManagerResponse) args[0],
                    (Account) args[1],
                    (String) args[2],
                    (boolean) args[3],
                    (Bundle) args[4]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code editProperties} to edit authenticator
     * properties in the virtual account manager.
     */
    @ProxyMethod("editProperties")
    public static class editProperties extends MethodHook {

        /**
         * Edits properties for the specified authenticator type.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the account type, args[2] is requireActivity
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().editProperties((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (boolean) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code confirmCredentialsAsUser} to confirm
     * account credentials for a user in the virtual account manager.
     */
    @ProxyMethod("confirmCredentialsAsUser")
    public static class confirmCredentialsAsUser extends MethodHook {

        /**
         * Confirms credentials for the specified account and user.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the Account, args[2] is the options Bundle,
         *             args[3] is expectActivityLaunch
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().confirmCredentialsAsUser((IAccountManagerResponse) args[0],
                    (Account) args[1],
                    (Bundle) args[2],
                    (boolean) args[3]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code accountAuthenticated} to mark
     * an account as authenticated in the virtual account manager.
     */
    @ProxyMethod("accountAuthenticated")
    public static class accountAuthenticated extends MethodHook {

        /**
         * Marks the specified account as authenticated.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account that was authenticated
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().accountAuthenticated((Account) args[0]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getAuthTokenLabel} to retrieve the
     * human-readable label for an auth token type.
     */
    @ProxyMethod("getAuthTokenLabel")
    public static class getAuthTokenLabel extends MethodHook {

        /**
         * Gets the auth token label for the specified type.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IAccountManagerResponse, args[1] is the account type, args[2] is the auth token type
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAuthTokenLabel((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (String) args[2]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getPackagesAndVisibilityForAccount} to retrieve
     * the packages and visibility mappings for an account.
     */
    @ProxyMethod("getPackagesAndVisibilityForAccount")
    public static class getPackagesAndVisibilityForAccount extends MethodHook {

        /**
         * Gets the packages and visibility for the specified account.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account
         * @return a Map of package names to visibility values
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getPackagesAndVisibilityForAccount((Account) args[0]);
        }
    }

    /**
     * Hook that intercepts {@code addAccountExplicitlyWithVisibility} to add
     * an account with explicit visibility settings.
     */
    @ProxyMethod("addAccountExplicitlyWithVisibility")
    public static class addAccountExplicitlyWithVisibility extends MethodHook {

        /**
         * Adds an account explicitly with specified package visibility.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the password, args[2] is the user data Bundle,
         *             args[3] is the visibility Map
         * @return true if the account was added successfully
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().addAccountExplicitlyWithVisibility((Account) args[0],
                    (String) args[1],
                    (Bundle) args[2],
                    (Map) args[3]
            );
        }
    }

    /**
     * Hook that intercepts {@code setAccountVisibility} to set the visibility
     * of an account for a specific package.
     */
    @ProxyMethod("setAccountVisibility")
    public static class setAccountVisibility extends MethodHook {

        /**
         * Sets the visibility of the account for the specified package.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the package name, args[2] is the visibility value
         * @return true if the visibility was set successfully
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().setAccountVisibility((Account) args[0],
                    (String) args[1],
                    (int) args[2]
            );
        }
    }

    /**
     * Hook that intercepts {@code getAccountVisibility} to get the visibility
     * of an account for a specific package.
     */
    @ProxyMethod("getAccountVisibility")
    public static class getAccountVisibility extends MethodHook {

        /**
         * Gets the visibility of the account for the specified package.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Account, args[1] is the package name
         * @return the visibility integer constant
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountVisibility((Account) args[0],
                    (String) args[1]
            );
        }
    }

    /**
     * Hook that intercepts {@code getAccountsAndVisibilityForPackage} to retrieve
     * accounts and their visibility for a specific package.
     */
    @ProxyMethod("getAccountsAndVisibilityForPackage")
    public static class getAccountsAndVisibilityForPackage extends MethodHook {

        /**
         * Gets accounts and their visibility mappings for the specified package.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the package name, args[1] is the account type
         * @return a Map of Account to visibility values
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsAndVisibilityForPackage((String) args[0],
                    (String) args[1]
            );
        }
    }

    /**
     * Hook that intercepts {@code registerAccountListener} to register
     * an account change listener in the virtual account manager.
     */
    @ProxyMethod("registerAccountListener")
    public static class registerAccountListener extends MethodHook {

        /**
         * Registers an account listener for the specified account types.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the account types array, args[1] is the calling package
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().registerAccountListener((String[]) args[0],
                    (String) args[1]
            );
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code unregisterAccountListener} to unregister
     * an account change listener from the virtual account manager.
     */
    @ProxyMethod("unregisterAccountListener")
    public static class unregisterAccountListener extends MethodHook {

        /**
         * Unregisters an account listener for the specified account types.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the account types array, args[1] is the calling package
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().unregisterAccountListener((String[]) args[0],
                    (String) args[1]
            );
            return 0;
        }
    }
}
