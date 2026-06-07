package top.niunaijun.blackbox.core.system.am;

import java.util.Objects;

/**
 * Represents a pending intent sender record within the virtual environment.
 * Virtualizes the Android {@link android.app.PendingIntent} tracking by storing the UID and
 * package name associated with a pending intent's creator.
 *
 * <p>Used by {@link BActivityManagerService} to resolve the origin of intent sender calls
 * (e.g., {@code getPackageForIntentSender} and {@code getUidForIntentSender}).</p>
 */
public class PendingIntentRecord {
    /** The UID of the process that created the pending intent. */
    public int uid;
    /** The package name of the app that created the pending intent. */
    public String packageName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingIntentRecord)) return false;
        PendingIntentRecord that = (PendingIntentRecord) o;
        return uid == that.uid &&
                Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, packageName);
    }
}
