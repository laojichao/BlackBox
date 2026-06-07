package black.dalvik.system;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden dalvik.system.VMRuntime.
 * Provides access to the Dalvik/ART VM runtime for ABI detection and SDK targeting.
 */
@BClassName("dalvik.system.VMRuntime")
public interface VMRuntime {
    /** Get the name of the current instruction set (e.g. "arm64", "x86"). */
    @BStaticMethod
    String getCurrentInstructionSet();

    /** Get the singleton VMRuntime instance. */
    @BStaticMethod
    Object getRuntime();

    /** Check if the given ABI name is a 64-bit architecture. */
    @BStaticMethod
    Boolean is64BitAbi(String String0);

    /** Check if the current runtime is 64-bit. */
    @BMethod
    Boolean is64Bit();

    /** Check if the VM is running in debuggable mode. */
    @BMethod
    Boolean isJavaDebuggable();

    /** Set the target SDK version for runtime behavior changes. */
    @BMethod
    void setTargetSdkVersion(int int0);
}
