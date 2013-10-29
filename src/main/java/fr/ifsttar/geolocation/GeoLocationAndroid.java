package fr.ifsttar.geolocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

/**
 * Geolocation implementation for Android. Allow to use GPS receiver in a Android device
 *
 * Created by florent on 14/10/13.
 */
public class GeoLocationAndroid extends Geolocation {


    private final LocationManager locationManager;

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location l) {
            setCurrentPos(new WGS84(l.getLongitude(),l.getLatitude(),l.getAltitude()));
            setCurrentSpeed((double)l.getSpeed());
            setCurrentTrack((double)l.getBearing());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    public GeoLocationAndroid(LocationManager locationManager) {
        this.locationManager = locationManager;
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public void dispose() {
        locationManager.removeUpdates(locationListener);
    }
}
