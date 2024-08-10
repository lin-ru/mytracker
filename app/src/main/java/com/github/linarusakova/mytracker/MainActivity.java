package com.github.linarusakova.mytracker;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.linarusakova.mytracker.util.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.linarusakova.mytracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public DBHelper dbHelper;
    private SQLiteDatabase db;
    SwitchCompat switchMode;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String version;
    int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        //night mode changer
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        dbHelper = DBHelper.getInstance(this.getBaseContext());
        db = dbHelper.getWritableDatabase();
        firstStartApp(true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void firstStartApp(boolean isFirstStart) {
        if (isFirstStart) {
            dbHelper.prepareAllDataBases(db);
        }
    }

    public void showAlertDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.info_dialog_titile));

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.info_page_layout, null);
        TextView textViewVersion = customLayout.findViewById(R.id.version);
        textViewVersion.setText(version);
        switchMode = customLayout.findViewById(R.id.switchMode);
        TextView donate = customLayout.findViewById(R.id.donate);
        donate.setMovementMethod(LinkMovementMethod.getInstance());
        switchMode.setChecked(nightMode);
        final int[] ThemeMODE = new int[1];
        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode.setChecked(switchMode.isChecked());
                nightMode = switchMode.isChecked();
            }
        });

        builder.setView(customLayout);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (switchMode.isChecked()) {
                ThemeMODE[0] = 2;
            } else {
                ThemeMODE[0] = 1;
            }
            editor = sharedPreferences.edit();
            editor.putBoolean("nightMode", switchMode.isChecked());
            editor.apply();
            sendDialogDataToActivity(getString(R.string.toast_theme_cnaged), ThemeMODE[0]);
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendDialogDataToActivity(String data, int ThemeMODE) {
        AppCompatDelegate.setDefaultNightMode(ThemeMODE);
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }
}