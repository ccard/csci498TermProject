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

public class CommandPoller extends IntentService {
	
	private ServerSocket ssocket;
	
	public CommandPoller() {
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
				String data = json_map.getString("data");
				
				// TODO: Do whatever to execute the command

				Log.i("COMMAND", "command is " + command + " data is " + data);
				out.write("command is " + command + " data is " + data + System.getProperty("line.separator"));
				
				
				out.flush();
				out.close();
				s.close();
			} catch (Exception e) {
				Log.e("ConnectionError", null, e);
			}
		}
	}


	
}
