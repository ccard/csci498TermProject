package csci498.ccard.findmyphone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class DataSender {
	
	public static final String SERVER_ADDRESS = "138.67.77.103";
	public static final String DONE = "DONE";
	public static final String ERROR = "ERROR";
	public static final String PHONE_ADD_ERROR = "PHONE_ADD_ERROR";
	public static final String USER_EXISTS_ERROR = "USER_EXISTS_ERROR";
	private static final String LOG_MSG = "DataSender";
	private static String lastResult = "";	
	private static DataSender ds = new DataSender();
	
	// Singleton Pattern
	private DataSender() {
		// No-op
	}
	
	public static DataSender getInstance() {
		return ds;
	}
	
	/**
	 * Sends a JSON string to the server
	 * @param jsonData data encoded as a string using JSON
	 */
	public void sendToServer(String jsonData) {
		lastResult = "";
		new SenderTask().execute(SERVER_ADDRESS, jsonData);
	}
	
	/**
	 * Sends a JSON string to a particular phone
	 * @param ipAddress the ip address of the phone
	 * @param jsonData data encoded as a string using JSON
	 */
	public void sendToPhone(String ipAddress, String jsonData) {
		lastResult = "";
		new SenderTask().execute(ipAddress, jsonData);
	}
	
//	public String getLastResult() {
//		return lastResult;
//	}
	
	public String waitForResult() {
		int i = 0;
		while ("".equals(lastResult) && (i++ < 20)) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				Log.e(LOG_MSG, null, e);
			}
		}
		return lastResult;		
	}

	private class SenderTask extends AsyncTask<String, Void, String> {

		private static final int TCP_SERVER_PORT = 5050;

		@Override
		protected String doInBackground(String... params) {
			Socket s = null;
			try {
				String ipAddress = params[0];
				// Open the socket to the server
				s = new Socket(ipAddress, TCP_SERVER_PORT);

				// Create the reader and writer for data from the server
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

				// Send the first parameter to the server
				Log.i(LOG_MSG, "Sending data:" + params[1]);
				out.write(params[1] + System.getProperty("line.separator"));
				out.flush();

				// Retrieve the response
				String returnMessage = in.readLine();

				s.close();
				if (returnMessage != null)	{
					lastResult = returnMessage;
					return returnMessage;
				}
			} catch (UnknownHostException e) {
				Log.e(LOG_MSG, null, e);
			} catch (IOException e) {
				Log.e(LOG_MSG, null, e);
			} finally {
				try {
					if(null != s) {
						s.close();
					}
				} catch (IOException e) {
					Log.e(LOG_MSG, null, e);
				}
			}
			return "";
		}

		@Override
		protected void onPostExecute(String objResult) {
			Log.i(LOG_MSG, "Result: " + objResult);
			lastResult = objResult;
		}
		
	}

}