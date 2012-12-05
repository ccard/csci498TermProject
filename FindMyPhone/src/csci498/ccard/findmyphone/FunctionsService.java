/*
 * Chris Card
 * Gurpreet Nanda
 * Tony Nguyen
 * 12/5/12
 * This service intened to be bound so we can get results from it
 */

package csci498.ccard.findmyphone;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class FunctionsService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	
	
	public class FunctionBinder extends Binder
	{
		FunctionsService getService()
		{
			
		}
	}

}
