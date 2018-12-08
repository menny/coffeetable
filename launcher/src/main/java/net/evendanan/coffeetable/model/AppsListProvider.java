package net.evendanan.coffeetable.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.core.util.Consumer;

public class AppsListProvider {

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    private static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppModel object1, AppModel object2) {
            final int packageDiff = sCollator.compare(object1.getPackageName(), object2.getPackageName());
            if (packageDiff == 0) {
                return sCollator.compare(object1.getAppLabel(), object2.getAppLabel());
            } else {
                return packageDiff;
            }
        }
    };
    private final PackageIntentReceiver mPackageIntentReceiver;
    private final Consumer<List<AppModel>> mOnAppsChanged;
    private final Context mContext;

    public AppsListProvider(Context context, Consumer<List<AppModel>> onAppsChanged) {
        mContext = context;
        mOnAppsChanged = onAppsChanged;
        mPackageIntentReceiver = new PackageIntentReceiver(context, this::forceRefresh);
        //initial call
        onAppsChanged.accept(getInstalledApps(context.getPackageManager()));
    }

    public void forceRefresh() {
        mOnAppsChanged.accept(getInstalledApps(mContext.getPackageManager()));
    }

    private List<AppModel> getInstalledApps(PackageManager pm) {
        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        if (installedPackages == null) return Collections.emptyList();

        ArrayList<AppModel> items = new ArrayList<>(installedPackages.size());
        for (int i = 0; i < installedPackages.size(); i++) {
            final String pkg = installedPackages.get(i).packageName;

            final String mainActivity;
            final Intent mainActivityIntent = pm.getLaunchIntentForPackage(pkg);
            if (mainActivityIntent == null || mainActivityIntent.getComponent() == null) {
                mainActivity = "";
            } else {
                mainActivity = mainActivityIntent.getComponent().getClassName();
            }

            try {
                final PackageInfo packageInfo = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
                if (packageInfo != null && packageInfo.activities != null) {
                    for (ActivityInfo activityInfo : packageInfo.activities) {
                        final Intent activityIntent;
                        if (mainActivity.equals(activityInfo.name)) {
                            activityIntent = mainActivityIntent;
                        } else {
                            activityIntent = new Intent();
                            activityIntent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
                        }
                        AppModel app = new AppModel(activityInfo.loadLabel(pm).toString(), activityInfo.loadIcon(pm),
                                activityInfo.enabled, activityInfo.exported,
                                activityInfo.packageName, activityInfo.name,
                                activityIntent,
                                activityIntent == mainActivityIntent);


                        items.add(app);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // sort the list
        Collections.sort(items, ALPHA_COMPARATOR);

        return items;
    }

    public void close() {
        mPackageIntentReceiver.close();
    }
}
