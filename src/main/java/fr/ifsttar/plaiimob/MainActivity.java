package fr.ifsttar.plaiimob;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collection;

import fr.ifsttar.plaiimob.cmo.beaconning.BeaconGenerator;
import fr.ifsttar.plaiimob.cmo.beaconning.BeaconRecv;
import fr.ifsttar.plaiimob.cmo.beaconning.BeaconRecvUDP;
import fr.ifsttar.plaiimob.cmo.beaconning.BeaconSenderUDP;
import fr.ifsttar.plaiimob.cmo.beaconning.packet.CMOHeader;
import fr.ifsttar.plaiimob.cmo.management.CMOManagement;
import fr.ifsttar.plaiimob.cmo.management.CMOTableEntry;
import fr.ifsttar.plaiimob.cmo.management.CMOTableListener;
import fr.ifsttar.plaiimob.geolocation.GeoLocationAndroid;
import fr.ifsttar.plaiimob.geolocation.GeolocationListener;
import fr.ifsttar.plaiimob.geolocation.WGS84;

public class MainActivity extends Activity {
    public static final int DST_PORT = 50123;
    public static final String BROAD_CAST_ADDR =  "10.0.0.255";
    public static final String NAME = "android";

    private CMOManagement cmoManagement = new CMOManagement();
    private GeoLocationAndroid geolocation;
    private DecimalFormat df = new DecimalFormat();
    //private boolean mBound = false;
    private BeaconGenerator beaconGenerator =
            new BeaconGenerator(
                    new BeaconSenderUDP(
                            BeaconSenderUDP.initUDP(),
                            DST_PORT, BROAD_CAST_ADDR),
                    NAME, CMOHeader.CMO_TYPE_BIKE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initLocationManager();
        initCMOManager();
        initBikeView();

        /*Intent intent = new Intent(this, BeaconRecvService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLocationManager();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_neighborhood:
                /*Intent intent_add = new Intent(this, NeighborhoodActivity.class);
                this.startActivity(intent_add);*/
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setMyPos(String pos) {
        TextView poseValue = (TextView)findViewById(R.id.dataMyPos);
        //planetNameValue.setText("50°38'13.92\"N, 3°3'47.88\"E");
        poseValue.setText(pos);
    }


    private void setNeighbour(String neighbour) {
        TextView poseValue = (TextView)findViewById(R.id.neighbour);
        //planetNameValue.setText("50°38'13.92\"N, 3°3'47.88\"E");
        poseValue.setText(neighbour);
    }

    private void updateNeighbour(){
        Collection<CMOTableEntry> ns = cmoManagement.getTable();
        StringBuilder s = new StringBuilder();


        for (CMOTableEntry e : ns) {
            s.append(e.getCmoID());
            s.append(" ");
            s.append(df.format(e.getLatitude()));
            s.append(" ");
            s.append(df.format(e.getLongitude()));
            s.append("\n");
        }

        setNeighbour(s.toString());
    }

    private void initBikeView() {
        BikeView bike = (BikeView) findViewById(R.id.bikeView);

        bike.setCMOManagement(cmoManagement);
        bike.setLocationManager(geolocation);
    }

    private void initCMOManager() {
        BeaconRecv bRecv=new BeaconRecvUDP(BeaconSenderUDP.initUDP(MainActivity.DST_PORT) , MainActivity.NAME);
        bRecv.addListener(cmoManagement);
        bRecv.start();

        df.setMaximumFractionDigits(5);

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

    private void initLocationManager() {

        geolocation = new GeoLocationAndroid((LocationManager) this.getSystemService(Context.LOCATION_SERVICE),
                            /*LocationManager.GPS_PROVIDER*/"TestPlaiimob");


        setMyPos("Waiting for gps provider...");

        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geolocation.setTestPosition(3.135364083021891,50.60616289950799);
            }
        } );

        geolocation.addPositionListener(new GeolocationListener() {
            @Override
            public void positionChanged(WGS84 position, Double speed, Double track, int time) {
                setMyPos("GPS - " + df.format(position.latitude()) + " " + df.format(position.longitude()));
                beaconGenerator.broadcastCMOStatPacket(position, speed.floatValue(), track.floatValue(), time);
            }
        });

        //geolocation.addTestProvider();
     }


    private void releaseLocationManager() {
        geolocation.removeTestProvider();
    }


}

