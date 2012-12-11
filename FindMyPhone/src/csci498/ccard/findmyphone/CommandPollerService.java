package csci498.ccard.findmyphone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.RemoteViews;

public class CommandPollerService extends IntentService {
	
	private static final String LOG_MSG = "CommandPollerService";
	private ServerSocket ssocket;
	
	public CommandPollerService() {
		super("CommandPollerService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			ssocket = new ServerSocket(5050);
		} catch (IOException e) {
			Log.e("CommandPoller", null, e);
		}
		
		Notification note = new Notification();
		
		note.icon = R.drawable.ic_launcher;
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		
		RemoteViews c = new RemoteViews(getPackageName(),R.layout.notification_view);
		c.setTextViewText(R.id.noteTitle, "FindMyPhone talking to server");
		
		note.contentView = c;
		
		
		startForeground(1133, note);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ssocket.close();
		} catch (IOException e) {
			Log.e("CommandPoller", null, e);
		}
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		getAndExecuteCommand();
	}
	
	public void getAndExecuteCommand() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		while (connectivityManager.getActiveNetworkInfo().isConnected()) {
			Socket s = null;
			try {
				s = ssocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

				JSONObject json_map = new JSONObject(in.readLine());
				String command = json_map.getString(getString(R.string.command));
				
				// TODO: Do whatever to execute the command
				Log.i(LOG_MSG, "RECEIVED MESSAGE: command is " + command);
				JSONObject temp = handleCommand(command, json_map);
				
				Log.i(LOG_MSG, "sending message: " + temp.toString());
//				Toast.makeText(this, temp.toString(), Toast.LENGTH_LONG).show();
				out.write(temp.toString());				
				
				out.flush();
				in.close();
				out.close();
				s.close();
			} catch (Exception e) {
				Log.e(LOG_MSG, null, e);
			} finally {
				try {
					s.close();
				} catch (IOException e) {
					Log.e(LOG_MSG, null, e);
				}
			}
		}
	}

	private JSONObject handleCommand(String command, JSONObject json_map) {
		if (getString(R.string.get_location).equals(command)) {
			CurrentPhoneManager.setPhoneLocation();
			JSONObject locations = new JSONObject();
			try {
				Thread.sleep(20);
				double lattitude = CurrentPhoneManager.getInstance().getPhone().getLastLattitude();
				double longitude = CurrentPhoneManager.getInstance().getPhone().getLastLongitude();
				locations.put(getString(R.string.last_lattitude), lattitude);
				locations.put(getString(R.string.last_longitude), longitude);
			} catch (Exception e) {
				Log.e(LOG_MSG, null, e);
			}
			updateLocations(locations);
			return locations; 
		}
		return null;
	}

	private void updateLocations(JSONObject locations) {		
		try {
			locations.put(getString(R.string.command), "update_location");
			locations.put("id_unique", CurrentPhoneManager.getInstance().getPhone().getUniqueID());
		} catch (JSONException e) {
			Log.e(LOG_MSG, null, e);
		}
		DataSender.getInstance().sendToServer(locations.toString());
	}
	
}