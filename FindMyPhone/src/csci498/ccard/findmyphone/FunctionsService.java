/*
 * Chris Card
 * Gurpreet Nanda
 * Tony Nguyen
 * 12/5/12
 * This service intened to be bound so we can get results from it
 * 
 * How to use this service properly: http://developer.android.com/guide/components/bound-services.html#Binder
 */

package csci498.ccard.findmyphone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class FunctionsService extends Service {

	private final IBinder binder = new FunctionBinder();
	
	private int Latitude;
	private int Longitude;
	
	private LocationManager man;
	
	@Override
	public IBinder onBind(Intent i) {
		man = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Latitude = (int)(39.755543*1E6);
    	Longitude = (int)(-105.2210997*1E6);
		return binder;
	}
	
	/**
	 * This allows access to methods in this service so that other class can access them
	 * @author Chris card
	 *
	 */
	public class FunctionBinder extends Binder
	{
		FunctionsService getService()
		{
			return FunctionsService.this;
		}
	}
	
	/**
	 * This starts the location listener to listen for the devices location
	 */
	public void startLocationListener()
	{
		man.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.5f, loclis);
	}
	
	/**
	 * This stops the location listener
	 * @note if startLocationListener is called then this should be called when location is no longer
	 * need or risk running the device battery down 
	 */
	public void stopLocationListener()
	{
		man.removeUpdates(loclis);
	}
	
	
	/**
	 * This method returns the latitude and longitude of default location or current location
	 * @return the latitude and longitude appended together in the format 'Latitude:Longitude'
	 */
	public String getLocation()
	{
		return Latitude+":"+Longitude;
	}
	
	
	//Gurpreet put the methods you want here look at top for link on how to use the binded service
	
	
	private LocationListener loclis = new LocationListener(){

		public void onLocationChanged(Location loc) {
			if(loc != null) {
				Latitude = (int)(loc.getLatitude()*1E6);
				Longitude = (int)(loc.getLongitude()*1E6);
			}
			
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	};

}
