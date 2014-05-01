package edu.glyndwr.adapters;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.glyndwr.activities.R;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;

public class SettingsAdapter extends BaseAdapter {

	private static LayoutInflater inflater = null;
	private static int ITEMS_COUNT = 6;
	private Map<String, String> data;
	public static boolean MESSAGES = false; 

	public SettingsAdapter(Activity a) {
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public SettingsAdapter(Activity a, Map<String, String> dataMap) {
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = dataMap;
	}

	public int getCount() {
		if (null != data)
			return data.size();
		return ITEMS_COUNT;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		

		
		// HELP ACTIVITY
		if (null != data) {
			View vi = convertView;
			if (convertView == null)
				vi = inflater.inflate(R.drawable.help_list_row, null);
			ImageView thumbImage = (ImageView) vi.findViewById(R.id.help_listrow_imageview_thumbnail);
			TextView textViewSettingName = (TextView) vi.findViewById(R.id.help_listrow_textview_pref_name);
			TextView textViewStatusEachRow = (TextView) vi.findViewById(R.id.help_listrow_textview_status);
			TextView textViewLastModificationDate = (TextView) vi.findViewById(R.id.help_listrow_textview_last_modification); 
			if (MESSAGES) {
				thumbImage.setImageResource(R.drawable.help_envelope); 
			} else {
				thumbImage.setImageResource(R.drawable.help_contacts);
			}
			textViewLastModificationDate.setText("opa");
			textViewSettingName.setText(data.keySet().toArray()[position].toString());
			textViewStatusEachRow.setText(data.values().toArray()[position].toString());
			return vi;
		}
		
		
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.drawable.list_row, null);
		
		ImageView thumbImage = (ImageView) vi.findViewById(R.id.settings_listrow_imageview_thumbnail);
		// ImageView thumbImageRight = (ImageView)
		// vi.findViewById(R.id.settings_imageview_rightimage);

		TextView textViewSettingName = (TextView) vi.findViewById(R.id.settings_listrow_textview_pref_name);
		TextView textViewStatusEachRow = (TextView) vi.findViewById(R.id.settings_listrow_textview_status);
		TextView textViewLastModificationDate = (TextView) vi.findViewById(R.id.settings_listrow_textview_last_modification);
		
		String rowSettingsStatus = "";

		switch (position) {
		case 0:
			thumbImage.setImageResource(R.drawable.phonee);
			textViewSettingName.setText("Unlock via call");
			textViewLastModificationDate
					.setText(SettingsEngine.getInstance().getString(
							SettingsEngine.LMD_UNLOCK_CALL_NUMBER, "UNKNOWN"));
			rowSettingsStatus = SettingsEngine.getInstance()
					.getString(SettingsEngine.UNLOCK_CALL_NUMBER, "")
					.equals("") ? "NOT SET" : SettingsEngine.getInstance()
					.getString(SettingsEngine.UNLOCK_CALL_NUMBER, "");
			textViewStatusEachRow.setText("STATUS: " + rowSettingsStatus);
			break;

		case 1:
			thumbImage.setImageResource(R.drawable.add);
			textViewSettingName.setText("Emergency number");
			textViewLastModificationDate.setText(SettingsEngine.getInstance()
					.getString(SettingsEngine.LMD_EMERGENCY_NUMBER, "UNKNOWN"));
			rowSettingsStatus = SettingsEngine.getInstance().getString(SettingsEngine.EMERGENCY_NUMBER, "").equals("") ? "NOT SET"
					: SettingsEngine.getInstance().getString(
							SettingsEngine.EMERGENCY_NUMBER, "");
			textViewStatusEachRow.setText("STATUS: " + rowSettingsStatus);
			break;

		case 2:
			thumbImage.setImageResource(R.drawable.synchronize2);
			textViewSettingName.setText("Synchronize time");
			textViewLastModificationDate.setText(SettingsEngine.getInstance().getString(SettingsEngine.LMD_SYNCHRONIZATION_PERIOD,"UNKNOWN"));
			String hour = SettingsEngine.getInstance().getString(SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR, "5");
			String minute = SettingsEngine.getInstance().getString(SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE, "0");
			if (Integer.valueOf(minute) < 10) minute = "0" + minute;
			textViewStatusEachRow.setText("SYNCHRONIZE PERIOD: " + hour + ":"+ minute);
			break;

		case 3:
			thumbImage.setImageResource(R.drawable.unlock2);
			textViewSettingName.setText("Unlock PIN");
			textViewLastModificationDate.setText(SettingsEngine.getInstance()
					.getString(SettingsEngine.LMD_UNLOCK_PIN, "UNKNOWN"));
			rowSettingsStatus = SettingsEngine.getInstance()
					.getString(SettingsEngine.UNLOCK_PIN, "").equals("") ? "NOT SET"
					: SettingsEngine.getInstance().getString(
							SettingsEngine.UNLOCK_PIN, "");
			textViewStatusEachRow.setText("STATUS: " + rowSettingsStatus);
			break;

		case 4:
			thumbImage.setImageResource(R.drawable.maps);
			textViewSettingName.setText("Send location");
			textViewLastModificationDate.setText(SettingsEngine.getInstance()
					.getString(SettingsEngine.LMD_GPS_STATE, "UNKNOWN"));
			boolean GPSstate = SettingsEngine.getInstance().getBoolean(
					SettingsEngine.GPS_STATE, false);
			String onOff = GPSstate ? "ON" : "OFF";
			textViewStatusEachRow.setText("STATUS: " + onOff);
			if (GPSstate) {
				Utils.turnGPSOff();
			} else {
				Utils.turnGPSOn();
			}

			break;
			/*
		case 5:
			
			thumbImage.setImageResource(R.drawable.internet);
			textViewSettingName.setText("Register the phone");
			textViewLastModificationDate.setText(SettingsEngine.getInstance().getString(SettingsEngine.LMD_REGISTRATION, "Unknown"));
			if (SettingsEngine.getInstance().getBoolean(SettingsEngine.REGISTRATION_STATUS, false)){
				String name = SettingsEngine.getInstance().getString(SettingsEngine.REGISTRATION_NAME, "");
				textViewStatusEachRow.setText("STATUS: " + name + " you are registered!");
			} else {
				textViewStatusEachRow.setText("STATUS: Not registered");
			}
			break;*/

		//case 6:
		case 5:
			thumbImage.setImageResource(R.drawable.folder);
			textViewSettingName.setText("Storage directory");
			
			String path = SettingsEngine.getInstance().getString(SettingsEngine.STORAGE_DIRECTORY,"");
			if (!"".equalsIgnoreCase(path)){
				Constants.STORAGE_DIR = path;
			}
			textViewStatusEachRow.setText("STORAGE DIR: " + Constants.STORAGE_DIR);
			break;

		}

		thumbImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.showToast("Left Image click : " + position);

			}
		});

		return vi;
	}
}
