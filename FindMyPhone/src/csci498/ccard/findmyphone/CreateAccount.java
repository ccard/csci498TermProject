/*
 * Chris Card
 * 9/6/12
 * This class allows user to create an account on the server
 */
package csci498.ccard.findmyphone;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccount extends Activity {
	
	private static final String Extra_Message = "csci498.ccard.findmyphone.PHONE";
	private EditText name;
	private EditText email;
	private EditText password;
	private EditText passwordConfirm;
	private Builder dialog;
	private Intent intent;
	private LocationManager locMgr;
	private Phone currentPhone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account);
		intent = getIntent();
		if (intent != null) {

			name = (EditText) findViewById(R.id.name);
			email = (EditText) findViewById(R.id.email);
			password = (EditText) findViewById(R.id.password1);
			passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);
			dialog = new AlertDialog.Builder(this);
			dialog.setNeutralButton("Ok", null);

			Button login = (Button) findViewById(R.id.createNewAccount);
			login.setOnClickListener(createNewAccount);
		}
		currentPhone = new Phone();
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		setPhoneLocation();
	}

	// This verify the user put in the required info and sends it to the server
	private OnClickListener createNewAccount = new OnClickListener() {
		
		public void onClick(View view) {
			if(name.getText().toString().equals("")) {
				dialog.setMessage("You must put in a name for this device");
				dialog.show();
			}
			else if (email.getText().equals("")) {
				dialog.setMessage("You must input an email address");
				dialog.show();
			} 
			else if (password.getText().equals("")) {
				dialog.setMessage("You must input a password");
				dialog.show();
			}
			else if (passwordConfirm.getText().equals("")) {
				dialog.setMessage("You must confirm your password");
				dialog.show();
			}
			else if (password.getText().toString().compareTo(passwordConfirm.getText().toString()) != 0) {
				dialog.setMessage("Your passwords don't match");
				dialog.show();
				password.setText("");
				passwordConfirm.setText("");
			}
			else {				
				setUpPhoneData();
				// Add stuff for this phone here
				
				JSONObject jsonData = currentPhone.toJSON();					
				// add stuff for account here
				try {
					jsonData.put("command", "create_account");
					jsonData.put("email", email.getText().toString());
					jsonData.put("password_hash", password.getText().toString());
				} catch (JSONException e) {
					Log.e("CreateAccount", null, e);
				}				
				
				DataSender.getInstance().sendToServer(jsonData.toString());
//				while (DataSender.getInstance().getLastResult() == "") {}
//				if (DataSender.getInstance().getLastResult() == "DONE") {
					showMyDevices(currentPhone);
//				} else {
//					Log.e("CreateAccount", "ERROR CREATING ACCOUNT");
//				}
			}
		}
		
	};

	// TODO: Maybe these functions should be somewhere else		
	private void setUpPhoneData() {
		currentPhone.setName(name.getText().toString());
		currentPhone.setIpAddress(getIpAddress());			
//		currentPhone.setNumber(number);
		currentPhone.setPhonetype("Smart Phone");
		currentPhone.setUniqueID(Secure.getString(getContentResolver(), Secure.ANDROID_ID));			
	}

	public String getIpAddress() {
		InetAddress iA;
		try {
			iA = InetAddress.getLocalHost();
			return iA.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e("CreateAccount", null, e);
		}
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = Formatter.formatIpAddress(inetAddress.hashCode());
						Log.i("CreateAccount", "***** IP="+ ip);
						return ip;
					}
				}
			}
		} catch (SocketException e) {
			Log.e("CreateAccount", null, e);
		}
		return null;
	}

	public void setPhoneLocation() {
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
	}
	
	private LocationListener onLocationChange = new LocationListener() {
		
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
			currentPhone.setLastLattitude(location.getLatitude()); 
			currentPhone.setLastLongitude(location.getLongitude());
			locMgr.removeUpdates(onLocationChange);
		}

	};
	
	/**
	 * This method finishes this activity and starts the my devices activity
	 */
	private void showMyDevices(Phone phone) {
		finish();
		Intent intent = new Intent(this, MyDevices.class);
		intent.putExtra(Extra_Message,phone.toString());
		//send info to my devices so this phone will be added 
		startActivity(intent);
	}

}
