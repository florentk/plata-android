package fr.ifsttar.cmo.dashboard;

import fr.ifsttar.utils.Physics;
import fr.ifsttar.geolocation.Geolocation;

/**
 * indicator for braking distance 
 * @has 1 - - Geolocation
 * @has 1 - - CoefFriction
 * @assoc - - - Physics
 */
public class BrakingDistance implements Indicator {

	private Geolocation geo;
	private CoefFriction cf;	
	private Double dist = null;
	
	
	
	public BrakingDistance(Geolocation geo, CoefFriction cf) {
		this.geo = geo;
		this.cf = cf;		
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		dist = Physics.BrakingDistance(geo.getCurrentSpeed(), cf.getCoef());
	}

	/**
	 * @return the speed
	 */
	public Double getDistance() {
		return dist;
	}

	public String name(){
		return "Braking distance";
	}


	public String toString(){
		if(getDistance() == null)
			return "N/A";
		
		return String.format("%01.1f m", getDistance()) ;
	}

}
