package black.android.content.pm;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.io.File;
import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.content.pm.PackageParser.
 * Provides access to APK parsing, certificate collection, and parsed package internals.
 */
@BClassName("android.content.pm.PackageParser")
public interface PackageParser {
    /** Creates a new PackageParser for the given archive path. */
    @BConstructor
    android.content.pm.PackageParser _new(String String0);

    /** Collects certificates for the given package. */
    @BMethod
    void collectCertificates(android.content.pm.PackageParser.Package p, int flags);

    /** Parses an APK file and returns the Package object. */
    @BMethod
    android.content.pm.PackageParser.Package parsePackage(File File0, String String1, DisplayMetrics DisplayMetrics2, int int3);

    /**
     * Mirror of PackageParser.SigningDetails.
     * Contains current and past signing certificate information.
     */
    @BClassName("android.content.pm.PackageParser$SigningDetails")
    interface SigningDetails {
        /** Past signing certificates used before key rotation. */
        @BField
        Signature[] pastSigningCertificates();

        /** Current signing certificates. */
        @BField
        Signature[] signatures();

        /** Returns whether past signing certificates exist. */
        @BMethod
        Boolean hasPastSigningCertificates();

        /** Returns whether any signing certificates exist. */
        @BMethod
        Boolean hasSignatures();
    }

    /**
     * Mirror of PackageParser.Component.
     * Base class for parsed components (activities, services, etc.).
     */
    @BClassName("android.content.pm.PackageParser$Component")
    interface Component {
        /** The fully qualified class name. */
        @BField
        String className();

        /** The ComponentName for this component. */
        @BField
        ComponentName componentName();

        /** The intent filters associated with this component. */
        @BField
        List<android.content.IntentFilter> intents();
    }

    /**
     * Mirror of PackageParser.PermissionGroup.
     * Represents a parsed permission group declaration.
     */
    @BClassName("android.content.pm.PackageParser$PermissionGroup")
    interface PermissionGroup {
        /** The PermissionGroupInfo metadata. */
        @BField
        PermissionGroupInfo info();
    }

    /**
     * Mirror of PackageParser.Permission.
     * Represents a parsed permission declaration.
     */
    @BClassName("android.content.pm.PackageParser$Permission")
    interface Permission {
        /** The PermissionInfo metadata. */
        @BField
        PermissionInfo info();
    }

    /**
     * Mirror of PackageParser.Service.
     * Represents a parsed service declaration.
     */
    @BClassName("android.content.pm.PackageParser$Service")
    interface Service {
        /** The ServiceInfo metadata. */
        @BField
        ServiceInfo info();
    }

    /**
     * Mirror of PackageParser.Provider.
     * Represents a parsed content provider declaration.
     */
    @BClassName("android.content.pm.PackageParser$Provider")
    interface Provider {
        /** The ProviderInfo metadata. */
        @BField
        ProviderInfo info();
    }

    /**
     * Mirror of PackageParser.Activity.
     * Represents a parsed activity declaration.
     */
    @BClassName("android.content.pm.PackageParser$Activity")
    interface Activity {
        /** The ActivityInfo metadata. */
        @BField
        ActivityInfo info();
    }

    /**
     * Mirror of PackageParser.Package.
     * Represents a fully parsed APK package with all its components.
     */
    @BClassName("android.content.pm.PackageParser$Package")
    interface Package {
        /** List of declared activities. */
        @BField
        List activities();

        /** Application-level metadata Bundle. */
        @BField
        Bundle mAppMetaData();

        /** The shared user ID, if any. */
        @BField
        String mSharedUserId();

        /** The package signing signatures. */
        @BField
        Signature[] mSignatures();

        /** The signing details object. */
        @BField
        Object mSigningDetails();

        /** The package version code. */
        @BField
        Integer mVersionCode();

        /** The package name. */
        @BField
        String packageName();

        /** List of declared permission groups. */
        @BField
        List permissionGroups();

        /** List of declared permissions. */
        @BField
        List permissions();

        /** List of protected broadcast action names. */
        @BField
        List<String> protectedBroadcasts();

        /** List of declared content providers. */
        @BField
        List providers();

        /** List of declared broadcast receivers. */
        @BField
        List receivers();

        /** List of requested permissions. */
        @BField
        List<String> requestedPermissions();

        /** List of declared services. */
        @BField
        List services();
    }
}
