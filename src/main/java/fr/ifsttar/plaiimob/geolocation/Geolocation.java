package fr.ifsttar.plaiimob.geolocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;


/**
 * Geolocation system interface <br><br>
 * 
 * The position coordinate use the WGS84 representation 
 * commonly used by the GPS <br><br>
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 * @has 1 - - WGS84
 * @has 0..* - - GeolocationListener
 * 
 */

public abstract class  Geolocation  {
	
	public static final int DEFAULT_UPDATA_INTERVAL = 250;	
	
	
	/** current position in WGC84 format */
	private WGS84 lastPos = null;
	
	/** current position in WGC84 format */
	private WGS84 prevPos = null;	
	
	/** velocity in unit by second   */
	private WGS84 velocity = null;
	/** velocity  history  */
	/*private static final int VELOCITIES_SIZE_MAX = 1;
	LinkedList<WGS84> velocities = new LinkedList<WGS84>();*/
	
	/** acceleration in unit by second by second  */
	//private WGS84 acc = null;	
	/** acceleration  history  */
	/*private static final int ACCS_SIZE_MAX = 1;
	LinkedList<WGS84> accs = new LinkedList<WGS84>();	*/
	
	/** current speed in meter per second */
	private Double currentSpeed=0.0;
	
	/** current orientation in degree (0 to 360) */
	private Double currentTrack=0.0;
	
	/** system time when the data has received*/
	private Date sysTime;
	
	/** time when the device has started*/
	private Date startTime = new Date();	
	
	private boolean ready = false;
	
	/** collection of listener for receive a event on position changing*/
	private final Collection<GeolocationListener> gpsListeners = new ArrayList<GeolocationListener>();

	
	

	
	/**
	 * must be call when the current position change
	 */
	private void positionChanged(){
		//call the method positionChanged of each registered listener
		for (GeolocationListener l : gpsListeners)
			l.positionChanged(lastPos,currentSpeed,currentTrack,getTime());
	}	
	


	
	/**
	 * get the current position with extrapolation
	 * @return the current position in WGS84 format
	 */
	public WGS84 getCurrentPos() {
		return getPredictPos();
	}
	
	
	public WGS84 getVelocity() {
		return velocity;
	}
	
	/**
	 * compute the time to reach a target point on the trajectory
	 * @param longitude longitude of target point
	 * @return the time to reach the target
	 */
	public double getPreditedTimeFromLongitude(double longitude) {
		if(velocity == null)
			return -1.0;
		
		//time elapsed since last pos
		final double dt = ((double)((new Date()).getTime() - sysTime.getTime()))/1000.0;	
		//distance beetween the last pos and the target point
		final double d = longitude - lastPos.longitude();
		
		//minus the computed time by the elapsed time
		final double t = (d / velocity.longitude()) - dt;
		
		if (t<0) return -1.0;
		
		return t;
	}

	private WGS84 getPredictPos() {
		if(! ready){
			return new WGS84();
		}
		
		if(velocity == null)
			//no velocity, assume no moving
			return lastPos;
		
		//compute the time since the last data acquisition
		final double dt = ((double)((new Date()).getTime() - sysTime.getTime()))/1000.0;	
		
		//if(acc == null)
			//no acceleration, assume a constant velocity
			return new WGS84(
					lastPos.longitude() +  (velocity.longitude() * dt) ,
					lastPos.latitude() +  (velocity.latitude() * dt) , 
					lastPos.h() + (velocity.h() * dt)
				);
		/*
		//full equation with velocity and acceleration
		return new WGS84(
				currentPos.longitude() +  (velocity.longitude() * dt) +  (acc.longitude() * dt * dt),
				currentPos.latitude() +  (velocity.latitude() * dt) +  (velocity.latitude() * dt * dt), 
				currentPos.h() + (velocity.h() * dt) + (velocity.h() * dt * dt)
				);		*/
	}	
	
	//
	
	/**
	 * get the last position recevied by the device
	 * without interpolation
	 * @return the last position
	 */
	public WGS84 getLastPos() {
		if(! ready)
			return new WGS84();
		
		return lastPos;
	}
	
	/**
	 * get the previous pos recevied by the device
	 * it is the old last pos.
	 * @return
	 */
	public WGS84 getPrevPos() {
		return prevPos;
	}
	
	


	/**
	 * compute the dirivate from WGS84 type. The derivate is smooth with the last computed derivate
	 * The position can be a derivate position, i.e. the velocity for compute a acceleration
	 * @param prevPos position at t0
	 * @param newPos position at t1
	 * @param dt t1 - t0
	 * @return the derivate
	 */
	private WGS84 computeDerivate(WGS84 prevPos, WGS84 newPos, double dt/*, LinkedList<WGS84> hist, int nbMaxHist*/){
		WGS84 d =  new WGS84(
				(newPos.longitude() - prevPos.longitude()) / dt,
				(newPos.latitude() - prevPos.latitude()) / dt,	
				(newPos.h() - prevPos.h()) / dt
		);		
		return d;
		/*
		if(d.longitude()>1.0) return new WGS84();
		
		hist.addLast(d);
		
		if (hist.size() > nbMaxHist)
			hist.removeFirst();

		double slat = 0.0;double slon = 0.0;double sh = 0.0;
		
		for (WGS84 e:hist){
			slat+=e.latitude();
			slon+=e.longitude();
			sh+=e.h();			
		}	
		
		
		
		return  new WGS84(
				slon/hist.size(),
				slat/hist.size(),
				sh/hist.size()
		);		
		*/
		
	}
	
//static int n =0;
	/**
	 * set the current position
	 * @param currentPos the current position in WGS84 format
	 */
	protected void setCurrentPos(WGS84 currentPos) {
		
		
		//if no current piosition, can't compute the velocity
		if(this.lastPos!=null){
			
			//System.out.println(n++ + " " + getPredictPos().sub(currentPos) + " " + velocity);

			double dt = ((double)((new Date()).getTime() - sysTime.getTime()))/1000.0;
			//System.out.println(dt);
			WGS84 newVelocity = computeDerivate(lastPos,currentPos, dt/*, velocities, VELOCITIES_SIZE_MAX*/);
			
			//if no velocity, can't compute the acceleration
			/*if(velocity != null){
				acc = computeDerivate(velocity,newVelocity, dt);
			}*/
			
			//if(velocity != null) System.out.println(n++ + " " + velocity + " "+ acc);
				
			//update the current velocity
			velocity=newVelocity;
			

		}		
		
		
		//update the current position
		prevPos = lastPos;
		lastPos = currentPos;
		sysTime = new Date();
		positionChanged();
		ready = true;
	}	
	

	/**
	 * register a new listener
	 * @param l
	 */
	public void addPositionListener(GeolocationListener l){
		gpsListeners.add(l);
	}
	
	/**
	 * remove a registered listener
	 * @param l
	 */
	public void removePositionListener(GeolocationListener l){
		gpsListeners.remove(l);
	}
	
	
	/**
	 *  get current speed 
	 *  @return speed in meter per second 
	*/
	public Double getCurrentSpeed() {
		return currentSpeed;
	}
	
	
	/** get current time when the data has received
	 * @return current time when the data has received 
	 *         in millisecond since this devise started
	 * */
	public int getTime() {
		if(sysTime!=null)
			return (int)(sysTime.getTime() - startTime.getTime());
		else 
			return 0;
	}
	
	/**
	 *  set current speed 
	 *  @param currentSpeed in meter per second 
	*/	
	protected void setCurrentSpeed(Double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	/** get current orientation 
	 *  @return orientation in degree (0 to 360)
	 */
	public Double getCurrentTrack() {
		return currentTrack;
	}

	/** set current orientation
	 *  @param currentTrack orientation in degree (0 to 360) 
	*/
	protected void setCurrentTrack(Double currentTrack) {
		this.currentTrack = currentTrack;
	}	
		
	/** set the time when data has acquire
	 *  @param currentTrack orientation in degree (0 to 360) 
	*/
	/*protected void setCurrentTime(Double currentTime) {
		this.devTime = currentTime;
	}		*/
	
	/**
	 * devise is ready
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * call for clean the devise
	 */
	public abstract void dispose();

}
