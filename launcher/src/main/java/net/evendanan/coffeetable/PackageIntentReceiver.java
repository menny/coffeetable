package net.evendanan.coffeetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class PackageIntentReceiver extends BroadcastReceiver {

    private final Context mContext;
    private final Runnable mOnPackagesChanged;

    public PackageIntentReceiver(Context context, Runnable onPackagesChanged) {
        mContext = context;
        mOnPackagesChanged = onPackagesChanged;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);

        // Register for events related to sdcard installation.
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        context.registerReceiver(this, sdFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnPackagesChanged.run();
    }

    public void close() {
        mContext.unregisterReceiver(this);
    }
}
