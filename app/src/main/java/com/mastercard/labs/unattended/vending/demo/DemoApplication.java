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

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mastercard.labs.unattended.services.PartnerService;
import com.mastercard.labs.unattended.vending.demo.util.LocationUtils;
import com.mastercard.labs.unattended.vending.demo.util.PrefHelper;

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
