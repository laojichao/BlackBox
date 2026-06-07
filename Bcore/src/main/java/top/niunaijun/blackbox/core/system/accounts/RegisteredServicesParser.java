package top.niunaijun.blackbox.core.system.accounts;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;

import black.android.content.res.BRAssetManager;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.core.system.pm.PackageManagerCompat;

/**
 * Parses authenticator metadata XML resources from installed packages within the virtual environment.
 * Used by {@link BAccountManagerService} to discover and load {@link android.accounts.AccountAuthenticator}
 * definitions declared in an application's {@code AndroidManifest.xml} via the
 * {@code <account-authenticator>} metadata tag.
 *
 * <p>Provides resource loading that resolves against the virtual package context rather
 * than the host system, ensuring correct resource IDs for virtualized APKs.</p>
 */
public class RegisteredServicesParser {

    /**
     * Retrieves the XML resource parser for a service's metadata.
     *
     * @param context     the context used to access resources
     * @param serviceInfo the service whose metadata to parse
     * @param name        the metadata key name (e.g., {@link android.accounts.AccountManager#AUTHENTICATOR_META_DATA_NAME})
     * @return the {@link XmlResourceParser} for the metadata XML, or null if not found
     */
    public XmlResourceParser getParser(Context context, ServiceInfo serviceInfo, String name) {
        Bundle meta = serviceInfo.metaData;
        if (meta != null) {
            int xmlId = meta.getInt(name);
            if (xmlId != 0) {
                try {
                    Resources resources = getResources(context, serviceInfo.applicationInfo);
                    if (resources == null)
                        return null;
                    return resources.getXml(xmlId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Loads the {@link Resources} for the given application within the virtual environment.
     *
     * @param context the context used for resource resolution
     * @param appInfo the application whose resources to load
     * @return the {@link Resources} instance, or null if loading fails
     */
    public Resources getResources(Context context, ApplicationInfo appInfo) {
        return PackageManagerCompat.getResources(context, appInfo);
    }
}