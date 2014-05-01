package edu.glyndwr.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import edu.glyndwr.activities.BlockUserDialogActivity;
import edu.glyndwr.activities.StartActivity;
import edu.glyndwr.services.SyncronizationService;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.tools.TelephonyManagerProcessor;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.ON_BOOT_COMPLETE.equals(intent.getAction())) {
        	
        	
            
            // Try to start activity 
            Intent startIntent = new Intent(context, StartActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
            
            	// start service
            try {
              Intent serviceLauncher = new Intent(context, SyncronizationService.class);
              context.startService(serviceLauncher);
              } catch (Exception e) {
				e.printStackTrace();
			}
              
              try {
            	  Utils.setApplicationContext(context);
            	  String emergencyNumber = SettingsEngine.getInstance().getString(SettingsEngine.EMERGENCY_NUMBER, "");
            	  TelephonyManagerProcessor tmp = new TelephonyManagerProcessor();
            	  Utils.sendTextMessage(emergencyNumber, "New number: "+tmp.getTelephonyOverview().phoneNumber);
            	  Utils.LOG("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! my = "+tmp.getTelephonyOverview().phoneNumber+", emergency "+emergencyNumber);
              } catch (Exception e) {
				e.printStackTrace();
			}
        }  
    }
}
