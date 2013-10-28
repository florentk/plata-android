package fr.ifsttar.plaiimob.cmo.dashboard;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import fr.ifsttar.plaiimob.cmo.management.CMOManagement;
import fr.ifsttar.plaiimob.cmo.management.CMOTableEntry;
import fr.ifsttar.plaiimob.cmo.utils.Physics;
import fr.ifsttar.plaiimob.geolocation.Geolocation;
import fr.ifsttar.plaiimob.geolocation.WGS84;

public class CrossingCMO implements Indicator {
    final DecimalFormat format = new DecimalFormat("#.##");
	public class CrossingCMOEntry {
		final Double t; //predicted time before reach the cross point
		final Double t_cmo; //predicted time before the cmo reach the cross point
		final Double d; // distance to the CMO
		final Double azimuth; //track for reach CMO
        final Short decision;
        final CMOTableEntry cmo;

		public CrossingCMOEntry(Double t, Double t_cmo, Double d, Double azimuth, Short decision, CMOTableEntry cmo) {
			this.t = t;
			this.t_cmo = t_cmo;
			this.d = d;
			this.azimuth = azimuth;
            this.decision = decision;
            this.cmo = cmo;
		}

        public CrossingCMOEntry(CMOTableEntry cmo) {
            this.t = 0.0;
            this.t_cmo =0.0;
            this.d = 0.0;
            this.azimuth = 0.0;
            this.decision = DECISION_NONE;
            this.cmo = cmo;
        }
		@Override
	    public String toString() {
			return
                "cmo=" + cmo +
                " t=" + format.format(t) +
				" s, t_cmo=" + format.format(t_cmo) +
				" s, d=" + format.format(d) + " m" +
				" m, a=" + format.format(azimuth) +
                " °, decision=" + decision;

            //return format.format(d) + " m";
		}
	}
	
	/**none hazard*/
	public static final short DECISION_NONE = 0;
	/**warning*/
	public static final short DECISION_WARNING = 1;
	/**hazard !*/
	public static final short DECISION_HAZARD = 2;
	
	private static final double DECISION_PERSIST = 4.0;
	
	private int decision=DECISION_NONE;	
	private Date lastDecissionTime = null;
    private CMOTableEntry closestCMO = null;
	
	
	
	/**  time before predicted collision for a warning  */
	private static final double LIMIT_WARNING = 10;
	
	/**  time before predicted collision for a hazard  */
	private static final double LIMIT_HAZARD = 2;	
	
	final private CMOManagement cmo;
	final private Geolocation geo;
	final private Hashtable<CMOTableEntry,CrossingCMOEntry> cmoTable = new Hashtable<CMOTableEntry,CrossingCMOEntry>();
	
	public void updateCrossingCMO(){		
		final WGS84 pos = geo.getLastPos();
		final WGS84 prevPos = geo.getPrevPos();		
		
		if (prevPos!=null) {
			short newDecision=DECISION_NONE;
            CMOTableEntry selectedCMO = null;

			//for each value in CMO table
			for ( CMOTableEntry e : cmo.getTable() ){
				final double  x = e.crossPosX(
						pos.longitude(), pos.latitude(), 
						prevPos.longitude(), prevPos.latitude());
				
				if (x != 0.0) {
					final WGS84 currentPos = geo.getLastPos();
					final double t = geo.getPreditedTimeFromLongitude(x);
					final double t_cmo = e.getPreditedTimeFromLongitude(x);
					final double d = e.distance(currentPos.longitude(),currentPos.latitude());
					final double a = e.azimuth(currentPos.longitude(),currentPos.latitude());
                    final short decision = (t < LIMIT_WARNING) ? DECISION_WARNING :
                                           (t < LIMIT_HAZARD)  ? DECISION_HAZARD :
                                                                 DECISION_NONE;
                    CrossingCMOEntry entry = new CrossingCMOEntry(t, t_cmo, d, geo.getCurrentTrack() - a, decision,e);
                    cmoTable.put(e,entry);

                    if(t >= 0.0 && t_cmo >= 0.0 && Math.abs(t-t_cmo) < LIMIT_HAZARD) {
						if (decision==DECISION_WARNING && newDecision < DECISION_WARNING){
							newDecision = decision;
                            selectedCMO = e;
                        }
						
						if (decision==DECISION_HAZARD && newDecision < DECISION_HAZARD){
							newDecision = decision;
                            selectedCMO = e;
                        }
					}


				}
			}
			
			if(lastDecissionTime==null){
				lastDecissionTime= new Date();
				decision = newDecision;
                closestCMO=selectedCMO;
			}else{
				final double elapsedTime = ((double)((new Date()).getTime() - lastDecissionTime.getTime()))/1000.0;
				
				if(newDecision > decision || elapsedTime >= DECISION_PERSIST){
					if(newDecision > decision) 
						lastDecissionTime= new Date();
					decision = newDecision;
                    closestCMO=selectedCMO;
				}
			}
		}
		
		//System.out.println(toString());
	}

    public void updateClosestCMO(){



        /*closestCMO = null;
        for(Entry<CMOTableEntry,CrossingCMOEntry> e:cmoTable.entrySet())
            if(closestCMO==null || (e.getValue().t < closestCMO.getValue().t &&  e.getValue().decision > DECISION_NONE) )
                closestCMO = e;

        if(closestCMO != null)
            Log.i("cmotable",cmoTable.toString());*/
    }
	
	public CrossingCMO(Geolocation geo, CMOManagement cmo) {
		super();
		this.cmo = cmo;
		this.geo = geo;
	}
	
	@Override
	public void update() {
		updateCrossingCMO();
        updateClosestCMO();
	}
	
	

	public int getDecision() {
		return decision;
	}

    public CMOTableEntry getClosestCMO(){
        if(closestCMO == null)
            return null;

        return closestCMO;
    }

	public Map<CMOTableEntry, CrossingCMOEntry> getCrossingTimeTable() {
		return cmoTable;
	}

	@Override
	public String name() {
		return "CrossingCMO";
	}

	@Override
	public String toString() {
		if(closestCMO==null)
            return "";
        else{
            final WGS84 currentPos = geo.getLastPos();
            final double d = closestCMO.distance(currentPos.longitude(),currentPos.latitude());
		    return format.format(d) + " m";
        }
	}
	
	

}
