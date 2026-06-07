package top.niunaijun.blackbox.fake.frameworks;

import android.os.RemoteException;

import java.util.Collections;
import java.util.List;

import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.user.BUserInfo;
import top.niunaijun.blackbox.core.system.user.IBUserManagerService;

/**
 * Client-side manager for virtual user operations. Provides a facade over
 * {@link IBUserManagerService} for creating, deleting, and listing virtual users.
 */
public class BUserManager extends BlackManager<IBUserManagerService> {
    private static final BUserManager sUserManager = new BUserManager();

    /**
     * Returns the singleton instance of {@link BUserManager}.
     *
     * @return the singleton BUserManager instance
     */
    public static BUserManager get() {
        return sUserManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.USER_MANAGER;
    }

    /**
     * Creates a new virtual user with the given user ID.
     *
     * @param userId the user ID to create
     * @return the BUserInfo for the created user, or null on failure
     */
    public BUserInfo createUser(int userId) {
        try {
            return getService().createUser(userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a virtual user by user ID.
     *
     * @param userId the user ID to delete
     */
    public void deleteUser(int userId) {
        try {
            getService().deleteUser(userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all virtual users.
     *
     * @return the list of BUserInfo, or an empty list on failure
     */
    public List<BUserInfo> getUsers() {
        try {
            return getService().getUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
