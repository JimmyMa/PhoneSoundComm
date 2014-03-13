package com.abc.ewallet.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

import android.os.AsyncTask;
import android.os.Handler;

import com.abc.ewallet.fsk.IntDemodulater;
import com.abc.ewallet.fsk.fir.FIRFactory;
import com.abc.ewallet.fsk.fir.FilterChain;
import com.abc.ewallet.io.audio.AudioReader;
import com.abc.ewallet.utils.Constants;

public class AudioRecieverTask extends AsyncTask<Void, Void, Void> {

	private AudioReader audioReader;
    private FileOutputStream outStream;
    private boolean running;
//    private Demodulater demodulater;
    private IntDemodulater demodulater;
    private FilterChain filterChain;
    protected LinkedList<byte[]> m_in_q;
    protected Handler callbackHandler;
    protected byte[] data;
    private int status = 0; // 0: running, 1: suspend, 2: stopped
    
    private int startPoint = 0;
	
	public AudioRecieverTask() {
        audioReader = new AudioReader();
        audioReader.startReader(48000, 48000, new AudioReader.Listener() {
            @Override
            public final void onReadComplete(byte[] buffer) {
                receiveAudio(buffer);
            }
            @Override
            public void onReadError(int error) {
                handleError(error);
            }
        });
        m_in_q = new LinkedList<byte[]>();
//        demodulater = new Demodulater();
        demodulater = new IntDemodulater();
        demodulater.init( audioReader.getBufferSize() / 2 );
        
        filterChain = new FilterChain( FIRFactory.createIntBPF_180_190_05_32(), FIRFactory.createIntMaxFilter( 3 ) );
//        filterChain = new FilterChain( FIRFactory.createBPF_180_190_05_32(), FIRFactory.createMaxFilter( 3 ) );
        
		try {
			if ( Constants.debug ) {
		        File saveFile=new File( Constants.PCM_IN_FILE );
				outStream = new FileOutputStream(saveFile);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		running = true;
	}
	
	public void setHandler( Handler callbackHandler ) {
        this.callbackHandler = callbackHandler;
	}
	
	public void stop() {
		audioReader.stopReader();
		status = 2;
		running = false;
	}
	
	public void suspend() {
		status = 1;
	}
	
	public void resume() {
		status = 0;
	}
	
	
	
	public byte[] getData() {
		return data;
	}
	
	@Override
	protected Void doInBackground(Void... params) {


		while( running ) {// || m_in_q.size() > 0 ) {
			try {
				byte[] receivedBuffer = null;
				synchronized (m_in_q) {
					if ( m_in_q.size() > 0 ) {
						receivedBuffer = m_in_q.remove( 0 );
					}
				}
				if ( status == 1 ) {
					Thread.sleep(500);
					continue;
				}
				if ( receivedBuffer != null && startPoint > 1 ) {
					if ( Constants.debug ) {
						outStream.write( receivedBuffer );
//						outStream.write( ("" + m_in_q.size() + ":" + receivedBuffer.length + "\n" ).getBytes() );
					}
//					float[] result = filterChain.doFilter(receivedBuffer);
					int[] result = filterChain.doIntFilter(receivedBuffer);
					if ( filterChain.hasSignal() && !demodulater.demodulateFMData( result ) ) {
//					if ( !demodulater.demodulateFMData( result ) ) {
						stop();
						data = demodulater.getData();
						callbackHandler.sendEmptyMessage( 2 );
						break;
//						synchronized (m_in_q) {
//							m_in_q.clear();
//						}
//
//						demodulater = new Demodulater();
//				        demodulater.init( audioReader.getBufferSize() / 2 );
//						filterChain = new FilterChain( FIRFactory.createBPF_180_190_05_16(), FIRFactory.createMaxFilter( 3 ) );
					}
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
      
		try {
//			data = demodulater.getData();
//			if ( demodulater.getStatus() == 1 ) {
//				callbackHandler.sendEmptyMessage( 2 );
//			} else {
//				callbackHandler.sendEmptyMessage( 1 );
//			}
			if ( Constants.debug ) {
				outStream.close();
			}
			stop();
	        filterChain.done();
//	        demodulater.done();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
    private final void receiveAudio(byte[] buffer) {
        // Lock to protect updates to these local variables.  See run().
        synchronized (m_in_q) {
			try {
				startPoint ++;
				byte[] receivedBuffer = buffer.clone();
				m_in_q.add( receivedBuffer );
				m_in_q.notify();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }
    
    private void handleError(int error) {
        synchronized (this) {
//            readError = error;
        }
    }

}
