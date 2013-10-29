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
    private final String provider;
    private TraceAndroid tracer = null;

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

    public GeoLocationAndroid(LocationManager locationManager, boolean fake) {
        this.locationManager = locationManager;

        if(fake)
            this.provider = "TestPlaiimob";
        else
            this.provider = LocationManager.GPS_PROVIDER;



        if (fake && locationManager.getProvider(provider)==null)
            addTestProvider();

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
    }

    public void addTestProvider() {

        locationManager.addTestProvider(provider, false, false,
                false, false, true, true, true, 0, 5);
        locationManager.setTestProviderEnabled(provider, true);


        //locationManager.setTestProviderStatus(provider, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

    }

    public void removeTestProvider() {
        locationManager.removeTestProvider(provider);
    }

    public void setTestPosition(double longitude, double latitude, float bearing, float speed){
        Location mockLocation = new Location(provider); // a string
        mockLocation.setLatitude(latitude);  // double
        mockLocation.setLongitude(longitude);
        mockLocation.setAltitude(0.0);
        mockLocation.setAccuracy(1.0f);
        mockLocation.setBearing(bearing);
        mockLocation.setSpeed(speed);
        mockLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= 17) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }

        locationManager.setTestProviderLocation( provider, mockLocation);
    }

    public void startTrace(String coordinate, int waitTime){
        tracer = new TraceAndroid(this,coordinate,waitTime);
        new Thread(tracer).start();
    }

    public void stopTrace(){
        if(tracer != null)
            tracer.stopTrace();
    }

    @Override
    public void dispose() {
        stopTrace();
        locationManager.removeUpdates(locationListener);
    }
}
