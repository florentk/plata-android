package fr.ifsttar.playmob;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import fr.ifsttar.playmob.cmo.beaconning.BeaconRecv;
import fr.ifsttar.playmob.cmo.beaconning.BeaconRecvListener;
import fr.ifsttar.playmob.cmo.beaconning.BeaconRecvUDP;
import fr.ifsttar.playmob.cmo.beaconning.BeaconSenderUDP;
import fr.ifsttar.playmob.cmo.beaconning.packet.CMOState;
import fr.ifsttar.playmob.cmo.management.CMOManagement;
import fr.ifsttar.playmob.cmo.management.CMOTableEntry;
import fr.ifsttar.playmob.cmo.management.CMOTableListener;

/**
 *
 */
public class BeaconRecvService extends Service {
    CMOManagement m = new CMOManagement();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        CMOManagement getManagement () {
            return m;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BeaconRecv bRecv=new BeaconRecvUDP(BeaconSenderUDP.initUDP(MainActivity.DST_PORT) , MainActivity.NAME);
        bRecv.addListener(m);
        bRecv.start();
        bRecv.interrupt();
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
