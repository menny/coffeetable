package net.evendanan.coffeetable.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.evendanan.coffeetable.AppsAdapterBase;
import net.evendanan.coffeetable.R;
import net.evendanan.coffeetable.model.AppModel;
import net.evendanan.coffeetable.model.AppsListProvider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppsSelectionFragment extends Fragment {

    private AppsSelectionAdapter mAppsAdapter;
    private AppsListProvider mAppsListProvider;
    private VisibleApps mVisibleApps;
    private SharedPreferences mPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.apps_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        setupFilterCheckBox(view, R.id.filter_out_non_main, mPreferences, "prefs_key_filter_out_not_main", true);
        setupFilterCheckBox(view, R.id.filter_out_not_export, mPreferences, "prefs_key_filter_out_not_export", true);
        setupFilterCheckBox(view, R.id.filter_out_disabled, mPreferences, "prefs_key_filter_out_disabled", true);

        RecyclerView appsGrid = view.findViewById(R.id.app_recycler_view);

        appsGrid.setHasFixedSize(true);
        appsGrid.setLayoutManager(new LinearLayoutManager(getActivity()));

        mVisibleApps = new VisibleApps(getContext(), this::onAppVisibilityChanged);

        mAppsAdapter = new AppsSelectionAdapter(getActivity(), this::onAppClicked, mVisibleApps);
        appsGrid.setAdapter(mAppsAdapter);

        mAppsListProvider = new AppsListProvider(getContext(), this::onAppsListChanged);
    }

    private void onAppsListChanged(List<AppModel> appModels) {
        final boolean filterOutNotMain = mPreferences.getBoolean("prefs_key_filter_out_not_main", true);
        final boolean filterDisabled = mPreferences.getBoolean("prefs_key_filter_out_not_export", true);
        final boolean filterNotExported = mPreferences.getBoolean("prefs_key_filter_out_disabled", true);
        ArrayList<AppModel> filteredApps = new ArrayList<>();
        for (AppModel appModel : appModels) {
            if (!mVisibleApps.isVisibleApp(appModel)) {
                if (filterOutNotMain && !appModel.getActivityType().equals(AppModel.ActivityType.Main)) continue;
                if (filterDisabled && !appModel.isEnabled()) continue;
                if (filterNotExported && !appModel.isExported()) continue;
            }
            filteredApps.add(appModel);
        }

        mAppsAdapter.submitList(filteredApps);
    }

    private void setupFilterCheckBox(@NonNull View rootView, @IdRes int checkboxId, final SharedPreferences pref, final String prefsKey, boolean defaultValue) {
        CheckBox filter = rootView.findViewById(checkboxId);
        filter.setChecked(pref.getBoolean(prefsKey, defaultValue));
        filter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pref.edit().putBoolean(prefsKey, isChecked).apply();
            mAppsListProvider.forceRefresh();
        });
    }

    private void onAppVisibilityChanged() {
        if (mAppsAdapter != null) {
            mAppsAdapter.notifyDataSetChanged();
        }
    }

    private void onAppClicked(AppModel appModel) {
        final boolean visibleApp = mVisibleApps.isVisibleApp(appModel);
        mVisibleApps.setAppVisibility(appModel, !visibleApp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAppsListProvider.close();
        mVisibleApps.close();
    }

    private static class AppsSelectionAdapter extends AppsAdapterBase<ActivityViewModel> {
        private final VisibleApps mVisibleApps;

        AppsSelectionAdapter(Context context, Consumer<AppModel> clickHandler, VisibleApps visibleApps) {
            super(context, clickHandler);
            mVisibleApps = visibleApps;
        }

        @Override
        protected ActivityViewModel createAppsViewHolder(LayoutInflater inflater, ViewGroup parent, Consumer<AppModel> clickHandler) {
            return new ActivityViewModel(
                    mVisibleApps,
                    inflater.inflate(R.layout.activity_row_item, parent, false),
                    clickHandler);
        }
    }

    private static class ActivityViewModel extends AppsAdapterBase.AppsViewModel {
        private final TextView mActivityClassName;
        private final AppCompatCheckBox mAppSelected;
        private final VisibleApps mPrefs;

        ActivityViewModel(@NonNull VisibleApps prefs, @NonNull View itemView, Consumer<AppModel> clickHandler) {
            super(itemView, clickHandler);
            mPrefs = prefs;
            mActivityClassName = itemView.findViewById(R.id.activity_class_name);
            mAppSelected = itemView.findViewById(R.id.app_selected_check_box);
            mAppSelected.setClickable(false);
        }

        @Override
        public void bind(AppModel item) {
            super.bind(item);
            mActivityClassName.setText(item.getActivityName());
            mAppSelected.setChecked(mPrefs.isVisibleApp(item));
        }
    }
}
