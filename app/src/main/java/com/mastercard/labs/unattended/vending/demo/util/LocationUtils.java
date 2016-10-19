package com.mastercard.labs.unattended.vending.demo.util;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by antoninovitale on 19/01/2016.
 */
public class LocationUtils {
    private static final String TAG = LocationUtils.class.getSimpleName();
    private static final int TIMEOUT_FOR_NOTIFICATION = 10000;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static LocationUtils instance;

    private boolean requestingLocation;
    private boolean locationFound;
    private Location lastLocation;
    private Handler defaultHandler;
    private LocationManager locationManager;
    private Context mContext;

    private LocationUtils() {
        defaultHandler = new Handler();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new LocationUtils();
            instance.setContext(context);
        }
    }

    public static LocationUtils getInstance() {
        if (instance == null) {
            throw new NullPointerException("You must call init first!");
        }
        return instance;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    private void setContext(Context context) {
        this.mContext = context;
    }

    public boolean isLocationProviderAvailable() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public Location getLocation(){
        float[] manualLocation = PrefHelper.getLatLog(mContext);
        if(manualLocation == null)
        {
            return getBestLastKnownLocation();
        }
        else
        {
            Location rLocation = new Location(LocationManager.GPS_PROVIDER);
            rLocation.setLatitude(manualLocation[0]);
            rLocation.setLongitude(manualLocation[1]);
            return rLocation;
        }
    }

    public Location getBestLastKnownLocation() {
        Location gpslocation = getLastKnownLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation = getLastKnownLocationByProvider(LocationManager.NETWORK_PROVIDER);
        //if we have only one location available, the choice is easy
        if (gpslocation == null) {
            if (networkLocation == null) {
                Log.d(TAG, "No known GPS or Network available");
            } else {
                Log.d(TAG, "No GPS Location available. Use network");
            }
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d(TAG, "No Network Location available");
            return gpslocation;
        }

        //a locationupdate is considered 'old' if its older than the configured update interval. this means, we didn't get a
        //update from this provider since the last check
        long old = System.currentTimeMillis() - 30 * 60 * 1000;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);

        //gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location");
            return gpslocation;
        }

        //gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network");
            return networkLocation;
        }

        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private Location getLastKnownLocationByProvider(String provider) {
        Location location = null;
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot acces Provider " + provider);
        }
        return location;
    }

    /**
     * A ridiculous manual request due to issues with google's service - which often requires phone restarts
     *
     * @param lat
     * @param lng
     * @return
     */
    public List<Address> getFromLocation(double lat, double lng) {
        List<Address> retList = null;
        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language=" + Locale.getDefault().getCountry(), lat, lng);
        try {
            URL url = new URL(address);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(httpURLConnection.getInputStream());
            StringBuilder stringBuilder = new StringBuilder();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            retList = new ArrayList<>();
            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String indiStr = result.getString("formatted_address");
                    Address addr = new Address(Locale.US);
                    JSONArray myarray = new JSONArray(result.getString("address_components"));
                    for (int i1 = 0; i1 < myarray.length(); i1++) {
                        JSONObject jsonobject2 = myarray.getJSONObject(i1);
                        if (jsonobject2.getString("types").contains("country")) {
                            addr.setCountryCode(jsonobject2.getString("short_name"));
                        }
                    }
                    addr.setAddressLine(0, indiStr);
                    retList.add(addr);
                }
            }
        } catch (Exception e) {
            Log.e("MyGeocoder", "Error calling Google geocode webservice.", e);
        }
        return retList;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private Runnable mNoLocationFoundRunnable = new Runnable() {
        @Override
        public void run() {
            requestingLocation = false;
            Log.d(TAG, "Manual timeout fired - stop looking for location update");
            locationManager.removeUpdates(onLocationChangeListener);
        }
    };

    private final LocationListener onLocationChangeListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            requestingLocation = false;
            Log.d(TAG, "onlocationChanged");

            if (location != null) {
                try {
                    defaultHandler.removeCallbacks(mNoLocationFoundRunnable);
                    if (locationFound && !isBetterLocation(location, lastLocation)) {
                        Log.d(TAG, "Ignore -Not as good a fix");
                        locationManager.removeUpdates(this);
                        return;
                    }
                    locationFound = true;
                    lastLocation = location;
                    locationManager.removeUpdates(this);
                    Log.d(TAG, "Found new location" + lastLocation);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }
            }
        }

        public void onProviderDisabled(String provider) {
            requestingLocation = false;
            Log.d(TAG, "onProviderDisabled" + provider);

        }

        public void onProviderEnabled(String provider) {
            requestingLocation = false;
            // required for interface, not used
            Log.d(TAG, "onProviderEnabled");

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            requestingLocation = false;
            // required for interface, not used
            Log.d(TAG, "onStatusChanged");

        }
    };

    public void requestLocationUpdate(boolean useLastLocationWhileFixing) {
        requestLocationUpdate(useLastLocationWhileFixing, TIMEOUT_FOR_NOTIFICATION);
    }

    private void requestLocationUpdate(boolean useLastLocationWhileFixing, int timeoutForNotification) {
        try {
            if (requestingLocation) {
                Log.d(TAG, "Already requesting location");
                if (useLastLocationWhileFixing) {
                    setupLastLocation();
                }
                return;
            }
            requestingLocation = true;
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled && isGPSEnabled) {
                Log.d("TAG", "both network and gps available");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, onLocationChangeListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, onLocationChangeListener);
                if (useLastLocationWhileFixing) {
                    setupLastLocation();
                }
                defaultHandler.postDelayed(mNoLocationFoundRunnable, timeoutForNotification);
            } else if (isGPSEnabled) {
                Log.d("TAG", "only gps");

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, onLocationChangeListener);
                if (useLastLocationWhileFixing) {
                    setupLastLocation();
                }
                defaultHandler.postDelayed(mNoLocationFoundRunnable, timeoutForNotification);

            } else if (isNetworkEnabled) {
                Log.d("TAG", "only network");

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, onLocationChangeListener);
                if (useLastLocationWhileFixing) {
                    setupLastLocation();
                }
                defaultHandler.postDelayed(mNoLocationFoundRunnable, timeoutForNotification);

            }
        } catch (Exception e) {
            Log.e(TAG, "e", e);
        }
    }

    public void removeLocationUpdates() {
        if (locationManager == null) {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        locationManager.removeUpdates(onLocationChangeListener);
    }

    private void setupLastLocation() {
        lastLocation = getBestLastKnownLocation();
        if (lastLocation != null) {
            locationFound = true;
        }
    }

}