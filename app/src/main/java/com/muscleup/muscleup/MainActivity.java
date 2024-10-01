package com.muscleup.muscleup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.muscleup.muscleup.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_awards, R.id.navigation_statistics, R.id.navigation_home, R.id.navigation_workouts, R.id.navigation_settings)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnItemSelectedListener(item -> {
            try {
                int currentId = navController.getCurrentDestination().getId();
                int targetId = item.getItemId();
                if (navController.getCurrentDestination() != null && currentId == targetId)
                    navController.popBackStack();
                if(currentId == R.id.navigation_home && targetId == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home);
                    return false;
                }
                else if(currentId == R.id.navigation_workouts && targetId == R.id.navigation_workouts) {
                    navController.navigate(R.id.navigation_workouts);
                    return false;
                }
                else if(currentId == R.id.navigation_awards && targetId == R.id.navigation_awards) {
                    navController.navigate(R.id.navigation_awards);
                    return false;
                }
                else if(currentId == R.id.navigation_settings && targetId == R.id.navigation_settings) {
                    navController.navigate(R.id.navigation_settings);
                    return false;
                }
                else if(currentId == R.id.navigation_statistics && targetId == R.id.navigation_statistics) {
                    navController.navigate(R.id.navigation_statistics);
                    return false;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            } catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            return false;
        });
    }
}
