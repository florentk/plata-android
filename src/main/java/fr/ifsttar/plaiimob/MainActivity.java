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

    private static final String trace = "3.129384328171011,50.60612721473346,0 3.129433123953031,50.60609339354851,0 3.129501801973679,50.60605972368994,0 3.129536840681919,50.60603959341814,0 3.129586619518761,50.60599949210775,0 3.129622989304707,50.60597287609448,0 3.129667644444249,50.60595297695314,0 3.129746648327497,50.6059133354455,0 3.129771085548826,50.60589359254514,0 3.129826718595731,50.60586735049008,0 3.129882155868275,50.60584120059227,0 3.129947286152945,50.60581514248094,0 3.130012188484919,50.60578917579181,0 3.130067100986475,50.60576325676378,0 3.130121943167887,50.60573737122493,0 3.130177952489003,50.60570514465905,0 3.130212783816122,50.60567946777941,0 3.130287906435617,50.60564750257182,0 3.130310050438791,50.60563475664375,0 3.130385981028931,50.60559665560472,0 3.130407957587363,50.60558400049061,0 3.130440849161379,50.60556506035466,0 3.130514859866032,50.60553360684595,0 3.130535435922857,50.6055273333362,0 3.130578868810043,50.60550229314956,0 3.130621001476144,50.60548357164916,0 3.130724197436086,50.60544627880649,0 3.130786279444834,50.6054215267193,0 3.130847102165461,50.60540302049822,0 3.13090020348299,50.60537228383698,0 3.130980778880686,50.60534779320102,0 3.131050577177233,50.60532948231483,0 3.13111152383947,50.60530516323938,0 3.131179685214468,50.60529320968903,0 3.131240840437304,50.60526310945079,0 3.131290794970747,50.60524506703699,0 3.131340814601341,50.60522697309789,0 3.131410452721147,50.60520292373359,0 3.131497969649639,50.60518494825997,0 3.131517589331737,50.60517899285945,0 3.131556778831565,50.60516709694371,0 3.131624852239469,50.60514930168412,0 3.131672595879776,50.60514340960408,0 3.131701491360668,50.60513749996935,0 3.131739116279697,50.60513754404777,0 3.131806208600778,50.60512574905685,0 3.131872014366396,50.60512582535896,0 3.131937270369423,50.60513183638203,0 3.132001529528205,50.60514975069616,0 3.132065465869007,50.60517368844174,0 3.132111307483927,50.60519161819294,0 3.132138356494117,50.60520955451458,0 3.13217588332274,50.60521550212642,0 3.132240673030129,50.60523946475986,0 3.132257620256257,50.60526962958113,0 3.132312460827278,50.60530594613621,0 3.132321628341327,50.60531201720765,0 3.13236795314857,50.60533633651934,0 3.132442872809861,50.60536682357635,0 3.132527121656309,50.60540975866572,0 3.13256447461659,50.60543442823395,0 3.132601688687175,50.60546540301733,0 3.132628831969692,50.60550913042089,0 3.132647927792845,50.60551543262189,0 3.132666273572414,50.60554070031246,0 3.132684689947957,50.60556606509734,0 3.132722197969998,50.60560429641848,0 3.13274058909583,50.60563632457211,0 3.132778664337637,50.60566850862944,0 3.132816364409217,50.60572032833627,0 3.132845006116195,50.60575292091461,0 3.132883532017092,50.60579216836514,0 3.132912243958224,50.60583814162413,0 3.132921239049977,50.60588441987711,0 3.132920307118856,50.60593100646471,0 3.13292964853243,50.60596447341224,0 3.132949069358437,50.60599810073042,0 3.132948344553701,50.60603866557803,0 3.132957474107117,50.60609311732421,0 3.132957004404291,50.60612050070268,0 3.132986750500258,50.60616177545183,0 3.132996605321844,50.60618250266592,0 3.133006016097908,50.60623807050065,0 3.133026002321847,50.60627302119698,0 3.133046010540149,50.60631518741376,0 3.133065656417727,50.60640765615302,0 3.133086083131099,50.6064363188032,0 3.133095923158398,50.60650815703374,0 3.133095772902927,50.6065297763583,0 3.133095571967262,50.60655869876567,0 3.133095319644903,50.60659500745847,0 3.133095116934719,50.60662418011424,0 3.133105322847888,50.60666808344119,0 3.133104999711191,50.60671970219039,0 3.133104721055163,50.60676422568661,0 3.133104487535339,50.60680152699901,0 3.133093623587822,50.60683908150647,0 3.13308264826457,50.60688437754212,0 3.133071607762033,50.60692994254972,0 3.133039208134714,50.6069606468064,0 3.133017504432748,50.60698373873316,0 3.132995531780546,50.6070222785761,0 3.132995024438602,50.6070608548918,0 3.132994412246468,50.60710739879112,0 3.13299389928311,50.60714639739037,0 3.132993685185496,50.60716216586515,0";

    private CMOManagement cmoManagement = new CMOManagement();
    private GeoLocationAndroid geolocation;
    private DecimalFormat df = new DecimalFormat();
    //private boolean mBound = false;
    private BeaconGenerator beaconGenerator =
            new BeaconGenerator(
                    new BeaconSenderUDP(
                            BeaconSenderUDP.initUDP(),
                            DST_PORT, BROAD_CAST_ADDR),
                    NAME, CMOHeader.CMO_TYPE_CAR);


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

        /*Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geolocation.setTestPosition(3.135364083021891,50.60616289950799,0.0f,0.0f);
            }
        } );*/


        geolocation.addPositionListener(new GeolocationListener() {
            @Override
            public void positionChanged(WGS84 position, Double speed, Double track, int time) {
                setMyPos("GPS - " + df.format(position.latitude()) + " " + df.format(position.longitude()));
                beaconGenerator.broadcastCMOStatPacket(position, speed.floatValue(), track.floatValue(), time);
            }
        });

        geolocation.startTrace(trace,200);

        //geolocation.addTestProvider();
     }


    private void releaseLocationManager() {
        geolocation.removeTestProvider();
    }


}

