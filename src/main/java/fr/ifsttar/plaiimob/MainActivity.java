package fr.ifsttar.plaiimob;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.GEMFFileArchive;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.ZipFileArchive;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import fr.ifsttar.cmo.beaconning.BeaconForward;
import fr.ifsttar.cmo.beaconning.BeaconGenerator;
import fr.ifsttar.cmo.beaconning.BeaconRecv;
import fr.ifsttar.cmo.beaconning.BeaconRecvUDP;
import fr.ifsttar.cmo.beaconning.BeaconSender;
import fr.ifsttar.cmo.beaconning.BeaconSenderUDP;
import fr.ifsttar.cmo.beaconning.packet.CMOHeader;
import fr.ifsttar.cmo.dashboard.CrossingCMO;
import fr.ifsttar.cmo.dashboard.Dashboard;
import fr.ifsttar.cmo.dashboard.DashboardListener;
import fr.ifsttar.cmo.management.CMOManagement;
import fr.ifsttar.cmo.management.CMOTableEntry;
import fr.ifsttar.cmo.management.CMOTableListener;
import fr.ifsttar.geolocation.GeoLocationAndroid;
import fr.ifsttar.geolocation.Geolocation;
import fr.ifsttar.geolocation.GeolocationListener;
import fr.ifsttar.geolocation.GeolocationTrace;
import fr.ifsttar.geolocation.GeolocationTraceAndroid;
import fr.ifsttar.geolocation.WGS84;

public class MainActivity extends Activity {

    public static final int DST_PORT = 50123;
    public static final String BROAD_CAST_ADDR =  "10.0.0.255";
    public static final String NETWORK_NAME = "\"plaiimob\"";
    public static final String DEFAULT_NAME = "android";
    public static final short MODE_MAP = 0;
    public static final short MODE_COMPASS = 1;

    private static final String trace = "3.129384328171011,50.60612721473346,0 3.129433123953031,50.60609339354851,0 3.129501801973679,50.60605972368994,0 3.129536840681919,50.60603959341814,0 3.129586619518761,50.60599949210775,0 3.129622989304707,50.60597287609448,0 3.129667644444249,50.60595297695314,0 3.129746648327497,50.6059133354455,0 3.129771085548826,50.60589359254514,0 3.129826718595731,50.60586735049008,0 3.129882155868275,50.60584120059227,0 3.129947286152945,50.60581514248094,0 3.130012188484919,50.60578917579181,0 3.130067100986475,50.60576325676378,0 3.130121943167887,50.60573737122493,0 3.130177952489003,50.60570514465905,0 3.130212783816122,50.60567946777941,0 3.130287906435617,50.60564750257182,0 3.130310050438791,50.60563475664375,0 3.130385981028931,50.60559665560472,0 3.130407957587363,50.60558400049061,0 3.130440849161379,50.60556506035466,0 3.130514859866032,50.60553360684595,0 3.130535435922857,50.6055273333362,0 3.130578868810043,50.60550229314956,0 3.130621001476144,50.60548357164916,0 3.130724197436086,50.60544627880649,0 3.130786279444834,50.6054215267193,0 3.130847102165461,50.60540302049822,0 3.13090020348299,50.60537228383698,0 3.130980778880686,50.60534779320102,0 3.131050577177233,50.60532948231483,0 3.13111152383947,50.60530516323938,0 3.131179685214468,50.60529320968903,0 3.131240840437304,50.60526310945079,0 3.131290794970747,50.60524506703699,0 3.131340814601341,50.60522697309789,0 3.131410452721147,50.60520292373359,0 3.131497969649639,50.60518494825997,0 3.131517589331737,50.60517899285945,0 3.131556778831565,50.60516709694371,0 3.131624852239469,50.60514930168412,0 3.131672595879776,50.60514340960408,0 3.131701491360668,50.60513749996935,0 3.131739116279697,50.60513754404777,0 3.131806208600778,50.60512574905685,0 3.131872014366396,50.60512582535896,0 3.131937270369423,50.60513183638203,0 3.132001529528205,50.60514975069616,0 3.132065465869007,50.60517368844174,0 3.132111307483927,50.60519161819294,0 3.132138356494117,50.60520955451458,0 3.13217588332274,50.60521550212642,0 3.132240673030129,50.60523946475986,0 3.132257620256257,50.60526962958113,0 3.132312460827278,50.60530594613621,0 3.132321628341327,50.60531201720765,0 3.13236795314857,50.60533633651934,0 3.132442872809861,50.60536682357635,0 3.132527121656309,50.60540975866572,0 3.13256447461659,50.60543442823395,0 3.132601688687175,50.60546540301733,0 3.132628831969692,50.60550913042089,0 3.132647927792845,50.60551543262189,0 3.132666273572414,50.60554070031246,0 3.132684689947957,50.60556606509734,0 3.132722197969998,50.60560429641848,0 3.13274058909583,50.60563632457211,0 3.132778664337637,50.60566850862944,0 3.132816364409217,50.60572032833627,0 3.132845006116195,50.60575292091461,0 3.132883532017092,50.60579216836514,0 3.132912243958224,50.60583814162413,0 3.132921239049977,50.60588441987711,0 3.132920307118856,50.60593100646471,0 3.13292964853243,50.60596447341224,0 3.132949069358437,50.60599810073042,0 3.132948344553701,50.60603866557803,0 3.132957474107117,50.60609311732421,0 3.132957004404291,50.60612050070268,0 3.132986750500258,50.60616177545183,0 3.132996605321844,50.60618250266592,0 3.133006016097908,50.60623807050065,0 3.133026002321847,50.60627302119698,0 3.133046010540149,50.60631518741376,0 3.133065656417727,50.60640765615302,0 3.133086083131099,50.6064363188032,0 3.133095923158398,50.60650815703374,0 3.133095772902927,50.6065297763583,0 3.133095571967262,50.60655869876567,0 3.133095319644903,50.60659500745847,0 3.133095116934719,50.60662418011424,0 3.133105322847888,50.60666808344119,0 3.133104999711191,50.60671970219039,0 3.133104721055163,50.60676422568661,0 3.133104487535339,50.60680152699901,0 3.133093623587822,50.60683908150647,0 3.13308264826457,50.60688437754212,0 3.133071607762033,50.60692994254972,0 3.133039208134714,50.6069606468064,0 3.133017504432748,50.60698373873316,0 3.132995531780546,50.6070222785761,0 3.132995024438602,50.6070608548918,0 3.132994412246468,50.60710739879112,0 3.13299389928311,50.60714639739037,0 3.132993685185496,50.60716216586515,0";

    private final DecimalFormat df = new DecimalFormat();
    private MapView mMapView;

    private boolean initialized = false;
    private BeaconRecv recv;
    private BeaconSender sender;
    private Geolocation geo;
    private CMOManagement cmoManagement;
    private short cmoType = CMOHeader.CMO_TYPE_UNDEFINED;
    private short currentMode = MODE_MAP;

    ////////////////////////////////////////////////////////////////////////////////
    // Events processing
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        df.setMaximumFractionDigits(2);

        if(savedInstanceState != null){
            this.cmoType = savedInstanceState.getShort("cmoType",CMOHeader.CMO_TYPE_UNDEFINED);
            this.currentMode = savedInstanceState.getShort("currentMode",MODE_MAP);
        }

        initWifi();

        if(cmoType == CMOHeader.CMO_TYPE_UNDEFINED){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_choose_cmotype);
            builder.setItems(R.array.testArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            cmoType = CMOHeader.CMO_TYPE_CAR;
                            break;
                        case 1:
                            cmoType = CMOHeader.CMO_TYPE_TRUCK;
                            break;
                        case 2:
                            cmoType = CMOHeader.CMO_TYPE_BUS;
                            break;
                        case 3:
                            cmoType = CMOHeader.CMO_TYPE_MOTORBIKE;
                            break;
                        case 4:
                            cmoType = CMOHeader.CMO_TYPE_WALKER;
                            break;
                        case 5:
                            cmoType = CMOHeader.CMO_TYPE_BIKE;
                            break;
                        case 6:
                            cmoType = CMOHeader.CMO_TYPE_SPOT;
                            break;
                    }
                    initialization(generateCmoName());
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    initialization(generateCmoName());
                }
            });
            builder.show();
        }else {
            initialization(generateCmoName());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putShort("cmoType", cmoType);
        outState.putShort("currentMode", currentMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispose();
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
            /*case R.id.action_neighborhood:
                Intent intent_add = new Intent(this, NeighborhoodActivity.class);
                this.startActivity(intent_add);
                break;*/

            case R.id.action_map:
                showMap();
                break;
            case R.id.action_compas:
                showCompass();
                break;

            case R.id.action_exit:
                finish();
                break;

            case R.id.action_settings:

                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    // end of event processing
    ////////////////////////////////////////////////////////////////////////////////

    private void initialization(String cmoName){

        //////////////////////
        //init backend
        final LocationManager loc = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //this.geo = initGeolocation(loc);
        this.geo = initGeolocationTrace(loc);
        this.sender = initBeaconSender();
        this.recv = initBeaconRecv(cmoName);

        initGenerator(sender, geo, cmoName, cmoType);
        initForwarder(recv,sender);

        this.cmoManagement = initCMOManager(recv);
        //////////////////////

        //////////////////////
        //init GUI
        initAlertView(cmoManagement, geo);
        initBikeView(cmoManagement, geo, cmoType);
        initMapView(cmoManagement, geo, cmoType);
        changeMode(currentMode);
        //////////////////////

        this.initialized = true;
    }

    private void dispose() {
        if(initialized){
            geo.dispose();
            recv.dispose();
            sender.dispose();
            cmoManagement.dispose();


            //fix bug http://code.google.com/p/osmdroid/issues/detail?id=265
            //final MapView mMapView = (MapView) findViewById(R.id.mapview);
            mMapView.getTileProvider().clearTileCache();
            System.gc();

            initialized = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // GUI support
    /*private void setMyPos(String pos) {
        TextView poseValue = (TextView)findViewById(R.id.dataMyPos);
        //planetNameValue.setText("50°38'13.92\"N, 3°3'47.88\"E");
        poseValue.setText(pos);
    }

   /* private void setNeighbour(String neighbour) {
        TextView poseValue = (TextView)findViewById(R.id.neighbour);
        //planetNameValue.setText("50°38'13.92\"N, 3°3'47.88\"E");
        poseValue.setText(neighbour);
    }*/

    private void changeMode(short mode){
        switch(mode){
            case MODE_COMPASS:
                showCompass();
                break;
            case MODE_MAP:
                showMap();
                break;
        }
    }

    private void showMap(){
        findViewById(R.id.bikeView).setVisibility(View.INVISIBLE);
        mMapView.setVisibility(View.VISIBLE);
        currentMode = MODE_MAP;
    }

    private void showCompass(){
        findViewById(R.id.bikeView).setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.INVISIBLE);
        currentMode = MODE_COMPASS;
    }

    private void alertUpdate(CrossingCMO crossingCMO, Geolocation geolocation){
        TextView txt = (TextView) findViewById(R.id.textAlert);
        ImageView img = (ImageView) findViewById(R.id.imageAlert);

        switch(crossingCMO.getDecision()){
            case CrossingCMO.DECISION_NONE:
                txt.setVisibility(View.VISIBLE);
                img.setVisibility(View.INVISIBLE);
                txt.setText(df.format(geolocation.getCurrentSpeed()) + " m/s");
                break;
            case CrossingCMO.DECISION_WARNING:
                txt.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                txt.setText(crossingCMO.toString());

                CMOTableEntry cmo = crossingCMO.getClosestCMO();
                if(cmo!=null)
                    img.setImageResource(getResourceFromCMOId(cmo.getCmoType()));
                break;
            case CrossingCMO.DECISION_HAZARD:
                txt.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                txt.setText(crossingCMO.toString());
                img.setImageResource(R.drawable.stop);
                break;
        }
    }

    private String generateCmoName(){
        String name;
        Context ctx = getApplicationContext();
        ContentResolver res = null;

        if(ctx != null)
            res = ctx.getContentResolver();

        if(res == null)
            name = DEFAULT_NAME;
        else
            name = Settings.Secure.getString(res, Settings.Secure.ANDROID_ID);

        if (name == null)
            name = DEFAULT_NAME;

        return name;
    }

    private void addMarkerResId(
            ItemizedIconOverlay<OverlayItem> overlayNeighborItem,
            int idRes, GeoPoint pos, String label)
    {
        OverlayItem overlayItem = new OverlayItem(label, "Neighbord", pos);
        Drawable marker = this.getResources().getDrawable(idRes);
        overlayItem.setMarker(marker);
        overlayNeighborItem.addItem(overlayItem);
    }

    private void updateNeighborItem(
            CMOManagement cmoManagement,
            Geolocation geolocation,
            short cmoType,
            ItemizedIconOverlay<OverlayItem> overlayNeighborItem)
    {
        Collection<CMOTableEntry> ns = cmoManagement.getTable();
        WGS84 myPos = geolocation.getCurrentPos();

        overlayNeighborItem.removeAllItems();

        for (CMOTableEntry cmo : ns ) {
            addMarkerResId(overlayNeighborItem,
                    getResourceFromCMOId(cmo.getCmoType()),
                    new GeoPoint(cmo.getLatitude(),cmo.getLongitude()),
                    cmo.getCmoID()
           );
        }

        addMarkerResId(overlayNeighborItem,
                getResourceFromCMOId(cmoType),
                new GeoPoint(myPos.latitude(),myPos.longitude()),
                "Me"
        );

        mMapView.invalidate();

    }

    private void initAlertView(CMOManagement cmoManagement, final Geolocation geolocation) {
        final Dashboard db = new Dashboard();
        final CrossingCMO crossingCMO = new CrossingCMO(geolocation,cmoManagement);

        cmoManagement.addListener(db);
        geolocation.addPositionListener(db);
        db.addIndicator(crossingCMO);
        db.addListener(new DashboardListener() {
            @Override
            public void dashboardUpdate() {
                alertUpdate(crossingCMO,geolocation);
            }
        });
    }

    private void initMapView(
            final CMOManagement cmoManagement,
            final Geolocation geolocation,
            final short cmoType
    ) {
        final Context context = getApplicationContext();
        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(context);
        final ITileSource tileSource = new XYTileSource("Mapnik", ResourceProxy.string.mapnik, 1, 18, 256, ".png", "");

        IArchiveFile[] archiveFiles=null;
        try {
            final ZipFileArchive zipfFileArchiveVDA = ZipFileArchive.getZipFileArchive(new File("/sdcard/osmdroid/vda18.zip"));
            final ZipFileArchive zipfFileArchiveLille = ZipFileArchive.getZipFileArchive(new File("/sdcard/osmdroid/lille16.zip"));

            archiveFiles = new IArchiveFile[] { zipfFileArchiveVDA, zipfFileArchiveLille};
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapTileFileArchiveProvider fileArchiveProvider = new MapTileFileArchiveProvider(registerReceiver, tileSource, archiveFiles);
        final MapTileProviderArray tileProviderArray = new MapTileProviderArray(tileSource, registerReceiver, new MapTileModuleProviderBase[] { fileArchiveProvider });


        FrameLayout layout = (FrameLayout) findViewById(R.id.mainView);
        mMapView =  new MapView(context, 256, new DefaultResourceProxyImpl(context), tileProviderArray);/*(MapView) findViewById(R.id.mapview);*/
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        MapController mMapController = mMapView.getController();
        mMapController.setZoom(17);
        GeoPoint gPt = new GeoPoint(50.60612721473346,3.129384328171011);
        mMapController.setCenter(gPt);

        layout.addView(mMapView);

        final ItemizedIconOverlay<OverlayItem> overlayNeighborItem = new ItemizedIconOverlay<OverlayItem>(
                getApplicationContext(),new ArrayList<OverlayItem>(),
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return true;
                    }
                });

        mMapView.getOverlays().add(overlayNeighborItem);

        cmoManagement.addListener(new CMOTableListener() {
            @Override
            public void tableChanged(CMOTableEntry table) {
                updateNeighborItem(cmoManagement,geolocation,cmoType,overlayNeighborItem);
            }

            @Override
            public void tableCMORemoved(CMOTableEntry table) {
                updateNeighborItem(cmoManagement,geolocation,cmoType,overlayNeighborItem);
            }

            @Override
            public void tableCMOAdded(CMOTableEntry table) {
                updateNeighborItem(cmoManagement,geolocation,cmoType,overlayNeighborItem);
            }
        });

        geolocation.addPositionListener(new GeolocationListener() {
            @Override
            public void positionChanged(WGS84 position, Double speed, Double track, int time) {
                updateNeighborItem(cmoManagement, geolocation,cmoType, overlayNeighborItem);
                WGS84 myPos = geolocation.getCurrentPos();
                mMapView.getController().setCenter(new GeoPoint(myPos.latitude(),myPos.longitude()));
            }
        });

        //addMarkerResId(R.drawable.car , gPt);

    }

    private void initBikeView(CMOManagement cmoManagement, Geolocation geolocation, short cmoType) {
        BikeView bike = (BikeView) findViewById(R.id.bikeView);

        bike.setCMOType(cmoType);
        bike.setCMOManagement(cmoManagement);
        bike.setLocationManager(geolocation);
    }
    // end of GUI support
    ////////////////////////////////////////////////////////////////////////////////

    private void initWifi(){
        final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifi.setWifiEnabled(true);

        (new Thread(new Runnable() {
            @Override
            public void run() {
                while(!wifi.isWifiEnabled()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (WifiConfiguration net : wifi.getConfiguredNetworks()){
                    if (net.SSID.compareTo(NETWORK_NAME)==0){
                        wifi.enableNetwork(net.networkId,true);
                        break;
                    }
                }
            }
        })).start();


    }

    ////////////////////////////////////////////////////////////////////////////////
    // Backend initialisation

    private BeaconGenerator initGenerator(
            BeaconSender beaconSender,
            final Geolocation geo,
            String cmoName,
            short cmoType
            )
    {
        final BeaconGenerator gen = new BeaconGenerator(beaconSender, cmoName, cmoType);
        geo.addPositionListener(new GeolocationListener() {
            @Override
            public void positionChanged(WGS84 position, Double speed, Double track, int time) {
                gen.broadcastCMOStatPacket(position, speed.floatValue(), track.floatValue(), time);
            }
        });
        return gen;
    }

    private BeaconSender initBeaconSender(){
        return new BeaconSenderUDP( DST_PORT, BROAD_CAST_ADDR);
    }

    private BeaconRecv initBeaconRecv(String cmoName){
        BeaconRecv bRecv=new BeaconRecvUDP(DST_PORT , cmoName);
        bRecv.start();
        return bRecv;
    }

    private CMOManagement initCMOManager(BeaconRecv bRecv) {
        CMOManagement cmoManagement = new CMOManagement();
        bRecv.addListener(cmoManagement);
        return cmoManagement;
    }

    private void initForwarder(BeaconRecv bRecv, BeaconSender beaconSender){
        bRecv.addListener(new BeaconForward(beaconSender));
    }

    private Geolocation initGeolocation(LocationManager locationManager) {
        return  new GeoLocationAndroid(locationManager);
     }

    private Geolocation initGeolocationTrace(LocationManager locationManager) {
        GeolocationTrace geoTrace = new GeolocationTraceAndroid(trace,300,locationManager);
        geoTrace.startTrace();
        return geoTrace;
    }


    // end of backend initialisation
    ////////////////////////////////////////////////////////////////////////////////


    /**
     * TODO : move to specific class
     * @param id CMO id
     * @return resource id
     */
    static public int getResourceFromCMOId(short id){
        switch(id){
            case CMOHeader.CMO_TYPE_BIKE:
                return R.drawable.bike;
            case CMOHeader.CMO_TYPE_BUS:
                return R.drawable.bus;
            case CMOHeader.CMO_TYPE_CAR:
                return R.drawable.twingo_red;
            case CMOHeader.CMO_TYPE_MOTORBIKE:
                return R.drawable.motorbike;
            case CMOHeader.CMO_TYPE_TRUCK:
                return R.drawable.truck;
            case CMOHeader.CMO_TYPE_WALKER:
                return R.drawable.walker;
            case CMOHeader.CMO_TYPE_SPOT:
                return R.drawable.ap;
        }

        return R.drawable.car;
    }

}

