package edu.glyndwr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import edu.glyndwr.utils.Utils;

public class BlockUserDialogActivity extends Activity {

	private AlertDialog mainDialog = null;
	private KeyguardManager keyguardManager;
	private KeyguardManager.KeyguardLock lock;
	public static String PIN = "Device locked (10 sec), buttons are disabled", MESSAGE = Utils.getContext().getResources().getString(R.string.dialog_block_user_message);
/*
	public BlockUserDialogActivity(String pin, String message) {
		super();
		this.pin = pin;
		this.message = message;
	}
	*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_blockuser_activity);

		keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();

		mainDialog = new AlertDialog.Builder(this)
				.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							// block back button
						if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
							return true;
						} else {
							// block search button
							if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_UP) {
								return true;
							}
						}
						return false;
					}
				})
				.setTitle(MESSAGE)
				.setMessage(PIN)
				.create();
		mainDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mainDialog.setCancelable(false);
		mainDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		mainDialog.show();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(mainDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.FILL_PARENT;
		mainDialog.getWindow().setAttributes(lp);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10000);
					mainDialog.dismiss();
					BlockUserDialogActivity.this.finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// initializae variable in Utils class
		Utils.setComponentName(getComponentName());
		Utils.setApplicationContext(getApplicationContext());
	}

	@Override
	protected void onPause() {
		super.onPause();
		lock.reenableKeyguard();
	}
}