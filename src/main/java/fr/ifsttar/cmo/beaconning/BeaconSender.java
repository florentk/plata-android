package fr.ifsttar.cmo.beaconning;


public interface BeaconSender {
	public void broadcastData(byte[] data) ;
    public void dispose();
}
