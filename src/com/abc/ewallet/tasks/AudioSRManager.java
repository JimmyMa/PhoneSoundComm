package com.abc.ewallet.tasks;

import android.os.AsyncTask;
import android.os.Handler;

public class AudioSRManager {
	private static AudioSRManager instance;
	private AudioRecieverTask audioRecieverTask;
	private AudioSenderTask audioSenderTask;
	
	private AudioSRManager() {
		
	}
	
	public static AudioSRManager getInstance() {
		if ( instance == null ) {
			instance = new AudioSRManager();
//			instance.audioRecieverTask = new AudioRecieverTask();
//			instance.audioRecieverTask.execute();
		}
		return instance;
	}
	
	public AudioRecieverTask getAudioRecieverTask() {
		return audioRecieverTask;
	}
	
	public void sendMsg( String msg, Callback callback ) {
//		audioRecieverTask.suspend();
		if ( audioSenderTask != null && audioSenderTask.getStatus() == AsyncTask.Status.RUNNING  ) {
			audioSenderTask.stop();
			audioSenderTask.cancel(true);
//			return;
		}
		audioSenderTask = new AudioSenderTask(callback);
		audioSenderTask.sentMsg( msg );
		audioSenderTask.execute();

	}
	
	public void receiveMsg(Handler callbackHandler) {
		audioRecieverTask.setHandler( callbackHandler );
		audioRecieverTask.resume();
	}
	
	public void stopListen() {
		if ( audioRecieverTask != null && audioRecieverTask.getStatus() == AsyncTask.Status.RUNNING  ) {
			audioRecieverTask.stop();
		}
	}
	
	public AudioRecieverTask startListen(Handler callbackHandler) {
		audioRecieverTask = new AudioRecieverTask();
		audioRecieverTask.setHandler(callbackHandler);
		audioRecieverTask.execute();
		return audioRecieverTask;
	}
}
