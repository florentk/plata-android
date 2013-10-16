package fr.ifsttar.plaiimob.cmo.beaconning;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import fr.ifsttar.plaiimob.cmo.beaconning.packet.*;


/**
 * 
 * JpcapCaptor (raw Ethernet) ----> BeaconRecv ----|CMOState|----> CMOStatListener
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */

public class BeaconRecvUDP extends  BeaconRecv {

    DatagramSocket socket = null;
	int delay;
	String myCmoId;


	public BeaconRecvUDP(DatagramSocket socket, int delay, String myCmoId){
		super();
		this.delay = delay;
		this.myCmoId = myCmoId;
		this.socket = socket;
	}
	
	
	public BeaconRecvUDP(DatagramSocket socket,String myCmoId){
		this(socket, 0,myCmoId);
	}	


	//receive a packet from UDP
	public void receivePacket(byte[] data) {
		
		if(delay!=0){
			try{sleep(100);}catch(InterruptedException e){}
		}

		//decode packet
		CMOState cmo = new CMOState(data);
		
		Log.i("RecvUDP",  "receive_cmo_packet: Thread : " + Thread.currentThread().getId() + " "  + cmo);
		
		//drop packet from me and expired packet
		if(cmo.getCmoID().compareTo(myCmoId)!=0){
			//notify the listerners
			notifyListener(cmo);
		}
	}
	
    @Override
	public void run(){
		byte[] data = new byte[256];
		
        if (socket == null) {
        	System.err.println("Socket is not initialized");
            return;
        }		
		
		try{
			while(true)
			{
				DatagramPacket packet = new DatagramPacket(data, data.length);
				socket.receive(packet);
				receivePacket(packet.getData());
			}
		} catch (IOException e) {
            Log.e("RecvUDP", "Could not receive packet: " + e);
		}
	}
	

	
	/* BeaconRecv factory */
	
	/**
	 * BeaconRecv factory. Create a BeaconRecv for UDP
	 * @return
	 */
	public static BeaconRecvUDP loopPacketForUDP(){
		return new BeaconRecvUDP(BeaconSenderUDP.initUDP() , "");
	}
	



}
