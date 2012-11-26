/*
 * Chris Card
 * 9/7/12
 */
package csci498.ccard.findmyphone;

import org.json.JSONException;
import org.json.JSONObject;

public class Phone {
	
	private int number;
	private String name;
	private String phonetype;	
	
	public Phone() {
		super();
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
	
	// TODO:Refactor the rest of this into a PhoneManager 
	
	/**
	 * This method sends a message to the selected phone through the server
	 * and the phone communication classes
	 * @param message the message to send
	 * @return true if the message was able to be sent false other wise
	 */
	public boolean sendMessage(String message) {
		try {
			new DataSender().execute(wrapData("send_message", message));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * this method sends the instruction to play a tone on the selected device
	 * @return true if success false other wise
	 */
	public boolean sendTone() {
		try {
			new DataSender().execute(wrapData("play_tone", ""));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString() {
		return name;
	}
	
	// Wraps data as a JSON object before sending it to the server
	private String wrapData(String command, String data) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("number", number);
		obj.put("name", name);
		obj.put("command", command);
		obj.put("data", data);
		
		return obj.toString();
	}
	
}
