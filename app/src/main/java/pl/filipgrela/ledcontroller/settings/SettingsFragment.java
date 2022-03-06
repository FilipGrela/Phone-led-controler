package pl.filipgrela.ledcontroller.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import pl.filipgrela.ledcontroller.R;

import static android.widget.Toast.LENGTH_LONG;

import java.util.function.LongFunction;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "my_preference_fragment";

    EditTextPreference editTextPreferenceIp;
    EditTextPreference editTextPreferencePort;

    SwitchPreference darkTheme;


    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    public SettingsFragment() {
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preference_screen, rootKey);

        pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        editTextPreferenceIp = findPreference("server_ip");
        editTextPreferenceIp.setSummary(getResources().getString(R.string.edit_text_preference_ip_summary) + ": " + pref.getString("Server_IP", "192.168.8.137"));
        editTextPreferenceIp.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putString("Server_IP", String.valueOf(newValue));
            editor.apply();

            editTextPreferenceIp.setText(pref.getString("Server_IP", "192.168.8.137"));
            editTextPreferenceIp.setSummary(getResources().getString(R.string.edit_text_preference_ip_summary) + ": " + pref.getString("Server_IP", "192.168.8.137"));
            makeToast(getResources().getString(R.string.edit_text_preference_ip_summary) + ": " + pref.getString("Server_IP", "192.168.8.137"));
            return false;
        });


        editTextPreferencePort = findPreference("server_port");
        editTextPreferencePort.setSummary(getResources().getString(R.string.edit_text_preference_port_summary) + ": " + pref.getInt("Server_port", 6061));
        editTextPreferencePort.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putInt("Server_port", Integer.parseInt(String.valueOf(newValue)));
            editor.apply();

            editTextPreferencePort.setText(String.valueOf(pref.getInt("Server_port", 6061)));
            editTextPreferencePort.setSummary(getResources().getString(R.string.edit_text_preference_port_summary) + ": " + pref.getInt("Server_port", 6061));
            makeToast(getResources().getString(R.string.edit_text_preference_port_summary) + ": " + pref.getInt("Server_port", 6061));
            return false;
        });

        darkTheme = findPreference("darkTheme");
        darkTheme.setSummary(((AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) ? "Enable" : "Disable") + " Dark Mode");
        darkTheme.setDefaultValue(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        darkTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                boolean isDarkModeEnabled = Boolean.parseBoolean(String.valueOf(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO));
                Log.d(TAG, String.valueOf(isDarkModeEnabled));

                AppCompatDelegate.setDefaultNightMode(!isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
                darkTheme.setSummary((isDarkModeEnabled ? "Disable" : "Enable") + " Dark Mode");

                Log.d(TAG, ((isDarkModeEnabled) ? "Disable" : "Enable") + " Dark Mode");
                return false;
            }
        });

        //TODO ręczna zmianę jezyka
    }

    @SuppressLint("ShowToast")
    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
