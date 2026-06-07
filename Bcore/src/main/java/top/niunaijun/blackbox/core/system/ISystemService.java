package top.niunaijun.blackbox.core.system;

/**
 * Lifecycle callback interface for BlackBox virtual engine subsystems.
 *
 * <p>Every core service (package manager, activity manager, user manager,
 * etc.) implements this interface.  {@link BlackBoxSystem#startup()}
 * invokes {@link #systemReady()} on each registered service once the
 * engine environment has been initialized.</p>
 */
public interface ISystemService {
    /**
     * Called once during engine startup after the virtual environment
     * directories have been created.  Implementations should perform
     * any one-time initialization they need (loading configs, registering
     * receivers, etc.).
     */
    void systemReady();
}
