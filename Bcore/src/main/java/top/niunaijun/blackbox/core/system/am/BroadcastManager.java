package top.niunaijun.blackbox.core.system.am;

import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.pm.BPackage;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.core.system.pm.PackageMonitor;
import top.niunaijun.blackbox.entity.am.PendingResultData;
import top.niunaijun.blackbox.proxy.ProxyBroadcastReceiver;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Manages broadcast receiver registration and dispatching within the virtual environment.
 * Virtualizes the Android broadcast system by registering proxy {@link BroadcastReceiver} instances
 * on the host that forward broadcasts to virtual app receivers.
 *
 * <p>Handles broadcast timeout enforcement (default 9 seconds) and automatically unregisters
 * receivers when their package is uninstalled. Implements {@link PackageMonitor} to track
 * package install/uninstall events.</p>
 */
public class BroadcastManager implements PackageMonitor {
    public static final String TAG = "BroadcastManager";

    public static final int TIMEOUT = 9000;

    public static final int MSG_TIME_OUT = 1;

    private static BroadcastManager sBroadcastManager;

    private final BActivityManagerService mAms;
    private final BPackageManagerService mPms;
    private final Map<String, List<BroadcastReceiver>> mReceivers = new HashMap<>();
    private final Map<String, PendingResultData> mReceiversData = new HashMap<>();

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIME_OUT:
                    try {
                        PendingResultData data = (PendingResultData) msg.obj;
                        data.build().finish();
                        Slog.d(TAG, "Timeout Receiver: " + data);
                    } catch (Throwable ignore) {
                    }
                    break;
            }
        }
    };

    /**
     * Creates or returns the singleton {@link BroadcastManager} instance.
     *
     * @param ams the virtual activity manager service
     * @param pms the virtual package manager service
     * @return the singleton BroadcastManager instance
     */
    public static BroadcastManager startSystem(BActivityManagerService ams, BPackageManagerService pms) {
        if (sBroadcastManager == null) {
            synchronized (BroadcastManager.class) {
                if (sBroadcastManager == null) {
                    sBroadcastManager = new BroadcastManager(ams, pms);
                }
            }
        }
        return sBroadcastManager;
    }

    /**
     * Constructs a new BroadcastManager with references to the activity and package managers.
     *
     * @param ams the virtual activity manager service
     * @param pms the virtual package manager service
     */
    public BroadcastManager(BActivityManagerService ams, BPackageManagerService pms) {
        mAms = ams;
        mPms = pms;
    }

    /**
     * Initializes the broadcast manager by registering proxy receivers for all installed
     * packages and subscribing to package install/uninstall events.
     */
    public void startup() {
        mPms.addPackageMonitor(this);
        List<BPackageSettings> bPackageSettings = mPms.getBPackageSettings();
        for (BPackageSettings bPackageSetting : bPackageSettings) {
            BPackage bPackage = bPackageSetting.pkg;
            registerPackage(bPackage);
        }
    }

    private void registerPackage(BPackage bPackage) {
        synchronized (mReceivers) {
            Slog.d(TAG, "register: " + bPackage.packageName + ", size: " + bPackage.receivers.size());
            for (BPackage.Activity receiver : bPackage.receivers) {
                List<BPackage.ActivityIntentInfo> intents = receiver.intents;
                for (BPackage.ActivityIntentInfo intent : intents) {
                    ProxyBroadcastReceiver proxyBroadcastReceiver = new ProxyBroadcastReceiver();
                    BlackBoxCore.getContext().registerReceiver(proxyBroadcastReceiver, intent.intentFilter);
                    addReceiver(bPackage.packageName, proxyBroadcastReceiver);
                }
            }
        }
    }

    private void addReceiver(String packageName, BroadcastReceiver receiver) {
        List<BroadcastReceiver> broadcastReceivers = mReceivers.get(packageName);
        if (broadcastReceivers == null) {
            broadcastReceivers = new ArrayList<>();
            mReceivers.put(packageName, broadcastReceivers);
        }
        broadcastReceivers.add(receiver);
    }

    /**
     * Registers a pending broadcast and starts a timeout timer. If the broadcast is not
     * finished within {@link #TIMEOUT} milliseconds, it is automatically completed.
     *
     * @param pendingResultData the pending result data for the broadcast
     */
    public void sendBroadcast(PendingResultData pendingResultData) {
        synchronized (mReceiversData) {
            // Slog.d(TAG, "sendBroadcast: " + pendingResultData);
            mReceiversData.put(pendingResultData.mBToken, pendingResultData);
            Message obtain = Message.obtain(mHandler, MSG_TIME_OUT, pendingResultData);
            mHandler.sendMessageDelayed(obtain, TIMEOUT);
        }
    }

    /**
     * Marks a broadcast as finished and cancels its timeout timer.
     *
     * @param data the pending result data of the completed broadcast
     */
    public void finishBroadcast(PendingResultData data) {
        synchronized (mReceiversData) {
            // Slog.d(TAG, "finishBroadcast: " + data);
            mHandler.removeMessages(MSG_TIME_OUT, mReceiversData.get(data.mBToken));
        }
    }

    /**
     * Handles package uninstallation by unregistering all proxy broadcast receivers for the package.
     *
     * @param packageName the uninstalled package name
     * @param removeApp   true if the app is being fully removed
     * @param userId      the virtual user ID
     */
    @Override
    public void onPackageUninstalled(String packageName, boolean removeApp, int userId) {
        if (removeApp) {
            synchronized (mReceivers) {
                List<BroadcastReceiver> broadcastReceivers = mReceivers.get(packageName);
                if (broadcastReceivers != null) {
                    Slog.d(TAG, "unregisterReceiver Package: " + packageName + ", size: " + broadcastReceivers.size());
                    for (BroadcastReceiver broadcastReceiver : broadcastReceivers) {
                        try {
                            BlackBoxCore.getContext().unregisterReceiver(broadcastReceiver);
                        } catch (Throwable ignored) {
                        }
                    }
                }
                mReceivers.remove(packageName);
            }
        }
    }

    /**
     * Handles package installation by re-registering proxy broadcast receivers for the package.
     *
     * @param packageName the installed package name
     * @param userId      the virtual user ID
     */
    @Override
    public void onPackageInstalled(String packageName, int userId) {
        synchronized (mReceivers) {
            mReceivers.remove(packageName);
            BPackageSettings bPackageSetting = mPms.getBPackageSetting(packageName);
            if (bPackageSetting != null) {
                registerPackage(bPackageSetting.pkg);
            }
        }
    }
}
