package fr.ifsttar.plaiimob.cmo.beaconning;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import fr.ifsttar.plaiimob.cmo.beaconning.packet.*;


/**
 * Receive CMO stat in a UDP packet
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */

public class BeaconRecvUDP extends  BeaconRecv {

    private final DatagramSocket socket;
    private final int delay;
    private final String myCmoId;

	public BeaconRecvUDP(int port, int delay, String myCmoId){
		super();
		this.delay = delay;
		this.myCmoId = myCmoId;
		this.socket = BeaconSenderUDP.initUDP(port);
	}
	
	
	public BeaconRecvUDP(int port, String myCmoId){
		this(port, 0,myCmoId);
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
            Log.e("RecvUDP","Socket is not initialized");
            return;
        }		
		
		try{
			while(actif)
			{
				DatagramPacket packet = new DatagramPacket(data, data.length);
				socket.receive(packet);
				if(actif) receivePacket(packet.getData());
			}
		} catch (IOException e) {
            Log.e("RecvUDP", "Could not receive packet: " + e);
		}
	}

    public void dispose(){
        stopRecv();
        socket.close();
    }
}
