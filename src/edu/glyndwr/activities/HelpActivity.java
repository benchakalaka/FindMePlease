package edu.glyndwr.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import edu.glyndwr.adapters.SettingsAdapter;
import edu.glyndwr.tools.AnimatedView;
import edu.glyndwr.tools.AudioRecorder;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.tools.TelephonyManagerProcessor;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.DeviceInfo;
import edu.glyndwr.utils.Utils;

public class HelpActivity extends Activity implements ISuperActivity,
		View.OnClickListener, AdapterView.OnItemClickListener,
		SurfaceHolder.Callback, LocationListener {

	private ImageButton imGPS, imMessages, imContacts, imBlockUser,
			imCapturePicture, imRecordVoice, imLoudMode, imRecVideo, imHelp,
			imRegistrations, imPhoneInfo;
	private Dialog mainDialog = null;
	private ListView listView;
	private SettingsAdapter listViewAdapter;
	public static final String EXTRA_USE_FRONT_FACING_CAMERA = "frontcamera";
	private static final String TAG = "RecordVideo";
	private Boolean mUseFrontFacingCamera = false;
	private VideoView mVideoView = null;
	private MediaRecorder mVideoRecorder = null;
	private Camera mCamera;

	private LocationManager locationManager;
	private String provider;
	private String uniqueOutFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		loadViews();
		mVideoView = (VideoView) this.findViewById(R.id.videoView);

		// mUseFrontFacingCamera =
		// getIntent().getExtras().getBoolean(EXTRA_USE_FRONT_FACING_CAMERA,
		// true);
		if (mUseFrontFacingCamera) {
			// If caller wants to use front facing camera, then make sure the
			// device has one...
			// Hard coded to only open front facing camera on Xoom (model MZ604)
			// For more universal solution try:
			// http://stackoverflow.com/questions/2779002/how-to-open-front-camera-on-android-platform
			String deviceModel = android.os.Build.MODEL;
			if (deviceModel.contains("MZ604")) {
				mUseFrontFacingCamera = true;
			} else {
				// Utils.showToast("The App isn't designed to use this Android's front facing camera.\n "
				// +"The device model is : " + deviceModel);
				mUseFrontFacingCamera = false;
			}
		}

		final SurfaceHolder holder = mVideoView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		// Criteria criteria = new Criteria();
		// provider = locationManager.getBestProvider(criteria, false);
		// Location location = locationManager.getLastKnownLocation(provider);
		/*
		 * // Initialize the location fields if (location != null) {
		 * System.out.println("Provider " + provider + " has been selected.");
		 * onLocationChanged(location); }
		 */

	}

	@Override
	protected void onResume() {
		super.onResume();
		// LocationProvider locationProvider = LocationManager.NETWORK_PROVIDER;
		// locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
		// 120000, 0, this);
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 10 * 1000, (float) 10.0, this);
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 90 * 1000, (float) 10.0, this);
		// locationManager.requestLocationUpdates(provider, 0, 0, this);
		// Utils.LOG("1="+isDataConnectionAvailable(getApplicationContext())+
		// ", 2="+isGpsEnabled(locationManager)+", 3="+isLocationByNetworkEnabled(locationManager));
	}

	public boolean isDataConnectionAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null)
			return false;

		return connectivityManager.getActiveNetworkInfo().isConnected();
	}

	public boolean isGpsEnabled(LocationManager manager) {
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}

	public boolean isLocationByNetworkEnabled(LocationManager manager) {
		if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return false;
		}
		return true;
	}

	public static boolean checkInternetConnection(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET?
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			Log.w(TAG, "Internet Connection NOT Present");
			return false;
		}
	}

	public static boolean isConnAvailAndNotRoaming(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			if (!conMgr.getActiveNetworkInfo().isRoaming())
				return true;
			else
				return false;
		} else {
			Log.w(TAG, "Internet Connection NOT Present");
			return false;
		}
	}

	public static boolean isRoaming(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return (conMgr.getActiveNetworkInfo() != null && conMgr
				.getActiveNetworkInfo().isRoaming());
	}

	@Override
	public void loadViews() {

		imGPS = (ImageButton) findViewById(R.id.help_imagebutton_gps);
		imMessages = (ImageButton) findViewById(R.id.help_imagebutton_messages);
		imContacts = (ImageButton) findViewById(R.id.help_imagebutton_contacts);
		imBlockUser = (ImageButton) findViewById(R.id.help_imagebutton_block_user);
		imCapturePicture = (ImageButton) findViewById(R.id.help_imagebutton_photocamera);
		imRecordVoice = (ImageButton) findViewById(R.id.help_imagebutton_microphone);
		imLoudMode = (ImageButton) findViewById(R.id.help_imagebutton_loud_mode);
		imRecVideo = (ImageButton) findViewById(R.id.help_imagebutton_camera);
		imHelp = (ImageButton) findViewById(R.id.help_imagebutton_help);
		imRegistrations = (ImageButton) findViewById(R.id.help_imagebutton_registration);
		imPhoneInfo = (ImageButton) findViewById(R.id.help_imagebutton_phone_info);

		imGPS.setOnClickListener(this);
		imMessages.setOnClickListener(this);
		imContacts.setOnClickListener(this);
		imBlockUser.setOnClickListener(this);
		imCapturePicture.setOnClickListener(this);
		imRecordVoice.setOnClickListener(this);
		imLoudMode.setOnClickListener(this);
		imRecVideo.setOnClickListener(this);
		imHelp.setOnClickListener(this);
		imRegistrations.setOnClickListener(this);
		imPhoneInfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (null == mainDialog)
			mainDialog = new Dialog(this);
		mainDialog.setContentView(R.layout.dialog_help_show_contacts);
		int ID = v.getId();

		switch (ID) {
		case R.id.help_imagebutton_help:
			showHelp();
			break;

		case R.id.help_imagebutton_contacts:
			showContacts();
			break;

		case R.id.help_imagebutton_messages:
			showMessages();
			break;

		case R.id.help_imagebutton_gps:
			if (Utils.isWifiActive()) {
				showGPSposition();
			} else {
				showTurnWiFiOnDialog();
			}
			break;

		case R.id.help_imagebutton_camera:
			showRecordVideoDialog();
			break;

		case R.id.help_imagebutton_microphone:
			showRecAudio();
			break;

		case R.id.help_imagebutton_phone_info:
			showPhoneInformation();
			break;

		case R.id.help_imagebutton_registration:
			showRegistrationForm();
			break;

		case R.id.help_imagebutton_loud_mode:
			turnOnLoudMode();
			break;

		case R.id.help_imagebutton_block_user:
			Intent startIntent = new Intent(getApplicationContext(),
					BlockUserDialogActivity.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startIntent);
			break;

		case R.id.help_imagebutton_photocamera:
			// Intent start = new
			// Intent(getApplicationContext(),TakePictureActivity.class);
			// start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Toast.makeText(getApplicationContext(),"Disabled",Toast.LENGTH_SHORT).show();
			break;

		}

	}

	private void showTurnWiFiOnDialog() {
		mainDialog = new Dialog(this);
		AnimatedView.IMAGE_NUMBER = 6;
		mainDialog.setContentView(R.layout.dialog_help_turn_on_wifi);
		mainDialog.setTitle("WiFi is not active");
		mainDialog.show();
		Button cancel = (Button) mainDialog
				.findViewById(R.id.dialog_button_turn_onwifi_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mainDialog.dismiss();
			}
		});

		Button turnOnWifi = (Button) mainDialog
				.findViewById(R.id.dialog_button_turn_on_wifi);
		turnOnWifi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mainDialog.dismiss();
				Utils.changeWiFiState(true);
				final ProgressDialog dialog = ProgressDialog.show(
						HelpActivity.this, "", "Turning on WiFi...");
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(8000);
							dialog.dismiss();
							showGPSposition();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				});
				t.start();

			}
		});

	}

	private void turnOnLoudMode() {
		try {
			// Get appropriate audio service
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			// set media volume to maximum value
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					AudioManager.FLAG_SHOW_UI);

			// set ring volume to maximum value
			audioManager.setStreamVolume(AudioManager.STREAM_RING,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
					AudioManager.FLAG_ALLOW_RINGER_MODES);

			// avoid situation with silent mode
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

			// get sound resource and playback it
			MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(),
					R.raw.siren);
			mPlayer.start();
			Utils.showToast("Ring volume: 100 , Media volume: 100");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void showRegistrationForm() {
		try {
			boolean isRegistered = SettingsEngine.getInstance().getBoolean(
					SettingsEngine.REGISTERED, false);
			if (isRegistered) {
				Utils.showToast("Already registered, show form");

			} else {
				Utils.showToast("Not registered yet, please register");
				mainDialog = new Dialog(this);
				AnimatedView.IMAGE_NUMBER = 5;
				mainDialog
						.setContentView(R.layout.dialog_help_registration_detail);
				mainDialog.setTitle("Detail registration information");
				Button ok = (Button) mainDialog
						.findViewById(R.id.dialog_button_registration_detail_ok);
				TextView tvDetail = (TextView) mainDialog
						.findViewById(R.id.dialog_textview_registration_detail);
				StringBuilder sb = new StringBuilder();
				String g = System.getProperty("line.separator");
				sb.append("Name: Ihor");
				sb.append(g);
				sb.append("Sername: Karpachev");
				sb.append(g);
				sb.append("Phone: HTC desire HD");
				sb.append(g);
				tvDetail.setText(sb.toString());
				ok.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mainDialog.dismiss();
					}
				});
				mainDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showGPSposition() {
		Intent i = new Intent(getApplicationContext(), GoogleMapsActivity.class);
		startActivity(i);
	}

	public void showRecordVideoDialog() {
		try {
			beginRecording(mVideoView.getHolder());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		mainDialog = new Dialog(this);
		AnimatedView.IMAGE_NUMBER = 6;
		mainDialog.setContentView(R.layout.dialog_help_rec_video);
		mainDialog.setTitle("Recording video");
		mainDialog.show();
		Button b = (Button) mainDialog
				.findViewById(R.id.dialog_help_recvideo_button_stop);
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					stopRecording();
					mainDialog.dismiss();
					Uri uri = Uri.parse(uniqueOutFile);
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					it.setDataAndType(uri, "video/3gpp");
					startActivity(it);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}

			}
		});

	}

	private void showHelp() {
		mainDialog.setContentView(R.layout.dialog_help_help);
		mainDialog.setTitle("HELP");
		mainDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mainDialog.show();
	}

	private void showPhoneInformation() {
		mainDialog.setContentView(R.layout.dialog_help_phone_info);
		mainDialog.setTitle("Phone information");
		mainDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		TextView userSettingsTextView = (TextView) mainDialog
				.findViewById(R.id.dialog_textview_phone_info_user_settings);
		TextView textViewNetworkDetail = (TextView) mainDialog
				.findViewById(R.id.dialog_textview_phone_info_network_detail);
		TextView textViewSimDetail = (TextView) mainDialog
				.findViewById(R.id.dialog_textview_pone_info_sim_detail);
		TextView textViewDeviceDetail = (TextView) mainDialog
				.findViewById(R.id.dialog_textview_pone_info_dev_detail);
		StringBuilder sb = new StringBuilder();
		sb.append("Unlock phone number : "
				+ SettingsEngine.getInstance().getString(
						SettingsEngine.UNLOCK_CALL_NUMBER, "") + "\n\n");
		sb.append("Emergency phone number :"
				+ SettingsEngine.getInstance().getString(
						SettingsEngine.EMERGENCY_NUMBER, "") + "\n\n");

		String hour = SettingsEngine.getInstance().getString(
				SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR, "5");
		String minute = SettingsEngine.getInstance().getString(
				SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE, "0");
		if (Integer.valueOf(minute) < 10)
			minute = "0" + minute;
		sb.append("Synchronize period: " + hour + ":" + minute + "\n\n");
		sb.append("Unlock PIN: "
				+ SettingsEngine.getInstance().getString(
						SettingsEngine.UNLOCK_PIN, "") + "\n\n");
		String gpsStatus = String.valueOf(
				SettingsEngine.getInstance().getBoolean(
						SettingsEngine.GPS_STATE, false)).equalsIgnoreCase(
				"true") ? "ON" : "OFF";
		sb.append("GPS spy mode : " + gpsStatus + "\n\n");
		sb.append("User private data : " + "\n");
		sb.append("        Name: "
				+ SettingsEngine.getInstance().getString(
						SettingsEngine.REGISTRATION_NAME, "") + "\n");
		sb.append("        Sername: "
				+ SettingsEngine.getInstance().getString(
						SettingsEngine.REGISTRATION_SERNAME, "") + "\n");
		sb.append("        Email: "
				+ SettingsEngine.getInstance().getString(
						SettingsEngine.REGISTRATION_EMAIL, ""));
		userSettingsTextView.setText(sb.toString());

		DeviceInfo dev = (new TelephonyManagerProcessor())
				.getTelephonyOverview();
		sb.delete(0, sb.toString().length());
		sb.append("Phone number: " + dev.phoneNumber + "\n\n");
		sb.append("Network country ISO: " + dev.networkCountryIso + "\n\n");
		sb.append("Network operator name: " + dev.networkOperatorName + "\n\n");
		sb.append("Network roaming: " + dev.networkRoaming + "\n\n");
		sb.append("Network type: " + dev.networkType + "\n\n");
		sb.append("Device phone type: " + dev.phoneTypeString);
		textViewNetworkDetail.setText(sb.toString());

		sb.delete(0, sb.toString().length());
		sb.append("Sim ID: " + dev.simSubscriberId + "\n\n");
		sb.append("Sim serial: " + dev.simSerialNumber + "\n\n");
		sb.append("Cell state: " + dev.callState + "\n\n");
		sb.append("Cell location: " + dev.cellLocationString + "\n\n");
		sb.append("Sim country ISO: " + dev.simCountryIso + "\n\n");
		sb.append("Sim operator: " + dev.simOperator + "\n\n");
		sb.append("Sim state: " + dev.simStateString);
		textViewSimDetail.setText(sb.toString());

		sb.delete(0, sb.toString().length());
		sb.append("Device ID: " + dev.deviceId + "\n\n");
		sb.append("Device software version: " + dev.deviceSoftwareVersion
				+ "\n\n");
		sb.append("Bootloader: " + android.os.Build.BOOTLOADER + "\n\n");
		sb.append("Board: " + android.os.Build.BOARD + "\n\n");
		sb.append("Brand: " + android.os.Build.BRAND + "\n\n");
		sb.append("Device: " + android.os.Build.DEVICE + "\n\n");
		sb.append("Display: " + android.os.Build.DISPLAY + "\n\n");
		sb.append("Hardware: " + android.os.Build.HARDWARE + "\n\n");
		sb.append("Manufacturer: " + android.os.Build.MANUFACTURER + "\n\n");
		sb.append("Model: " + android.os.Build.MODEL + "\n\n");
		sb.append("Product: " + android.os.Build.PRODUCT + "\n\n");
		// sb.append("Serial: " + android.os.Build.SERIAL);

		textViewDeviceDetail.setText(sb.toString());
		mainDialog.show();

	}

	private void showRecAudio() {
		mainDialog = new Dialog(this);
		mainDialog.setContentView(R.layout.dialog_help_rec_voice);
		mainDialog.setTitle("Record audio, select duration");

		final SeekBar seek = (SeekBar) mainDialog
				.findViewById(R.id.dialog_help_seekbar_record_audio);
		seek.setMax(10);
		Button b = (Button) mainDialog
				.findViewById(R.id.dialog_help_recaudio_button_ok);
		Button cancelButton = (Button) mainDialog
				.findViewById(R.id.dialog_unlocknumber_button_cancel);
		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mainDialog.setTitle(String.valueOf("Record audio: " + progress
						+ " second(s)"));
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mainDialog.dismiss();
			}
		});
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HelpActivity.this.recordMicrophoneAudio(seek.getProgress());
				mainDialog.dismiss();
			}
		});
		mainDialog.show();
	}

	private void recordMicrophoneAudio(final int durationInSeconds) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String audiofileName = File.separator + sdf.format(new Date())
				+ ".3gp";
		final AudioRecorder recorder = new AudioRecorder(audiofileName);
		try {
			recorder.start();
			final ProgressDialog recordingProgress = ProgressDialog.show(
					HelpActivity.this, "Recording audio", durationInSeconds
							+ " second(s)");
			Thread audioThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						try {
							Thread.sleep(durationInSeconds * 1000);
							recorder.stop();
							HelpActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									recordingProgress.dismiss();
									Uri uri = Uri.parse(Constants.STORAGE_DIR
											+ audiofileName);
									Intent it = new Intent(Intent.ACTION_VIEW,
											uri);
									it.setDataAndType(uri, "video/3gpp");
									startActivity(it);
								}
							});

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

	private void showMessages() {
		mainDialog.setTitle("Messages which sends to server");
		SettingsAdapter.MESSAGES = true;
		listView = (ListView) mainDialog
				.findViewById(R.id.help_listview_settingslist);
		final Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);
		cursor.moveToFirst();
		final ProgressDialog ad = ProgressDialog.show(HelpActivity.this, "",
				"Gathering messages information...");
		final Map<String, String> map = Collections
				.synchronizedMap(new LinkedHashMap<String, String>());
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				/*
				 * String[] columns = cursor.getColumnNames(); for (int i = 0; i
				 * < columns.length; i++) { Log.d("app", "columns " + i + ": " +
				 * columns[i] + ": " + cursor.getString(i)); }
				 */
				try {
					do {
						map.put(cursor.getString(3), cursor.getString(12));
					} while (cursor.moveToNext());

					listView.setOnItemClickListener(HelpActivity.this);
					listViewAdapter = new SettingsAdapter(HelpActivity.this,
							map);
					listView.setAdapter(listViewAdapter);
					ad.dismiss();
					HelpActivity.this.runOnUiThread(dialogShower);
				} catch (Exception ex) {

					ad.dismiss();
					ex.printStackTrace();
					HelpActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Utils.showToast("Failed to get messages!");
						}
					});
				}
			}
		});
		t.start();

	}

	private void showContacts() {
		SettingsAdapter.MESSAGES = false;
		mainDialog.setTitle("Contacts which sends to server");
		listView = (ListView) mainDialog
				.findViewById(R.id.help_listview_settingslist);
		ContentResolver cr = getContentResolver();
		final Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);

		if (cur.getCount() > 0) {
			final Map<String, String> map = Collections
					.synchronizedMap(new LinkedHashMap<String, String>());
			final ProgressDialog ad = ProgressDialog.show(HelpActivity.this,
					"", "Gathering contacts information...");
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					while (cur.moveToNext()) {
						String id = cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts._ID));
						String name = cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						if (Integer.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
							Cursor phones = getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + id, null, null);
							String phoneNumber = "";
							int i = 1;
							while (phones.moveToNext()) {
								String number = phones.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								if (!phoneNumber.contains(number))
									phoneNumber += i++ + ") " + number + "\n";
							}
							map.put(name, phoneNumber);
							phones.close();
						}
					}
					listView.setOnItemClickListener(HelpActivity.this);
					listViewAdapter = new SettingsAdapter(HelpActivity.this,
							map);
					listView.setAdapter(listViewAdapter);

					ad.dismiss();
					/*
					 * for (Map.Entry<String,String> e : map.entrySet()){
					 * Log.d(e.getKey(),e.getValue()); }
					 */

					HelpActivity.this.runOnUiThread(dialogShower);

				}
			});
			t.start();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mainDialog) {
			try {
				mainDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		locationManager.removeUpdates(this);

	}

	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		Toast.makeText(getApplicationContext(),
				String.valueOf("latitude=" + lat + ", longtitude=" + lng),
				Toast.LENGTH_SHORT).show();
		Utils.LOG("latitude=" + lat + ", longtitude=" + lng);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	private Runnable dialogShower = new Runnable() {
		@Override
		public void run() {
			mainDialog.show();
		}
	};

	// /////////////////////////////////////// CAMERA
	// //////////////////////////////////////////////////////
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// beginRecording(holder);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Log.v(TAG, "Width x Height = " + width + "x" + height);
	}

	public void stopRecording() throws Exception {
		// mRecording = false;
		if (mVideoRecorder != null) {
			mVideoRecorder.stop();
			mVideoRecorder.release();
			mVideoRecorder = null;
		}
		if (mCamera != null) {
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	protected void onDestroy() {
		try {
			stopRecording();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		super.onDestroy();

	}

	/**
	 * Uses the surface defined in video_recorder.xml Tested using 2.2 (HTC
	 * Desire/Hero phone) -> Use all defaults works, records back facing camera
	 * with AMR_NB audio 3.0 (Motorola Xoom tablet) -> Use all defaults doesn't
	 * work, works with these specs, might work with others
	 * 
	 * @param holder
	 *            The surfaceholder from the videoview of the layout
	 * @throws Exception
	 */
	public void beginRecording(SurfaceHolder holder) throws Exception {
		TelephonyManagerProcessor tmp = new TelephonyManagerProcessor();
		if (null == holder){
			holder = mVideoView.getHolder();
		}
		if (mVideoRecorder != null) {
			mVideoRecorder.stop();
			mVideoRecorder.release();
			mVideoRecorder = null;
		}
		if (mCamera != null) {
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		uniqueOutFile = Constants.STORAGE_DIR
				+ String.valueOf(File.separator + tmp.getTelephonyOverview().deviceId/*System.currentTimeMillis()*/)
				+ ".3gp";
		File outFile = new File(uniqueOutFile);
		if (outFile.exists()) {
			outFile.delete();
		}

		try {
			if (mUseFrontFacingCamera) {
				// hard coded assuming 1 is the front facing camera
				mCamera = Camera.open(1);
			} else {
				mCamera = Camera.open();
			}

			// Camera setup is based on the API Camera Preview demo
			mCamera.setPreviewDisplay(holder);
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(640, 480);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
			mCamera.unlock();

			mVideoRecorder = new MediaRecorder();
			mVideoRecorder.setCamera(mCamera);

			// Media recorder setup is based on Listing 9-6, Hashimi et all 2010
			// values based on best practices and good quality,
			// tested via upload to YouTube and played in QuickTime on Mac Snow
			// Leopard
			mVideoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mVideoRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// THREE_GPP
			// is big-endian, storing and transferring the most significant
			// bytes first. MPEG_4 as another option
			mVideoRecorder.setVideoSize(640, 480);// YouTube recommended size:
													// 320x240,
			// OpenGazer eye tracker: 640x480
			// YouTube HD: 1280x720
			mVideoRecorder.setVideoFrameRate(20); // might be auto-determined
													// due to lighting
			mVideoRecorder.setVideoEncodingBitRate(3000000);// 3 megapixel, or
															// the max of
			// the camera
			mVideoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// MPEG_4_SP
			// Simple Profile is
			// for low bit
			// rate and low
			// resolution
			// H264 is MPEG-4 Part 10
			// is commonly referred to
			// as H.264 or AVC
			int sdk = android.os.Build.VERSION.SDK_INT;
			// Gingerbread and up can have wide band ie 16,000 hz recordings
			// (Okay quality for human voice)
			if (sdk >= 10) {
				mVideoRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
				mVideoRecorder.setAudioSamplingRate(16000);
			} else {
				// Other devices only have narrow band, ie 8,000 hz
				// (Same quality as a phone call, not really good quality for
				// any purpose.
				// For human voice 8,000 hz means /f/ and /th/ are
				// indistinguishable)
				mVideoRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			}
			mVideoRecorder.setMaxDuration(60000); // limit to 60 seconds
			mVideoRecorder.setPreviewDisplay(holder.getSurface());
			mVideoRecorder.setOutputFile(uniqueOutFile);
			mVideoRecorder.prepare();
			mVideoRecorder.start();
			// mRecording = true;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

}
