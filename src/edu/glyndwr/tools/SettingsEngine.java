package edu.glyndwr.tools;

import edu.glyndwr.utils.Utils;
import android.content.SharedPreferences;

/**
 * Provides all work with settings in application, wrapper for class
 * SharedPreferences
 * 
 * @author ben
 * 
 */
public class SettingsEngine {

	private static SettingsEngine SETTINGS = null;

	
	public static final String LAST_SYCHRONIZATION_DATE = "LSDATE";
	
	// Constants for application stored fields
	private static final String PREFERENCE_NAME = "FindMeSettings";

	
	
	// STRING CONSTANTS FOR RESTORING
	
	public static final String SERVER_RSA_PUBLIC_KEY = "server_rsa_public_key";

	/**
	 * UnlockPhoneNumber - for show application to dial UNP
	 */
	public static final String UNLOCK_CALL_NUMBER = "UPN";
	/**
	 * LMD - last modification date and time of unlock phone number
	 */
	public static final String LMD_UNLOCK_CALL_NUMBER = "LMDUPN";

	/**
	 * EMERGENCY_NUMBER - provide number of last resort
	 */
	public static final String EMERGENCY_NUMBER = "EN";
	/**
	 * LMD - last modification date and time of emergency number
	 */
	public static final String LMD_EMERGENCY_NUMBER = "LMDEN";

	/**
	 * SYNCHRONIZATION_PERIOD_HOUR - accurate hour of synchronization
	 */
	public static final String SYNCHRONIZATION_PERIOD_HOUR = "SPH";

	/**
	 * SYNCHRONIZATION_PERIOD_MINUTE - accurate minute of synchronization
	 */
	public static final String SYNCHRONIZATION_PERIOD_MINUTE = "SPM";

	/**
	 * LMD - last modification date and time of synchronization period
	 */
	public static final String LMD_SYNCHRONIZATION_PERIOD = "LMDSP";

	/**
	 * UnlockPhoneNumber - unlock pin for user
	 */
	public static final String UNLOCK_PIN = "UP";
	/**
	 * LMD - last modification date and time of unlock pin
	 */
	public static final String LMD_UNLOCK_PIN = "LMDUP";

	/**
	 * GPS_STATE - current gps state
	 */
	public static final String GPS_STATE = "GPS";
	/**
	 * LMD - last modification date and time gps
	 */
	public static final String LMD_GPS_STATE = "LMDGPS";

	/**
	 * REGISTRATION_NAME - current gps state
	 */
	public static final String REGISTRATION_NAME = "ENAME";
	/**
	 * REGISTRATION_SERNAME - current gps state
	 */
	public static final String REGISTRATION_SERNAME = "RSERNAME";
	/**
	 * REGISTRATION_EMAIL - current gps state
	 */
	public static final String REGISTRATION_EMAIL = "RMAIL";
	/**
	 * LMD - last modification date and time of registration data
	 */
	public static final String LMD_REGISTRATION = "LMDR";

	/**
	 * Main server address
	 */
	public static final String REST_SERVER_URL = "http://SERVER.com";

	/**
	 * REGISTRATION_LOGIN - user registration login
	 */
	public static final String REGISTRATION_LOGIN = "RLOGIN";

	
	/**
	 * REGISTRATION_PASSWORD - user registration login
	 */
	public static final String REGISTRATION_PASSWORD = "RPASSWORD";
	
	/**
	 * Storage directory
	 */
	public static String STORAGE_DIRECTORY = ""; 
		
	/**
	 * Flag which indicates that this user has already registered
	 */
	public static String REGISTERED = "FINISH_REGISTRATION"; 

	private static SharedPreferences prefferences = Utils.getContext().getSharedPreferences(PREFERENCE_NAME, 0);
	private SharedPreferences.Editor editor = prefferences.edit();

	/**
	 * Realize singleton pattern
	 * 
	 * @return instance of SettingsEngine class
	 */
	public static SettingsEngine getInstance() {
		if (null == SETTINGS) {
			SETTINGS = new SettingsEngine();
		}
		return SETTINGS;
	}

	/**
	 * Hide constructor from out for singleton sceleton
	 */
	private SettingsEngine() {
	}

	/**
	 * Return boolean from shared preferences class
	 * 
	 * @param key
	 *            for saving
	 * @param defValue
	 *            string representation of default value
	 * @return boolean saved value for string key
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return prefferences.getBoolean(key, defValue);
	}

	/**
	 * Return string from shared preferences class
	 * 
	 * @param key
	 *            for saving
	 * @param defValue
	 *            string representation of default value
	 * @return string saved value for string key
	 */
	public String getString(String key, String defValue) {
		if (null == defValue)
			defValue = "";
		return prefferences.getString(key, defValue);
	}

	/**
	 * Remove settings with such key
	 * 
	 * @param key
	 *            to remove
	 */
	public void remove(String key) {
		if (null == key || "".equals(key))
			return;
		editor.remove(key);
		editor.commit();
	}

	/**
	 * Save boolean parameter to shared preferences class
	 * 
	 * @param key
	 *            string for saving
	 * @param value
	 *            boolean value for saving
	 */
	public void saveBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Save string parameter to shared preferences class
	 * 
	 * @param key
	 * @param value
	 */
	public void saveString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}
}
