package edu.glyndwr.rest.tasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;    

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
  
import android.os.AsyncTask;
import edu.glyndwr.crypto.RSACryptoProvider;
import edu.glyndwr.rest.RestService;
import edu.glyndwr.rest.ServicesNames;
import edu.glyndwr.tools.SecurePreferences;
import edu.glyndwr.utils.Utils;

public class GetServerRSAPublicKeyTask extends AsyncTask<Void, Void, JSONObject> {

	private RestService client;
	
	
	public GetServerRSAPublicKeyTask() {
		
			client = new RestService(ServicesNames.GET_SERVER_PUBLIC_KEY);
			client.setTimeout(4000);
			client.addHeader("Accept", "application/json");
			client.addHeader("Content-Type", "application/json");
	}

	@Override  
	protected JSONObject doInBackground(Void... params) {
		JSONObject retValue = null;
		try {
			Utils.LOG(ServicesNames.GET_SERVER_PUBLIC_KEY);
			return retValue = client.execute(new JSONObject().toString());
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
			
			
		} else {
			Utils.LOG("GETSERVERRSAPUBLICKEYTASK :: onPostexecute - result == null");
		}
	}

}
