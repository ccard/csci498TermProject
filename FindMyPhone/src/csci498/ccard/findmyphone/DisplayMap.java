/*
 * Chris Card
 * 9/4/12
 * this activity displays the Map for the user
 */
package csci498.ccard.findmyphone;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DisplayMap extends MapActivity {

	private LocationManager locmgr = null;	
	private GeoPoint loc;
	private MapController mc;
	private Overlay items;
	private float gpsAccuracy;
	private Phone otherPhone;
	private GetOther other;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
       
        Intent intent = getIntent();
        if(intent != null) {
        	locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, onLocChange);
        
        	MapView map = (MapView)findViewById(R.id.mapview);
        	map.setBuiltInZoomControls(true);
        	JSONObject details;
        	try {
				details = new JSONObject(intent.getStringExtra(MyDevices.Extra_Message));
				otherPhone = new Phone(details);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("DisplayMap", e.toString());
				
				new Builder(this).setTitle("Error")
								 .setMessage("Error while trying to load the selected phone onto the map")
								 .setNegativeButton("Ok", new OnClickListener(){

									public void onClick(DialogInterface arg0,
											int arg1) {
										onDestroy();
									}
									 
								 })
								 .create().show();
			}
        	
        	//initailizes overlays for drop pins to show the phones location
        	items = new Overlay(getResources().getDrawable(R.drawable.droppin));
        	mc = map.getController();
        	int lat = (int)(39.755543*1E6);
        	int lon = (int)(-105.2210997*1E6);
        	loc = new GeoPoint(lat,lon);
        	mc.setCenter(loc);
        	mc.setZoom(14);
        	gpsAccuracy = (float) 6.0;
        	items.addOverlay(new OverlayItem(loc,"Me",""));
        	items.addOverlay(new OverlayItem(new GeoPoint((int)otherPhone.getLastLattitude(), (int)otherPhone.getLastLongitude()), otherPhone.getName(),""));
        	map.getOverlays().add(items);
        	other = new GetOther();
        	other.execute(otherPhone.getIpAddress());
        }
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	locmgr.removeUpdates(onLocChange);
    	if(other.getStatus() == AsyncTask.Status.RUNNING)
    	{
    		other.cancel(true);
    	}
    }

    //this listens for changes for location of the phone inquestion
    private LocationListener onLocChange = new LocationListener() {

		public void onLocationChanged(Location location) {
			if(location != null) {
				int latE6 = (int)(location.getLatitude()*1E6);
				int lonE6 = (int)(location.getLongitude()*1E6);
				
				gpsAccuracy = location.getAccuracy();
				
				//put methods here to allow alternate phone to get this phones location
				loc = new GeoPoint(latE6,lonE6);
				OverlayItem temp = items.getItem(0);
				items.updateItem(0, new OverlayItem(loc,temp.getTitle(),temp.getSnippet()));
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
    
    private class Overlay extends ItemizedOverlay<OverlayItem>
    {
    	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		public Overlay(Drawable icon) {
			super(icon);
			// TODO Auto-generated constructor stub
		}
		
		public void addOverlay(OverlayItem overlay)
		{
			mOverlays.add(overlay);
			populate();
		}
		
		public void updateItem(int item, OverlayItem overlay)
		{
			mOverlays.remove(item);
			mOverlays.add(item, overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int position) {
			return mOverlays.get(position);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mOverlays.size();
		}
    	
    }
	
    private class GetOther extends AsyncTask<String, String, String>
    {

		@Override
		protected String doInBackground(String... params) {
			//passed in string is the ip address;
			String ip = params[0];
			
			//run comunications code here
			//update the gui once it gets a message of the location
			//pass in location in the format Latitud:Longitude
			JSONObject phoneCommand = new JSONObject();
			try {
				phoneCommand.put("command", "get_location");
			} catch (JSONException e) {
				Log.e("DisplayMap", null, e);
			}
			DataSender.getInstance().sendToPhone(ip, phoneCommand.toString());
			String result = DataSender.getInstance().waitForResult();
			try {
				JSONObject phoneStatus = new JSONObject(result);
				
				StringBuilder location = new StringBuilder();
				location.append(phoneStatus.getString("last_lattitude"));
				location.append(":");
				location.append(phoneStatus.getString("last_longitude"));
				
				publishProgress(location.toString());
			} catch (Exception e) {
				Log.e("DisplayMap", null, e);
			}
						
			return null;
		}
		
		protected void onProgressUpdate(String location)
		{
			String words[] = location.split(":");
			int lat = Integer.parseInt(words[0]);
			int lon = Integer.parseInt(words[1]);
			
			GeoPoint p = new GeoPoint(lat,lon);
			
			OverlayItem temp = items.getItem(1);
			items.updateItem(1, new OverlayItem(p,temp.getTitle(),temp.getSnippet()));
		}
    	
    }
	
}
