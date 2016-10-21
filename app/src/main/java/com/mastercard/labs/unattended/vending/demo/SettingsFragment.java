/*
 * Copyright 2016 MasterCard International.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * Neither the name of the MasterCard International Incorporated nor the names of its
 * contributors may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package com.mastercard.labs.unattended.vending.demo;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

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

