package edu.glyndwr.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.utils.Utils;

public class LaunchAppViaDialReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
        
		
		Utils.startRecordAudio();
		Bundle bundle = intent.getExtras();
		if (null == bundle)
			return;
		
		
		    
		
		String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Utils.LOG("phone number = "+phoneNubmer);
		Utils.setApplicationContext(context);
		// Retrieve number from settings
			String phoneNumber = SettingsEngine.getInstance().getString(SettingsEngine.UNLOCK_CALL_NUMBER, "");
		
		// if user dial appropriate phone number
		if (phoneNubmer.equalsIgnoreCase(phoneNumber)) {
			try {
				// show application in main list
				Utils.showApplication(context);
				// show toast, for user notification and debugging
				Utils.showToast("Appearance in progress....");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}
	
		 
		
	}
	
	
	
	
	
	
	
	
	
}
