package top.niunaijun.blackbox.utils.compat;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Compatibility utility for parsing the argument arrays of IActivityManager.startActivity calls.
 * <p>
 * The parameter list of {@code IActivityManager.startActivity()} differs between Android versions.
 * On Android 11 (R) and above, an additional {@code callingFeatureId} parameter was inserted.
 * This class maintains version-aware index mappings so that each parameter can be reliably
 * extracted from the raw argument array regardless of the Android version.
 */
public class StartActivityCompat {
    private static int index = 0;
    private static int appThreadIndex;
    private static int callingPageIndex;
    private static int callingFeatureIdIndex;
    private static int intentIndex;
    private static int resolvedTypeIndex;
    private static int resultToIndex;
    private static int resultWhoIndex;
    private static int requestCodeIndex;
    private static int flagsIndex;
    private static int profilerInfoIndex;
    private static int optionsIndex;

    static {
        if (BuildCompat.isR()) {
            appThreadIndex = index++;
            callingPageIndex = index++;
            callingFeatureIdIndex = index++;
            intentIndex = index++;
            resolvedTypeIndex = index++;
            resultToIndex = index++;
            resultWhoIndex = index++;
            requestCodeIndex = index++;
            flagsIndex = index++;
            profilerInfoIndex = index++;
            optionsIndex = index++;
        } else {
            appThreadIndex = index++;
            callingPageIndex = index++;
            intentIndex = index++;
            resolvedTypeIndex = index++;
            resultToIndex = index++;
            resultWhoIndex = index++;
            requestCodeIndex = index++;
            flagsIndex = index++;
            profilerInfoIndex = index++;
            optionsIndex = index++;
        }
    }

    /**
     * Extracts the IApplicationThread argument from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the IApplicationThread object, or null if the array is too short
     */
    public static Object getIApplicationThread(Object[] args) {
        if (args == null || args.length < appThreadIndex) {
            return null;
        }
        return args[appThreadIndex];
    }

    /**
     * Extracts the calling package name from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the calling package name, or null if the array is too short
     */
    public static String getCallingPackage(Object[] args) {
        if (args == null || args.length < callingPageIndex) {
            return null;
        }
        return (String) args[callingPageIndex];
    }

    /**
     * Extracts the Intent from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the Intent, or null if the array is too short
     */
    public static Intent getIntent(Object[] args) {
        if (args == null || args.length < intentIndex) {
            return null;
        }
        return (Intent) args[intentIndex];
    }

    /**
     * Extracts the resolved MIME type from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the resolved type string, or null if the array is too short
     */
    public static String getResolvedType(Object[] args) {
        if (args == null || args.length < resolvedTypeIndex) {
            return null;
        }
        return (String) args[resolvedTypeIndex];
    }

    /**
     * Extracts the resultTo IBinder token from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the IBinder token of the activity expecting a result, or null if the array is too short
     */
    public static IBinder getResultTo(Object[] args) {
        if (args == null || args.length < resultToIndex) {
            return null;
        }
        return (IBinder) args[resultToIndex];
    }

    /**
     * Extracts the resultWho string from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the resultWho identifier, or null if the array is too short
     */
    public static String getResultWho(Object[] args) {
        if (args == null || args.length < resultWhoIndex) {
            return null;
        }
        return (String) args[resultWhoIndex];
    }

    /**
     * Extracts the request code from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the request code, or -1 if the array is too short
     */
    public static int getRequestCode(Object[] args) {
        if (args == null || args.length < requestCodeIndex) {
            return -1;
        }
        return (int) args[requestCodeIndex];
    }

    /**
     * Extracts the start flags from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the flags bitmask, or -1 if the array is too short
     */
    public static int getFlags(Object[] args) {
        if (args == null || args.length < flagsIndex) {
            return -1;
        }
        return (int) args[flagsIndex];
    }

    /**
     * Extracts the ProfilerInfo object from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the ProfilerInfo object, or null if the array is too short
     */
    public static Object getProfilerInfo(Object[] args) {
        if (args == null || args.length < profilerInfoIndex) {
            return null;
        }
        return args[profilerInfoIndex];
    }

    /**
     * Extracts the activity options Bundle from the raw startActivity argument array.
     *
     * @param args the argument array from the IActivityManager.startActivity call
     * @return the options Bundle, or null if the array is too short
     */
    public static Bundle getOptions(Object[] args) {
        if (args == null || args.length < optionsIndex) {
            return null;
        }
        return (Bundle) args[optionsIndex];
    }


    /**
     * Returns the current index of the IApplicationThread argument in the parameter array.
     *
     * @return the appThread argument index
     */
    public static int getAppThreadIndex() {
        return appThreadIndex;
    }

    /**
     * Sets the index of the IApplicationThread argument in the parameter array.
     *
     * @param appThreadIndex the new appThread argument index
     */
    public static void setAppThreadIndex(int appThreadIndex) {
        StartActivityCompat.appThreadIndex = appThreadIndex;
    }

    /**
     * Returns the current index of the calling package argument in the parameter array.
     *
     * @return the callingPage argument index
     */
    public static int getCallingPageIndex() {
        return callingPageIndex;
    }

    /**
     * Sets the index of the calling package argument in the parameter array.
     *
     * @param callingPageIndex the new callingPage argument index
     */
    public static void setCallingPageIndex(int callingPageIndex) {
        StartActivityCompat.callingPageIndex = callingPageIndex;
    }

    /**
     * Returns the current index of the Intent argument in the parameter array.
     *
     * @return the intent argument index
     */
    public static int getIntentIndex() {
        return intentIndex;
    }

    /**
     * Sets the index of the Intent argument in the parameter array.
     *
     * @param intentIndex the new intent argument index
     */
    public static void setIntentIndex(int intentIndex) {
        StartActivityCompat.intentIndex = intentIndex;
    }

    /**
     * Returns the current index of the resolved type argument in the parameter array.
     *
     * @return the resolvedType argument index
     */
    public static int getResolvedTypeIndex() {
        return resolvedTypeIndex;
    }

    /**
     * Sets the index of the resolved type argument in the parameter array.
     *
     * @param resolvedTypeIndex the new resolvedType argument index
     */
    public static void setResolvedTypeIndex(int resolvedTypeIndex) {
        StartActivityCompat.resolvedTypeIndex = resolvedTypeIndex;
    }

    /**
     * Returns the current index of the resultTo IBinder argument in the parameter array.
     *
     * @return the resultTo argument index
     */
    public static int getResultToIndex() {
        return resultToIndex;
    }

    /**
     * Sets the index of the resultTo IBinder argument in the parameter array.
     *
     * @param resultToIndex the new resultTo argument index
     */
    public static void setResultToIndex(int resultToIndex) {
        StartActivityCompat.resultToIndex = resultToIndex;
    }

    /**
     * Returns the current index of the resultWho argument in the parameter array.
     *
     * @return the resultWho argument index
     */
    public static int getResultWhoIndex() {
        return resultWhoIndex;
    }

    /**
     * Sets the index of the resultWho argument in the parameter array.
     *
     * @param resultWhoIndex the new resultWho argument index
     */
    public static void setResultWhoIndex(int resultWhoIndex) {
        StartActivityCompat.resultWhoIndex = resultWhoIndex;
    }

    /**
     * Returns the current index of the request code argument in the parameter array.
     *
     * @return the requestCode argument index
     */
    public static int getRequestCodeIndex() {
        return requestCodeIndex;
    }

    /**
     * Sets the index of the request code argument in the parameter array.
     *
     * @param requestCodeIndex the new requestCode argument index
     */
    public static void setRequestCodeIndex(int requestCodeIndex) {
        StartActivityCompat.requestCodeIndex = requestCodeIndex;
    }

    /**
     * Returns the current index of the flags argument in the parameter array.
     *
     * @return the flags argument index
     */
    public static int getFlagsIndex() {
        return flagsIndex;
    }

    /**
     * Sets the index of the flags argument in the parameter array.
     *
     * @param flagsIndex the new flags argument index
     */
    public static void setFlagsIndex(int flagsIndex) {
        StartActivityCompat.flagsIndex = flagsIndex;
    }

    /**
     * Returns the current index of the ProfilerInfo argument in the parameter array.
     *
     * @return the profilerInfo argument index
     */
    public static int getProfilerInfoIndex() {
        return profilerInfoIndex;
    }

    /**
     * Sets the index of the ProfilerInfo argument in the parameter array.
     *
     * @param profilerInfoIndex the new profilerInfo argument index
     */
    public static void setProfilerInfoIndex(int profilerInfoIndex) {
        StartActivityCompat.profilerInfoIndex = profilerInfoIndex;
    }

    /**
     * Returns the current index of the options Bundle argument in the parameter array.
     *
     * @return the options argument index
     */
    public static int getOptionsIndex() {
        return optionsIndex;
    }

    /**
     * Sets the index of the options Bundle argument in the parameter array.
     *
     * @param optionsIndex the new options argument index
     */
    public static void setOptionsIndex(int optionsIndex) {
        StartActivityCompat.optionsIndex = optionsIndex;
    }
}
