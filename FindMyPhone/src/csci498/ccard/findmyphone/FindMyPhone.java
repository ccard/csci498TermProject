/*
 * Chris Card
 * 9/4/12
 * this is the main activity for find my phone
 */
package csci498.ccard.findmyphone;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FindMyPhone extends Activity {

	public static final String Extra_Message = "csci498.ccard.findmyphone.PHONE";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_phone);
        
        
        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(onLogin);
        
        TextView create = (TextView)findViewById(R.id.createAccount);
        create.setOnClickListener(onCreateAccount);
    }

    //provides functionality to allow the login button to login to server to verify that
    //the password and email and get the info from server
    private OnClickListener onLogin = new OnClickListener()
    {
		public void onClick(View view) {
			//login confermation from server here and get info
			displayMyDevices("whats up");
		}
    };
    
    //allwos user to create an account on server
    private OnClickListener onCreateAccount = new OnClickListener()
    {
		public void onClick(View v) {
			displayCreateAccount(v);
		}		
    };
    
    /**
     * This method starts the create account activity and finisht this activity
     * @param view
     */
    public void displayCreateAccount(View view)
    {
    	finish();
    	Intent intent = new Intent (this, CreateAccount.class);
    	intent.putExtra(Extra_Message, "Welcome");
    	startActivity(intent);
    }
   
    /**
     * This method finish this activity and starts the my device activy with info from the server
     * @param phoneInfo the info gotten from the server
     */
    public void displayMyDevices(String phoneInfo)
    {
    	finish();
    	Intent intent = new Intent (this, MyDevices.class);
    	intent.putExtra(Extra_Message, phoneInfo);
    	startActivity(intent);
    }
}
