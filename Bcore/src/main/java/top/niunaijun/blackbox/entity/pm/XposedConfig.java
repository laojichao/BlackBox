package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Parcelable configuration for the Xposed framework within the BlackBox virtual environment.
 * <p>
 * Stores the global Xposed enable/disable state and a per-module enable/disable map
 * keyed by module package name. This configuration controls which Xposed modules are
 * active for a given virtual user.
 * </p>
 *
 * @see InstalledModule
 */
public class XposedConfig implements Parcelable {
    /** Whether the Xposed framework is globally enabled in the virtual environment. */
    public boolean enable;

    /**
     * Map of Xposed module package names to their enabled state.
     * A value of {@code true} means the module is enabled.
     */
    public Map<String, Boolean> moduleState = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.moduleState.size());
        for (Map.Entry<String, Boolean> entry : this.moduleState.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    /**
     * Default constructor for creating an {@link XposedConfig} with Xposed disabled
     * and an empty module state map.
     */
    public XposedConfig() {
    }

    /**
     * Constructs an {@link XposedConfig} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    public XposedConfig(Parcel in) {
        this.enable = in.readByte() != 0;
        int mModuleStateSize = in.readInt();
        this.moduleState = new HashMap<String, Boolean>(mModuleStateSize);
        for (int i = 0; i < mModuleStateSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.moduleState.put(key, value);
        }
    }

    public static final Parcelable.Creator<XposedConfig> CREATOR = new Parcelable.Creator<XposedConfig>() {
        @Override
        public XposedConfig createFromParcel(Parcel source) {
            return new XposedConfig(source);
        }

        @Override
        public XposedConfig[] newArray(int size) {
            return new XposedConfig[size];
        }
    };
}
