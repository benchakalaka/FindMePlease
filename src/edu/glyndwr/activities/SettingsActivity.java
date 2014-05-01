package edu.glyndwr.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import edu.glyndwr.adapters.SettingsAdapter;
import edu.glyndwr.recievers.LaunchAppViaDialReceiver;
import edu.glyndwr.tools.AnimatedView;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;

public class SettingsActivity extends Activity implements ISuperActivity,
		AdapterView.OnItemClickListener {

	private Dialog mainDialog = null;
	private ListView settingsListView;
	private SettingsAdapter settingsListViewAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		loadViews();
	}

	@Override
	public void loadViews() {
		settingsListView = (ListView) findViewById(R.id.settings_listview_settingslist);
		settingsListViewAdapter = new SettingsAdapter(this);
		settingsListView.setAdapter(settingsListViewAdapter);
		settingsListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		showAppropriateDialog(pos);
	}

	private void showAppropriateDialog(final int position) {
		if (null == mainDialog)
			mainDialog = new Dialog(this);

		switch (position) {

		// SHOW UNLOCK VIA CALL DIALOG
		case 0:
			AnimatedView.IMAGE_NUMBER = 0;
			mainDialog.setContentView(R.layout.dialog_input_number);
			mainDialog.setTitle("UNLOCKING PHONE NUMBER");
			final EditText editTextInputPhoneNumber = (EditText) mainDialog
					.findViewById(R.id.dialog_inputnumber_edittext_phonenumber);
			editTextInputPhoneNumber.setRawInputType(Configuration.KEYBOARD_12KEY);

			editTextInputPhoneNumber.setText(SettingsEngine.getInstance().getString(SettingsEngine.UNLOCK_CALL_NUMBER, ""));
			editTextInputPhoneNumber.setSelection(editTextInputPhoneNumber.getText().length());

			Button dialogButtonUnlockNumberOk = (Button) mainDialog.findViewById(R.id.dialog_unlocknumber_button_ok),
			dialogButtonUnlockCancel = (Button) mainDialog.findViewById(R.id.dialog_unlocknumber_button_cancel);

			dialogButtonUnlockCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mainDialog.dismiss();
				}
			});
			dialogButtonUnlockNumberOk.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							SettingsEngine.getInstance().saveString(SettingsEngine.UNLOCK_CALL_NUMBER,editTextInputPhoneNumber.getText().toString());
							SettingsEngine.getInstance().saveString(SettingsEngine.LMD_UNLOCK_CALL_NUMBER,java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
							settingsListViewAdapter.notifyDataSetChanged();
							IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
							registerReceiver(new LaunchAppViaDialReceiver(), intentFilter);
							mainDialog.dismiss();
						}
					});
			mainDialog.show();
			break;

		// SHOW ADD PHONE NUMBER
		case 1:
			AnimatedView.IMAGE_NUMBER = 1;
			mainDialog.setContentView(R.layout.dialog_emergency_phone_number);
			mainDialog.setTitle("EMERGENCY NUMBER");
			final EditText editTextAdditionNumber = (EditText) mainDialog.findViewById(R.id.dialog_emergency_edittext_phone_number);
			editTextAdditionNumber
					.setRawInputType(Configuration.KEYBOARD_12KEY);
			try {
				editTextAdditionNumber.setText(SettingsEngine.getInstance().getString(SettingsEngine.EMERGENCY_NUMBER, ""));
				editTextAdditionNumber.setSelection(editTextAdditionNumber.getText().length());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Button dialogButtonAdditionNumberOk = (Button) mainDialog
					.findViewById(R.id.dialog_emergency_ok),
			dialogButtonAdditionNumberCancel = (Button) mainDialog
					.findViewById(R.id.dialog_emergency_cancel);

			dialogButtonAdditionNumberCancel
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mainDialog.dismiss();
						}
					});
			dialogButtonAdditionNumberOk
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							SettingsEngine.getInstance()
									.saveString(
											SettingsEngine.EMERGENCY_NUMBER,
											editTextAdditionNumber.getText()
													.toString());
							SettingsEngine.getInstance().saveString(
									SettingsEngine.LMD_EMERGENCY_NUMBER,
									java.text.DateFormat.getDateTimeInstance()
											.format(Calendar.getInstance()
													.getTime()));
							settingsListViewAdapter.notifyDataSetChanged();

							Log.d("TAG", "Addition number: "
									+ editTextAdditionNumber.getText()
											.toString());
							mainDialog.dismiss();
						}
					});
			mainDialog.show();
			break;

		// SHOW SYCNHRONIZATION TIME PICKER
		case 2:
			AnimatedView.IMAGE_NUMBER = 2;
			mainDialog.setContentView(R.layout.dialog_timepicker);
			final TimePicker timePicker = (TimePicker) mainDialog
					.findViewById(R.id.dialog_timepicker_time);
			timePicker.setIs24HourView(true);

			timePicker.setCurrentHour(Integer.valueOf(SettingsEngine
					.getInstance().getString(
							SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR, "5")));
			timePicker
					.setCurrentMinute(Integer
							.valueOf(SettingsEngine
									.getInstance()
									.getString(
											SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE,
											"0")));

			Button dialogButtonSynchOk = (Button) mainDialog
					.findViewById(R.id.dialog_timepicker_button_ok),
			dialogButtonSynchCancel = (Button) mainDialog
					.findViewById(R.id.dialog_timepicker_button_cancel);

			dialogButtonSynchCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mainDialog.dismiss();
				}
			});
			dialogButtonSynchOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SettingsEngine.getInstance().saveString(SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR,String.valueOf(timePicker.getCurrentHour()));
					SettingsEngine.getInstance().saveString(SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE,String.valueOf(timePicker.getCurrentMinute()));
					SettingsEngine.getInstance().saveString(SettingsEngine.LMD_SYNCHRONIZATION_PERIOD,java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));

					settingsListViewAdapter.notifyDataSetChanged();
					Log.d("TAG","You typed number: " + timePicker.getCurrentHour()+ ":" + timePicker.getCurrentMinute());
					
					if (null != Utils.SYNCHRONIZATION_SERVICE){
						stopService(Utils.SYNCHRONIZATION_SERVICE);
						startService(Utils.SYNCHRONIZATION_SERVICE);
						
					}
					
					
					mainDialog.dismiss();
				}
			});
			mainDialog.setTitle("SYNCHRONIZATION TIME");
			mainDialog.show();
			break;

		// SHOW UNLOCK PIN DIALOG
		case 3:
			AnimatedView.IMAGE_NUMBER = 0;
			mainDialog.setContentView(R.layout.dialog_unlock_pin);
			mainDialog.setTitle("UNLOCKING PIN");
			// editText.setText("+3");
			// editText.setSelection(editText.getText().length());
			final EditText editTextUnlockPin = (EditText) mainDialog
					.findViewById(R.id.dialog_unlockpin_edittext_pin);
			editTextUnlockPin.setRawInputType(Configuration.KEYBOARD_12KEY);
			editTextUnlockPin.setText(SettingsEngine.getInstance().getString(
					SettingsEngine.UNLOCK_PIN, ""));
			editTextUnlockPin
					.setSelection(editTextUnlockPin.getText().length());

			Button dialogButtonUnlockPinOk = (Button) mainDialog
					.findViewById(R.id.dialog_unlockpin_button_ok),
			dialogButtonUnlockPinCancel = (Button) mainDialog
					.findViewById(R.id.dialog_unlockpin_button_cancel);

			dialogButtonUnlockPinCancel
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mainDialog.dismiss();
						}
					});
			dialogButtonUnlockPinOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SettingsEngine.getInstance().saveString(
							SettingsEngine.UNLOCK_PIN,
							editTextUnlockPin.getText().toString());
					SettingsEngine.getInstance().saveString(
							SettingsEngine.LMD_UNLOCK_PIN,
							java.text.DateFormat.getDateTimeInstance().format(
									Calendar.getInstance().getTime()));
					settingsListViewAdapter.notifyDataSetChanged();
					Log.d("TAG", "Unlock PIN: "
							+ editTextUnlockPin.getText().toString());
					mainDialog.dismiss();
				}
			});
			mainDialog.show();
			break;

		// SHOW GPS STATE PICKER
		case 4:
			AnimatedView.IMAGE_NUMBER = 4;
			mainDialog.setContentView(R.layout.dialog_choose_gps_state);

			final ToggleButton toggleButtonGpsState = (ToggleButton) mainDialog.findViewById(R.id.dialog_gps_togglebutton_gpsstate_on_off);
			Button dialogButtonGpsOk = (Button) mainDialog.findViewById(R.id.dialog_gps_button_ok),
			dialogButtonGpsCancel = (Button) mainDialog.findViewById(R.id.dialog_gps_button_cancel);
			toggleButtonGpsState.setChecked(SettingsEngine.getInstance().getBoolean(SettingsEngine.GPS_STATE, false));
			dialogButtonGpsCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mainDialog.dismiss();
				}
			});
			dialogButtonGpsOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SettingsEngine.getInstance().saveBoolean(SettingsEngine.GPS_STATE,toggleButtonGpsState.isChecked());
					SettingsEngine.getInstance().saveString(SettingsEngine.LMD_GPS_STATE,java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));

					settingsListViewAdapter.notifyDataSetChanged();
					Log.d("TAG","GPS: "+ String.valueOf(toggleButtonGpsState.isChecked() ? "ON" : "OFF"));
					mainDialog.dismiss();
				}
			});
			mainDialog.setTitle("GPS STATE PICKER");
			mainDialog.show();
			break;

		// SHOW REGISTRATION FORM
		/*case 5:
			AnimatedView.IMAGE_NUMBER = 5;
			mainDialog.setContentView(R.layout.dialog_registration_form);
			mainDialog.setTitle("REGISTRATION FORM");
			final EditText editTextName = (EditText) mainDialog.findViewById(R.id.dialog_registration_edittext_name),
			editTextSername = (EditText) mainDialog.findViewById(R.id.dialog_registration_edittext_sername),
			editTextMail = (EditText) mainDialog.findViewById(R.id.dialog_registration_edittext_mail),
			editTextLogin = (EditText) mainDialog.findViewById(R.id.dialog_registration_edittext_login),
			editTextPassword = (EditText) mainDialog.findViewById(R.id.dialog_registration_edittext_password);

			editTextName.setText(SettingsEngine.getInstance().getString(SettingsEngine.REGISTRATION_NAME, ""));
			editTextSername.setText(SettingsEngine.getInstance().getString(SettingsEngine.REGISTRATION_SERNAME, ""));
			editTextMail.setText(SettingsEngine.getInstance().getString(SettingsEngine.REGISTRATION_EMAIL, ""));
			
			editTextLogin.setText(SettingsEngine.getInstance().getString(SettingsEngine.REGISTRATION_LOGIN, ""));
			editTextPassword.setText(SettingsEngine.getInstance().getString(SettingsEngine.REGISTRATION_PASSWORD, ""));

			editTextName.setSelection(editTextName.getText().length());
			editTextSername.setSelection(editTextSername.getText().length());
			editTextMail.setSelection(editTextMail.getText().length());

			Button dialogButtonRegistrationOk = (Button) mainDialog.findViewById(R.id.dialog_registration_button_ok),
			dialogButtonRegistrationCancel = (Button) mainDialog.findViewById(R.id.dialog_registration_button_cancel);

			dialogButtonRegistrationCancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mainDialog.dismiss();
						}
					});
			dialogButtonRegistrationOk
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							/*
							boolean isRegistered = SettingsEngine.getInstance().getBoolean(SettingsEngine.REGISTRATION_STATUS, false);
							
							if (isRegistered){
								Utils.showToast("You are already registered!");
								return;
							}
							
							settingsListViewAdapter.notifyDataSetChanged();
							
							SettingsEngine.getInstance().saveString(SettingsEngine.REGISTRATION_LOGIN,editTextLogin.getText().toString());
							SettingsEngine.getInstance().saveString(SettingsEngine.REGISTRATION_PASSWORD,editTextPassword.getText().toString());
							SettingsEngine.getInstance().saveString(SettingsEngine.REGISTRATION_NAME,editTextName.getText().toString());
							SettingsEngine.getInstance().saveString(SettingsEngine.REGISTRATION_SERNAME,editTextSername.getText().toString());
							SettingsEngine.getInstance().saveString(SettingsEngine.REGISTRATION_EMAIL,editTextMail.getText().toString());

							SettingsEngine.getInstance().saveString(SettingsEngine.LMD_REGISTRATION,java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));

							DeviceInfo dev = (new TelephonyManagerProcessor()).getTelephonyOverview();
							
							JSONObject jsonToServer = new JSONObject();
							try {
								jsonToServer.putOpt("login", editTextLogin.getText().toString());
								jsonToServer.putOpt("password", editTextPassword.getText().toString());
								jsonToServer.putOpt("email",editTextMail.getText().toString());
								jsonToServer.putOpt("firstName", editTextName.getText().toString());
								jsonToServer.putOpt("familyName", editTextSername.getText().toString());
								jsonToServer.putOpt("deviceId",dev.deviceId);
								jsonToServer.putOpt("deviceModel",android.os.Build.MODEL);
								
							    GetServerRSAPublicKeyTask authorization = new GetServerRSAPublicKeyTask(jsonToServer);
								authorization.execute();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						    
							
							mainDialog.dismiss();
						}
					});
			mainDialog.show();
			break;*/

		case 5:
			AnimatedView.IMAGE_NUMBER = 5;
			mainDialog.setContentView(R.layout.dialog_choose_directory);
			mainDialog.setTitle("CHOOSE STORE DIRECTORY");

			final Spinner spinner = (Spinner) mainDialog
					.findViewById(R.id.dialog_choose_directory_spinner);
			final List<String> list = new ArrayList<String>();

			if (Utils.isExternalStorageAvaliable()
					&& Utils.isExternalStorageWritable()) {
				list.add("External storage directory");
			}

			list.add("Internat storage");

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);

			Button dialogButtonDirectoryOk = (Button) mainDialog.findViewById(R.id.dialog_choose_directory_button_ok),
			dialogButtonDirectoryCancel = (Button) mainDialog.findViewById(R.id.dialog_choose_directory_button_cancel);
			final EditText editTextStoraDir = (EditText) mainDialog
					.findViewById(R.id.dialog_choose_directory_edittext_dirname);
			String tmp = "";
			try {
				tmp = Constants.STORAGE_DIR.substring(0,Constants.STORAGE_DIR.length());
				tmp = tmp.substring(tmp.lastIndexOf(File.separator)+1,tmp.length());
			} catch (Exception ex) {
				ex.printStackTrace();
				tmp = "";
			}
			if (!"".equals(tmp))
				editTextStoraDir.setText(tmp);

			dialogButtonDirectoryOk
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							String dirName = editTextStoraDir.getText().toString();
							if ("".equalsIgnoreCase(dirName)) {
								Utils.showToast("Directory name cannot be empty!");
								return;
							}
							if (list.get(0).toString().equalsIgnoreCase(spinner.getSelectedItem().toString())) {
								SettingsEngine.getInstance().saveString(SettingsEngine.STORAGE_DIRECTORY,Constants.EXTERNAL_STORAGE_DIR+ File.separator + dirName);
								Constants.STORAGE_DIR = Constants.EXTERNAL_STORAGE_DIR+ File.separator+ dirName;
								File f = new File(Constants.STORAGE_DIR);
								if (!f.exists()) {
									f.mkdirs();
								}

							} else {
								// Internal storage
							}
							settingsListViewAdapter.notifyDataSetChanged();
							mainDialog.dismiss();
						}
					});

			dialogButtonDirectoryCancel
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							mainDialog.dismiss();

						}
					});

			mainDialog.show();
			break;
		}
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
	}

}
