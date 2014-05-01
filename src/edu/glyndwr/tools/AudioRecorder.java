package edu.glyndwr.tools;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import edu.glyndwr.utils.Constants;

public class AudioRecorder {

	  final MediaRecorder recorder = new MediaRecorder();
	  final String path;

	  public AudioRecorder(String path) {
	    this.path = sanitizePath(path);
	  }
	  
	  /**
	   * Convert path to appropriate format
	   * @param path path to convert
	   * @return
	   */
	  private String sanitizePath(String path) {
	    if (!path.startsWith("/")) {
	      path = "/" + path;
	    }
	    if (!path.contains(".")) {
	      path += ".3gp";
	    }
	    return Constants.STORAGE_DIR + path;
	  }
	  /**
	   * Start recording
	   * @throws IOException
	   */
	  public void start() throws IOException {
	    String state = android.os.Environment.getExternalStorageState();
	    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
	        throw new IOException("SD Card is not mounted.  It is " + state + ".");
	    }

	    // make sure the directory we plan to store the recording in exists
	    File directory = new File(path).getParentFile();
	    if (!directory.exists() && !directory.mkdirs()) {
	      throw new IOException("Path to file could not be created.");
	    }

	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    recorder.setOutputFile(path);
	    recorder.prepare();
	    recorder.start();
	  }

	  /**
	   * Stop recording
	   * @throws IOException
	   */
	  public void stop() throws IOException {
	    recorder.stop();
	    recorder.release();
	  }
	}
