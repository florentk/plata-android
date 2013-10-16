package fr.ifsttar.plaiimob.geolocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by florent on 14/10/13.
 */
public class GeoLocationAndroid extends Geolocation {


    private LocationManager locationManager;
    String provider;

    public GeoLocationAndroid(LocationManager locationManager, String provider) {
        this.locationManager = locationManager;
        this.provider = provider;

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location l) {
                // Called when a new location is found by the network location provider.
                setCurrentPos(new WGS84(l.getLongitude(),l.getLatitude(),l.getAltitude()));
                setCurrentSpeed((double)l.getSpeed());
                setCurrentTrack((double)l.getBearing());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);

    }

    public void addTestProvider() {
        locationManager.addTestProvider(/*LocationManager.GPS_PROVIDER*/provider, false, false,
                 false, false, true, true, true, 0, 5);
        locationManager.setTestProviderEnabled(/*LocationManager.GPS_PROVIDER*/provider, true);
    }

    public void removeTestProvider() {
        locationManager.removeTestProvider(provider);
    }

    public void setTestPosition(double longitude, double latitude){
        Location mockLocation = new Location("Ifsttar"); // a string
        mockLocation.setLatitude(latitude);  // double
        mockLocation.setLongitude(longitude);
        mockLocation.setAltitude(0.0);
        mockLocation.setTime(System.currentTimeMillis());
        locationManager.setTestProviderLocation( provider, mockLocation);
    }

    @Override
    public void dispose() {

    }
}
