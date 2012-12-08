/*
 * Chris Card
 * 9/4/12
 * this is the main activity for find my phone
 */
package csci498.ccard.findmyphone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindMyPhone extends Activity {

	private static final String LOG_TAG = "FindMyPhone";
	private EditText email;
	private EditText password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_phone);
        
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(onLogin);
        
        TextView create = (TextView) findViewById(R.id.createAccount);
        create.setOnClickListener(onCreateAccount);
        
        CurrentPhoneManager.setContext(this);
        
        startService(new Intent(this, CommandPollerService.class));
    }

    //provides functionality to allow the login button to login to server to verify that
    //the password and email and get the info from server
    private OnClickListener onLogin = new OnClickListener() {
    	
		public void onClick(View view) {
			//login confirmation from server here and get info
			JSONObject json = new JSONObject();
			try {
				json.put(getString(R.string.command), getString(R.string.login));
				json.put(getString(R.string.email), email.getText().toString());
				json.put(getString(R.string.password_hash), password.getText().toString().hashCode());
			} catch (JSONException e) {
				Log.e(LOG_TAG, null, e);
			}
			
			DataSender.getInstance().sendToServer(json.toString());
			String result = DataSender.getInstance().waitForResult();
			
			if ("".equals(result)) {
				Toast.makeText(FindMyPhone.this, getString(R.string.error_connecting_to_server), Toast.LENGTH_SHORT).show();				
			} else if (DataSender.ERROR.equals(result)) {
				Toast.makeText(FindMyPhone.this, getString(R.string.incorrect_email_password), Toast.LENGTH_SHORT).show();
				Log.e(LOG_TAG, "ERROR CREATING ACCOUNT");
			} else {
				displayMyDevices(result);
			}
		
//			displayMyDevices("whats up");
		}
		
    };
    
    //allwos user to create an account on server
    private OnClickListener onCreateAccount = new OnClickListener() {
    	
		public void onClick(View v) {
			displayCreateAccount(v);
		}		
		
    };
    
    /**
     * This method starts the create account activity and finisht this activity
     * @param view
     */
    public void displayCreateAccount(View view) {
    	finish();
    	Intent intent = new Intent (this, CreateAccount.class);
    	intent.putExtra(MyDevices.Extra_Message, "Welcome");
    	startActivity(intent);
    }
   
    /**
     * This method finish this activity and starts the my device activy with info from the server
     * @param phoneInfo the info gotten from the server
     */
    public void displayMyDevices(String phoneInfo) {
    	finish();
    	Intent intent = new Intent (this, MyDevices.class);
    	intent.putExtra(MyDevices.Extra_Message, phoneInfo);
    	intent.putExtra(MyDevices.EMAIL_EXTRA_MESSAGE, email.getText().toString());
    	startActivity(intent);
    }
    
}
