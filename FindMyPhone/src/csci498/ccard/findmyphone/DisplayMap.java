/*
 * Chris Card
 * 9/4/12
 * this activity displays the Map for the user
 */
package csci498.ccard.findmyphone;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DisplayMap extends MapActivity {

	private LocationManager locmgr = null;	
	private GeoPoint loc;
	private GeoPoint locOther;
	private MapController mc;
	private Overlay items;
	private float gpsAccuracy;
	private Phone otherPhone;
	private GetOther other;
	private MapView map;
	
	private AtomicBoolean run = new AtomicBoolean(true);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
       
        Intent intent = getIntent();
        if(intent != null) {
        
        	map = (MapView)findViewById(R.id.mapview);
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
        	
        	mc = map.getController();
        	//CurrentPhoneManager.setPhoneLocation();
        	double lattitude = 0;
        	double longitude = 0;
        	int i = 0;
        	while (lattitude == 0 && longitude == 0 && i++ < 10) {
	        	try {
	        		Thread.sleep(30);
	            	lattitude = CurrentPhoneManager.getInstance().getPhone().getLastLattitude();
	            	longitude = CurrentPhoneManager.getInstance().getPhone().getLastLongitude(); 
	        	} catch (Exception e) {
	        		Log.e("DisplayMap", null, e);
	        	}
        	}
//        	double lattitude = CurrentPhoneManager.getInstance().getPhone().getLastLattitude();
//        	double longitude = CurrentPhoneManager.getInstance().getPhone().getLastLongitude(); 
        	int lat = (int) (lattitude);
        	int lon = (int) (longitude);
        	loc = new GeoPoint(lat, lon);
        	mc.setCenter(loc);
        	mc.setZoom(14);
        	gpsAccuracy = (float) 6.0;
        	locOther = new GeoPoint((int) (otherPhone.getLastLattitude() * 1E6), (int) (otherPhone.getLastLongitude() * 1E6));
        	items = new Overlay(getResources().getDrawable(R.drawable.droppin));
        	updateOverlay();
        	map.getOverlays().add(items);
        	other = new GetOther();
        	other.execute(otherPhone.getIpAddress());
        	
        }
    }
    
    public void updateOverlay()
    {
    	items.clear();
    	items.addOverlay(new OverlayItem(loc, "Me", ""));
    	items.addOverlay(new OverlayItem(locOther, otherPhone.getName(),""));
    	Log.v("here", "hi");
    	map.invalidate();
    	mc.setCenter(locOther);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(other.getStatus() == AsyncTask.Status.RUNNING)
    	{
    		run.set(false);
    	}
    }

 
	
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    
    private class Overlay extends ItemizedOverlay<OverlayItem>
    {
    	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		public Overlay(Drawable icon) {
			super(boundCenterBottom(icon));
			// TODO Auto-generated constructor stub
		}
		
		public void addOverlay(OverlayItem overlay)
		{
			mOverlays.add(overlay);
			populate();
		}
		
		public void clear()
		{
			mOverlays.clear();
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
			//pass in location in the format Latitude:Longitude
			JSONObject phoneCommand = new JSONObject();
			try {
				phoneCommand.put(getString(R.string.command), R.string.get_location);
			} catch (JSONException e) {
				Log.e("DisplayMap", null, e);
			}
			while (run.get()) {
				DataSender.getInstance().sendToPhone(ip, phoneCommand.toString());
				String result = DataSender.getInstance().waitForResult();
				
				if ("".equals(result)) {
//					Toast.makeText(DisplayMap.this, "No response from phone", Toast.LENGTH_LONG).show();
					
				} else {
					try {
						JSONObject phoneStatus = new JSONObject(result);
						
						StringBuilder location = new StringBuilder();
						location.append(phoneStatus.getString(getString(R.string.last_lattitude)));
						location.append(":");
						location.append(phoneStatus.getString(getString(R.string.last_longitude)));
						
						publishProgress(location.toString());						
					} catch (Exception e) {
						Log.e("DisplayMap.java", null, e);
					}
				}
				
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.e("DisplayMap.java",null, e);
				}
			}
						
			return "";
		}
		
		@Override
		protected void onProgressUpdate(String... location)
		{
			Log.v("before split", "");
			String words[] = location[0].split(":");
			Log.v("After split", "hi");
			int lat = (int) (Double.parseDouble(words[0]) * 1E6);
			int lon = (int) (Double.parseDouble(words[1]) * 1E6);
			Log.v("THis","lat: "+lat+" lom: "+lon);
			locOther = new GeoPoint(lat,lon);
			loc = new GeoPoint((int)CurrentPhoneManager.getInstance().getPhone().getLastLattitude(),(int)CurrentPhoneManager.getInstance().getPhone().getLastLongitude());
			
			updateOverlay();
		}
    	
    }
	
}
