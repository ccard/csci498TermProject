/*
 * Chris Card
 * 9/7/12
 */
package csci498.ccard.findmyphone;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class Phone {
	
	private int number;
	private String name;
	private String phonetype;	
	private String ipAddress;
	private String lastLattitude;
	private String lastLongitude;
	private String uniqueID;
	
	public Phone() {
		super();
	}
	
	public Phone(JSONObject json) {
		try {
			name = json.getString("name");
			phonetype = json.getString("phone_type");
			ipAddress = json.getString("ip_address");
			lastLattitude = json.getString("last_lattitude");
			lastLongitude = json.getString("last_longitude");
			uniqueID = json.getString("id_unique");
		} catch (JSONException e) {
			Log.e("Phone", null, e);
		}		
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("name", name);
			json.put("phone_type", phonetype);
			json.put("ip_address", ipAddress);
			json.put("last_lattitude", lastLattitude);
			json.put("last_longitude", lastLongitude);
			json.put("id_unique", uniqueID);
		} catch (JSONException e) {
			Log.e("Phone", null, e);
		}		
		return json;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhonetype() {
		return phonetype;
	}
	
	public void setPhonetype(String phonetype) {
		this.phonetype = phonetype;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getLastLattitude() {
		return lastLattitude;
	}
	
	public void setLastLattitude(String lastLattitude) {
		this.lastLattitude = lastLattitude;
	}
	
	public String getLastLongitude() {
		return lastLongitude;
	}
	
	public void setLastLongitude(String lastLongitude) {
		this.lastLongitude = lastLongitude;
	}
	
	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	public String toString() {
		return name;
	}
	
}
