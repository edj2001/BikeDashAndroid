package com.grandpaj.BikeDash;

import android.location.Location;
import android.support.v4.util.CircularArray;

/**
 * Created by Linda on 9/7/2016.
 */
public class LocationHistory {
    private CircularArray history;
    private Location lastLocation;
    private double  tripDistance = 0.0;
    private float distanceChange;
    private long timeDifference;

    {
        history = new CircularArray(20);
    }

    public void append(Location newLocation) {
        if(lastLocation != null) {
            tripDistance += lastLocation.distanceTo(newLocation);

            distanceChange = lastLocation.distanceTo(newLocation);
            timeDifference = newLocation.getTime()-lastLocation.getTime();
            /* calculate speed from values in circular array */

        }

        history.addLast(newLocation);
        lastLocation = newLocation;

    }

    public double getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(double newTripDistance){
        tripDistance = newTripDistance;
    }
    public double getCalculatedSpeed() {
        return 0.0;
    }
}
