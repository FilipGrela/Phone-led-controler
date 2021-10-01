package pl.filipgrela.ledcontroller.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import pl.filipgrela.ledcontroller.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String FRAGMENT_TAG = "my_preference_fragment";

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preference_screen, rootKey);
    }
}
