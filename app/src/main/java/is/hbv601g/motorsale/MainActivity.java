package is.hbv601g.motorsale;


import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import is.hbv601g.motorsale.databinding.ActivityMainBinding;
import is.hbv601g.motorsale.viewModels.UserViewModel;

import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private UserViewModel userViewModel;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.login ) {
                if (userViewModel.getUser().getValue() != null) {
                    navController.navigate(R.id.userFragment);
                    return true;
                }
                else {
                    navController.navigate(R.id.loginFragment);
                    return true;
                }
            }

            if (itemId == R.id.home) {
                navController.navigate(R.id.listingsFragment);
                return true;
            }
            if (userViewModel.getUser().getValue()!=null ){
                if (itemId == R.id.favoritesFragment){
                    navController.navigate(R.id.favoritesFragment);
                    return true;
                }
            }
            else {
                Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            }


            return NavigationUI.onNavDestinationSelected(item, navController);
        });

    }


    /**
     * Called when an item in the options menu is selected.
     *
     * @return True if the item was handled, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}