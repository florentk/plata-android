package fr.ifsttar.plaiimob.cmo.beaconning;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class BeaconSenderUDP implements BeaconSender {
	/** constants */
	public static final int DEFAULT_PORT = 50123;
	public static final String DEFAULT_BROADCAST_ADDRESS =  "192.168.0.255";

    /** parameters */
	private final DatagramSocket socket;
	private final int port;
	private final InetAddress broadcastAddress;

    public BeaconSenderUDP(int port, String address) {
		super();
        this.port = port;
        this.socket = initUDP();

        InetAddress broadcastAddress;
        try {
            broadcastAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            broadcastAddress = null;
            Log.e("SendUDP", "" + e);
        }

        this.broadcastAddress = broadcastAddress;
	}
    
    public BeaconSenderUDP() {
    	this(DEFAULT_PORT,DEFAULT_BROADCAST_ADDRESS);
	}
    
    public BeaconSenderUDP(int port) {
    	this(port, DEFAULT_BROADCAST_ADDRESS);
	}
	
    private InetAddress getBroadcastAddress() throws IOException {
        return broadcastAddress;
    }
	
	@Override
	public void broadcastData(byte[] data) {
        if (socket == null) {
        	System.err.println("Socket is not initialized");
            return;
        }

        try{
            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), port);
            socket.send(packet);
        } catch (IOException e) {
            Log.e("SendUDP", "Could not send discovery request " + e);
        }
	}
	
	public static DatagramSocket initUDP(int port){
        try{
        	DatagramSocket socket = new DatagramSocket(port);
            socket.setBroadcast(true);
            return socket;
        } catch (IOException e) {
        	System.err.println("Could not create UDP socket " + port);
        	return null;
        }
    }
	
	public static DatagramSocket initUDP(){
        try{
        	DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            return socket;
        } catch (IOException e) {
        	System.err.println("Could not create UDP socket " + e);
        	return null;
        }
    }

    @Override
    public void dispose() {
        socket.close();
    }
}
