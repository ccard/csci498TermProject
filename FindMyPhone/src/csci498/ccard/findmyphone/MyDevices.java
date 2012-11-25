/*
 * Chris Card
 * 9/6/12
 * This class controls the primary functionality of our app i.e. sending the phone in question
 * a message to display on the screen or play a tone or find the location of the phone 
 */

package csci498.ccard.findmyphone;

import java.util.ArrayList;
import java.util.List;


import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;

public class MyDevices extends Activity {

	private ViewFlipper flip;
	private TextView phoneName;
	private EditText message;
	private Intent intent;
	private List<Phone> myPhones;
	private Phone current;
	private ArrayAdapter<Phone> phoneAddapter;
	
	private Builder dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_devices);

        
        
        intent = getIntent();
        //checks to see if this was started by an intent
        if(intent != null)
        {
        	
        	//populate phone list from servers here
        	myPhones = new ArrayList<Phone>();//replace this with what ever method populates the phone list from servers
        	
        	//stores the list of phones so it can populate the listview with out having to keep
        	//refering to the list view
        	phoneAddapter = new PhoneAdapter();
        	ListView list = (ListView)findViewById(R.id.MyPhones);
        	
        	list.setAdapter(phoneAddapter);
        	list.setOnItemClickListener(onPhoneSelect);
            //replace this with blocks gotten from the server
        	current = new Phone();
           if(intent.getStringExtra(FindMyPhone.Extra_Message) != null)
            {
                current.setName(intent.getStringExtra(FindMyPhone.Extra_Message).toString());
            }
            else
            {
                current.setName(getSharedPreferences("myfile",MODE_PRIVATE).getString("myfile", ""));
                getSharedPreferences("myfile",MODE_PRIVATE).edit().remove("myfile").commit();
            }
        	
        
        	current.setNumber(000000100011);
        	current.setPhonetype("Smart Phone");
        	phoneAddapter.add(current);
        	//end of phone list population from severs
        	
        	
        	//initializes all the values to be used else where in code
        	flip = (ViewFlipper)findViewById(R.id.viewFlipperMyDevice);
        	phoneName = (TextView)findViewById(R.id.phonename);
        	message = (EditText)findViewById(R.id.message);
        	
        	//initalizes all fields that need onclicklisteneres or onitemclick listeners
        	initOnClicks();
        	
        }
        
    }
    
    @Override
    public void onStop()
    {
        super.onStop();
        SharedPreferences finalSettings = getSharedPreferences("myfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = finalSettings.edit();
        editor.putString("myfile",current.getName());
        editor.putString("myfile", current.getName());
        editor.commit();
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	getMenuInflater().inflate(R.menu.my_devices, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if(item.getItemId() == R.id.add_phone)
    	{
    		//add phone info to server here
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * This method initializes all TextViews, ListViews or Buttons that need onclicklisteners
     * or onitemclicklisteners
     */
    private void initOnClicks()
    {
    	TextView senda = (TextView)findViewById(R.id.sendamessage);
    	senda.setOnClickListener(onSendaMessage);
    	
    	TextView tone = (TextView)findViewById(R.id.playtone);
    	tone.setOnClickListener(onPlayTone);
    	
    	TextView map = (TextView)findViewById(R.id.showphone);
    	map.setOnClickListener(onShowPhone);
    	
    	Button send = (Button)findViewById(R.id.send);
    	send.setOnClickListener(onSend);
    	
    	Button backOptions = (Button)findViewById(R.id.backsend);
    	backOptions.setOnClickListener(onBack);
    	
    	Button backMyPhones = (Button)findViewById(R.id.backphone);
    	backMyPhones.setOnClickListener(onBack);
    }

    //This provides functionality for the back buttons
    private OnClickListener onBack = new OnClickListener()
    {
		public void onClick(View view) 
		{
			flip.showPrevious();
		}
    	
    };
    
    
    //This provides functionality for when the user selects there device
    private OnItemClickListener onPhoneSelect = new OnItemClickListener()
    {
    	
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long length) 
		{
			current = myPhones.get(position);
			
			phoneName.setText(current.getName());
			flip.showNext();
			
		}
    	
    };
    
    //This provides functionality for when the user selects the option to send the message to the
    //phone
    private OnClickListener onSendaMessage = new OnClickListener()
    {
		public void onClick(View v) {
			flip.showNext();
		}
    	
    };
    
    //This provides functionality for the send button
    private OnClickListener onSend = new OnClickListener()
    {
		public void onClick(View v) {
			
			//send message to the phone
			if(! current.sendMessage(message.getText().toString()))
			{
				dialog.setMessage("Unable to send message to phone " + current.getName());
				dialog.show();
			}
			
			flip.showPrevious();
		}
    };

    //This Provides functionality for the play tone option
    private OnClickListener onPlayTone = new OnClickListener()
    {
    	public void onClick(View view)
    	{
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
    private OnClickListener onShowPhone = new OnClickListener()
    {
    	public void onClick(View view)
    	{
    		displayLocation();
    	}
    };
    
    /**
     * This initializes the displaymap activity that uses google maps to show the location of the device
     */
    private void displayLocation()
    {
    	Intent locintent = new Intent(this,DisplayMap.class);
    	//passlocation gotten from phone here to the displaymap class via intent extra
    	startActivity(locintent);
    }
    
    /**
	 * This class holds the PhoneAdapter for populating the listview with the restaurants
	 * @author Chris
	 *
	 */
	class PhoneAdapter extends ArrayAdapter<Phone>{
		
		PhoneAdapter()
		{
			super(MyDevices.this, android.R.layout.simple_list_item_1, myPhones);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			PhoneHolder holder = null;
			
			if(row == null)
			{
				LayoutInflater inflater = getLayoutInflater();
				
				row = inflater.inflate(R.layout.phone_view, null);
				holder = new PhoneHolder(row);
				row.setTag(holder);
			}
			else
			{
				holder = (PhoneHolder)row.getTag();
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
	static class PhoneHolder{
		
		private TextView name = null;
		private TextView type = null;
		
		PhoneHolder(View row)
		{
			name = (TextView)row.findViewById(R.id.phoneName);
			type = (TextView)row.findViewById(R.id.phoneType);
		}
		
		void populateForm(Phone p)
		{
			name.setText(p.getName());
			type.setText(p.getPhonetype());
		
		}
	}

}
