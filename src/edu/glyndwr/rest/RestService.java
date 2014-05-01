package edu.glyndwr.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import edu.glyndwr.utils.Utils;
/**
 * 
 * @author Karpachev Ihor
 * 
 */
public class RestService {

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	/**
	 * Address from file ServicesNames.java
	 */
	private String serviceAddress;

	private String url;

	/**
	 * Timeout connection
	 */
	private int SO_TIMEOUT = -1;

	private String message;

	private String response;

	public RestService(String urlOfService) {
		serviceAddress = urlOfService;
		this.url = ServicesNames.SERVER_ADRESS + serviceAddress;
		Utils.LOG("service addr: " + url);
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void setTimeout(int sO_TIMEOUT) {
		SO_TIMEOUT = sO_TIMEOUT;
	}

	public int getTimeout() {
		return SO_TIMEOUT;
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public JSONObject execute() throws SocketTimeoutException,
			ConnectTimeoutException, UnsupportedEncodingException,
			NoHttpResponseException {
		Utils.LOG(this.url);

		try {
			HttpPost request = new HttpPost(url);
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			return executeRequest(request, url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public JSONObject execute(String json) throws SocketTimeoutException,
			ConnectTimeoutException, UnsupportedEncodingException,
			NoHttpResponseException {
		HttpPost request = null;
		try {
			Utils.LOG(this.url);
			request = new HttpPost(url);
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			request.setEntity(new StringEntity(json));
			return executeRequest(request, url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	private JSONObject executeRequest(HttpUriRequest request, String url)
			throws SocketTimeoutException, ConnectTimeoutException,
			NoHttpResponseException {

		HttpClient client = prepareHttpClient();
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(request);
			message = httpResponse.getStatusLine().getReasonPhrase();
			HttpEntity entity = httpResponse.getEntity();

			Utils.LOG("Request status: " + message);

			if (entity != null && "ok".equalsIgnoreCase(message)) {
				return new JSONObject(EntityUtils.toString(entity));
			}
		} catch (ConnectTimeoutException ee) {
			client.getConnectionManager().shutdown();
			throw new ConnectTimeoutException();
		} catch (NoHttpResponseException nhre) {
			client.getConnectionManager().shutdown();
			throw new NoHttpResponseException(nhre.getMessage());
		} catch (SocketTimeoutException ee) {
			client.getConnectionManager().shutdown();
			throw new SocketTimeoutException();
		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getErrorMessage() {
		return message;
	}

	public String getResponse() {
		return response;
	}

	private HttpClient prepareHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();

		if (getTimeout() != -1) {
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, getTimeout());
		}
		return new DefaultHttpClient(httpParameters);

	}
}
