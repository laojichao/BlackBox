package top.niunaijun.blackbox.core.system.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.AtomicFile;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Persistent settings for an installed package within the virtual environment.
 *
 * <p>Tracks the parsed {@link BPackage}, the assigned application ID, installation
 * options, and per-user state ({@link BPackageUserState}). Provides thread-safe
 * read/write access to user-level flags (installed, stopped, hidden) and
 * serializes itself to disk via {@link Parcel} through an {@link AtomicFile}.</p>
 *
 * <p>Xposed modules are treated specially: when the XP framework is enabled and the
 * module is activated, {@link #readUserState(int)} forces the installed flag to true
 * for all users.</p>
 *
 * @see BPackage
 * @see BPackageUserState
 * @see BXposedManagerService
 */
public class BPackageSettings implements Parcelable {
    /** The parsed package data for this application. */
    public BPackage pkg;
    /** The unique application ID (UID) assigned to this package. */
    public int appId;
    /** Installation options and flags used when this package was installed. */
    public InstallOption installOption;
    /** Map from virtual user ID to that user's state for this package. */
    public Map<Integer, BPackageUserState> userState = new HashMap<>();
    /** Default user state returned when no explicit state exists for a user. */
    static final BPackageUserState DEFAULT_USER_STATE = new BPackageUserState();

    /**
     * Constructs an empty BPackageSettings. Fields must be populated before use.
     */
    public BPackageSettings() {
    }

    /**
     * Returns a snapshot list of all per-user states for this package.
     *
     * @return a new list of all BPackageUserState values
     */
    public List<BPackageUserState> getUserState() {
        return new ArrayList<>(userState.values());
    }

    /**
     * Returns the list of virtual user IDs that have a state entry for this package.
     *
     * @return a new list of user ID integers
     */
    public List<Integer> getUserIds() {
        return new ArrayList<>(userState.keySet());
    }

    /**
     * Sets the installed flag for a specific virtual user.
     *
     * @param inst   true to mark as installed, false otherwise
     * @param userId the virtual user ID
     */
    public void setInstalled(boolean inst, int userId) {
        modifyUserState(userId).installed = inst;
    }

    /**
     * Returns whether this package is installed for a specific virtual user.
     * Xposed modules may return true even without an explicit user state entry.
     *
     * @param userId the virtual user ID
     * @return true if installed for the given user, false otherwise
     */
    public boolean getInstalled(int userId) {
        return readUserState(userId).installed;
    }

    /**
     * Returns whether this package is in the stopped state for a specific virtual user.
     *
     * @param userId the virtual user ID
     * @return true if stopped, false otherwise
     */
    public boolean getStopped(int userId) {
        return readUserState(userId).stopped;
    }

    /**
     * Sets the stopped flag for a specific virtual user.
     *
     * @param stop   true to mark as stopped, false otherwise
     * @param userId the virtual user ID
     */
    public void setStopped(boolean stop, int userId) {
        modifyUserState(userId).stopped = stop;
    }

    /**
     * Returns whether this package is hidden for a specific virtual user.
     *
     * @param userId the virtual user ID
     * @return true if hidden, false otherwise
     */
    public boolean getHidden(int userId) {
        return readUserState(userId).hidden;
    }

    /**
     * Sets the hidden flag for a specific virtual user.
     *
     * @param hidden true to hide the package, false to make it visible
     * @param userId the virtual user ID
     */
    public void setHidden(boolean hidden, int userId) {
        modifyUserState(userId).hidden = hidden;
    }

    /**
     * Removes the per-user state entry for a specific virtual user.
     *
     * @param userId the virtual user ID whose state to remove
     */
    public void removeUser(int userId) {
        userState.remove(userId);
    }

    /**
     * Returns a read-only copy of the user state for a specific virtual user.
     * For Xposed modules with the XP framework enabled, forces the installed flag to true.
     * For {@link BUserHandle#USER_ALL}, always returns installed as true.
     *
     * @param userId the virtual user ID
     * @return a defensive copy of the user state (never null)
     */
    public BPackageUserState readUserState(int userId) {
        BPackageUserState state = userState.get(userId);
        if (state == null) {
            state = new BPackageUserState();
        }
        state = new BPackageUserState(state);
        // xp模块所有用户可见、如果开启的话
        if (installOption.isFlag(InstallOption.FLAG_XPOSED) &&
                BXposedManagerService.get().isModuleEnable(pkg.packageName) &&
                BXposedManagerService.get().isXPEnable()) {
            state.installed = true;
        }
        if (userId == BUserHandle.USER_ALL) {
            state.installed = true;
        }
        return state;
    }

    private BPackageUserState modifyUserState(int userId) {
        BPackageUserState state = userState.get(userId);
        if (state == null) {
            state = new BPackageUserState();
            userState.put(userId, state);
        }
        return state;
    }

    /**
     * Persists this package settings to disk using an atomic file write.
     * Serializes via Parcel and writes to the package configuration file.
     *
     * @return true if the save succeeded, false on I/O error
     */
    public boolean save() {
        synchronized (this) {
            Parcel parcel = Parcel.obtain();
            AtomicFile atomicFile = new AtomicFile(BEnvironment.getPackageConf(pkg.packageName));
            FileOutputStream fileOutputStream = null;
            try {
                writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                fileOutputStream = atomicFile.startWrite();
                FileUtils.writeParcelToOutput(parcel, fileOutputStream);
                atomicFile.finishWrite(fileOutputStream);
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
                atomicFile.failWrite(fileOutputStream);
                return false;
            } finally {
                parcel.recycle();
                CloseUtils.close(fileOutputStream);
            }
        }
    }

    /**
     * Returns a bitmask indicating the set of special object types
     * marshaled by this Parcelable.
     *
     * @return 0, indicating no special objects
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens this BPackageSettings object into a Parcel, including the package,
     * app ID, install options, and all per-user state entries.
     *
     * @param dest  the Parcel in which the object should be written
     * @param flags additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pkg, flags);
        dest.writeInt(this.appId);
        dest.writeParcelable(this.installOption, flags);
        dest.writeInt(this.userState.size());
        for (Map.Entry<Integer, BPackageUserState> entry : this.userState.entrySet()) {
            dest.writeValue(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    /**
     * Restores a BPackageSettings from a previously serialized Parcel.
     *
     * @param in the Parcel containing serialized package settings data
     */
    protected BPackageSettings(Parcel in) {
        this.pkg = in.readParcelable(BPackage.class.getClassLoader());
        this.appId = in.readInt();
        this.installOption = in.readParcelable(InstallOption.class.getClassLoader());
        int userStateSize = in.readInt();
        this.userState = new HashMap<Integer, BPackageUserState>(userStateSize);
        for (int i = 0; i < userStateSize; i++) {
            Integer key = (Integer) in.readValue(Integer.class.getClassLoader());
            BPackageUserState value = in.readParcelable(BPackageUserState.class.getClassLoader());
            this.userState.put(key, value);
        }
    }

    /** Factory for creating BPackageSettings arrays and instances from Parcels. */
    public static final Creator<BPackageSettings> CREATOR = new Creator<BPackageSettings>() {
        @Override
        public BPackageSettings createFromParcel(Parcel source) {
            return new BPackageSettings(source);
        }

        @Override
        public BPackageSettings[] newArray(int size) {
            return new BPackageSettings[size];
        }
    };
}
