package com.abc.ewallet.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.abc.ewallet.R;
import com.abc.ewallet.fsk.Demodulater;
import com.abc.ewallet.tasks.AudioRecieverTask;
import com.abc.ewallet.tasks.AudioSRManager;
import com.abc.ewallet.tasks.AudioSenderTask;
import com.abc.ewallet.tasks.Callback;
import com.abc.ewallet.utils.Constants;

public class ConfirmActivity extends Activity {
	
	private Button confirmButton;
	private Button demodulateButton;
	private TextView display;
	private boolean running = false;
	
	private AudioRecieverTask task;
	AudioManager audioManager;
	private int dataNumber = 1;
	
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	if ( msg.what == 2 ) {
        		byte[] data1 = task.getData();
        		String data = null;
        		if ( data1 != null ) {
        			data = new String( data1 );
        		} else {
        			data = "no data!";
        		}
	        	display.setText( "￥" + data );
        	} else {
        		display.setText( "error!" );
        	}
//        	confirmButton.setText( R.string.confirm );
        }
    };
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm);
        
        display = (TextView) findViewById(R.id.DataLabel);
        
        confirmButton = (Button) findViewById(R.id.confirmBtn);
        confirmButton.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	AudioSRManager.getInstance().sendMsg( "OK", new Callback() {

        			@Override
        			public void run() {
//        				task = AudioSRManager.getInstance().startListen(mHandler);
        			}
            		
            	} );
            }  
        });  
        
        demodulateButton = (Button) findViewById(R.id.demodulateBtn);
        demodulateButton.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {
            	demodulate();
            }  
        });  
        
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }
    
	public void demodulate() {
//		File inFile = new File(Constants.FILTER1_IN_FILE);
//		try {
//			FileReader fr = new FileReader(inFile);
//			java.io.BufferedReader br = new BufferedReader(fr);
//
//			int frameSize = 24000;
//			String line = null;
//			int[] data = new int[frameSize];
//			int i = 0;
//			Demodulater dd = new Demodulater();
//			dd.init(frameSize);
//			while ((line = br.readLine()) != null) {
//				data[i++] = Integer.parseInt(line);
//				// System.out.println( data[i - 1] );
//				if (i == frameSize) {
//					if ( !dd.demodulateFMData(data) ) {
//						break;
//					}
//					i = 0;
//				}
//			}
//			br.close();
//    		byte[] data1 = dd.getData();
//    		String result = null;
//    		if ( data1 != null ) {
//    			result = new String( data1 );
//    		} else {
//    			result = "no data!";
//    		}
//        	display.setText( result );
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
    
    public void startListen() {
    	running = true;
//    	confirmButton.setText( "终     止" );
    	display.setText( "" );
//    	task = AudioSRManager.getInstance().getAudioRecieverTask();
//    	AudioSRManager.getInstance().receiveMsg(mHandler);
    }
    
    public void stopListen() {
    	running = false;
//    	confirmButton.setText( R.string.confirm );
    	task.stop();
    }

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		task = AudioSRManager.getInstance().startListen(mHandler);
//		startListen();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		AudioSRManager.getInstance().stopListen();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		task = AudioSRManager.getInstance().startListen(mHandler);
		startListen();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AudioSRManager.getInstance().stopListen();
	}
}
