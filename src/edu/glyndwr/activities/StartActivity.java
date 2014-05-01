package edu.glyndwr.activities;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import edu.glyndwr.tools.SettingsEngine;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;

public class StartActivity extends Activity implements ISuperActivity,
		View.OnClickListener {

	private KeyguardManager keyguardManager;
	private KeyguardManager.KeyguardLock lock;
	private ImageButton imageButtonSettings, imageButtonHelp;

	/*
	 * 
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState); }
	 * 
	 * @Override protected void onRestoreInstanceState(Bundle
	 * savedInstanceState) { super.onRestoreInstanceState(savedInstanceState); }
	 * 
	 * That way the activity goes through onCreate again and afterwards calls
	 * the onRestoreInstanceState method where you can set your EditText value
	 * again.
	 * 
	 * If you want to store more complex Objects you can use
	 * 
	 * @Override public Object onRetainNonConfigurationInstance() { }
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		setContentView(R.layout.activity_start);
		loadViews();

		// Lock power button issue
		keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Utils.hideApplication(getApplicationContext());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Utils.setApplicationContext(getApplicationContext());
		Utils.setComponentName(getComponentName());
		final String PIN = SettingsEngine.getInstance().getString(
				SettingsEngine.UNLOCK_PIN, "");
		if (null != PIN && !"".equalsIgnoreCase(PIN)) {
			final Dialog lockDialog = new Dialog(this);
			lockDialog.setContentView(R.layout.dialog_unlock_pin);
			lockDialog.setTitle(R.string.hint_unlock_pin);
			lockDialog.setCancelable(false);
			final EditText ed = (EditText) lockDialog
					.findViewById(R.id.dialog_unlockpin_edittext_pin);
			ed.setText(PIN);
			Button buttonOk = (Button) lockDialog
					.findViewById(R.id.dialog_unlockpin_button_ok);
			Button buttonCancel = (Button) lockDialog
					.findViewById(R.id.dialog_unlockpin_button_cancel);
			buttonCancel.setText("Clear");
			buttonCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ed.setText("");
				}
			});

			buttonOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (PIN.equals(ed.getText().toString()))
						lockDialog.dismiss();
				}  
			});
			lockDialog.show();
		}

		String path = SettingsEngine.getInstance().getString(
				SettingsEngine.STORAGE_DIRECTORY, "");
		if (!"".equalsIgnoreCase(path)) {
			Constants.STORAGE_DIR = path;
		}
		
		//this.registerReceiver(new LaunchAppViaDialReceiver(), )
		
		//sendfile();  
	//uploadFile(new File("/sdcard/mp3/q.mp3"));
		
		//Intent i = new Intent (getApplicationContext(),RecordVideoActivity.class);
		//startActivity(i);
	
	}

	
	
	
	
	
	
	
	
	/*********  work only for Dedicated IP ***********/
    static final String FTP_HOST= "192.168.0.4";
     
    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "ben";
     
    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS  ="260690ben";
	
	
	
	
 public void uploadFile(File file){
         
         
         FTPClient client = new FTPClient();
          
        try {
             
            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            //client.changeDirectory("/upload/");
             
            client.upload(file, new MyTransferListener());
             
        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);    
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
         
    }
     
    /*******  Used to file upload and show progress  **********/
     
    public class MyTransferListener implements FTPDataTransferListener {
 
        public void started() {}
        public void transferred(int length) {}
        public void completed() {}
        public void aborted() {}
        public void failed() {Utils.LOG("failed");}
 
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public void sendfile (){
		try {
		    SimpleFTP ftp = new SimpleFTP();

		    // Connect to an FTP server on port 21.
		    Utils.LOG("start frp method");
		    ftp.connect("192.168.0.4", 21, "ben", "");

		    // Set binary mode.  
		    ftp.bin();

		    // Change to a new working directory on the FTP server.
		    //ftp.cwd("web");

		    Utils.LOG(ftp.pwd());
		  
		    
		    // Upload some files.
		    Utils.LOG("sending file path:"+Constants.STORAGE_DIR + "gg.pdf");
		    File f = new File("/sdcard/mp3/q.mp3");
		    if (!f.isFile()){
		    	Utils.LOG("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! File does not exist");
		    	return;
		    }
		    ftp.stor(f);
		    
		    //ftp.stor(new File("comicbot-latest.png"));

		    // You can also upload from an InputStream, e.g.
		    // ftp.stor(new FileInputStream(new File("test.png")), "test.png");
		    // ftp.stor(someSocket.getInputStream(), "blah.dat");
  
		    // Quit from the FTP server.
		    ftp.disconnect();
		} catch (IOException e) {
		    // Jibble.
		}
	}
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void loadViews() {
		imageButtonSettings = (ImageButton) findViewById(R.id.start_imagebutton_settings);
		imageButtonHelp = (ImageButton) findViewById(R.id.start_imagebutton_help);
		imageButtonHelp.setOnClickListener(this);
		imageButtonSettings.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		int ID = arg0.getId();
		Intent activity = null;
		switch (ID) {
		case R.id.start_imagebutton_help:
			//new TextPlayer("привет").start();
			activity = new Intent(getApplicationContext(), HelpActivity.class);
			startActivity(activity);
			break;

		case R.id.start_imagebutton_settings:
			activity = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(activity);
			break;
		}

	}
}