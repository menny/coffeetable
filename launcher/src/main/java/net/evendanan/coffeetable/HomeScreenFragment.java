package net.evendanan.coffeetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import net.evendanan.coffeetable.databinding.HomescreenBinding;
import net.evendanan.coffeetable.model.AppModel;
import net.evendanan.coffeetable.model.AppsListProvider;
import net.evendanan.coffeetable.settings.VisibleApps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreenFragment extends Fragment {

    private HomescreenBinding binding;
    private HomeScreenAppsAdapter mAppsAdapter;
    private AppsListProvider mAppsListProvider;
    private VisibleApps mVisibleApps;
    private AppModel mSettingsFragmentAppModel;
    private AppModel mLoadingAppModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = HomescreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSettingsFragmentAppModel = new AppModel(
                getString(R.string.settings_app_name),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_settings, null),
                true, false, BuildConfig.APPLICATION_ID, "AppsSelectionFragment", null, false);
        mLoadingAppModel = new AppModel(
                getString(R.string.loading_apps),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_loading, null),
                true, false, BuildConfig.APPLICATION_ID, "Loading", null, false);

        binding.appRecyclerView.setHasFixedSize(true);
        binding.appRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.apps_grid_column_count)));

        mAppsAdapter = new HomeScreenAppsAdapter(getContext(), this::onAppClicked);
        binding.appRecyclerView.setAdapter(mAppsAdapter);

        mVisibleApps = new VisibleApps(requireContext(), this::onAppsVisibilityChanged);

        mAppsListProvider = new AppsListProvider(requireContext(), this::onListOfAppsReceived);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVisibleApps.close();
        mAppsListProvider.close();
        binding = null;
    }

    private void onAppsVisibilityChanged() {
        if (mAppsListProvider != null) {
            mAppsListProvider.forceRefresh();
        }
    }

    private void onAppClicked(AppModel appModel) {
        if (appModel != mLoadingAppModel) {
            if (appModel == mSettingsFragmentAppModel) {
                //navigate to settings
                NavHostFragment.findNavController(this).navigate(R.id.action_HomeScreen_to_SettingsFragment);
            } else {
                startActivity(appModel.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
    }

    private void onListOfAppsReceived(List<AppModel> appModels) {
        if (appModels.isEmpty()) {
            //loading state
            mAppsAdapter.setItems(Collections.singletonList(mLoadingAppModel));
        } else {
            ArrayList<AppModel> filteredApps = new ArrayList<>();
            for (AppModel appModel : appModels) {
                if (mVisibleApps.isVisibleApp(appModel)) {
                    filteredApps.add(appModel);
                }
            }

            filteredApps.add(mSettingsFragmentAppModel);
            mAppsAdapter.setItems(filteredApps);
        }
    }

    private static class HomeScreenAppsAdapter extends AppsAdapterBase<AppsAdapterBase.AppsViewModel> {
        HomeScreenAppsAdapter(Context context, Consumer<AppModel> clickHandler) {
            super(context, clickHandler);
        }

        @Override
        protected AppsAdapterBase.AppsViewModel createAppsViewHolder(LayoutInflater inflater, ViewGroup parent, Consumer<AppModel> clickHandler) {
            return new AppsViewModel(inflater.inflate(R.layout.home_screen_item_cell, parent, false), clickHandler);
        }
    }
}