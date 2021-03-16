package net.evendanan.coffeetable.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import net.evendanan.coffeetable.R;
import net.evendanan.coffeetable.databinding.AboutAppBinding;

public class AboutFragment extends Fragment {

    private AboutAppBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AboutAppBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.navigateBackToSettings.setOnClickListener(v ->
                NavHostFragment.findNavController(AboutFragment.this)
                        .navigate(R.id.action_About_to_Settings));
    }
}
