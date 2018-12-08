package net.evendanan.coffeetable.settings;

import android.os.Bundle;

import net.evendanan.chauffeur.lib.FragmentChauffeurActivity;
import net.evendanan.chauffeur.lib.SimpleTransitionExperience;
import net.evendanan.chauffeur.lib.TransitionExperience;
import net.evendanan.coffeetable.R;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class MainSettingsFragment extends PreferenceFragmentCompat {
    private static final TransitionExperience NO_ANIMATION = new SimpleTransitionExperience(0, 0, 0, 0);

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main_settings);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof FragmentChauffeurActivity) {
            final FragmentChauffeurActivity driver = (FragmentChauffeurActivity) activity;
            if (preference.getKey().equals("about")) {
                driver.addFragmentToUi(new AboutFragment(), NO_ANIMATION);
            } else if (preference.getKey().equals("select_apps")) {
                driver.addFragmentToUi(new AppsSelectionFragment(), NO_ANIMATION);
            }
        }
        return super.onPreferenceTreeClick(preference);
    }
}
