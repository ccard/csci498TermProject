/*
 * Chris Card
 * 9/6/12
 * This class controls the primary functionality of our app i.e. sending the phone in question
 * a message to display on the screen or play a tone or find the location of the phone 
 */

package csci498.ccard.findmyphone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MyDevices extends Activity {

	public static final String EMAIL_EXTRA_MESSAGE = "csci498.ccard.findmyphone.PHONE_EMAIL";
	public static final String Extra_Message = "csci498.ccard.findmyphone.PHONE";
	private ViewFlipper flip;
	private TextView phoneName;
	private EditText message;
	private Intent intent;
	private List<PhoneManager> myPhones;
	private PhoneManager current;
	private ArrayAdapter<PhoneManager> phoneAdapter;	
	private Builder dialog;
	private String email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_devices);

		intent = getIntent();
		//checks to see if this was started by an intent
		if(intent != null) {
			if (getIntent().getStringExtra(EMAIL_EXTRA_MESSAGE) != null) {
				email = getIntent().getStringExtra(EMAIL_EXTRA_MESSAGE);
			}
			//populate phone list from servers here
			myPhones = new ArrayList<PhoneManager>();//replace this with what ever method populates the phone list from servers

			//stores the list of phones so it can populate the listview with out having to keep
			//refering to the list view
			phoneAdapter = new PhoneAdapter();
			ListView list = (ListView)findViewById(R.id.MyPhones);

			list.setAdapter(phoneAdapter);
			list.setOnItemClickListener(onPhoneSelect);
						
			JSONObject json = null;
			//replace this with blocks gotten from the server
			//			Phone temp = new Phone();
			try {
				if(intent.getStringExtra(Extra_Message) != null) {
					json = new JSONObject(intent.getStringExtra(Extra_Message).toString());
				}
				else {
					//				temp.setName(getSharedPreferences("myfile",MODE_PRIVATE).getString("myfile", ""));
					//				getSharedPreferences("myfile",MODE_PRIVATE).edit().remove("myfile").commit();
				}

				for (Iterator<String> it = json.keys(); it.hasNext();) {
					String key = (String) it.next();
					JSONObject jsonPhone = new JSONObject(json.getString(key));
					Phone currentPhone = new Phone(jsonPhone);
					current = new PhoneManager(currentPhone);
					phoneAdapter.add(current);
				}
			} catch (Exception e) {
				Log.e("MyDevices", null, e);
			}
//			
			
			//end of phone list population from severs

			//initializes all the values to be used else where in code
			flip = (ViewFlipper) findViewById(R.id.viewFlipperMyDevice);
			phoneName = (TextView) findViewById(R.id.phonename);
			message = (EditText) findViewById(R.id.message);

			//initalizes all fields that need onclicklisteneres or onitemclick listeners
			initOnClicks();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		SharedPreferences finalSettings = getSharedPreferences("myfile", MODE_PRIVATE);
		SharedPreferences.Editor editor = finalSettings.edit();
		editor.putString("myfile", current.getPhone().getName());
		editor.putString("myfile", current.getPhone().getName());
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_devices, menu);
		return true;
	}

	// TODO: Implement some way to get the name of the phone
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.add_phone) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Add Phone");
			alert.setMessage("Name this phone:");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  String value = input.getText().toString();
			  addPhoneToServer(value);
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
			
			
			return true;
		}

		return false;
	}

	/**
	 * This method initializes all TextViews, ListViews or Buttons that need onclicklisteners
	 * or onitemclicklisteners
	 */
	private void initOnClicks() {
		TextView senda = (TextView) findViewById(R.id.sendamessage);
		senda.setOnClickListener(onSendaMessage);

		TextView tone = (TextView) findViewById(R.id.playtone);
		tone.setOnClickListener(onPlayTone);

		TextView map = (TextView) findViewById(R.id.showphone);
		map.setOnClickListener(onShowPhone);

		Button send = (Button) findViewById(R.id.send);
		send.setOnClickListener(onSend);

		Button backOptions = (Button) findViewById(R.id.backsend);
		backOptions.setOnClickListener(onBack);

		Button backMyPhones = (Button) findViewById(R.id.backphone);
		backMyPhones.setOnClickListener(onBack);
		
//		Button addPhone = (Button) findViewById(R.id.add_phone_button);
//		addPhone.setOnClickListener(onAddPhone);
	}
	
	//This provides functionality for the back buttons
	private OnClickListener onBack = new OnClickListener() {
		
		public void onClick(View view)  {
			flip.showPrevious();
		}

	};


	//This provides functionality for when the user selects there device
	private OnItemClickListener onPhoneSelect = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long length)  {
			current = myPhones.get(position);

			phoneName.setText(current.getPhone().getName());
			flip.showNext();
		}

	};

	//This provides functionality for when the user selects the option to send the message to the
	//phone
	private OnClickListener onSendaMessage = new OnClickListener() {
		
		public void onClick(View v) {
			flip.showNext();
		}

	};

	//This provides functionality for the send button
	private OnClickListener onSend = new OnClickListener() {
		
		public void onClick(View v) {
			//send message to the phone
			if(! current.sendMessage(message.getText().toString())) {
				dialog.setMessage("Unable to send message to phone " + current.getPhone().getName());
				dialog.show();
			}

			flip.showPrevious();
		}
		
	};

	//This Provides functionality for the play tone option
	private OnClickListener onPlayTone = new OnClickListener() {
		
		public void onClick(View view) {
			//send tone instructions to phone here to phone here

			//this gete the uri ringtone manager from the device
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			//this gets the default notification tone from the ring tone manager
			Ringtone tone = RingtoneManager.getRingtone(getApplicationContext(), notification);
			tone.play();
		}
		
	};

	//This opens the display location activity that incorperates google maps to show the location of the
	//device on a map
	private OnClickListener onShowPhone = new OnClickListener() {
		public void onClick(View view) {
			displayLocation();
		}
	};

	private void addPhoneToServer(String name) {
		JSONObject json = CurrentPhoneManager.getInstance().getPhone().toJSON();			
		try {
			json.put(getString(R.string.command), getString(R.string.add_phone));
			json.put(getString(R.string.email), email);
			json.put(getString(R.string.name), name);
		} catch (JSONException e) {
			Log.e("MyDevices", null, e);
		}
		
		DataSender.getInstance().sendToServer(json.toString());
	}
	
	/**
	 * This initializes the displaymap activity that uses google maps to show the location of the device
	 */
	private void displayLocation() {
		Intent locintent = new Intent(this, DisplayMap.class);
		locintent.putExtra(Extra_Message, current.getPhone().toJSON().toString());
		startActivity(locintent);
	}

	/**
	 * This class holds the PhoneAdapter for populating the listview with the restaurants
	 * @author Chris
	 *
	 */
	class PhoneAdapter extends ArrayAdapter<PhoneManager> {

		PhoneAdapter() {
			super(MyDevices.this, android.R.layout.simple_list_item_1, myPhones);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			PhoneManagerHolder holder = null;

			if(row == null) {
				LayoutInflater inflater = getLayoutInflater();

				row = inflater.inflate(R.layout.phone_view, null);
				holder = new PhoneManagerHolder(row);
				row.setTag(holder);
			}
			else {
				holder = (PhoneManagerHolder)row.getTag();
			}

			holder.populateForm(myPhones.get(position));

			return row;
		}

	}

	/**
	 * This static class is used to populate the RestaurantAdapter rows
	 * @author Chris
	 *
	 */
	static class PhoneManagerHolder {

		private TextView name = null;
		private TextView type = null;

		PhoneManagerHolder(View row) {
			name = (TextView)row.findViewById(R.id.phoneName);
			type = (TextView)row.findViewById(R.id.phoneType);
		}

		void populateForm(PhoneManager p) {
			name.setText(p.getPhone().getName());
			type.setText(p.getPhone().getPhonetype());
		}
		
	}

}
