package pl.filipgrela.ledcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import pl.filipgrela.ledcontroller.fragments.home.HomeFragment;
import pl.filipgrela.ledcontroller.fragments.hsvcontroller.HsvControllerFragment;
import pl.filipgrela.ledcontroller.settings.SettingsActivity;
import pl.filipgrela.ledcontroller.settings.ShutdownDialog;

import static android.widget.Toast.LENGTH_LONG;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{
    private final String TAG = "MainActivity";


    SettingsActivity settingsActivity;

    BottomNavigationView bottomNavigationView;
    NavController navController;

    boolean isDarkModeDisabled = Boolean.parseBoolean(String.valueOf(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(isDarkModeDisabled ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.fragmentContainerView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.hsvControllerFragment, R.id.fragmentTest)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView); // fragment z maina
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        settingsActivity = new SettingsActivity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_menu:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.shutdown:

                DialogFragment shutdownDialog = new ShutdownDialog(getApplicationContext());
                shutdownDialog.show(getSupportFragmentManager(), TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ShowToast")
    private void makeToast(String msg){
        Toast.makeText(getApplicationContext(), msg, LENGTH_LONG).show();
    }
}