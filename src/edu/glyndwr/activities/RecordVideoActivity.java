package edu.glyndwr.activities;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.VideoView;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;

public class RecordVideoActivity extends Activity implements SurfaceHolder.Callback {

	
	
	private VideoView mVideoView = null;
	private MediaRecorder mVideoRecorder = null;
	private Camera mCamera;
	private String uniqueOutFile;
	
	private AlertDialog mainDialog = null;
	private KeyguardManager keyguardManager;
	private KeyguardManager.KeyguardLock lock;
	private Boolean mUseFrontFacingCamera = false;
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
		setContentView(R.layout.dialog_hidden_video_activity);
		mVideoView = (VideoView) findViewById(R.id.hiddenVideoView);
		
		if (mVideoView == null){
			Utils.LOG("NULLLLLLLLLLLLLL");
		} 
		
		if (null == mVideoView.getHolder()){
			Utils.LOG("holder ==== NULL");
		}
		
		if (mUseFrontFacingCamera) {
			// If caller wants to use front facing camera, then make sure the
			// device has one...
			// Hard coded to only open front facing camera on Xoom (model MZ604)
			// For more universal solution try:
			// http://stackoverflow.com/questions/2779002/how-to-open-front-camera-on-android-platform
			String deviceModel = android.os.Build.MODEL;
			if (deviceModel.contains("MZ604")) {
				mUseFrontFacingCamera = true;
			} else {
				// Utils.showToast("The App isn't designed to use this Android's front facing camera.\n "
				// +"The device model is : " + deviceModel);
				mUseFrontFacingCamera = false;
			}
		}

		final SurfaceHolder holder = mVideoView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		
/*
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
*/
		
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
		//lock.reenableKeyguard();
	}
	
	
	
	
	
	
	
	
	
	
	public void stopRecording() throws Exception {
		// mRecording = false;
		if (mVideoRecorder != null) {
			mVideoRecorder.stop();
			mVideoRecorder.release();
			mVideoRecorder = null;
		}
		if (mCamera != null) {
			
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}
	
	
	
	
	
	
	/**
	 * Uses the surface defined in video_recorder.xml Tested using 2.2 (HTC
	 * Desire/Hero phone) -> Use all defaults works, records back facing camera
	 * with AMR_NB audio 3.0 (Motorola Xoom tablet) -> Use all defaults doesn't
	 * work, works with these specs, might work with others
	 * 
	 * @param holder
	 *            The surfaceholder from the videoview of the layout
	 * @throws Exception
	 */
	public void beginRecording(SurfaceHolder holder) throws Exception {
		
		if (mVideoRecorder != null) {
			mVideoRecorder.stop();
			mVideoRecorder.release();
			mVideoRecorder = null;
		}
		if (mCamera != null) {
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		uniqueOutFile = Constants.STORAGE_DIR
				+ String.valueOf(File.separator + System.currentTimeMillis())
				+ ".3gp";
		File outFile = new File(uniqueOutFile);
		if (outFile.exists()) {
			outFile.delete();
		}

		try {
			
		        mCamera = Camera.open();
		      
		    
				

			// Camera setup is based on the API Camera Preview demo
			mCamera.setPreviewDisplay(holder);
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(640, 480);
			mCamera.setParameters(parameters);
			mCamera.stopPreview();
			mCamera.startPreview();
			mCamera.unlock();

			mVideoRecorder = new MediaRecorder();
			mVideoRecorder.setCamera(mCamera);

			// Media recorder setup is based on Listing 9-6, Hashimi et all 2010
			// values based on best practices and good quality,
			// tested via upload to YouTube and played in QuickTime on Mac Snow
			// Leopard
			mVideoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mVideoRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// THREE_GPP
			// is big-endian, storing and transferring the most significant
			// bytes first. MPEG_4 as another option
			mVideoRecorder.setVideoSize(640, 480);// YouTube recommended size:
													// 320x240,
			// OpenGazer eye tracker: 640x480
			// YouTube HD: 1280x720
			mVideoRecorder.setVideoFrameRate(20); // might be auto-determined
													// due to lighting
			mVideoRecorder.setVideoEncodingBitRate(3000000);// 3 megapixel, or
															// the max of
			// the camera
			mVideoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// MPEG_4_SP
			// Simple Profile is
			// for low bit
			// rate and low
			// resolution
			// H264 is MPEG-4 Part 10
			// is commonly referred to
			// as H.264 or AVC
			int sdk = android.os.Build.VERSION.SDK_INT;
			// Gingerbread and up can have wide band ie 16,000 hz recordings
			// (Okay quality for human voice)
			if (sdk >= 10) {
				mVideoRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
				mVideoRecorder.setAudioSamplingRate(16000);
			} else {
				// Other devices only have narrow band, ie 8,000 hz
				// (Same quality as a phone call, not really good quality for
				// any purpose.
				// For human voice 8,000 hz means /f/ and /th/ are
				// indistinguishable)
				mVideoRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			}
			mVideoRecorder.setMaxDuration(30000); // limit to 60 seconds
			mVideoRecorder.setPreviewDisplay(holder.getSurface());
			mVideoRecorder.setOutputFile(uniqueOutFile);
			mVideoRecorder.prepare();
			mVideoRecorder.start();
			// mRecording = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		
		try {
			beginRecording(arg0);
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(10000);

						
						try {
							stopRecording();
							Uri uri = Uri.parse(uniqueOutFile);
							Intent it = new Intent(Intent.ACTION_VIEW, uri);
							it.setDataAndType(uri, "video/3gpp");
							startActivity(it);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						

						
						mainDialog.dismiss();
						RecordVideoActivity.this.finish();
					} catch (InterruptedException e) {
						e.printStackTrace();  
					}
				}
			});
			t.start();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}