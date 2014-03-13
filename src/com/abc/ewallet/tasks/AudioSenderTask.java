package com.abc.ewallet.tasks;

import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

import com.abc.ewallet.fsk.Modulater;

public class AudioSenderTask  extends AsyncTask<Void, Void, Void> {
	private AudioTrack track;
	private String message;
	private Modulater modulater;
	private int minSize;
	private Callback callback;
	
	public AudioSenderTask( Callback callback  ) {
		modulater = new Modulater();
		this.callback = callback;
	}
	
	public void sentMsg(String message) {
		this.message = message;
	}
	
	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			byte[] buffer = modulater.modulate( message );
			
			minSize = AudioTrack.getMinBufferSize(Modulater.SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			track = new AudioTrack(AudioManager.STREAM_MUSIC, Modulater.SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minSize,
					AudioTrack.MODE_STREAM);
			track.setStereoVolume(100f, 100f);
			track.play();
			
			int i = 0;
			for ( i = 0; i < buffer.length / minSize; i ++ ) {
				track.write( buffer, i * minSize, minSize );
			}
			track.write( buffer, i * minSize, buffer.length - i * minSize );
			
			track.stop();
			track.release();
			callback.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void stop() {
		track.stop();
		track.release();
	}
}
