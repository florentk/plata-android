package fr.ifsttar.geolocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

/**
 * Allow to play geolocation trace for Android
 */

public class GeolocationTraceAndroid extends GeolocationTrace {

    private final LocationManager locationManager;
    private final String provider = "TestPlaiimob";

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

    public GeolocationTraceAndroid(String coordinates, int waitTime, LocationManager locationManager) {
		super(coordinates,waitTime);
        this.locationManager = locationManager;
        if (locationManager.getProvider(provider)==null)
            addTestProvider();
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
	}

    public void addTestProvider() {
        locationManager.addTestProvider(provider, false, false,
                false, false, true, true, true, 0, 5);
        locationManager.setTestProviderEnabled(provider, true);
    }

    public void removeTestProvider() {
        locationManager.removeTestProvider(provider);
    }


    @Override
    public void dispose() {
        super.dispose();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void setTestPosition(double longitude, double latitude, float bearing, float speed){
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

	
	/*static public GeolocationTraceAndroid traceFromFile(String path, int waitTime) throws FileNotFoundException, IOException {
		return traceFromInput(new FileReader(new File(path)),waitTime);
	}
	
	static public GeolocationTraceAndroid traceFromInput(InputStreamReader i, int waitTime) throws IOException{
		BufferedReader br = new BufferedReader(i);
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(' ');
	            line = br.readLine();
	        }
	        return new GeolocationTraceAndroid(sb.toString(),waitTime);
	    
		} finally {
	        br.close();
	    }
	}

	public static void main (String[] args) throws Exception{
		if(args.length == 0)
			return;
		
		GeolocationTraceAndroid t=null;
		
		if(args.length == 2)
			t = traceFromFile(args[0],Integer.parseInt(args[1]));
		if(args.length == 1)
			t = traceFromInput(new InputStreamReader(System.in),Integer.parseInt(args[0]));
			
		t.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		t.start();
		t.join();
	}
	*/
	
}
