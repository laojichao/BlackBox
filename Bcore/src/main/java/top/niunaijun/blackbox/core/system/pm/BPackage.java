package top.niunaijun.blackbox.core.system.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageParser;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Parcelable representation of an Android application package within the virtual environment.
 *
 * <p>This class mirrors Android's {@link PackageParser.Package} but is designed to be
 * serializable across process boundaries via {@link Parcel}. It contains all component
 * declarations (activities, services, providers, receivers), permissions, signing details,
 * and metadata required to represent a fully parsed APK within the BlackBox container.</p>
 *
 * <p>Each component type has a corresponding inner class (e.g., {@link Activity},
 * {@link Service}) that wraps the platform's parsed data and maintains back-references
 * to the owning {@link BPackage}.</p>
 *
 * @see BPackageSettings
 * @see PackageParser.Package
 */
public class BPackage implements Parcelable {
    /** List of declared activities in this package. */
    public ArrayList<Activity> activities = new ArrayList<Activity>(0);
    /** List of declared broadcast receivers in this package. */
    public ArrayList<Activity> receivers = new ArrayList<Activity>(0);
    /** List of declared content providers in this package. */
    public ArrayList<Provider> providers = new ArrayList<Provider>(0);
    /** List of declared services in this package. */
    public ArrayList<Service> services = new ArrayList<Service>(0);
    /** List of declared instrumentation components in this package. */
    public ArrayList<Instrumentation> instrumentation = new ArrayList<Instrumentation>(0);
    /** List of permissions declared by this package. */
    public ArrayList<Permission> permissions = new ArrayList<Permission>(0);
    /** List of permission groups declared by this package. */
    public ArrayList<PermissionGroup> permissionGroups = new ArrayList<PermissionGroup>(0);
    /** Permissions requested by this package at install time. */
    public ArrayList<String> requestedPermissions = new ArrayList<String>();
    /** APK signatures (pre-Android P). */
    public Signature[] mSignatures;
    /** Signing details including certificate chain (Android P and above). */
    public SigningDetails mSigningDetails;
    /** Application-level metadata bundle from AndroidManifest.xml. */
    public Bundle mAppMetaData;
    /** Package settings associated with this package, including per-user state. */
    public BPackageSettings mExtras;
    /** The unique package name (e.g., "com.example.app"). */
    public String packageName;
    /** Preferred ordering value for intent resolution priority. */
    public int mPreferredOrder;
    /** Shared user ID string, if this package shares a UID with other packages. */
    public String mSharedUserId;
    /** Shared libraries required by this package at runtime. */
    public ArrayList<String> usesLibraries;
    /** Optional shared libraries used by this package. */
    public ArrayList<String> usesOptionalLibraries;
    /** Version code integer from the manifest. */
    public int mVersionCode;
    /** The parsed application-level information. */
    public ApplicationInfo applicationInfo;
    /** Human-readable version name string. */
    public String mVersionName;
    /** Base path to the APK file on disk. */
    public String baseCodePath;

    /** Resource label ID for the shared user. */
    public int mSharedUserLabel;
    /** Hardware configuration preferences declared by the application. */
    public ArrayList<ConfigurationInfo> configPreferences = null;
    /** Hardware features required or requested by the application. */
    public ArrayList<FeatureInfo> reqFeatures = null;

    /** Installation options and flags used when this package was installed. */
    public InstallOption installOption;

    /**
     * Constructs a BPackage by converting all components from a platform-parsed
     * {@link PackageParser.Package} into BlackBox's serializable representation.
     *
     * @param aPackage the parsed package from the platform's PackageParser
     */
    public BPackage(PackageParser.Package aPackage) {
        this.activities = new ArrayList<>(aPackage.activities.size());
        for (PackageParser.Activity activity : aPackage.activities) {
            Activity selfActivity = new Activity(activity);
            for (ActivityIntentInfo intent : selfActivity.intents) {
                intent.activity = selfActivity;
            }
            selfActivity.owner = this;
            this.activities.add(selfActivity);
        }

        this.receivers = new ArrayList<>(aPackage.receivers.size());
        for (PackageParser.Activity receiver : aPackage.receivers) {
            Activity selfReceiver = new Activity(receiver);
            for (ActivityIntentInfo intent : selfReceiver.intents) {
                intent.activity = selfReceiver;
            }
            selfReceiver.owner = this;
            this.receivers.add(selfReceiver);
        }

        this.providers = new ArrayList<>(aPackage.providers.size());
        for (PackageParser.Provider provider : aPackage.providers) {
            Provider selfProvider = new Provider(provider);
            for (ProviderIntentInfo intent : selfProvider.intents) {
                intent.provider = selfProvider;
            }
            selfProvider.owner = this;
            this.providers.add(selfProvider);
        }

        this.services = new ArrayList<>(aPackage.services.size());
        for (PackageParser.Service service : aPackage.services) {
            Service selfService = new Service(service);
            for (ServiceIntentInfo intent : selfService.intents) {
                intent.service = selfService;
            }
            selfService.owner = this;
            this.services.add(selfService);
        }

        this.instrumentation = new ArrayList<>(aPackage.instrumentation.size());
        for (PackageParser.Instrumentation instrumentation1 : aPackage.instrumentation) {
            Instrumentation selfInstrumentation = new Instrumentation(instrumentation1);
            selfInstrumentation.owner = this;
            this.instrumentation.add(selfInstrumentation);
        }

        this.permissions = new ArrayList<>(aPackage.permissions.size());
        for (PackageParser.Permission permission : aPackage.permissions) {
            Permission selfPermission = new Permission(permission);
            selfPermission.owner = this;
            this.permissions.add(selfPermission);
        }

        this.permissionGroups = new ArrayList<>(aPackage.permissionGroups.size());
        for (PackageParser.PermissionGroup permissionGroup : aPackage.permissionGroups) {
            PermissionGroup selfPermissionGroup = new PermissionGroup(permissionGroup);
            selfPermissionGroup.owner = this;
            this.permissionGroups.add(selfPermissionGroup);
        }

        this.requestedPermissions = aPackage.requestedPermissions;
        if (BuildCompat.isPie()) {
            this.mSigningDetails = new SigningDetails(aPackage.mSigningDetails);
            this.mSignatures = this.mSigningDetails.signatures;
        } else {
            this.mSignatures = aPackage.mSignatures;
        }
        this.mAppMetaData = aPackage.mAppMetaData;
        // this.mExtras = new BPackageSettings((PackageSetting) aPackage.mExtras);
        this.packageName = aPackage.packageName;
        this.mPreferredOrder = aPackage.mPreferredOrder;
        this.mSharedUserId = aPackage.mSharedUserId;
        this.usesLibraries = aPackage.usesLibraries;
        this.usesOptionalLibraries = aPackage.usesOptionalLibraries;
        this.mVersionCode = aPackage.mVersionCode;
        this.applicationInfo = aPackage.applicationInfo;
        this.mVersionName = aPackage.mVersionName;
        this.baseCodePath = aPackage.baseCodePath;
        this.mSharedUserLabel = aPackage.mSharedUserLabel;
        this.configPreferences = aPackage.configPreferences;
        this.reqFeatures = aPackage.reqFeatures;
    }

    /**
     * Restores a BPackage from a previously serialized {@link Parcel}.
     *
     * @param in the Parcel containing serialized package data
     */
    protected BPackage(Parcel in) {
        int N = in.readInt();
        this.activities = new ArrayList<>(N);
        while (N-- > 0) {
            Activity activity = new Activity(in);
            for (ActivityIntentInfo intent : activity.intents) {
                intent.activity = activity;
            }
            activity.owner = this;
            this.activities.add(activity);
        }

        N = in.readInt();
        this.receivers = new ArrayList<>(N);
        while (N-- > 0) {
            Activity activity = new Activity(in);
            for (ActivityIntentInfo intent : activity.intents) {
                intent.activity = activity;
            }
            activity.owner = this;
            this.receivers.add(activity);
        }

        N = in.readInt();
        this.providers = new ArrayList<>(N);
        while (N-- > 0) {
            Provider provider = new Provider(in);
            for (ProviderIntentInfo intent : provider.intents) {
                intent.provider = provider;
            }
            provider.owner = this;
            this.providers.add(provider);
        }

        N = in.readInt();
        this.services = new ArrayList<>(N);
        while (N-- > 0) {
            Service service = new Service(in);
            for (ServiceIntentInfo intent : service.intents) {
                intent.service = service;
            }
            service.owner = this;
            this.services.add(service);
        }

        N = in.readInt();
        this.instrumentation = new ArrayList<>(N);
        while (N-- > 0) {
            Instrumentation instrumentation = new Instrumentation(in);
            instrumentation.owner = this;
            this.instrumentation.add(instrumentation);
        }

        N = in.readInt();
        this.permissions = new ArrayList<>(N);
        while (N-- > 0) {
            Permission permission = new Permission(in);
            permission.owner = this;
            this.permissions.add(permission);
        }

        N = in.readInt();
        this.permissionGroups = new ArrayList<>(N);
        while (N-- > 0) {
            PermissionGroup permissionGroup = new PermissionGroup(in);
            permissionGroup.owner = this;
            this.permissionGroups.add(permissionGroup);
        }

        in.readStringList(this.requestedPermissions);
        if (BuildCompat.isPie()) {
            this.mSigningDetails = in.readParcelable(SigningDetails.class.getClassLoader());
        }
        this.mSignatures = in.createTypedArray(Signature.CREATOR);
        this.mAppMetaData = in.readBundle(Bundle.class.getClassLoader());
//        this.mExtras = in.readParcelable(BPackageSettings.class.getClassLoader());
        this.packageName = in.readString();
        this.mPreferredOrder = in.readInt();
        this.mSharedUserId = in.readString();
        this.usesLibraries = in.createStringArrayList();
        this.usesOptionalLibraries = in.createStringArrayList();
        this.mVersionCode = in.readInt();
        this.applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        this.mVersionName = in.readString();
        this.baseCodePath = in.readString();
        this.mSharedUserLabel = in.readInt();
        this.configPreferences = in.createTypedArrayList(ConfigurationInfo.CREATOR);
        this.reqFeatures = in.createTypedArrayList(FeatureInfo.CREATOR);
        this.installOption = in.readParcelable(InstallOption.class.getClassLoader());
    }

    /**
     * Represents an Android activity component within a virtual package.
     * Wraps {@link PackageParser.Activity} with intent filter information
     * and maintains a back-reference to the owning {@link BPackage}.
     */
    public final static class Activity extends Component<ActivityIntentInfo> {
        /** The parsed activity information (name, permissions, theme, etc.). */
        public ActivityInfo info;

        /**
         * Constructs an Activity by converting from a platform-parsed Activity.
         *
         * @param activity the platform-parsed activity component
         */
        public Activity(PackageParser.Activity activity) {
            super(activity);
            this.info = activity.info;
            if (activity.intents != null) {
                int size = activity.intents.size();
                this.intents = new ArrayList<>(size);
                for (PackageParser.ActivityIntentInfo intent : activity.intents) {
                    this.intents.add(new ActivityIntentInfo(intent));
                }
            }
        }

        /**
         * Restores an Activity from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized activity data
         */
        public Activity(Parcel parcel) {
            int N = parcel.readInt();
            this.intents = new ArrayList<>(N);
            while (N-- > 0) {
                IntentInfo intentInfo = parcel.readParcelable(BPackage.class.getClassLoader());
                this.intents.add(new ActivityIntentInfo(intentInfo));
            }
        }
    }

    /**
     * Represents an Android service component within a virtual package.
     * Wraps {@link PackageParser.Service} with its intent filters.
     */
    public static final class Service extends Component<ServiceIntentInfo> {
        /** The parsed service information (name, permissions, intent filter, etc.). */
        public ServiceInfo info;

        /**
         * Constructs a Service by converting from a platform-parsed Service.
         *
         * @param service the platform-parsed service component
         */
        public Service(PackageParser.Service service) {
            super(service);
            info = service.info;
            if (service.intents != null) {
                int size = service.intents.size();
                intents = new ArrayList<>(size);
                for (PackageParser.ServiceIntentInfo intent : service.intents) {
                    intents.add(new ServiceIntentInfo(intent));
                }
            }
        }

        /**
         * Restores a Service from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized service data
         */
        public Service(Parcel parcel) {
            int N = parcel.readInt();
            intents = new ArrayList<>(N);
            while (N-- > 0) {
                IntentInfo intentInfo = parcel.readParcelable(BPackage.class.getClassLoader());
                intents.add(new ServiceIntentInfo(intentInfo));
            }
        }
    }

    /**
     * Represents an Android content provider component within a virtual package.
     * Wraps {@link PackageParser.Provider} with its intent filters.
     */
    public static final class Provider extends Component<ProviderIntentInfo> {
        /** The parsed provider information (authority, permissions, etc.). */
        public ProviderInfo info;

        /**
         * Constructs a Provider by converting from a platform-parsed Provider.
         *
         * @param provider the platform-parsed provider component
         */
        public Provider(PackageParser.Provider provider) {
            super(provider);
            info = provider.info;
            if (provider.intents != null) {
                int size = provider.intents.size();
                intents = new ArrayList<>(size);
                for (PackageParser.ProviderIntentInfo intent : provider.intents) {
                    intents.add(new ProviderIntentInfo(intent));
                }
            }
        }

        /**
         * Restores a Provider from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized provider data
         */
        public Provider(Parcel parcel) {
            int N = parcel.readInt();
            intents = new ArrayList<>(N);
            while (N-- > 0) {
                IntentInfo intentInfo = parcel.readParcelable(BPackage.class.getClassLoader());
                intents.add(new ProviderIntentInfo(intentInfo));
            }
        }
    }

    /**
     * Represents an Android instrumentation component within a virtual package.
     * Used for testing frameworks that monitor application interaction.
     */
    public static final class Instrumentation extends Component<IntentInfo> {
        /** The parsed instrumentation information (target package, runner, etc.). */
        public InstrumentationInfo info;

        /**
         * Constructs an Instrumentation by converting from a platform-parsed Instrumentation.
         *
         * @param instrumentation the platform-parsed instrumentation component
         */
        public Instrumentation(PackageParser.Instrumentation instrumentation) {
            super(instrumentation);
            info = instrumentation.info;
            if (instrumentation.intents != null) {
                int size = instrumentation.intents.size();
                this.intents = new ArrayList<>(size);
                for (PackageParser.IntentInfo intent : instrumentation.intents) {
                    this.intents.add(new IntentInfo(intent));
                }
            }
        }

        /**
         * Restores an Instrumentation from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized instrumentation data
         */
        public Instrumentation(Parcel parcel) {
            int N = parcel.readInt();
            this.intents = new ArrayList<>(N);
            while (N-- > 0) {
                IntentInfo intentInfo = parcel.readParcelable(BPackage.class.getClassLoader());
                this.intents.add(intentInfo);
            }
        }
    }

    /**
     * Represents a declared permission within a virtual package.
     * Wraps {@link PackageParser.Permission} with its parsed information.
     */
    public static final class Permission extends Component<IntentInfo> {
        /** The parsed permission information (name, protection level, etc.). */
        public PermissionInfo info;

        /**
         * Constructs a Permission by converting from a platform-parsed Permission.
         *
         * @param permission the platform-parsed permission component
         */
        public Permission(PackageParser.Permission permission) {
            super(permission);
            this.info = permission.info;
            if (permission.intents != null) {
                int size = permission.intents.size();
                this.intents = new ArrayList<>(size);
                for (PackageParser.IntentInfo intent : permission.intents) {
                    this.intents.add(new IntentInfo(intent));
                }
            }
        }

        /**
         * Restores a Permission from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized permission data
         */
        public Permission(Parcel parcel) {
            int N = parcel.readInt();
            this.intents = new ArrayList<>(N);
            while (N-- > 0) {
                IntentInfo intentInfo = parcel.readParcelable(BPackage.class.getClassLoader());
                this.intents.add(intentInfo);
            }
        }
    }

    /**
     * Represents a permission group within a virtual package.
     * Permission groups organize related permissions for UI presentation.
     */
    public static final class PermissionGroup extends Component<IntentInfo> {
        /** The parsed permission group information. */
        public PermissionGroupInfo info;

        /**
         * Constructs a PermissionGroup by converting from a platform-parsed PermissionGroup.
         *
         * @param group the platform-parsed permission group component
         */
        public PermissionGroup(PackageParser.PermissionGroup group) {
            super(group);
            this.info = group.info;
            if (group.intents != null) {
                int size = group.intents.size();
                this.intents = new ArrayList<>(size);
                for (PackageParser.IntentInfo intent : group.intents) {
                    this.intents.add(new IntentInfo(intent));
                }
            }
        }

        /**
         * Restores a PermissionGroup from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized permission group data
         */
        public PermissionGroup(Parcel parcel) {
            int N = parcel.readInt();
            this.intents = new ArrayList<>(N);
            while (N-- > 0) {
                IntentInfo intentInfo = parcel.readParcelable(BPackage.class.getClassLoader());
                this.intents.add(intentInfo);
            }
        }
    }

    /**
     * Intent filter information associated with an {@link Activity} component.
     * Links an intent filter to its parent activity for resolution purposes.
     */
    public static class ActivityIntentInfo extends IntentInfo {
        /** The activity this intent info is associated with. */
        public Activity activity;

        /**
         * Constructs from a platform-parsed IntentInfo.
         *
         * @param intentInfo the platform intent filter info to copy from
         */
        public ActivityIntentInfo(PackageParser.IntentInfo intentInfo) {
            super(intentInfo);
        }

        /**
         * Constructs from a BlackBox IntentInfo copy.
         *
         * @param intentInfo the BlackBox intent info to copy from
         */
        public ActivityIntentInfo(IntentInfo intentInfo) {
            super(intentInfo);
        }
    }

    /**
     * Intent filter information associated with a {@link Service} component.
     * Links an intent filter to its parent service for resolution purposes.
     */
    public static class ServiceIntentInfo extends IntentInfo {
        /** The service this intent info is associated with. */
        public Service service;

        /**
         * Constructs from a platform-parsed IntentInfo.
         *
         * @param intentInfo the platform intent filter info to copy from
         */
        public ServiceIntentInfo(PackageParser.IntentInfo intentInfo) {
            super(intentInfo);
        }

        /**
         * Constructs from a BlackBox IntentInfo copy.
         *
         * @param intentInfo the BlackBox intent info to copy from
         */
        public ServiceIntentInfo(IntentInfo intentInfo) {
            super(intentInfo);
        }
    }

    /**
     * Intent filter information associated with a {@link Provider} component.
     * Links an intent filter to its parent content provider for resolution purposes.
     */
    public static class ProviderIntentInfo extends IntentInfo {
        /** The content provider this intent info is associated with. */
        public Provider provider;

        /**
         * Constructs from a platform-parsed IntentInfo.
         *
         * @param intentInfo the platform intent filter info to copy from
         */
        public ProviderIntentInfo(PackageParser.IntentInfo intentInfo) {
            super(intentInfo);
        }

        /**
         * Constructs from a BlackBox IntentInfo copy.
         *
         * @param intentInfo the BlackBox intent info to copy from
         */
        public ProviderIntentInfo(IntentInfo intentInfo) {
            super(intentInfo);
        }
    }

    /**
     * Parcelable wrapper for APK signing details (Android Pie and above).
     * Holds the signing certificate chain used to verify APK integrity
     * within the virtual environment.
     */
    public static final class SigningDetails implements Parcelable {
        /** Array of signing certificates for this package. */
        public Signature[] signatures;

        /** Sentinel value indicating unknown signing details. */
        public static final PackageParser.SigningDetails UNKNOWN = null;

        /**
         * Returns a bitmask indicating the set of special object types
         * marshaled by this Parcelable.
         *
         * @return 0, indicating no special objects
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Flattens this SigningDetails object into a Parcel.
         *
         * @param dest  the Parcel in which the object should be written
         * @param flags additional flags about how the object should be written
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(this.signatures, flags);
        }

        /**
         * Constructs SigningDetails from a platform-parsed SigningDetails.
         * Uses past signing certificates if available, otherwise uses current signatures.
         *
         * @param signingDetails the platform signing details to copy from
         */
        public SigningDetails(PackageParser.SigningDetails signingDetails) {
            if (signingDetails.pastSigningCertificates == null) {
                this.signatures = signingDetails.signatures;
            } else {
                this.signatures = signingDetails.pastSigningCertificates;
            }
        }

        protected SigningDetails(Parcel in) {
            this.signatures = in.createTypedArray(Signature.CREATOR);
        }

        public static final Creator<SigningDetails> CREATOR = new Creator<SigningDetails>() {
            @Override
            public SigningDetails createFromParcel(Parcel source) {
                return new SigningDetails(source);
            }

            @Override
            public SigningDetails[] newArray(int size) {
                return new SigningDetails[size];
            }
        };
    }

    /**
     * Parcelable wrapper for intent filter information associated with a component.
     * Holds the raw {@link IntentFilter}, display metadata (label, icon), and
     * whether this filter represents a default handler within the virtual environment.
     */
    public static class IntentInfo implements Parcelable {
        /** The underlying intent filter defining action, category, and data matches. */
        public IntentFilter intentFilter;
        /** Whether this intent info declares itself as a default handler. */
        public boolean hasDefault;
        /** Resource ID for the label string, or 0 if not specified. */
        public int labelRes;
        /** Non-localized label string, or null if not specified. */
        public String nonLocalizedLabel;
        /** Drawable resource ID for the icon, or 0 if not specified. */
        public int icon;
        /** Drawable resource ID for the logo, or 0 if not specified. */
        public int logo;
        /** Drawable resource ID for the banner, or 0 if not specified. */
        public int banner;

        /**
         * Constructs an IntentInfo by copying data from a platform-parsed IntentInfo.
         *
         * @param intentInfo the platform intent filter info to copy from
         */
        public IntentInfo(PackageParser.IntentInfo intentInfo) {
            this.intentFilter = intentInfo;
            this.hasDefault = intentInfo.hasDefault;
            this.labelRes = intentInfo.labelRes;
            this.nonLocalizedLabel = intentInfo.nonLocalizedLabel == null ? null : intentInfo.nonLocalizedLabel.toString();
            this.icon = intentInfo.icon;
            this.logo = intentInfo.logo;
            this.banner = intentInfo.banner;
        }

        /**
         * Constructs an IntentInfo by copying data from an existing BlackBox IntentInfo.
         *
         * @param intentInfo the BlackBox intent info to copy from
         */
        public IntentInfo(IntentInfo intentInfo) {
            this.intentFilter = intentInfo.intentFilter;
            this.hasDefault = intentInfo.hasDefault;
            this.labelRes = intentInfo.labelRes;
            this.nonLocalizedLabel = intentInfo.nonLocalizedLabel == null ? null : intentInfo.nonLocalizedLabel.toString();
            this.icon = intentInfo.icon;
            this.logo = intentInfo.logo;
            this.banner = intentInfo.banner;
        }

        /**
         * Returns a bitmask indicating the set of special object types
         * marshaled by this Parcelable.
         *
         * @return 0, indicating no special objects
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Flattens this IntentInfo object into a Parcel.
         *
         * @param dest  the Parcel in which the object should be written
         * @param flags additional flags about how the object should be written
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.intentFilter, flags);
            dest.writeByte(this.hasDefault ? (byte) 1 : (byte) 0);
            dest.writeInt(this.labelRes);
            dest.writeString(this.nonLocalizedLabel);
            dest.writeInt(this.icon);
            dest.writeInt(this.logo);
            dest.writeInt(this.banner);
        }

        /**
         * Restores an IntentInfo from a previously serialized Parcel.
         *
         * @param in the Parcel containing serialized intent info data
         */
        protected IntentInfo(Parcel in) {

        /** Factory for creating IntentInfo arrays and instances from Parcels. */
        public static final Creator<IntentInfo> CREATOR = new Creator<IntentInfo>() {
            @Override
            public IntentInfo createFromParcel(Parcel source) {
                return new IntentInfo(source);
            }

            @Override
            public IntentInfo[] newArray(int size) {
                return new IntentInfo[size];
            }
        };
    }

    /**
     * Base class for all package components (activities, services, providers, etc.)
     * within the virtual environment. Holds common metadata shared by every component
     * type including the class name, metadata bundle, and associated intent filters.
     *
     * @param <II> the concrete IntentInfo subtype associated with this component
     */
    public static class Component<II extends BPackage.IntentInfo> {
        /** The owning BPackage that declared this component. */
        public BPackage owner;
        /** List of intent filters declared for this component. */
        public ArrayList<II> intents;
        /** Fully qualified class name of this component. */
        public String className;
        /** Optional metadata bundle from the component's manifest declaration. */
        public Bundle metaData;
        /** Lazily computed ComponentName combining the package name and class name. */
        public ComponentName componentName;

        /**
         * Restores a Component from a previously serialized Parcel.
         *
         * @param parcel the Parcel containing serialized component data
         */
        public Component(Parcel parcel) {
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
        }

        /**
         * Constructs a Component by copying class name and metadata from a
         * platform-parsed component.
         *
         * @param component the platform-parsed component to copy from
         */
        public Component(PackageParser.Component<?> component) {
            this.className = component.className;
            this.metaData = component.metaData;
        }

        /**
         * Returns the {@link ComponentName} for this component, constructing it from
         * the owning package name and class name if not already cached.
         *
         * @return the ComponentName, or null if both className and owner are null
         */
        public ComponentName getComponentName() {
            if (componentName != null) {
                return componentName;
            }
            if (className != null) {
                componentName = new ComponentName(owner.packageName,
                        className);
            }
            return componentName;
        }
    }

    /**
     * Returns a bitmask indicating the set of special object types
     * marshaled by this Parcelable.
     *
     * @return 0, indicating no special objects
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens this BPackage object into a Parcel, writing all activities,
     * receivers, providers, services, instrumentation, permissions, permission
     * groups, signing details, and package metadata.
     *
     * @param dest  the Parcel in which the object should be written
     * @param flags additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int size = this.activities.size();
        dest.writeInt(size);
        for (Activity activity : this.activities) {
            dest.writeString(activity.className);
            dest.writeBundle(activity.metaData);

            dest.writeParcelable(activity.info, flags);
            if (activity.intents != null) {
                int N = activity.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(activity.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        size = this.receivers.size();
        dest.writeInt(size);
        for (Activity receiver : this.receivers) {
            dest.writeString(receiver.className);
            dest.writeBundle(receiver.metaData);

            dest.writeParcelable(receiver.info, flags);
            if (receiver.intents != null) {
                int N = receiver.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(receiver.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        size = this.providers.size();
        dest.writeInt(size);
        for (Provider provider : this.providers) {
            dest.writeString(provider.className);
            dest.writeBundle(provider.metaData);

            dest.writeParcelable(provider.info, flags);
            if (provider.intents != null) {
                int N = provider.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(provider.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        size = this.services.size();
        dest.writeInt(size);
        for (Service service : this.services) {
            dest.writeString(service.className);
            dest.writeBundle(service.metaData);

            dest.writeParcelable(service.info, flags);
            if (service.intents != null) {
                int N = service.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(service.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        size = this.instrumentation.size();
        dest.writeInt(size);
        for (Instrumentation instrumentation : this.instrumentation) {
            dest.writeString(instrumentation.className);
            dest.writeBundle(instrumentation.metaData);

            dest.writeParcelable(instrumentation.info, flags);
            if (instrumentation.intents != null) {
                int N = instrumentation.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(instrumentation.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        size = this.permissions.size();
        dest.writeInt(size);
        for (Permission permission : this.permissions) {
            dest.writeString(permission.className);
            dest.writeBundle(permission.metaData);

            dest.writeParcelable(permission.info, flags);
            if (permission.intents != null) {
                int N = permission.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(permission.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        size = this.permissionGroups.size();
        dest.writeInt(size);
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            dest.writeString(permissionGroup.className);
            dest.writeBundle(permissionGroup.metaData);

            dest.writeParcelable(permissionGroup.info, flags);
            if (permissionGroup.intents != null) {
                int N = permissionGroup.intents.size();
                dest.writeInt(N);
                while (N-- > 0) {
                    dest.writeParcelable(permissionGroup.intents.get(N), flags);
                }
            } else {
                dest.writeInt(0);
            }
        }

        dest.writeStringList(this.requestedPermissions);
        if (BuildCompat.isPie()) {
            dest.writeParcelable(this.mSigningDetails, flags);
        }
        dest.writeTypedArray(this.mSignatures, flags);
        dest.writeBundle(this.mAppMetaData);
//        dest.writeParcelable(this.mExtras, flags);
        dest.writeString(this.packageName);
        dest.writeInt(this.mPreferredOrder);
        dest.writeString(this.mSharedUserId);
        dest.writeStringList(this.usesLibraries);
        dest.writeStringList(this.usesOptionalLibraries);
        dest.writeInt(this.mVersionCode);
        dest.writeParcelable(this.applicationInfo, flags);
        dest.writeString(this.mVersionName);
        dest.writeString(this.baseCodePath);
        dest.writeInt(this.mSharedUserLabel);
        dest.writeTypedList(this.configPreferences);
        dest.writeTypedList(this.reqFeatures);
        dest.writeParcelable(this.installOption, flags);
    }

    /** Factory for creating BPackage arrays and instances from Parcels. */
    public static final Parcelable.Creator<BPackage> CREATOR = new Parcelable.Creator<BPackage>() {
        @Override
        public BPackage createFromParcel(Parcel source) {
            return new BPackage(source);
        }

        @Override
        public BPackage[] newArray(int size) {
            return new BPackage[size];
        }
    };
}
