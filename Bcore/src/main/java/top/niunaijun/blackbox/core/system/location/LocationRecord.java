package top.niunaijun.blackbox.core.system.location;

/**
 * Associates a virtual package name with its owning virtual user ID for location tracking.
 * <p>
 * Used internally by {@link BLocationManagerService} to identify which package and user
 * a registered location listener belongs to, so the correct spoofed location can be
 * delivered when updates are dispatched.
 * </p>
 */
public class LocationRecord {
    /** The virtual package name whose location is being tracked. */
    public String packageName;
    /** The virtual user ID that owns the tracked package. */
    public int userId;

    /**
     * Creates a new location record linking a package to a virtual user.
     *
     * @param packageName the virtual package name
     * @param userId      the virtual user ID
     */
    public LocationRecord(String packageName, int userId) {
        this.packageName = packageName;
        this.userId = userId;
    }
}
