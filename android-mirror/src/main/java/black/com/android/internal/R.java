package black.com.android.internal;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden com.android.internal.R inner classes.
 * Provides access to internal Android framework resource IDs.
 */
@BClassName("com.android.internal.R")
public interface R {
    /**
     * Mirror of com.android.internal.R.styleable.
     * Internal styleable resource attribute indices.
     */
    @BClassName("com.android.internal.R$styleable")
    interface styleable {
        /** Styleable array for AccountAuthenticator attributes. */
        @BStaticField
        int[] AccountAuthenticator();

        /** Index of accountPreferences attribute. */
        @BStaticField
        int AccountAuthenticator_accountPreferences();

        /** Index of accountType attribute. */
        @BStaticField
        int AccountAuthenticator_accountType();

        /** Index of customTokens attribute. */
        @BStaticField
        int AccountAuthenticator_customTokens();

        /** Index of icon attribute. */
        @BStaticField
        int AccountAuthenticator_icon();

        /** Index of label attribute. */
        @BStaticField
        int AccountAuthenticator_label();

        /** Index of smallIcon attribute. */
        @BStaticField
        int AccountAuthenticator_smallIcon();

        /** Styleable array for SyncAdapter attributes. */
        @BStaticField
        Object SyncAdapter();

        /** Index of accountType attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_accountType();

        /** Index of allowParallelSyncs attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_allowParallelSyncs();

        /** Index of contentAuthority attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_contentAuthority();

        /** Index of isAlwaysSyncable attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_isAlwaysSyncable();

        /** Index of settingsActivity attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_settingsActivity();

        /** Index of supportsUploading attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_supportsUploading();

        /** Index of userVisible attribute in SyncAdapter. */
        @BStaticField
        int SyncAdapter_userVisible();

        /** Styleable array for View attributes. */
        @BStaticField
        Object View();

        /** Index of background attribute in View. */
        @BStaticField
        int View_background();

        /** Styleable array for Window attributes. */
        @BStaticField
        int[] Window();

        /** Index of windowBackground attribute. */
        @BStaticField
        int Window_windowBackground();

        /** Index of windowDisablePreview attribute. */
        @BStaticField
        int Window_windowDisablePreview();

        /** Index of windowFullscreen attribute. */
        @BStaticField
        int Window_windowFullscreen();

        /** Index of windowIsFloating attribute. */
        @BStaticField
        int Window_windowIsFloating();

        /** Index of windowIsTranslucent attribute. */
        @BStaticField
        int Window_windowIsTranslucent();

        /** Index of windowShowWallpaper attribute. */
        @BStaticField
        int Window_windowShowWallpaper();
    }

    /**
     * Mirror of com.android.internal.R.drawable.
     * Internal drawable resource IDs.
     */
    @BClassName("com.android.internal.R$drawable")
    interface drawable {
        /** Bottom bright popup background drawable. */
        @BStaticField
        int popup_bottom_bright();

        /** Bottom dark popup background drawable. */
        @BStaticField
        int popup_bottom_dark();

        /** Bottom medium popup background drawable. */
        @BStaticField
        int popup_bottom_medium();

        /** Center bright popup background drawable. */
        @BStaticField
        int popup_center_bright();

        /** Center dark popup background drawable. */
        @BStaticField
        int popup_center_dark();

        /** Full bright popup background drawable. */
        @BStaticField
        int popup_full_bright();

        /** Full dark popup background drawable. */
        @BStaticField
        int popup_full_dark();

        /** Top bright popup background drawable. */
        @BStaticField
        int popup_top_bright();

        /** Top dark popup background drawable. */
        @BStaticField
        int popup_top_dark();
    }

    /**
     * Mirror of com.android.internal.R.layout.
     * Internal layout resource IDs.
     */
    @BClassName("com.android.internal.R$layout")
    interface layout {
        /** Resolver list layout for app chooser dialogs. */
        @BStaticField
        int resolver_list();
    }

    /**
     * Mirror of com.android.internal.R.id.
     * Internal view ID resource constants.
     */
    @BClassName("com.android.internal.R$id")
    interface id {
        /** Alert dialog title view ID. */
        @BStaticField
        int alertTitle();

        /** Dialog button 1 (positive) view ID. */
        @BStaticField
        int button1();

        /** Dialog button 2 (negative) view ID. */
        @BStaticField
        int button2();

        /** Dialog button 3 (neutral) view ID. */
        @BStaticField
        int button3();

        /** Dialog button panel container view ID. */
        @BStaticField
        int buttonPanel();

        /** Dialog content panel container view ID. */
        @BStaticField
        int contentPanel();

        /** Custom view container ID. */
        @BStaticField
        int custom();

        /** Custom panel container view ID. */
        @BStaticField
        int customPanel();

        /** Dialog icon view ID. */
        @BStaticField
        int icon();

        /** Left spacer view ID in button panel. */
        @BStaticField
        int leftSpacer();

        /** Dialog message text view ID. */
        @BStaticField
        int message();

        /** Resolver list view ID. */
        @BStaticField
        int resolver_list();

        /** Right spacer view ID in button panel. */
        @BStaticField
        int rightSpacer();

        /** Dialog scroll view container ID. */
        @BStaticField
        int scrollView();

        /** Primary text view ID. */
        @BStaticField
        int text1();

        /** Secondary text view ID. */
        @BStaticField
        int text2();

        /** Title divider view ID. */
        @BStaticField
        int titleDivider();

        /** Top title divider view ID. */
        @BStaticField
        int titleDividerTop();

        /** Title template view ID. */
        @BStaticField
        int title_template();

        /** Top panel container view ID. */
        @BStaticField
        int topPanel();
    }
}
