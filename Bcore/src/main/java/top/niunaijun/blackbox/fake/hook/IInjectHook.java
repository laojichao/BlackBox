package top.niunaijun.blackbox.fake.hook;

/**
 * Interface for components that can inject themselves as hooks into the system
 * and verify whether their hook environment is still intact.
 */
public interface IInjectHook {
    /**
     * Injects this hook into the target system (e.g., replaces a system service
     * or instrumentation instance).
     */
    void injectHook();

    /**
     * Checks whether the hook environment has been tampered with.
     *
     * @return true if the environment is bad and re-injection is needed
     */
    boolean isBadEnv();
}
