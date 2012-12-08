/*
 * Chris Card
 * 9/6/12
 * This class allows user to create an account on the server
 */
package csci498.ccard.findmyphone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccount extends Activity {
	
	private static final String LOG_TAG = "CreateAccount";
//	private static final String Extra_Message = "csci498.ccard.findmyphone.PHONE";
	private EditText name;
	private EditText email;
	private EditText password;
	private EditText passwordConfirm;
	private Builder dialog;
//	private Intent intent;
	private Phone currentPhone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account);
//		intent = getIntent();
//		if (intent != null) {

			name = (EditText) findViewById(R.id.name);
			email = (EditText) findViewById(R.id.email);
			password = (EditText) findViewById(R.id.password1);
			passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);
			dialog = new AlertDialog.Builder(this);
			dialog.setNeutralButton("Ok", null);

			Button login = (Button) findViewById(R.id.createNewAccount);
			login.setOnClickListener(createNewAccount);
//		}
		currentPhone = CurrentPhoneManager.getInstance().getPhone();
		CurrentPhoneManager.setPhoneLocation();
	}

	// This verify the user put in the required info and sends it to the server
	private OnClickListener createNewAccount = new OnClickListener() {
		
		public void onClick(View view) {
			// TODO: Extract into input validation function
			if(name.getText().toString().equals("")) {
				dialog.setMessage("You must put in a name for this device");
				dialog.show();
			}
			else if (email.getText().equals("")) {
				dialog.setMessage("You must input an email address");
				dialog.show();
			} 
			else if (password.getText().equals("")) {
				dialog.setMessage("You must input a password");
				dialog.show();
			}
			else if (passwordConfirm.getText().equals("")) {
				dialog.setMessage("You must confirm your password");
				dialog.show();
			}
			else if (password.getText().toString().compareTo(passwordConfirm.getText().toString()) != 0) {
				dialog.setMessage("Your passwords don't match");
				dialog.show();
				password.setText("");
				passwordConfirm.setText("");
			}
			else {				
				// Add stuff for this phone here
				currentPhone.setName(name.getText().toString());
				JSONObject jsonData = currentPhone.toJSON();					
				// add stuff for account here
				try {
					jsonData.put("command", "create_account");
					jsonData.put("email", email.getText().toString());
					jsonData.put("password_hash", password.getText().toString().hashCode());
				} catch (JSONException e) {
					Log.e(LOG_TAG, null, e);
				}				
				
				DataSender.getInstance().sendToServer(jsonData.toString());				
				String sentStatus = DataSender.getInstance().waitForResult();
				
				if (DataSender.DONE.equals(sentStatus)) {
					showMyDevices(currentPhone);
				} else if (DataSender.USER_EXISTS_ERROR.equals(sentStatus)) {
					dialog.setMessage(R.string.email_already_registered);
					dialog.show();
					Log.e(LOG_TAG, "ERROR CREATING ACCOUNT");
				} else if (DataSender.PHONE_ADD_ERROR.equals(sentStatus)) {
					dialog.setMessage(R.string.this_phone_is_already_registered);
					dialog.show();
					Log.e(LOG_TAG, "ERROR CREATING ACCOUNT");
				} else {
					dialog.setMessage(R.string.error_connecting_to_server);
					dialog.show();
				}
			}
		}
		
	};
	
	/**
	 * This method finishes this activity and starts the my devices activity
	 */
	private void showMyDevices(Phone phone) {
		finish();
		JSONObject json = new JSONObject();
		try {
			json.put(phone.getName(), phone.toJSON().toString());
		} catch (JSONException e) {
			Log.e(LOG_TAG, null, e);
		}
		Intent intent = new Intent(this, MyDevices.class);
		intent.putExtra(MyDevices.Extra_Message, json.toString());
		//send info to my devices so this phone will be added 
		startActivity(intent);
	}

}
