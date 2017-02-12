package com.grandpaj.BikeDash;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSManager
{
    private static final int gpsMinTime = 2000;
    private static final int gpsMinDistance = 2;

    private static LocationManager locationManager = null;
    private static LocationListener locationListener = null;
    private static GPSCallback gpsCallback = null;

    public GPSManager()
    {
        GPSManager.locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(final Location location)
            {
                if (GPSManager.gpsCallback != null)
                {
                    GPSManager.gpsCallback.onGPSUpdate(location);
                }
            }

            @Override
            public void onProviderDisabled(final String provider)
            {
            }

            @Override
            public void onProviderEnabled(final String provider)
            {
            }

            @Override
            public void onStatusChanged(final String provider, final int status, final Bundle extras)
            {
            }
        };
    }

    public GPSCallback getGPSCallback()
    {
        return GPSManager.gpsCallback;
    }

    public void setGPSCallback(final GPSCallback gpsCallback)
    {
        GPSManager.gpsCallback = gpsCallback;
    }

    public void startListening(final Context context)
    {
        if (GPSManager.locationManager == null)
        {
            GPSManager.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        final Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        final String bestProvider = GPSManager.locationManager.getBestProvider(criteria, true);

        if (bestProvider != null && bestProvider.length() > 0)
        {
            GPSManager.locationManager.requestLocationUpdates(bestProvider, GPSManager.gpsMinTime,
                    GPSManager.gpsMinDistance, GPSManager.locationListener);
        }
        else
        {
            final List<String> providers = GPSManager.locationManager.getProviders(true);

            for (final String provider : providers)
            {
                GPSManager.locationManager.requestLocationUpdates(provider, GPSManager.gpsMinTime,
                        GPSManager.gpsMinDistance, GPSManager.locationListener);
            }
        }
    }

    public void stopListening()
    {
        try
        {
            if (GPSManager.locationManager != null && GPSManager.locationListener != null)
            {
                GPSManager.locationManager.removeUpdates(GPSManager.locationListener);
            }

            GPSManager.locationManager = null;
        }
        catch (final Exception ex)
        {

        }
    }
}