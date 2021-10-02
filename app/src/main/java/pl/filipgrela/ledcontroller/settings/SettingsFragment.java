package pl.filipgrela.ledcontroller.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import pl.filipgrela.ledcontroller.R;

import static android.widget.Toast.LENGTH_LONG;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String FRAGMENT_TAG = "my_preference_fragment";

    EditTextPreference editTextPreferenceIp;
    EditTextPreference editTextPreferencePort;

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

        editTextPreferenceIp = (EditTextPreference)findPreference("server_ip");
        editTextPreferenceIp.setSummary(getResources().getString(R.string.edit_text_preference_ip_summary) + ": " + pref.getString("Server_IP", "192.168.8.137"));
        editTextPreferenceIp.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putString("Server_IP", String.valueOf(newValue));
            editor.apply();

            editTextPreferenceIp.setText(pref.getString("Server_IP", "192.168.8.137"));
            editTextPreferenceIp.setSummary(getResources().getString(R.string.edit_text_preference_ip_summary) + ": " + pref.getString("Server_IP", "192.168.8.137"));
            makeToast(getResources().getString(R.string.edit_text_preference_ip_summary) + ": " + pref.getString("Server_IP", "192.168.8.137"));
            return false;
        });


        editTextPreferencePort = (EditTextPreference)findPreference("server_port");
        editTextPreferencePort.setSummary(getResources().getString(R.string.edit_text_preference_port_summary) + ": " + pref.getInt("Server_port", 6061));
        editTextPreferencePort.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putInt("Server_port", Integer.parseInt(String.valueOf(newValue)));
            editor.apply();

            editTextPreferencePort.setText(String.valueOf(pref.getInt("Server_port", 6061)));
            editTextPreferencePort.setSummary(getResources().getString(R.string.edit_text_preference_port_summary) + ": " + pref.getInt("Server_port", 6061));
            makeToast(getResources().getString(R.string.edit_text_preference_port_summary) + ": " + pref.getInt("Server_port", 6061));
            return false;
        });
    }

    @SuppressLint("ShowToast")
    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
