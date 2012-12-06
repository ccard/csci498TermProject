package csci498.ccard.findmyphone;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
/**
 * Class for managing sending data to the phone in question
 * @author gnanda
 *
 */
public class PhoneManager {
	
	protected Phone phone;

	public PhoneManager(Phone phone) {
		super();
		this.phone = phone;
	}

	public Phone getPhone() {
		return phone;
	}
	
	/**
	 * This method sends a message to the selected phone through the server
	 * and the phone communication classes
	 * @param message the message to send
	 * @return true if the message was able to be sent false other wise
	 */
	public boolean sendMessage(String message) {
		try {
			DataSender.getInstance().sendToPhone(phone.getIpAddress(), wrapData("send_message", message));
			return true;
		} catch (Exception e) {
			Log.e("PhoneManager", null, e);
		}
		return false;
	}

	/**
	 * this method sends the instruction to play a tone on the selected device
	 * @return true if success false other wise
	 */
	public boolean sendTone() {
		try {
			DataSender.getInstance().sendToPhone(phone.getIpAddress(), wrapData("play_tone", ""));
			return true;
		} catch (Exception e) {
			Log.e("PhoneManager", null, e);
		}
		return false;
	}
	
	// Wraps data as a JSON object before sending it to the server
	private String wrapData(String command, String data) throws JSONException {
		JSONObject obj = phone.toJSON();
		obj.put("command", command);
		obj.put("data", data);
		
		return obj.toString();
	}

}
