package edu.glyndwr.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import edu.glyndwr.utils.Constants;
import edu.glyndwr.utils.Utils;

public class TakePictureActivity extends Activity implements
		SurfaceHolder.Callback {
	// a variable to store a reference to the Image View at the main.xml file
	private ImageView iv_image;
	// a variable to store a reference to the Surface View at the main.xml file
	private SurfaceView sv;

	// a bitmap to display the captured image
	private static Bitmap bmp;

	public static boolean HIDDEN_MODE_TAKING_PICTURE = false;
	// Camera variables
	// a surface holder
	private SurfaceHolder sHolder;
	// a variable to control the camera
	private Camera mCamera;
	// the camera parameters
	private Parameters parameters;

	private AlertDialog d = null;

	
	


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_help_take_picture);

		// get the Image View at the main.xml file
		iv_image = (ImageView) findViewById(R.id.dialog_imageview_picture_preview);

		// get the Surface View at the main.xml file
		sv = (SurfaceView) findViewById(R.id.help_surfaceview_photo_preview);

		// Get a surface
		sHolder = sv.getHolder();

		// add the callback interface methods defined below as the Surface View
		// callbacks
		sHolder.addCallback(this);

		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		/*
		d = new AlertDialog.Builder(this)
				.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK
								&& event.getAction() == KeyEvent.ACTION_UP) {
							return true;
						} else {
							if (keyCode == KeyEvent.KEYCODE_SEARCH
									&& event.getAction() == KeyEvent.ACTION_UP) {
								return true;
							}
						}
						return false;
					}
				})
				.setTitle(
						getResources().getString(
								R.string.dialog_block_user_message))
				.setMessage(
						getResources().getString(
								R.string.dialog_block_user_title)).create();
		d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		d.setCancelable(false);
		d.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		d.show();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(d.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.FILL_PARENT;
		d.getWindow().setAttributes(lp);*/
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// get camera parameters
		parameters = mCamera.getParameters();

		// set camera parameters
		mCamera.setParameters(parameters);
		mCamera.startPreview();

		// sets what code should be executed after the picture is taken
		Camera.PictureCallback mCall = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				
				try {
				// decode the data obtained by the camera into a Bitmap
				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (!HIDDEN_MODE_TAKING_PICTURE)
					// set the iv_image
					iv_image.setImageBitmap(bmp);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
				final String pictureName = File.separator
						+ sdf.format(new Date()) + ".jpg";
				File f = new File(Constants.STORAGE_DIR + pictureName);
				Utils.showToast(Constants.STORAGE_DIR + pictureName);

				try {
					f.createNewFile();
					// write the bytes in file
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					// remember close de FileOutput
					fo.close();					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				} catch (Exception ex){
					ex.printStackTrace();
				}
				Intent i = new Intent(Utils.getContext(), HelpActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			
			}
		};

		try {
		mCamera.takePicture(null, null, mCall);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (HIDDEN_MODE_TAKING_PICTURE) {
					HIDDEN_MODE_TAKING_PICTURE = false;
					Utils.setVolumeMode(false);
					TakePictureActivity.this.finish();					
				}

			}
		});

		t.start();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw the preview.
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop the preview
		mCamera.stopPreview();
		// release the camera
		mCamera.release();
		// unbind the camera from this object
		mCamera = null;
	}
}
