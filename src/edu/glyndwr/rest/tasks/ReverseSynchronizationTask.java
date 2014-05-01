package edu.glyndwr.rest.tasks;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.VideoView;
import edu.glyndwr.activities.BlockUserDialogActivity;
import edu.glyndwr.activities.HelpActivity;
import edu.glyndwr.activities.R;
import edu.glyndwr.rest.RestService;
import edu.glyndwr.rest.ServicesNames;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.tools.TelephonyManagerProcessor;
import edu.glyndwr.tools.TextPlayer;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.DeviceInfo;
import edu.glyndwr.utils.Utils;
/**
 * Syncronization FROM SERVER ---------> TO CLIENT
 * @author Karpachev Ihor
 *
 */
public class ReverseSynchronizationTask extends AsyncTask<Void, Void, JSONObject> {

	private RestService client;
	private JSONObject toServer;
	boolean serverAvaliable = true;	

	public ReverseSynchronizationTask() {
		toServer = new JSONObject();
		DeviceInfo dev = (new TelephonyManagerProcessor()).getTelephonyOverview();
		try {
			toServer.putOpt("device_id" , String.valueOf(dev.deviceId));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		client = new RestService(ServicesNames.REVERSE_SYNCHRONIZATION);
		client.setTimeout(4000);
		client.addHeader("Accept", "application/json;charset=utf-8");
		client.addHeader("Content-Type", "application/json;charset=utf-8");
		client.addParam("out", toServer.toString());
	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		JSONObject retValue = null;
		try {
			Utils.LOG(toServer.toString());
			return retValue = client.execute(toServer.toString());
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoHttpResponseException e) {
			e.printStackTrace();
		}
		return retValue;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (null != result) {
			try {
				String isStolen = result.getString("is_stolen");
				
				  
				Utils.LOG("json+"+result.toString());
				
				if (isStolen.equalsIgnoreCase("true")){
					
					try {
						String res = result.getString("block_mobile_device");
						if ("true".equalsIgnoreCase(res)){
							Intent i = new Intent(Utils.getContext(), BlockUserDialogActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							Utils.getContext().startActivity(i);
						}
						  
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					// device was stollen
					/*try {
						String target = result.getString("time");
					    Date date =  new Date(Long.valueOf(target));  
					    Utils.LOG("....................."+date.getMinutes()+":"+date.getHours());
					    
					} catch (Exception ee){
						ee.printStackTrace();
					}*/
					
					/*try {
						Utils.LOG("start recording");
						String video = result.getString("is_capture_video");
						if ("true".equals(video)){
							Utils.LOG("start recording");
							int i = Integer.valueOf(result.getString("minutes_video"));
							HelpActivity h = new HelpActivity();
							h.beginRecording(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}*/
					
					
					
					SettingsEngine.getInstance().saveString(SettingsEngine.EMERGENCY_NUMBER, result.getString("emergency_phone_number"));
				//	SettingsEngine.getInstance().saveString(SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR, result.getString("hours"));
				//	SettingsEngine.getInstance().saveString(SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE,result.getString("minutes") );
					SettingsEngine.getInstance().saveBoolean(SettingsEngine.GPS_STATE, Boolean.valueOf(result.getString("gps_spy_mode")));
					SettingsEngine.getInstance().saveString(SettingsEngine.UNLOCK_CALL_NUMBER, result.getString("unlock_phone_number"));
					SettingsEngine.getInstance().saveString(SettingsEngine.UNLOCK_PIN, result.getString("unlock_pin"));
					
				}
				
				
				/*settingsObj.putOpt("emergency_phone_number", s.getEmergencyPhoneNumber());
				settingsObj.putOpt("gps_spy_mode", s.getGpsSpyMode());
				settingsObj.putOpt("synchronize", s.getSynchronizationPeriod());
				settingsObj.putOpt("unlock_phone_number", s.getUnlockPhoneNumber());
				settingsObj.putOpt("unlock_pin", s.getUnlockPin());
				settingsObj.putOpt("is_delete_message", s.isDeleteMessages());
				settingsObj.putOpt("is_delete_contacts", s.isDeleteContacts());
				settingsObj.putOpt("is_stolen", s.isStolen());
				*/
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
