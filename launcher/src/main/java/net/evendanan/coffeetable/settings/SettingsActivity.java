package net.evendanan.coffeetable.settings;

import android.os.Bundle;

import net.evendanan.chauffeur.lib.permissions.PermissionsFragmentChauffeurActivity;
import net.evendanan.coffeetable.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SettingsActivity extends PermissionsFragmentChauffeurActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    @Override
    protected int getFragmentRootUiElementId() {
        return R.id.root_content;
    }

    @NonNull
    @Override
    protected Fragment createRootFragmentInstance() {
        return new MainSettingsFragment();
    }
}
