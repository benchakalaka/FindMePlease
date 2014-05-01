package edu.glyndwr.recievers;

import java.util.StringTokenizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import edu.glyndwr.activities.BlockUserDialogActivity;
import edu.glyndwr.activities.BuildConfig;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.tools.TelephonyManagerProcessor;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.DeviceInfo;
import edu.glyndwr.utils.Utils;

public class SmsReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

			Utils.setApplicationContext(context);
			Bundle bundle = intent.getExtras();
			SmsMessage[] msgs = null;
			String msg_from = "", msgBody = "";
			if (bundle != null) {
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						msg_from = msgs[i].getOriginatingAddress();
						msgBody += msgs[i].getMessageBody();
					}
					Log.d("Message caught", "from: " + msg_from + ", body: "+ msgBody);
					
					StringTokenizer stCommand = new StringTokenizer(msgBody, ","); 
					while (stCommand.hasMoreElements()) {
						String command = stCommand.nextElement().toString();
						if (command.contains(Constants.BLOCK_USER_WITH_MESSAGE)){
							//StringTokenizer stPinAndMessage = new StringTokenizer(command, " ");
							String message = command.substring(command.indexOf("["));
							String pin = command.substring(command.indexOf(" ")+1, command.indexOf("[")-1);
							Utils.LOG("message:"+message+", pin:"+pin);
							BlockUserDialogActivity.PIN = "pin:"+pin;
							BlockUserDialogActivity.MESSAGE = message;
							Intent startIntent = new Intent(context,BlockUserDialogActivity.class);
							startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(startIntent);
						}
					}
					
					if (msg_from.equals(SettingsEngine.getInstance().getString(SettingsEngine.EMERGENCY_NUMBER, ""))){
						
						if (msgBody.contains(Constants.SIGNAL_MESSAGE_DELETE_CONTACTS)){
							Log.d("Secure message caught", "delete contacts");
							Utils.deleteAllContacts();
						} 
						
						if (msgBody.contains(Constants.SIGNAL_MESSAGE_DELETE_MESSAGES)){
							Log.d("Secure message caught", "delete messages, not working");
							Utils.deleteAllMessages();
						}
						
						if (msgBody.contains(Constants.SIGNAL_MESSAGE_SEND_INFO_VIA_SMS)){
							Log.d("Secure message caught", "send via info");
							DeviceInfo dev = (new TelephonyManagerProcessor()).getTelephonyOverview();
							Utils.sendTextMessage(msg_from, dev.toString());  
						}
						
						if (msgBody.contains(Constants.TURN_ON_WIFI)){
							Log.d("Secure message caught", "turn on wifi");
							if (!Utils.isWifiActive()){
								Utils.turnGPSOn();
								Utils.changeWiFiState(true);
							}
						}
						
						if (msgBody.contains(Constants.TURN_ON_GPS)){
							Log.d("Secure message caught", "turn on gps");
							Utils.turnGPSOn();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				
			}

		} else {
			if (BuildConfig.DEBUG)
				Utils.LOG("Message recieved, but flag -> automating forwarding not set");
		}

	}
}
