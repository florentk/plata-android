package fr.ifsttar.cmo.beaconning;


import android.util.Log;


import fr.ifsttar.cmo.beaconning.packet.CMOState;

/**
 * 
 * forward CMO stat beacon
 * 
 * BeaconRecvListener --|CMOState|--> BeaconForward ----|CMOState|----> JPcap (row ethernet)
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */
public class BeaconForward implements BeaconRecvListener {
	private final BeaconSender sender;
	
	public BeaconForward(BeaconSender sender) {
		this.sender = sender;
	}

	/**
	 * @see fr.ifsttar.plaiimob.cmo.beaconning.BeaconRecvListener(fr.ifsttar.plaiimob.cmo.beaconning.packet.CMOState)
	 */
	@Override
	public void cmoStatChanged(CMOState stat) {
		if(stat.getTTL()==0)
			return;
		
		Log.i("BeaconForward","forward_cmo_packet: " + stat.getCmoID() + " " + stat.getSeq()+ " " + stat.getTTL());
		
		sender.broadcastData(BeaconGenerator.createCMOStatPacket( (byte)((int)stat.getTTL()-1), stat.getSeq(), stat.getLifetime(), stat.getCmoID(), stat.getCmoType(), stat.getLongitude(), stat.getLatitude(), stat.getH(), stat.getSpeed(), stat.getTrack(), stat.getTime()));
	}

	/*public static void startForwarder(String strDevice){
	    NetworkInterface device = PcapsTool.toNetworkInterface(strDevice);
	    
	    if(device==null){
	    	System.out.println("The interface " + strDevice + " doesn't exist");
	    	PcapsTool.printDevice();
	    	return;
	    }
		
	    try{
	    	 
	    	 
	    	BeaconRecv recv =  new BeaconRecvEthernet(JpcapCaptor.openDevice(device, 2000, false, 20), "");
	    	BeaconForward f = new BeaconForward(new BeaconSenderEthernet(JpcapSender.openDevice(device)));
	    	
			recv.addListener(f);
			recv.start();
			
			try{
				recv.join();	   
			}catch (InterruptedException e){}
	    	
	    }catch (java.io.IOException e){
	    	System.out.println("Cannot open network interface : "+e);
	    }

	}


	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("Not enough arguments");
			System.out.println("Usage : java BeaconForward <device>");			
			System.exit(1);
		}		
		
		startForwarder(args[0]);
	}
*/
}
