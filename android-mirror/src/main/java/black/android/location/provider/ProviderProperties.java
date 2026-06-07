package black.android.location.provider;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.location.provider.ProviderProperties fields.
 * Describes the capabilities and requirements of a location provider.
 */
@BClassName("android.location.provider.ProviderProperties")
public interface ProviderProperties {
    /** Whether the provider requires a network connection. */
    @BField
    boolean mHasNetworkRequirement();

    /** Whether the provider requires satellite (GPS) connectivity. */
    @BField
    boolean mHasSatelliteRequirement();

    /** Whether the provider requires cell tower connectivity. */
    @BField
    boolean mHasCellRequirement();

    /** Whether using the provider incurs monetary cost. */
    @BField
    boolean mHasMonetaryCost();

    /** Whether the provider supports altitude reporting. */
    @BField
    boolean mHasAltitudeSupport();

    /** Whether the provider supports speed reporting. */
    @BField
    boolean mHasSpeedSupport();

    /** Whether the provider supports bearing reporting. */
    @BField
    boolean mHasBearingSupport();
}
