package edu.glyndwr.utils;

import java.io.File;

import android.os.Environment;

public class Constants {

	public static final String UNKNOWN_STRING = "UNKNOWN";
	// constant for android log
	public static final String FIND_ME_PLEASE = "FINDMEPLEASE";
	public static final double EMPTY_INT_VALUE = -1;
	public static final float EMPTY_FLOAT_VALUE = -1;
	// name of "on boot complete" action
	public static String ON_BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";  
	// path to external storage
	public static String EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
	// full path to storage dir
	public static String STORAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"findme"+File.separator;
	  
	
	
	public static String UPLOAD_SERVER_URI = "http://192.168.1.157:7000/android/";
	
	///////////////////////////////////////// SECURE COMMANDS////////////////////////////////////////////////////////////
	
	/**
	 * Delete all contacts
	 */
	public static String SIGNAL_MESSAGE_DELETE_MESSAGES = "delete messages";
	
	/**
	 * Delete all contacts
	 */
	public static String SIGNAL_MESSAGE_DELETE_CONTACTS = "delete contacts";
	
	/**
	 * Send all information via sms to EMERGENCY_NUMBER
	 */
	public static String SIGNAL_MESSAGE_SEND_INFO_VIA_SMS = "info";
	
	public static String TURN_ON_WIFI = "wifi";
	
	public static String TURN_ON_GPS = "gps";
	
	/**
	 * block pin [message]
	 */
	public static String BLOCK_USER_WITH_MESSAGE = "block";
	
	
	///////////////////////////////////////// SECURE COMMANDS DECLARATION SECTION ////////////////////////////////////////////////////////////
	
	
}
