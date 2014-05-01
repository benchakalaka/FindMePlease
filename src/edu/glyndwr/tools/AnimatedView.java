package edu.glyndwr.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;
import edu.glyndwr.activities.R;
import edu.glyndwr.utils.Utils;

public class AnimatedView extends View {
	private Movie mMovie;
	private long mMovieStart;
	private static final boolean DECODE_STREAM = true;
	public static int IMAGE_NUMBER = 0; 
	
	

	public AnimatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		java.io.InputStream is = null;
		try {
		switch (IMAGE_NUMBER){
		case 0:
			
			break;
		case 1:
			is = context.getResources().openRawResource(R.drawable.ringing_phone_gif);
			break;
		case 2:
			is = context.getResources().openRawResource(R.drawable.synchronization_earth_gif);
			break;		
		case 4:
			is = context.getResources().openRawResource(R.drawable.spy_mode_sattelite_gif);
			break;
		case 5:
			is = context.getResources().openRawResource(R.drawable.synchronization_earth_gif);
			break;
		case 6:
			is = context.getResources().openRawResource(R.drawable.settings_processing_gif);
			break;
			
		}
		} catch (Exception ex){
			ex.printStackTrace();
			is = context.getResources().openRawResource(R.drawable.synchronization_earth_gif);
		}
		
		if (DECODE_STREAM) {
			mMovie = Movie.decodeStream(is);
		} else {
			byte[] array = Utils.streamToBytes(is);
			mMovie = Movie.decodeByteArray(array, 0, array.length);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		long now = android.os.SystemClock.uptimeMillis();
		if (mMovieStart == 0) { 
			mMovieStart = now;
		}
		if (mMovie != null) {
			int dur = mMovie.duration();
			if (dur == 0) {
				dur = 3000;
			}
			int relTime = (int) ((now - mMovieStart) % dur);
			mMovie.setTime(relTime);
			mMovie.draw(canvas,0, 0);
			invalidate();
		}
	}
}