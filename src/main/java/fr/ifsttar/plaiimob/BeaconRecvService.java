package fr.ifsttar.plaiimob;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import fr.ifsttar.plaiimob.cmo.beaconning.BeaconRecv;
import fr.ifsttar.plaiimob.cmo.beaconning.BeaconRecvUDP;
import fr.ifsttar.plaiimob.cmo.beaconning.BeaconSenderUDP;
import fr.ifsttar.plaiimob.cmo.management.CMOManagement;

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
