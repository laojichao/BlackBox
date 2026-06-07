package top.niunaijun.blackbox.fake.service;

import android.os.IInterface;
import android.os.storage.StorageVolume;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.os.mount.BRIMountServiceStub;
import black.android.os.storage.BRIStorageManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Proxy for the Android storage/mount system service.
 * <p>
 * Intercepts storage volume list queries and directory creation operations,
 * providing virtual storage paths specific to the current user in the virtual
 * environment. Handles API differences between Oreo+ and older Android versions.
 */
public class IStorageManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by acquiring the real mount/storage binder service.
     */
    public IStorageManagerProxy() {
        super(BRServiceManager.get().getService("mount"));
    }

    /**
     * Returns the underlying storage manager service interface, selecting the
     * appropriate implementation based on the Android API level.
     *
     * @return the real storage/mount service binder interface
     */
    @Override
    protected Object getWho() {
        IInterface mount;
        if (BuildCompat.isOreo()) {
            mount = BRIStorageManagerStub.get().asInterface(BRServiceManager.get().getService("mount"));
        } else {
            mount = BRIMountServiceStub.get().asInterface(BRServiceManager.get().getService("mount"));
        }
        return mount;
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("mount");
    }

    /**
     * Checks whether the current environment is invalid for this proxy.
     *
     * @return always {@code false}, indicating the environment is always valid
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Hook that intercepts {@code getVolumeList} and returns storage volumes
     * specific to the virtual user, falling back to the real implementation on error.
     */
    @ProxyMethod("getVolumeList")
    public static class GetVolumeList extends MethodHook {
        /**
         * Retrieves the volume list for the virtual user environment.
         *
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments (uid, packageName, flags)
         * @return an array of {@link StorageVolume} for the virtual user,
         *         or the original method result as fallback
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args == null) {
                StorageVolume[] volumeList = BlackBoxCore.getBStorageManager().getVolumeList(BActivityThread.getBUid(), null, 0, BActivityThread.getUserId());
                if (volumeList == null) {
                    return method.invoke(who, args);
                }
                return volumeList;
            }
            try {
                int uid = (int) args[0];
                String packageName = (String) args[1];
                int flags = (int) args[2];
                StorageVolume[] volumeList = BlackBoxCore.getBStorageManager().getVolumeList(uid, packageName, flags, BActivityThread.getUserId());
                if (volumeList == null) {
                    return method.invoke(who, args);
                }
                return volumeList;
            } catch (Throwable t) {
                return method.invoke(who, args);
            }
        }
    }

    /**
     * Hook that intercepts {@code mkdirs} and returns 0 (success) without
     * actually creating directories in the real filesystem.
     */
    @ProxyMethod("mkdirs")
    public static class mkdirs extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always 0
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
