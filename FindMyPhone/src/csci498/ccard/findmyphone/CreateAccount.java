/*
 * Chris Card
 * 9/6/12
 * This class allows user to create an account on the server
 */
package csci498.ccard.findmyphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccount extends Activity {
	
	private static final String Extra_Message = "csci498.ccard.findmyphone.PHONE";
	private EditText name;
	private EditText email;
	private EditText password;
	private EditText passwordConfirm;
	private Builder dialog;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account);
		intent = getIntent();
		if (intent != null) {

			name = (EditText) findViewById(R.id.name);
			email = (EditText) findViewById(R.id.email);
			password = (EditText) findViewById(R.id.password1);
			passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);
			dialog = new AlertDialog.Builder(this);
			dialog.setNeutralButton("Ok", null);

			Button login = (Button) findViewById(R.id.createNewAccount);
			login.setOnClickListener(createNewAccount);
		}
	}

	// This verify the user put in the required info and sends it to the server
	private OnClickListener createNewAccount = new OnClickListener() {
		
		public void onClick(View view) {
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
				Phone phone = new Phone();
				phone.setName(name.getText().toString());
				// credential and phone info pass to server here
				showMyDevices(phone);
			}
		}
		
	};

	/**
	 * This method finishes this activity and starts the my devices activity
	 */
	private void showMyDevices(Phone phone) {
		finish();
		Intent intent = new Intent(this, MyDevices.class);
		intent.putExtra(Extra_Message,phone.toString());
		//send info to my devices so this phone will be added 
		startActivity(intent);
	}

}
