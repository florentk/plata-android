package fr.ifsttar.playmob;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collection;

import fr.ifsttar.playmob.cmo.beaconning.BeaconGenerator;
import fr.ifsttar.playmob.cmo.beaconning.BeaconRecv;
import fr.ifsttar.playmob.cmo.beaconning.BeaconRecvUDP;
import fr.ifsttar.playmob.cmo.beaconning.BeaconSenderUDP;
import fr.ifsttar.playmob.cmo.beaconning.packet.CMOHeader;
import fr.ifsttar.playmob.cmo.management.CMOManagement;
import fr.ifsttar.playmob.cmo.management.CMOTableEntry;
import fr.ifsttar.playmob.cmo.management.CMOTableListener;

public class MainActivity extends Activity {

    private CMOManagement cmoManagement = new CMOManagement();
    //private boolean mBound = false;

    public static final int DST_PORT = 50123;
    public static final String BROAD_CAST_ADDR =  "10.0.0.255";
    public static final String NAME = "android";

    private BeaconGenerator beaconGenerator =
            new BeaconGenerator(
                    new BeaconSenderUDP(
                            BeaconSenderUDP.initUDP(),
                            DST_PORT, BROAD_CAST_ADDR),
                    NAME, CMOHeader.CMO_TYPE_BIKE);

    int compteur =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLocationManager();
        initCMOManager();

        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beaconGenerator.broadcastCMOStatPacket(3.12715f,50.61537f,0.0f,0.0f,0.0f,0);
                setMyPos("envoyé !" + (compteur++));
            }
        } );

        /*Intent intent = new Intent(this, BeaconRecvService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);*/
    }

    protected void setMyPos(String pos) {
        TextView poseValue = (TextView)findViewById(R.id.dataMyPos);
        //planetNameValue.setText("50°38'13.92\"N, 3°3'47.88\"E");
        poseValue.setText(pos);
    }


    protected void setNeighbour(String neighbour) {
        TextView poseValue = (TextView)findViewById(R.id.neighbour);
        //planetNameValue.setText("50°38'13.92\"N, 3°3'47.88\"E");
        poseValue.setText(neighbour);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void updateNeighbour(){
        Collection<CMOTableEntry> ns = cmoManagement.getTable();
        StringBuffer s = new StringBuffer();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);

        for (CMOTableEntry e : ns) {
            s.append(e.getCmoID() + " " );
            s.append(df.format(e.getLatitude()) + " ");
            s.append(df.format(e.getLongitude()) + " ");
            s.append("\n");
        }

        setNeighbour(s.toString());
    }

    public void initCMOManager() {
        BeaconRecv bRecv=new BeaconRecvUDP(BeaconSenderUDP.initUDP(MainActivity.DST_PORT) , MainActivity.NAME);
        bRecv.addListener(cmoManagement);
        bRecv.start();

        cmoManagement.addListener(new CMOTableListener() {
            @Override
            public void tableChanged(CMOTableEntry table) {
                updateNeighbour();
            }

            @Override
            public void tableCMORemoved(CMOTableEntry table) {
                updateNeighbour();
            }

            @Override
            public void tableCMOAdded(CMOTableEntry table) {
                updateNeighbour();
            }
        });
    }

    public void initLocationManager() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setMyPos("Waiting for gps provider...");

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location l) {
                // Called when a new location is found by the network location provider.
                setMyPos("GPS - " + l.toString());
                beaconGenerator.broadcastCMOStatPacket((float)l.getLongitude(),(float)l.getLatitude(),
                        (float) l.getAltitude(),l.getSpeed(),l.getBearing(),(int)l.getTime());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

  /*  private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BeaconRecvService.LocalBinder binder = (BeaconRecvService.LocalBinder) service;
            cmoManagement = binder.getManagement();
            mBound = true;

            cmoManagement.addListener(new CMOTableListener() {
                @Override
                public void tableChanged(CMOTableEntry table) {
                    setNeighbour(table.toString());
                }

                @Override
                public void tableCMORemoved(CMOTableEntry table) {

                }

                @Override
                public void tableCMOAdded(CMOTableEntry table) {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };*/
}

