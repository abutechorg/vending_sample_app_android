package com.mastercard.labs.unattended.vending.demo;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mastercard.labs.unattended.services.PartnerService;
import com.mastercard.labs.unattended.vending.demo.util.LocationUtils;
import com.mastercard.labs.unattended.vending.demo.util.PrefHelper;

/**
 * Created by jameslian on 15/8/16.
 */
public class DemoApplication extends android.app.Application implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = DemoApplication.class.getSimpleName();
    private PartnerService partnerService;


    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        getPreferences().registerOnSharedPreferenceChangeListener(this);
        LocationUtils.init(this);

        initPartnerService();
    }

    private void initPartnerService() {
        try {
            partnerService = new PartnerService(this, PrefHelper.getURL(this));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static DemoApplication getApplication(Activity context) {
        return (DemoApplication) context.getApplication();
    }

    public static DemoApplication getApplication(Service service) {
        return (DemoApplication) service.getApplication();
    }

    public PartnerService getPartnerService() {
        return partnerService;
    }

    @Override
    public void onTerminate() {
        getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onTerminate();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: detected changes in preferences");

        if (key.startsWith("partner_server"))
            initPartnerService();
    }
}
