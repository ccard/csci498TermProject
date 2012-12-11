package csci498.ccard.findmyphone;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

public class CurrentPhoneManager extends PhoneManager {
	
	private static final String LOG_TAG = "CurrentPhoneManager";
	private static Context context;
	private static CurrentPhoneManager instance;
	private static LocationManager locMgr;

	static {
		Phone thisPhone = new Phone();
		instance = new CurrentPhoneManager(thisPhone);
	}
	
	private CurrentPhoneManager(Phone phone) {
		super(phone);	
		
	}
	
	public static void setContext(Context context) {
		CurrentPhoneManager.context = context;		
		setUpPhoneData(instance.phone);
		locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		setPhoneLocation();
	}
	
	public static CurrentPhoneManager getInstance() {
		return instance;
	}
	
	private static void setUpPhoneData(Phone thisPhone) {
		thisPhone.setName("");
		thisPhone.setIpAddress(getIpAddress());			
//		currentPhone.setNumber(number);
		thisPhone.setPhonetype("Smart Phone");
		thisPhone.setUniqueID(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));			
	}

	public static String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						String ip = inetAddress.getHostAddress();
						Log.i(LOG_TAG, "***** IP="+ ip);
						return ip;
					}
				}
			}
		} catch (SocketException e) {
			Log.e(LOG_TAG, null, e);
		} 
		return null;
	}

	public static void setPhoneLocation() {
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
	}
	
	private static LocationListener onLocationChange = new LocationListener() {
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// None
			
		}
		
		public void onProviderEnabled(String provider) {
			// None
			
		}
		
		public void onProviderDisabled(String provider) {
			// None
			
		}
		
		public void onLocationChanged(Location location) {
			if (location != null) {
				instance.phone.setLastLattitude(location.getLatitude()*1E6); 
				instance.phone.setLastLongitude(location.getLongitude()*1E6);
				locMgr.removeUpdates(onLocationChange);
			}
		}

	};

}
