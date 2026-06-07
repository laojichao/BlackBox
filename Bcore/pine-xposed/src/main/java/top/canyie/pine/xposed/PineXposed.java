package top.canyie.pine.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.IXposedMod;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Entry point for the Pine-based Xposed compatibility layer.
 *
 * <p>This class is responsible for loading Xposed modules, dispatching load-package callbacks,
 * and managing the hook enable/disable state. It bridges the Pine hooking engine with the
 * Xposed module API, allowing standard Xposed modules to work on Pine-powered environments.
 *
 * <p>Modules are loaded by reading {@code assets/xposed_init} from the module APK, which lists
 * the fully qualified class names implementing {@link IXposedMod} sub-interfaces.
 */
public final class PineXposed {
    /** Log tag used for PineXposed messages. */
    public static final String TAG = "PineXposed";

    /**
     * When set to {@code true}, all method hook callbacks are skipped. This can be toggled at
     * runtime to temporarily disable hooking without unhooking individual methods.
     */
    public static boolean disableHooks = false;

    /**
     * When set to {@code true}, {@link IXposedHookZygoteInit#initZygote} callbacks are skipped
     * during module loading.
     */
    public static boolean disableZygoteInitCallbacks = false;

    private static ExtHandler sExtHandler;

    /**
     * Returns the currently registered extension handler, or {@code null} if none is set.
     *
     * @return the current {@link ExtHandler}, or {@code null}
     */
    public static ExtHandler getExtHandler() {
        return sExtHandler;
    }

    /**
     * Sets an extension handler that receives callbacks for each loaded {@link IXposedMod} instance.
     *
     * @param n the extension handler to register, or {@code null} to remove
     */
    public static void setExtHandler(ExtHandler n) {
        sExtHandler = n;
    }

    private static final CopyOnWriteSortedSet<XC_LoadPackage> sLoadedPackageCallbacks = new CopyOnWriteSortedSet<>();

    private PineXposed() {
    }

    /**
     * Loads an Xposed module from the given file path.
     *
     * @param module the absolute path to the module APK file
     */
    public static void loadModule(String module) {
        loadModule(new File(module));
    }

    /**
     * Loads an Xposed module from the given file.
     *
     * @param module the module APK file
     */
    public static void loadModule(File module) {
        loadModule(module, false);
    }

    /**
     * Loads an Xposed module from the given file, specifying whether the module starts with
     * the system server process.
     *
     * @param module the module APK file
     * @param startsSystemServer {@code true} if the module is loaded in the system server process
     */
    public static void loadModule(File module, boolean startsSystemServer) {
        if (!module.exists()) {
            Log.e(TAG, "  File " + module + " does not exist");
            return;
        }
        ClassLoader initCl = PineXposed.class.getClassLoader();
        String modulePath = module.getAbsolutePath();
        ModuleClassLoader mcl = new ModuleClassLoader(modulePath, initCl);
        loadOpenedModule(modulePath, mcl, startsSystemServer);
    }

    /**
     * Loads an already-opened module, reading entry point classes from {@code assets/xposed_init}
     * and instantiating them.
     *
     * <p>For each entry point class, this method:
     * <ol>
     *   <li>Validates that it implements an {@link IXposedMod} sub-interface</li>
     *   <li>Calls {@link IXposedHookZygoteInit#initZygote} if applicable</li>
     *   <li>Registers it as a load-package callback if applicable</li>
     *   <li>Passes it to the {@link ExtHandler} if one is set</li>
     * </ol>
     *
     * @param modulePath the absolute path to the module APK
     * @param mcl the class loader to use for loading the module's classes
     * @param startsSystemServer {@code true} if the module starts with the system server
     */
    public static void loadOpenedModule(String modulePath, ClassLoader mcl, boolean startsSystemServer) {
        if (!checkModule(mcl)) return;
        InputStream initIs;
        try {
            final String filename = "assets/xposed_init";
            if (mcl instanceof ModuleClassLoader) {
                // Fast and provided more error info
                URL url = ((ModuleClassLoader) mcl).findResource(filename);
                initIs = url != null ? url.openStream() : null;
            } else {
                initIs = mcl.getResourceAsStream(filename);
            }
            if (initIs == null) {
                Log.e(TAG, "  Failed to load module " + modulePath);
                Log.e(TAG, "  assets/xposed_init not found in the module APK");
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, "  Failed to load module " + modulePath);
            Log.e(TAG, "  Cannot open assets/xposed_init in the module APK", e);
            return;
        }

        BufferedReader xposedInitReader = new BufferedReader(new InputStreamReader(initIs));
        try {
            String className;
            while ((className = xposedInitReader.readLine()) != null) {
                className = className.trim();
                if (className.isEmpty() || className.startsWith("#"))
                    continue;

                try {
                    Class<?> c = mcl.loadClass(className);

                    if (!IXposedMod.class.isAssignableFrom(c)) {
                        Log.e(TAG, "    Cannot load callback class " + className + " in module " + modulePath + " :");
                        Log.e(TAG, "    This class doesn't implement any sub-interface of IXposedMod, skipping it");
                        continue;
                    }

                    IXposedMod callback = (IXposedMod) c.newInstance();

                    if (callback instanceof IXposedHookZygoteInit && !disableZygoteInitCallbacks) {
                        IXposedHookZygoteInit.StartupParam param = new IXposedHookZygoteInit.StartupParam();
                        param.modulePath = modulePath;
                        param.startsSystemServer = startsSystemServer;
                        ((IXposedHookZygoteInit) callback).initZygote(param);
                    }

                    if (callback instanceof IXposedHookLoadPackage)
                        hookLoadPackage((IXposedHookLoadPackage) callback);

                    ExtHandler extHandler = sExtHandler;
                    if (extHandler != null)
                        extHandler.handle(callback);
                } catch (Throwable e) {
                    Log.e(TAG, "    Failed to load class " + className + " from module " + modulePath + " :", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "  Failed to load module " + modulePath);
            Log.e(TAG, "  Cannot read assets/xposed_init in the module APK", e);
        } finally {
            closeQuietly(xposedInitReader);
        }
    }

    /**
     * Validates that a module class loader is safe to use. Checks for common issues such as
     * Android Studio Instant Run artifacts and Xposed API classes bundled into the module APK.
     *
     * @param mcl the module's class loader
     * @return {@code true} if the module passed validation and can be loaded
     */
    public static boolean checkModule(ClassLoader mcl) {
        boolean fastPath = mcl instanceof ModuleClassLoader;
        try {
            String name = "com.android.tools.fd.runtime.BootstrapApplication";
            Class<?> cls = fastPath ? ((ModuleClassLoader) mcl).findClass(name) : mcl.loadClass(name);
            if (cls != null) {
                Log.e(TAG, "  Cannot load module, please disable \"Instant Run\" in Android Studio.");
                return false;
            }
        } catch (ClassNotFoundException ignored) {
        }

        boolean conflict;
        if (fastPath) {
            try {
                conflict = ((ModuleClassLoader) mcl).findClass(XposedBridge.class.getName()) != null;
            } catch (ClassNotFoundException ignored) {
                conflict = false;
            }
        } else {
            try {
                conflict = mcl.loadClass(XposedBridge.class.getName()) != XposedBridge.class;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "  Cannot load module, XposedBridge is not available on the class loader", e);
                Log.e(TAG, "  Make sure you have set parent of the class loader");
                return false;
            }
        }
        if (conflict) {
            Log.e(TAG, "  Cannot load module:");
            Log.e(TAG, "  The Xposed API classes are compiled into the module's APK.");
            Log.e(TAG, "  This may cause strange issues and must be fixed by the module developer.");
            Log.e(TAG, "  For details, see: http://api.xposed.info/using.html");
            return false;
        }
        return true;
    }

    /**
     * Registers an {@link IXposedHookLoadPackage} callback to be invoked when any package loads.
     *
     * @param callback the load-package hook callback to register
     */
    public static void hookLoadPackage(IXposedHookLoadPackage callback) {
        sLoadedPackageCallbacks.add(new XC_LoadPackage.Wrapper(callback));
    }

    /**
     * Dispatches a load-package event to all registered callbacks. This method should be called
     * by the host application or framework when a new package is loaded.
     *
     * @param packageName the name of the package being loaded
     * @param processName the process in which the package runs
     * @param appInfo the application info of the loaded package
     * @param isFirstApp {@code true} if this is the main application for the process
     * @param classLoader the class loader used by the loaded package
     */
    public static void onPackageLoad(String packageName, String processName, ApplicationInfo appInfo,
                                     boolean isFirstApp, ClassLoader classLoader) {
        XC_LoadPackage.LoadPackageParam param = new XC_LoadPackage.LoadPackageParam(sLoadedPackageCallbacks);
        param.packageName = packageName;
        param.processName = processName;
        param.appInfo = appInfo;
        param.isFirstApplication = isFirstApp;
        param.classLoader = classLoader;
        XC_LoadPackage.callAll(param);
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
    }

    /**
     * Extension handler interface for receiving notifications about each loaded module entry point.
     * Implementations can perform additional processing on modules that do not implement the
     * standard {@link IXposedHookLoadPackage} or {@link IXposedHookZygoteInit} interfaces.
     */
    public interface ExtHandler {
        /**
         * Called for each {@link IXposedMod} instance created during module loading.
         *
         * @param callback the instantiated module entry point
         */
        void handle(IXposedMod callback);
    }
}
