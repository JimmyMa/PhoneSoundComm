package com.abc.ewallet.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abc.ewallet.R;
import com.abc.ewallet.tasks.AudioRecieverTask;
import com.abc.ewallet.tasks.AudioSRManager;
import com.abc.ewallet.tasks.Callback;

public class PayActivity extends Activity {
	
	EditText inputEditText;
	Button sendButton;
	private TextView display;
	AlertDialog.Builder builder2;

	private AudioRecieverTask task;
	
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	if ( msg.what == 2 ) {
        		byte[] data1 = task.getData();
        		String data = null;
        		if ( data1 != null ) {
        			data = new String( data1 );
//        			AudioSRManager.getInstance().sendMsg( "OK" );

        			builder2.setPositiveButton( "返回", null );
        		} else {
        			data = "no data!";
        		}
	        	display.setText( data );
        	} else {
        		display.setText( "error!" );
        	}
//        	confirmButton.setText( R.string.confirm );
        }
    };
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.pay);
        
        inputEditText = (EditText) findViewById(R.id.inputEditText);
        inputEditText.setText( "1234" );
        
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {
            	sendData();
                LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
                View view=inflater.inflate(R.layout.style, null);  
                display = (TextView)view.findViewById(R.id.textView2 );
                ((TextView)view.findViewById(R.id.textView1 )).setText( "￥" + inputEditText.getText() );
                builder2=new AlertDialog.Builder(PayActivity.this);  
                builder2.setView(view);  
                builder2.setTitle("收款中...").setPositiveButton("取消", new DialogInterface.OnClickListener() {  
                      
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        // TODO Auto-generated method stub   
                        dialog.cancel();  
                        AudioSRManager.getInstance().stopListen();
                    }  
                }).create().show();  

            }  
        });  
    }
    
    private void sendData() {
    	String data = inputEditText.getText().toString();
//    	Toast.makeText(getApplicationContext(), data, 1000 );
    	AudioSRManager.getInstance().sendMsg( data, new Callback() {

			@Override
			public void run() {
				task = AudioSRManager.getInstance().startListen(mHandler);
			}
    		
    	});
    }
}
