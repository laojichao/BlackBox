package top.niunaijun.blackbox.core.system.user;

import android.os.Parcel;
import android.os.RemoteException;

import androidx.core.util.AtomicFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Virtual user manager service for the BlackBox virtual environment.
 * <p>
 * Manages the lifecycle of virtual users -- creation, enumeration, deletion, and
 * persistence. Each virtual user has an isolated data directory and external storage
 * path. User data is serialized to disk via {@link Parcel} and restored on startup.
 * Deleting a user also removes all packages installed under that user via
 * {@link BPackageManagerService}.
 * </p>
 */
public class BUserManagerService extends IBUserManagerService.Stub implements ISystemService {
    private static BUserManagerService sService = new BUserManagerService();
    /** In-memory map of user ID to user info, guarded by {@link #mUserLock}. */
    public final HashMap<Integer, BUserInfo> mUsers = new HashMap<>();
    /** Lock object synchronizing user creation and deletion operations. */
    public final Object mUserLock = new Object();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global {@link BUserManagerService} instance
     */
    public static BUserManagerService get() {
        return sService;
    }

    /**
     * Called when the system is ready. Loads persisted user information from disk.
     */
    @Override
    public void systemReady() {
        scanUserL();
    }

    /**
     * Retrieves the user information for a given virtual user ID.
     *
     * @param userId the virtual user ID to look up
     * @return the {@link BUserInfo} for the user, or {@code null} if no such user exists
     */
    @Override
    public BUserInfo getUserInfo(int userId) {
        synchronized (mUserLock) {
            return mUsers.get(userId);
        }
    }

    /**
     * Checks whether a virtual user with the given ID exists.
     *
     * @param userId the virtual user ID to check
     * @return {@code true} if the user exists, {@code false} otherwise
     */
    @Override
    public boolean exists(int userId) {
        synchronized (mUsers) {
            return mUsers.get(userId) != null;
        }
    }

    /**
     * Creates a new virtual user with the specified ID if one does not already exist.
     * <p>
     * If a user with the given ID already exists, the existing user info is returned.
     * Otherwise a new {@link BUserInfo} with {@link BUserStatus#ENABLE} status is
     * created, persisted to disk, and returned.
     * </p>
     *
     * @param userId the virtual user ID to create
     * @return the {@link BUserInfo} for the created or existing user
     * @throws RemoteException if remote binder communication fails
     */
    @Override
    public BUserInfo createUser(int userId) throws RemoteException {
        synchronized (mUserLock) {
            if (exists(userId)) {
                return getUserInfo(userId);
            }
            return createUserLocked(userId);
        }
    }

    /**
     * Returns a list of all valid (non-negative ID) virtual users.
     *
     * @return a list of {@link BUserInfo} objects for all active virtual users
     */
    @Override
    public List<BUserInfo> getUsers() {
        synchronized (mUsers) {
            ArrayList<BUserInfo> bUsers = new ArrayList<>();
            for (BUserInfo value : mUsers.values()) {
                if (value.id >= 0) {
                    bUsers.add(value);
                }
            }
            return bUsers;
        }
    }

    /**
     * Returns a list of all virtual users, including those with internal or negative IDs.
     *
     * @return a list of all {@link BUserInfo} instances in the user map
     */
    public List<BUserInfo> getAllUsers() {
        synchronized (mUsers) {
            return new ArrayList<>(mUsers.values());
        }
    }

    /**
     * Deletes a virtual user and all associated data.
     * <p>
     * Removes the user from the in-memory map, deletes all packages installed under
     * this user via {@link BPackageManagerService}, cleans up the user's internal and
     * external storage directories, and persists the updated user list to disk.
     * </p>
     *
     * @param userId the virtual user ID to delete
     * @throws RemoteException if remote binder communication fails
     */
    @Override
    public void deleteUser(int userId) throws RemoteException {
        synchronized (mUserLock) {
            synchronized (mUsers) {
                BPackageManagerService.get().deleteUser(userId);

                mUsers.remove(userId);
                saveUserInfoLocked();
                FileUtils.deleteDir(BEnvironment.getUserDir(userId));
                FileUtils.deleteDir(BEnvironment.getExternalUserDir(userId));
            }
        }
    }

    private BUserInfo createUserLocked(int userId) {
        BUserInfo bUserInfo = new BUserInfo();
        bUserInfo.id = userId;
        bUserInfo.status = BUserStatus.ENABLE;
        mUsers.put(userId, bUserInfo);
        synchronized (mUsers) {
            saveUserInfoLocked();
        }
        return bUserInfo;
    }

    private void saveUserInfoLocked() {
        Parcel parcel = Parcel.obtain();
        AtomicFile atomicFile = new AtomicFile(BEnvironment.getUserInfoConf());
        FileOutputStream fileOutputStream = null;
        try {
            ArrayList<BUserInfo> bUsers = new ArrayList<>(mUsers.values());
            parcel.writeTypedList(bUsers);
            try {
                fileOutputStream = atomicFile.startWrite();
                FileUtils.writeParcelToOutput(parcel, fileOutputStream);
                atomicFile.finishWrite(fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
                atomicFile.failWrite(fileOutputStream);
            } finally {
                CloseUtils.close(fileOutputStream);
            }
        } finally {
            parcel.recycle();
        }
    }

    private void scanUserL() {
        synchronized (mUserLock) {
            Parcel parcel = Parcel.obtain();
            InputStream is = null;
            try {
                File userInfoConf = BEnvironment.getUserInfoConf();
                if (!userInfoConf.exists()) {
                    return;
                }
                is = new FileInputStream(BEnvironment.getUserInfoConf());
                byte[] bytes = FileUtils.toByteArray(is);
                parcel.unmarshall(bytes, 0, bytes.length);
                parcel.setDataPosition(0);

                ArrayList<BUserInfo> loadUsers = parcel.createTypedArrayList(BUserInfo.CREATOR);
                if (loadUsers == null)
                    return;
                synchronized (mUsers) {
                    mUsers.clear();
                    for (BUserInfo loadUser : loadUsers) {
                        mUsers.put(loadUser.id, loadUser);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                parcel.recycle();
                CloseUtils.close(is);
            }
        }
    }
}