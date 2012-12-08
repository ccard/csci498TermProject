package csci498.ccard.findmyphone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class CommandPollerService extends IntentService {
	
	private ServerSocket ssocket;
	
	public CommandPollerService() {
		super("CommandPoller");
		try {
			ssocket = new ServerSocket(5050);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			try {
				Socket s = ssocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

				JSONObject json_map = new JSONObject(in.readLine());
				String command = json_map.getString("command");
				
				// TODO: Do whatever to execute the command
				Log.i("COMMAND", "RECEIVED MESSAGE: command is " + command);
				JSONObject temp = handleCommand(command, json_map);
				
				
				Toast.makeText(this, temp.toString(), Toast.LENGTH_LONG).show();
				out.write(temp.toString());				
				
				out.flush();
				out.close();
				s.close();
			} catch (Exception e) {
				Log.e("ConnectionError", null, e);
			}
		}
	}

	private JSONObject handleCommand(String command, JSONObject json_map) {
		if ("get_location".equals(command)) {
			CurrentPhoneManager.setPhoneLocation();
			return CurrentPhoneManager.getInstance().getPhone().toJSON(); 
		}
		return null;
	}


	
}
