package net.evendanan.coffeetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeScreen extends Activity {

    private AppsAdapter mAppsAdapter;
    private AppsListProvider mAppsListProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        RecyclerView appsGrid = findViewById(R.id.app_recycler_view);

        appsGrid.setHasFixedSize(true);
        appsGrid.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.apps_grid_column_count)));

        mAppsAdapter = new AppsAdapter(this, this::onAppClicked);
        appsGrid.setAdapter(mAppsAdapter);

        mAppsListProvider = new AppsListProvider(this, this::onListOfAppsReceived);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppsListProvider.close();
    }

    private void onListOfAppsReceived(List<AppModel> appModels) {
        ArrayList<AppModel> filteredApps = new ArrayList<>();
        for (AppModel appModel : appModels) {
            if (appModel.getActivityType().equals(AppModel.ActivityType.Main)) {
                filteredApps.add(appModel);
            }
        }
        mAppsAdapter.submitList(filteredApps);
    }

    private void onAppClicked(AppModel appModel) {
        startActivity(appModel.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
