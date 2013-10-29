package fr.ifsttar.cmo.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import fr.ifsttar.cmo.beaconning.BeaconRecv;
import fr.ifsttar.cmo.management.CMOManagement;
import fr.ifsttar.cmo.management.CMOTableEntry;
import fr.ifsttar.cmo.management.CMOTableListener;
import fr.ifsttar.geolocation.*;
import fr.ifsttar.weather.*;

/**
 * Collection of Indicator and update notification
 * 
 *          Indicator 1        .....           Indicator N 
 *               |               |                  |
 *               |----------------------------------|
 *                             |   /|\  Indicator.update()
 *                             |    |
 * DashboardListener.update() \|/   |
 *                        ---------------
 * CMOTableListener ----->|             |
 *                        |  Dashboard  |------------> DashboardListener.dashboardUpdate()
 * GeolocationListener -->|             |
 *                        ---------------
 *
 * @author florent kaisser
 * @has 0..* - - Indicator
 * @has 0..* - - DashboardListener
 *
 */
public class Dashboard implements CMOTableListener, GeolocationListener, WeatherListener {

	private Collection<Indicator> indicators =new ArrayList<Indicator>();




	private Collection<DashboardListener> listeners =new ArrayList<DashboardListener>();	
	
	public void addIndicator(Indicator indicator){
		indicators.add(indicator);
	}
	
	public void removeIndicator(Indicator indicator){
		indicators.remove(indicator);		
	}	
	
	public void addListener(DashboardListener l){
		listeners.add(l);
	}
	
	public void removeListerner(DashboardListener l){
		listeners.remove(l);		
	}	
	
	/**
	 * process the update
	 */
	public void setUpdate(){
		// compute the new data of indicators
		for (Indicator i : indicators) 
			i.update();
		
		// notify the listener
		for (DashboardListener l : listeners) l.dashboardUpdate();
	}
	
	
	public void weatherChanged(Weather data) {
		setUpdate();
	}
	
	/**
	 * @see fr.ifsttar.geolocation.GeolocationListener(fr.ifsttar.geolocation.WGS84, java.lang.Double, java.lang.Double)
	 */
	@Override
	public void positionChanged(WGS84 position, Double speed, Double track, int time) {
		setUpdate();
	}

	/**
	 * @see fr.ifsttar.cmo.management.CMOTableListener(java.lang.String, fr.ifsttar.cmo.management.CMOTable)
	 */
	@Override
	public void tableChanged(CMOTableEntry table) {
		setUpdate();
	}
	
	/**
	 * @see fr.ifsttar.cmo.management.CMOTableListener(java.lang.String, fr.ifsttar.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMOAdded(CMOTableEntry table) {
		setUpdate();
	}

	/** 
	 * @see fr.ifsttar.cmo.management.CMOTableListener(java.lang.String, fr.ifsttar.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMORemoved(CMOTableEntry table) {
		setUpdate();
	}	
	
	/**
	 * @return the indicators
	 */
	public Collection<Indicator> getIndicators() {
		return indicators;
	}	

	public String toString(){
		StringBuffer s=new StringBuffer();
		
		for (Indicator i : indicators)
			s.append(i.name() + " : " + i.toString()+"\n");
			
		return s.toString();
	}
}
