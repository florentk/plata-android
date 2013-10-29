package fr.ifsttar.cmo.beaconning;


import android.util.Log;

import fr.ifsttar.cmo.beaconning.packet.*;
import fr.ifsttar.geolocation.WGS84;


public class BeaconGenerator {
	/** freqency interval between each packet send (ms)*/
	public static final int BEACON_FREQ_DEFAULT = 500;
	
	/** beacon lifetime in number of interval value
	 *  between each beacon*/
	public static final int BEACON_LIFETIME = 4;

	/**
	 * Value for initialize the TTL (number of hop maximum)
	 */
	public static final byte TTL_INIT = 16;

    private final String id;
    private final short type;
    private final int beaconFreq;
    private final BeaconSender sender;

	private static int seq=0;

	/**
	 * 
	 * @param sender Jpcap sender
	 * @param id CMO id
	 * @param type CMO type
	 * @param beaconFreq interval between two beacon
	 */
	public BeaconGenerator(BeaconSender sender, String id, short type, int beaconFreq) {
		this.id = id;
		this.type = type;
		this.beaconFreq = beaconFreq;
		this.sender = sender;
	}

	/**
	 * The beacon frequency is {@value #BEACON_FREQ_DEFAULT}
	 * @param sender Jpcap sender
	 * @param id CMO id
	 * @param type CMO type
	 */
	public BeaconGenerator(BeaconSender sender, String id, short type){
		this(sender, id, type, BEACON_FREQ_DEFAULT);
	}
	
	public static  byte[] createCMOStatPacket(byte ttl, int seq, int lifetime, String cmoID,
			short cmoType,Float longitude, Float latitude, Float h, Float speed,
			Float track, int time){
		
		CMOHeader cmo_header = new CMOHeader(ttl, seq,lifetime, cmoID, cmoType);
		CMOState cmo_stat = new CMOState (cmo_header,longitude, latitude, h, speed, track, time);

		return cmo_stat.toByteArray();		
	}
	
	private byte[] createCMOStatPacket(Float longitude, Float latitude, Float h, Float speed,
			Float track, int time){
		return createCMOStatPacket((byte)TTL_INIT, seq++, beaconFreq * BEACON_LIFETIME, id, type,longitude, latitude, h, speed, track, time);
	}
	
	
	/** 
	 * broadcast a CMOStat packet
	 */
	private void broadcastCMOStatPacket(float longitude, float latitude, float h, float speed,
                                       float track, int t) {

		Log.i("Gen", "send_cmo_packet: " + longitude + " " + latitude + " " + h + " "
                + speed + " " + track + " " + t);

        byte[] data = createCMOStatPacket(
                new Float(longitude),
                new Float(latitude),
                new Float(h),
                new Float(speed),
                new Float(track),
                t);
		
		sender.broadcastData(data);
		
	}

    public void broadcastCMOStatPacket(final WGS84 pos,final float speed,final float track, final int t) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                broadcastCMOStatPacket(pos.longitude().floatValue(), pos.latitude().floatValue(),
                        pos.h().floatValue(), speed, track, t);
            }
        }).start();
    }

	/**
	 * @return the curent sequence number
	 */
	public static int getSeq() {
		return seq;
	}

	/**
	 * @return the CMO identity
	 */
	public String getCMOId() {
		return id;
	}

	/**
	 * @return the type of CMO
	 */
	public short getCMOType() {
		return type;
	}

	/**
	 * @return the beaconning freqency
	 */
	public int getBeaconFreq() {
		return beaconFreq;
	}

}
