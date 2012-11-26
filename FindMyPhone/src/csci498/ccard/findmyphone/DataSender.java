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

public class DataSender extends AsyncTask<String, Void, String> {

	private static final String IP_ADDRESS = "138.67.77.103";
	private static final int TCP_SERVER_PORT = 5050;

	@Override
	protected String doInBackground(String... params) {
		try {
			Socket s = new Socket(IP_ADDRESS, TCP_SERVER_PORT);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

			out.write(params[0] + System.getProperty("line.separator"));
			out.flush();

			String returnMessage = in.readLine();

			s.close();
			if (returnMessage != null)	return returnMessage;

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void onPostExecute(String objResult) {
		// TODO: Do something with the result
		Log.i("MSG: ", objResult);
	}
	
}