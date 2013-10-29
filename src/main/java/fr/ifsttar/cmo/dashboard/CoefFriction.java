package fr.ifsttar.cmo.dashboard;

import fr.ifsttar.utils.Physics;
import fr.ifsttar.weather.*;

public class CoefFriction implements Indicator {

	private Weather w;
	private Double coef = Physics.COEF_FRICTION_AVG;
	
	public CoefFriction(Weather weather) {
		this.w = weather;
	}

	@Override
	public void update() {
		coef = Physics.COEF_FRICTION_AVG;
	
		if(w.validData()) {
			if (w.isRain() || w.isRainfall() || w.isSnow() || w.isSnowfall()) coef /= 2.0;
		}		
	}

	/**
	 * @return the speed
	 */
	public Double getCoef() {
		return coef;
	}

	public String name(){
		return "Coefficient of friction";
	}


	public String toString(){
		return String.format("%01.2f", getCoef()) ;
	}

}
