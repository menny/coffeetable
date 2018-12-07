package net.evendanan.coffeetable;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

public class HomeScreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_content, new AppsGridFragment())
                    .commit();
        }
    }
}
