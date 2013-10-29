package fr.ifsttar.geolocation;


import java.util.ArrayList;
import java.util.List;

import fr.ifsttar.utils.Physics;

/**
 * Allow to play geolocation trace for Android
 */

public class GeolocationTrace extends Geolocation implements Runnable {


    protected final List<GpsData> positions;
	protected final int waitTime;

    private boolean actif = true;

    public GeolocationTrace(String coordinates, int waitTime) {
		this.waitTime=waitTime;
		positions = parse(coordinates);
	}
	
	static private double computeTrack(double longi1, double lati1, double longi2, double lati2){
		double dx = longi2-longi1;
		double dy = lati2-lati1;
		double a = 0;
		
		if (dx >= 0 && dy < 0) {
			a = 180 - Math.toDegrees(Math.atan(dx/-dy)) ;
		} else if (dx < 0 && dy > 0) {
			a = 360 - Math.toDegrees(Math.atan(-dx/dy));
		} else if (dx <= 0 && dy < 0) {
			a = 180 + Math.toDegrees(Math.atan(dx/dy));
		} else if (dy != 0) {
			//dx >= 0 && dy > 0 
			a = Math.toDegrees(Math.atan(dx/dy));
		} else {
			//dy==0
			if (dx>0) a = 90; else a = 270;
		}
		
		return a;
	}

    private double computeSpeed(double longi1, double lati1, double longi2, double lati2){
        double dx = longi2-longi1;
        double dy = lati2-lati1;
        return (Physics.cartesianDistance(dx,dy) / waitTime) * 1000.0;
    }

    static  private WGS84 parsePos(String pos){
		pos.trim();
		
		String[] tokens = pos.split(",");
		
		if (tokens.length == 3)
			return new WGS84(
					Double.parseDouble(tokens[0]),
					Double.parseDouble(tokens[1]),
					Double.parseDouble(tokens[2]));
		else
			return null;
	}

    private List<GpsData> parse(String coordinates) {
		ArrayList<GpsData> l = new ArrayList<GpsData>();
		double t=0;
		WGS84 oldPos = null;
		
		coordinates.trim();
		String[] tokens = coordinates.split(" ");
		
		for (String spos:tokens) {
			WGS84 pos = parsePos(spos);
			double track = 0;
            double speed = 0;
			
			if(oldPos!=null){
				track = computeTrack(oldPos.longitude(), oldPos.latitude(), 
						pos.longitude(), pos.latitude());
                speed = computeSpeed(oldPos.longitude(), oldPos.latitude(),
                        pos.longitude(), pos.latitude());
            }

			l.add(new GpsData(t,pos,speed,track));
			t+=waitTime;
			oldPos = pos;
		}
		
		return l;
	}

    protected void setTestPosition(double longitude, double latitude, float bearing, float speed){
        setCurrentPos(new WGS84(longitude,latitude,0.0));
        setCurrentSpeed((double)speed);
        setCurrentTrack((double)bearing);
    }

    	@Override
        public void run() {
            //super.run();
            while(actif){
                for(GpsData data:positions){
                    if(!actif) break;
                    try {
                        WGS84 pos = data.getPosition();

                        setTestPosition(
                                pos.longitude(),
                                pos.latitude(),
                                data.getTrack().floatValue(),
                                data.getSpeed().floatValue()
                        );

                        if(waitTime!=0) Thread.sleep(waitTime);

                    } catch(InterruptedException ie) {
                        System.err.println("Waiting time error : " + ie);
                    }
                }
            }
        }



    public void startTrace(){
        new Thread(this).start();
    }

	public void stopTrace(){
        actif = false;
    }

    @Override
    public void dispose() {
        stopTrace();
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
