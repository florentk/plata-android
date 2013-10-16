package fr.ifsttar.plaiimob;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;

import fr.ifsttar.plaiimob.cmo.management.CMOManagement;
import fr.ifsttar.plaiimob.cmo.management.CMOTableEntry;
import fr.ifsttar.plaiimob.cmo.management.CMOTableListener;
import fr.ifsttar.plaiimob.geolocation.Geolocation;
import fr.ifsttar.plaiimob.geolocation.GeolocationListener;
import fr.ifsttar.plaiimob.geolocation.WGS84;

/**
 * Created by florent on 10/10/13.
 */
public class BikeView  extends View implements GeolocationListener {

    final private Paint paint;
    final private Bitmap bicycle;
    final private Bitmap car;
    final private DecimalFormat df;
    final int m_w;
    final int m_h;

    private CMOManagement cmoManagement = null;
    private Geolocation geoLocation = null;

    public BikeView(Context context, AttributeSet attrs) {
        super(context,attrs);

        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1);
        paint.setTextSize(10);

        df = new DecimalFormat();
        df.setMaximumFractionDigits(0);

        bicycle = BitmapFactory.decodeResource(getResources(), R.drawable.bike);
        car = BitmapFactory.decodeResource(getResources(), R.drawable.car);

        Rect bText = new Rect();
        paint.getTextBounds("123.45 m",0,8,bText);
        m_w = bicycle.getWidth() + bText.width();
        m_h = bText.height();
    }

    public void setCMOManagement(CMOManagement cmoManagement) {
       this.cmoManagement = cmoManagement;

       cmoManagement.addListener(new CMOTableListener() {
            @Override
            public void tableChanged(CMOTableEntry table) {
                invalidate();
            }

            @Override
            public void tableCMORemoved(CMOTableEntry table) {
                invalidate();
            }

            @Override
            public void tableCMOAdded(CMOTableEntry table) {
                invalidate();
            }
        });
    }

    public void setLocationManager(Geolocation geoLocation) {
        this.geoLocation = geoLocation;

        geoLocation.addPositionListener(this);

       }

    @Override
    public void positionChanged(WGS84 position, Double speed, Double track, int time) {
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawCircle(10.0f,10.0f,54.0f,paint);

        //long t =  new Date().getTime()/200;
        //bike_x=(t%40)*5


        final int L = getWidth() - m_w;
        final int l = getHeight() - m_h;
        final int r = Math.min(l ,L) / 2;

        //canvas.drawCircle(L/2 + m_w/2,l/2 + m_h/2,r,paint);
        canvas.drawBitmap(car,
                L/2 + m_w/2 - car.getWidth()/2,
                l/2 + m_h/2 - car.getHeight()/2,
                paint);



        if (cmoManagement!=null && geoLocation!=null) {
            Collection<CMOTableEntry> cmos = cmoManagement.getTable();
            WGS84 pos = geoLocation.getCurrentPos();

            for (CMOTableEntry e:cmos) {
                final double a = e.azimuthRad(pos.longitude(),pos.latitude());
                //final double a = (System.currentTimeMillis() % (int)(Math.PI*2000))/1000.0;
                final double d = e.distance(pos.longitude(),pos.latitude());
                final int x = (int)((((double)L)/2.0) + ((double)r)*Math.sin(a)) + m_w / 4;
                final int y = (int)((((double)l)/2.0) - ((double)r)*Math.cos(a)) + m_h / 4;
                final String text = df.format(d) + " m" ;
                Rect bText = new Rect();
                paint.getTextBounds(text,0,text.length(),bText);

                int x_img = x + (int)((bText.width()-(float)bicycle.getWidth()) / 2.0f);
                canvas.drawBitmap(bicycle,x_img ,y ,paint);
                canvas.drawText(text,x,y + bicycle.getHeight() + bText.height(),paint);
            }
        }

    }


}
