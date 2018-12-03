package net.evendanan.coffeetable;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsLoader extends AsyncTaskLoader<List<AppModel>> {
    private List<AppModel> mInstalledApps;

    private final PackageManager mPm;
    private PackageIntentReceiver mPackageObserver;

    AppsLoader(Context context) {
        super(context.getApplicationContext());

        mPm = getContext().getPackageManager();
    }

    @Override
    public List<AppModel> loadInBackground() {
        return getAppModels(getContext(), mPm);
    }

    @NonNull
    private static List<AppModel> getAppModels(final Context context, final PackageManager pm) {
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        if (apps == null) return Collections.emptyList();

        // create corresponding apps and load their labels
        ArrayList<AppModel> items = new ArrayList<>(apps.size());
        for (int i = 0; i < apps.size(); i++) {
            String pkg = apps.get(i).packageName;

            // only apps which are launchable
            if (context.getPackageManager().getLaunchIntentForPackage(pkg) != null) {
                AppModel app = new AppModel(context, apps.get(i));
                app.loadLabel(context);
                items.add(app);
            }
        }

        // sort the list
        Collections.sort(items, ALPHA_COMPARATOR);

        return items;
    }

    @Override
    public void deliverResult(List<AppModel> apps) {
        if (!isReset()) {
            mInstalledApps = apps;

            super.deliverResult(apps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mInstalledApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mInstalledApps);
        }

        // watch for changes in app install and uninstall operation
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }

        if (takeContentChanged() || mInstalledApps == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mInstalledApps != null) {
            mInstalledApps = null;
        }

        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }


    /**
     * Perform alphabetical comparison of application entry objects.
     */
    private static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppModel object1, AppModel object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}
