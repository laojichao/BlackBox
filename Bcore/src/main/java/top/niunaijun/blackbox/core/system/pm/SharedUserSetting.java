/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.niunaijun.blackbox.core.system.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.AtomicFile;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Settings data for a particular shared user ID within the virtual environment.
 * <p>
 * Multiple virtual packages can share a single Linux UID by declaring the same shared user ID
 * in their manifests. This class stores the mapping from a shared user name to its assigned UID
 * and provides persistence via Parcel serialization to disk.
 * <p>
 * A static map ({@link #sSharedUsers}) holds all active shared user entries and is serialized
 * atomically using {@link AtomicFile}.
 */
public final class SharedUserSetting implements Parcelable {
    /** Global registry of all shared user settings, keyed by shared user name. */
    public static final Map<String, SharedUserSetting> sSharedUsers = new HashMap<>();

    String name;
    int userId;

    // The lowest targetSdkVersion of all apps in the sharedUserSetting, used to assign seinfo so
    // that all apps within the sharedUser run in the same selinux context.
    int seInfoTargetSdkVersion;


    /**
     * Creates a new shared user setting with the given name.
     *
     * @param _name the shared user identifier (e.g., from android:sharedUserId in the manifest)
     */
    SharedUserSetting(String _name) {
        name = _name;
    }

    @Override
    public String toString() {
        return "SharedUserSetting{" + Integer.toHexString(System.identityHashCode(this)) + " "
                + name + "/" + userId + "}";
    }

    /**
     * Persists the current shared user registry to disk using atomic file writes.
     */
    public static void saveSharedUsers() {
        Parcel parcel = Parcel.obtain();
        FileOutputStream fileOutputStream = null;
        AtomicFile atomicFile = new AtomicFile(BEnvironment.getSharedUserConf());
        try {
            parcel.writeMap(sSharedUsers);

            fileOutputStream = atomicFile.startWrite();
            FileUtils.writeParcelToOutput(parcel, fileOutputStream);
            atomicFile.finishWrite(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            atomicFile.failWrite(fileOutputStream);
        } finally {
            parcel.recycle();
        }
    }

    /**
     * Loads shared user settings from disk into the static {@link #sSharedUsers} map.
     */
    public static void loadSharedUsers() {
        Parcel parcel = Parcel.obtain();
        try {
            byte[] sharedUsersBytes = FileUtils.toByteArray(BEnvironment.getSharedUserConf());
            parcel.unmarshall(sharedUsersBytes, 0, sharedUsersBytes.length);
            parcel.setDataPosition(0);

            HashMap hashMap = parcel.readHashMap(SharedUserSetting.class.getClassLoader());
            synchronized (sSharedUsers) {
                sSharedUsers.clear();
                sSharedUsers.putAll(hashMap);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            parcel.recycle();
        }
    }

    /**
     * Returns the Parcelable contents descriptor (always 0 for this class).
     *
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes this setting's fields to a Parcel for serialization.
     *
     * @param dest  the Parcel to write to
     * @param flags additional flags for writing
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.userId);
        dest.writeInt(this.seInfoTargetSdkVersion);
    }

    /**
     * Reads fields from a Parcel into this existing instance.
     *
     * @param source the Parcel to read from
     */
    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.userId = source.readInt();
    }

    /**
     * Restores a SharedUserSetting from a Parcel.
     *
     * @param in the Parcel to read from
     */
    protected SharedUserSetting(Parcel in) {
        this.name = in.readString();
        this.userId = in.readInt();
    }

    public static final Parcelable.Creator<SharedUserSetting> CREATOR = new Parcelable.Creator<SharedUserSetting>() {
        @Override
        public SharedUserSetting createFromParcel(Parcel source) {
            return new SharedUserSetting(source);
        }

        @Override
        public SharedUserSetting[] newArray(int size) {
            return new SharedUserSetting[size];
        }
    };
}
