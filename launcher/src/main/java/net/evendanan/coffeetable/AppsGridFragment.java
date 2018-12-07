package net.evendanan.coffeetable;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AppsGridFragment extends Fragment {

    private AppsAdapter mAppsAdapter;
    private AppsListProvider mAppsListProvider;

    public AppsGridFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.apps_grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView appsGrid = view.findViewById(R.id.app_recycler_view);

        appsGrid.setHasFixedSize(true);
        appsGrid.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.apps_grid_column_count)));

        mAppsAdapter = new AppsAdapter(getActivity(), this::onAppClicked);
        appsGrid.setAdapter(mAppsAdapter);

        mAppsListProvider = new AppsListProvider(getContext(), this::onListOfAppsReceived);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAppsListProvider.close();
    }

    private void onAppClicked(AppModel appModel) {
        startActivity(appModel.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
