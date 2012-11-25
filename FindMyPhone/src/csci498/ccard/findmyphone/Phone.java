/*
 * Chris Card
 * 9/7/12
 */
package csci498.ccard.findmyphone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
			new CommandSender().execute(wrapData("send_message", message));
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
	public boolean sendTone()
	{
		try {
			new CommandSender().execute(wrapData("play_tone", ""));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString()
	{
		return name;
	}
	
	private String wrapData(String command, String data) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("number", number);
		obj.put("name", name);
		obj.put("command", command);
		obj.put("data", data);
		
		return obj.toString();
	}
	
	private class CommandSender extends AsyncTask<String, Void, String> {
		
		private static final String IP_ADDRESS = "138.67.77.103";
		private static final int TCP_SERVER_PORT = 5050;
		
		@Override
		protected String doInBackground(String... params) {
			try {
				Socket s = new Socket(IP_ADDRESS, TCP_SERVER_PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				String outMsg = "TCP connecting to " + TCP_SERVER_PORT + System.getProperty("line.separator");
				
				out.write(params[0] + System.getProperty("line.separator"));
				out.flush();
				
				String returnMessage = in.readLine();
				
				Log.i("TcpClient", "sent: " + outMsg);
				s.close();
				return returnMessage;
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String objResult) {
			// TODO: Do something with the result
			Log.i("MSG: ", objResult);
		}
		
	}
	
}
