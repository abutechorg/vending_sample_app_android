package com.mastercard.labs.unattended.vending.demo;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by jameslian on 16/8/16.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int[] PREF_KEYS = {R.string.pref_key_latitude,
            R.string.pref_key_longitude,
            R.string.pref_key_partner_server, R.string.pref_key_vending_controller_mock_error};

    private boolean floatCheck(String floatStr) {
        if (!floatStr.toString().equals("") && floatStr.toString().matches("-?[0-9\\.]*")) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Invalid Value", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initPreferencesSummary();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference instanceof EditTextPreference) {
            String newValue = (String) o;
            Log.d(TAG, "onPreferenceChange: value" + newValue);
            if (preference.getKey().equals(getString(R.string.pref_key_latitude)) || preference.getKey().equals(getString(R.string.pref_key_longitude))) {
                if (!floatCheck(newValue))
                    return false;
            }
            preference.setSummary(newValue);
            return true;
        } else if (preference instanceof ListPreference) {
            //for toggling the location settings
            Log.d(TAG, "onPreferenceChange: list preferences");
            String pref_value = (String) o;
            preference.setSummary(pref_value);
            return true;

        }
        return false;
    }

    private void initPreferencesSummary() {
        for (int x = 0; x < PREF_KEYS.length; x++) {
            Preference pref = findPreference(getString(PREF_KEYS[x]));
            if (pref != null) {
                pref.setOnPreferenceChangeListener(this);
                if (pref instanceof EditTextPreference)
                    onPreferenceChange(pref, ((EditTextPreference) pref).getText());
                else if (pref instanceof ListPreference)
                    onPreferenceChange(pref, ((ListPreference) pref).getValue());
            }
        }

    }
}

