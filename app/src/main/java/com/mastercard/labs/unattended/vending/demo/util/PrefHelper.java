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

package com.mastercard.labs.unattended.vending.demo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mastercard.labs.unattended.vending.demo.R;
import com.mastercard.labs.unattendedsdk.VendControllerFactory;

public class PrefHelper {
    private static final String TAG = PrefHelper.class.getSimpleName();

    public static String getURL(Context context) throws Exception {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selection = sharedPreferences.getString(context.getString(R.string.pref_key_partner_server), context.getString(R.string.pref_values_partner_server_default));
        return selection;

    }

    public static float[] getLatLog(Context context) {
        float[] returnLocation = new float[2];
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isManualLocation = sharedPreferences.getBoolean(context.getString(R.string.pref_key_toggle_location), true);
        if (isManualLocation) {
            returnLocation[0] = Float.parseFloat(sharedPreferences.getString(context.getString(R.string.pref_key_latitude), "0.0f"));
            returnLocation[1] = Float.parseFloat(sharedPreferences.getString(context.getString(R.string.pref_key_longitude), "0.0f"));
            return returnLocation;
        }

        return null;

    }

    public static String[] getMockConfig(Context context) {
        String[] returnConfigs = new String[1];

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        returnConfigs[0] = sharedPreferences.getString(context.getString(R.string.pref_key_vending_controller_mock_error), "");

        return returnConfigs;
    }

    public static String getVendControllerType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isMock = sharedPreferences.getBoolean(context.getString(R.string.pref_key_vending_controller_type), false);
        return isMock ? VendControllerFactory.VEND_CONTROLLER_TYPE_MOCK : VendControllerFactory.VEND_CONTROLLER_TYPE;
    }

}
