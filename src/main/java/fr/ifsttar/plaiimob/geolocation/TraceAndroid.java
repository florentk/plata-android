package fr.ifsttar.plaiimob.geolocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TraceAndroid implements Runnable {

	
	private final List<GpsData> positions; 
	private final int waitTime;
    private final GeoLocationAndroid geo;

    private boolean actif = true;

    public TraceAndroid(GeoLocationAndroid geo, String coordinates, int waitTime) {
		this.waitTime=waitTime;
        this.geo=geo;
		positions = parse(coordinates);
	}
	
	static public double computeTrack(double longi1, double lati1, double longi2, double lati2){
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
			
			if(oldPos!=null)
				track = computeTrack(oldPos.longitude(), oldPos.latitude(), 
						pos.longitude(), pos.latitude());

			l.add(new GpsData(t,pos,0.0,track));
			t+=waitTime;
			oldPos = pos;
		}
		
		return l;
	}

    	@Override
        public void run() {
            //super.run();
            while(actif){
                for(GpsData data:positions){
                    if(!actif) break;
                    try {
                        WGS84 pos = data.getPosition();

                        geo.setTestPosition(pos.longitude(),pos.latitude(),data.getTrack().floatValue(),0.0f);

                        if(waitTime!=0) Thread.sleep(waitTime);

                    } catch(InterruptedException ie) {
                        System.err.println("Waiting time error : " + ie);
                    }
                }
            }
        }

	public void stopTrace(){
        actif = false;
    }
	
	/*static public TraceAndroid traceFromFile(String path, int waitTime) throws FileNotFoundException, IOException {
		return traceFromInput(new FileReader(new File(path)),waitTime);
	}
	
	static public TraceAndroid traceFromInput(InputStreamReader i, int waitTime) throws IOException{
		BufferedReader br = new BufferedReader(i);
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(' ');
	            line = br.readLine();
	        }
	        return new TraceAndroid(sb.toString(),waitTime);
	    
		} finally {
	        br.close();
	    }
	}

	public static void main (String[] args) throws Exception{
		if(args.length == 0)
			return;
		
		TraceAndroid t=null;
		
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
