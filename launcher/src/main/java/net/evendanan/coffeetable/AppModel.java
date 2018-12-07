package net.evendanan.coffeetable;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppModel {
    private final boolean mEnabled;
    private final boolean mExported;
    private final String mPackageName;
    private final String mActivityName;

    private final CharSequence mAppLabel;
    private final Drawable mIcon;

    private final ActivityType mIntentType;

    private final Intent mLaunchIntent;

    public CharSequence getAppLabel() {
        return mAppLabel;
    }

    public AppModel(String label, Drawable icon, boolean enabled, boolean exported, String packageName, String className, Intent launchIntent, boolean isMain) {
        mAppLabel = label;
        mIcon = icon;
        mEnabled = enabled;
        mExported = exported;
        mPackageName = packageName;
        mActivityName = className;
        mLaunchIntent = launchIntent;
        mIntentType = isMain ? ActivityType.Main : ActivityType.Internal;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public ActivityType getActivityType() {
        return mIntentType;
    }

    public Intent getLaunchIntent() {
        return new Intent(mLaunchIntent);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean isExported() {
        return mExported;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getActivityName() {
        return mActivityName;
    }

    public enum ActivityType {
        Main,
        Internal,
        Shortcut
    }
}
