package net.evendanan.coffeetable.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.evendanan.coffeetable.model.AppModel;

public class VisibleApps {
    private static final String PREFS_PREFIX_KEY = "VisibleApps_COMPONENT_NAME_";
    private final SharedPreferences mPrefs;
    private final Runnable mOnVisibleAppsChanged;

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.startsWith(PREFS_PREFIX_KEY)) {
                mOnVisibleAppsChanged.run();
            }
        }
    };

    public VisibleApps(Context context, Runnable onVisibleAppsChanged) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mOnVisibleAppsChanged = onVisibleAppsChanged;

        mPrefs.registerOnSharedPreferenceChangeListener(mPrefsChanged);
    }

    private static String getAppKey(AppModel appModel) {
        return PREFS_PREFIX_KEY + appModel.getPackageName() + "_" + appModel.getActivityName();
    }

    public void close() {
        mPrefs.unregisterOnSharedPreferenceChangeListener(mPrefsChanged);
    }

    public boolean isVisibleApp(AppModel appModel) {
        final String appKey = getAppKey(appModel);
        return mPrefs.getBoolean(appKey, false);
    }

    public void setAppVisibility(AppModel appModel, boolean visibility) {
        mPrefs.edit().putBoolean(getAppKey(appModel), visibility).apply();
    }
}
