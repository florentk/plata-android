package fr.ifsttar.cmo.dashboard;

import fr.ifsttar.geolocation.Geolocation;

/**
 * indicator for show the speed
 * @author florent kaisser
 * @has 1 - - Geolocation
 */
public class Speed implements Indicator {

	private Geolocation geo;
	private Double speed = new Double(0.0);
	
	
	
	public Speed(Geolocation geo) {
		this.geo = geo;
	}



	@Override
	public void update() {
		//convert the speed in kilometer by hour
		speed = new Double( geo.getCurrentSpeed().doubleValue() * 3.6 );
	}
	
	
	
	/**
	 * @return the speed
	 */
	public Double getSpeed() {
		return speed;
	}

	public String name(){
		return "Speed";
	}


	public String toString(){
		return String.format("%01.1f km/h", getSpeed()) ;
	}

}
