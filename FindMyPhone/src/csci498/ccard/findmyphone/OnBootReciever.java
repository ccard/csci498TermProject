/*
 * Chris card
 * Gurpreet Nanda
 * Tony Nguyen
 * 10/29/12
 */
package csci498.ccard.findmyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctxt, Intent intent) {
		ctxt.startService(new Intent(ctxt, CommandPollerService.class));
	}

}
