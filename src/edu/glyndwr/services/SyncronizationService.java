package edu.glyndwr.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import edu.glyndwr.recievers.RepeatingAlarmReciever;
import edu.glyndwr.utils.Utils;

public class SyncronizationService extends Service {

	private AlarmManager alarmManager;
	private String previousState = "IDLE";
	// private static final int INTERVAL = 5*60*60*1000; // 5 hours by default
	private static final int INTERVAL = 30 * 1000; // 2 min by default
	private int REQUEST_CODE = 1234;

	@Override
	public void onCreate() {
		super.onCreate();
		Intent intent = new Intent(this, RepeatingAlarmReciever.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				REQUEST_CODE, intent, 0);

		// int hour =
		// Integer.valueOf(SettingsEngine.getInstance().getString(SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR,
		// "0"));
		// int min =
		// Integer.valueOf(SettingsEngine.getInstance().getString(SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE,
		// "0"));
		// Utils.LOG(String.valueOf("Service started :: Service synch interval -> "+hour
		// +":"+min));

		// start service
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), INTERVAL, pendingIntent);
		MyPhoneStateListener phoneListener = new MyPhoneStateListener();
		TelephonyManager telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public IBinder onBind(Intent intent) {
	
		return null;
	}

	@Override
	public void onDestroy() {
		if (alarmManager != null) {
			Intent intent = new Intent(this, RepeatingAlarmReciever.class);
			alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE,intent, 0));
		}
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if ("OFFHOOK".equals(previousState)){
					// user finish communication
					Utils.stopRecordingAudio();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Utils.LOG("OFFHOOK");
				previousState = "OFFHOOK";
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//Utils.LOG("RINGING");
				previousState = "RINGING";
				break;
			}
		}
	}

}
