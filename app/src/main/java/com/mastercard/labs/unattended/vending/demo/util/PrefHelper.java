package com.mastercard.labs.unattended.vending.demo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mastercard.labs.unattended.vending.demo.R;
import com.mastercard.labs.unattendedsdk.VendControllerFactory;

/**
 * Created by jameslian on 15/8/16.
 */
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
