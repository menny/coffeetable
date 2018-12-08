package net.evendanan.coffeetable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.evendanan.coffeetable.model.AppModel;
import net.evendanan.coffeetable.model.AppsListProvider;
import net.evendanan.coffeetable.settings.VisibleApps;

import java.util.ArrayList;
import java.util.List;

import androidx.core.util.Consumer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeScreen extends Activity {

    private HomeScreenAppsAdapter mAppsAdapter;
    private AppsListProvider mAppsListProvider;
    private VisibleApps mVisibleApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        RecyclerView appsGrid = findViewById(R.id.app_recycler_view);

        appsGrid.setHasFixedSize(true);
        appsGrid.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.apps_grid_column_count)));

        mAppsAdapter = new HomeScreenAppsAdapter(this, this::onAppClicked);
        appsGrid.setAdapter(mAppsAdapter);

        mVisibleApps = new VisibleApps(getApplicationContext(), this::onAppsVisibilityChanged);

        mAppsListProvider = new AppsListProvider(this, this::onListOfAppsReceived);
    }

    private void onAppsVisibilityChanged() {
        if (mAppsListProvider != null) {
            mAppsListProvider.forceRefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVisibleApps.close();
        mAppsListProvider.close();
    }

    private void onListOfAppsReceived(List<AppModel> appModels) {
        ArrayList<AppModel> filteredApps = new ArrayList<>();
        for (AppModel appModel : appModels) {
            if (mVisibleApps.isVisibleApp(appModel)) {
                filteredApps.add(appModel);
            }
        }

        mAppsAdapter.submitList(filteredApps);
    }

    private void onAppClicked(AppModel appModel) {
        startActivity(appModel.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private static class HomeScreenAppsAdapter extends AppsAdapterBase<AppsAdapterBase.AppsViewModel> {

        HomeScreenAppsAdapter(Context context, Consumer<AppModel> clickHandler) {
            super(context, clickHandler);
        }

        @Override
        protected AppsAdapterBase.AppsViewModel createAppsViewHolder(LayoutInflater inflater, ViewGroup parent, Consumer<AppModel> clickHandler) {
            return new AppsViewModel(inflater.inflate(R.layout.app_cell_item, parent, false), clickHandler);
        }
    }

}
