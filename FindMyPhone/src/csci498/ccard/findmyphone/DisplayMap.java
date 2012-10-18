/*
 * Chris Card
 * 9/4/12
 * this activity displays the Map for the user
 */
package csci498.ccard.findmyphone;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;

import android.view.Menu;

public class DisplayMap extends MapActivity {

	
	private LocationManager locmgr = null;
	
	private GeoPoint loc;
	private MapController mc;
	private float gpsAccuracy;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
       
        Intent intent = getIntent();
        if(intent != null)
        {
        	 locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, onLocChange);
        
        
        	MapView map = (MapView)findViewById(R.id.mapview);
        	map.setBuiltInZoomControls(true);
        	//getlocation from intent from ohther phone here
        	
        	//initailizes overlays for drop pins to show the phones location
        	List<Overlay> mOverlays = map.getOverlays();
        	mc = map.getController();
        	int lat = (int)(39.755543*1E6);
        	int lon = (int)(-105.2210997*1E6);
        	loc = new GeoPoint(lat,lon);
        	mc.setCenter(loc);
        	mc.setZoom(14);
        	gpsAccuracy = (float) 6.0;
        	MapOverlay overlay = new MapOverlay();
        	mOverlays.add(overlay);
        }
    }
    
    @Override
    public void onStop()
    {
    	super.onStop();
    	locmgr.removeUpdates(onLocChange);
    }

    //this listens for changes for location of the phone inquestion
    private LocationListener onLocChange = new LocationListener(){

		public void onLocationChanged(Location location) {
			if(location != null)
			{
				int latE6 = (int)(location.getLatitude()*1E6);
				int lonE6 = (int)(location.getLongitude()*1E6);
				
				gpsAccuracy = location.getAccuracy();
				//send to server
				
				//put methods here to allow alternate phone to get this phones location
				loc = new GeoPoint(latE6,lonE6);
				mc.animateTo(loc);
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
    	
    };
	
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
    /**
     * This class allows the overlay droppins to be drawn and shown on the map view
     * @author Chris card
     *
     */
	public class MapOverlay extends Overlay {

		private Paint paintBoarder;
		private Paint fillPaint;

		public MapOverlay()
		{
			super();

			paintBoarder = new Paint();
			paintBoarder.setStyle(Paint.Style.STROKE);
			paintBoarder.setAntiAlias(true);
			paintBoarder.setColor(0xee4D2EFF);
			fillPaint = new Paint();
			fillPaint.setStyle(Paint.Style.FILL);
			fillPaint.setColor(0x154D2Eff);
		}

		//draw method for point and canvas
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
		{
			super.draw(canvas, mapView, shadow);
			
			Point myScreen = new Point();
			mapView.getProjection().toPixels(loc, myScreen);
			Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.droppin);
			canvas.drawBitmap(marker, myScreen.x-20, myScreen.y-45,null);

			int radius = (int)mapView.getProjection().metersToEquatorPixels(gpsAccuracy);

			canvas.drawCircle(myScreen.x, myScreen.y, radius, paintBoarder);
			canvas.drawCircle(myScreen.x, myScreen.y, radius, fillPaint);
			return true;
		}

	}
}
