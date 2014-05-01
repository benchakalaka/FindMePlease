package edu.glyndwr.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import edu.glyndwr.rest.tasks.DirectSynchronizationTask;
import edu.glyndwr.rest.tasks.ReverseSynchronizationTask;
import edu.glyndwr.utils.Utils;

public class RepeatingAlarmReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (null == Utils.getContext()) {
			Utils.setApplicationContext(context);
		} else {
			// Log.d(this.getClass().getName(),"Timed alarm onReceive() started at time: "+
			// new java.sql.Timestamp(System.currentTimeMillis()).toString());
			DirectSynchronizationTask dt = new DirectSynchronizationTask();
			dt.execute(null, null, null);
			
			ReverseSynchronizationTask dt2 = new ReverseSynchronizationTask();
			dt2.execute(null, null, null);
			
		}
	}
}
