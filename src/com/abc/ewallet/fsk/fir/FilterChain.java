package com.abc.ewallet.fsk.fir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import com.abc.ewallet.utils.Constants;

public class FilterChain {
	
	FIRFilter f1;
	FIRFilter f2;
	FIRFilter f3;
    private FileOutputStream f1os1;
    private FileOutputStream f1os2;

    private boolean hasSignal;
    private float max;

	public FilterChain( FIRFilter f1, FIRFilter f2, FIRFilter f3 ) {
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		if ( Constants.debug ) {
			try {
				f1os1 = new FileOutputStream( new File( Constants.FILTER1_IN_FILE ) );
				f1os2 = new FileOutputStream( new File( Constants.FILTER2_IN_FILE ) );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public FilterChain( FIRFilter f1, FIRFilter f2 ) {
		this.f1 = f1;
		this.f2 = f2;
		if ( Constants.debug ) {
			try {
				f1os1 = new FileOutputStream( new File( Constants.FILTER1_IN_FILE ) );
				f1os2 = new FileOutputStream( new File( Constants.FILTER2_IN_FILE ) );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public float[] doFilter( byte[] data ) {
		float[] result = new float[ data.length/2 ];
		ByteBuffer bb = ByteBuffer.wrap( data );
		ShortBuffer sb = bb.asShortBuffer();
//		write( f1os, data );
		StringBuffer string = new StringBuffer();
		for ( int i = 0; i < sb.capacity(); i ++ ) {
			short s = sb.get( i );
			s =  (short)((s << 8) & 0xFF00  | ((s >> 8) & 0xFF));
			float f = f1.do_filter( s );
			string.append( f ).append( "\n" );
			result[i] = f;
		}
		write( f1os1, string.toString().getBytes() );
		
		if ( !f1.shouldNext() ) {
			hasSignal = false;
			return result;
		}
		string = new StringBuffer();
		for ( int i = 0; i < result.length; i ++ ) {
			float f = f2.do_filter( result[i] );
			string.append( f ).append( "\n" );
			result[i] = f;
			if ( f > max ) {
				max = f;
			}
//			write( f3os, result[i] );
		}
		write( f1os2, string.toString().getBytes() );
		hasSignal = true;
		return result;
	}
	
	public int[] doIntFilter( byte[] data ) {
		int[] result = new int[ data.length/2 ];
		ByteBuffer bb = ByteBuffer.wrap( data );
		ShortBuffer sb = bb.asShortBuffer();
//		write( f1os, data );
		StringBuffer string = new StringBuffer();
		for ( int i = 0; i < sb.capacity(); i ++ ) {
			short s = sb.get( i );
			s =  (short)((s << 8) & 0xFF00  | ((s >> 8) & 0xFF));
			int f = f1.do_intFilter( s );
			if ( Constants.debug ) {
				string.append( f ).append( "\n" );
			}
			result[i] = f;
		}
		write( f1os1, string.toString().getBytes() );
		
		if ( !f1.shouldNext() ) {
			hasSignal = false;
			return result;
		}
		string = new StringBuffer();
		for ( int i = 0; i < result.length; i ++ ) {
			int f = f2.do_intFilter( result[i] );
			if ( Constants.debug ) {
				string.append( f ).append( "\n" );
			}
			result[i] = f;
			if ( f > max ) {
				max = f;
			}
//			write( f3os, result[i] );
		}
		write( f1os2, string.toString().getBytes() );
		hasSignal = true;
		return result;
	}
	
	public boolean hasSignal() {
		return hasSignal;
	}
	
	public float getMax() {
		return max;
	}
	
	public void done() {
		if ( !Constants.debug ) {
			return;
		}
		try {
			f1os1.close();
			f1os2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void write( OutputStream out, byte[] data ) {
		if ( !Constants.debug ) {
			return;
		}
		try {
			out.write( data );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void write( OutputStream out, float f ) {
		if ( !Constants.debug ) {
			return;
		}
		try {
			out.write( ("" + f ).getBytes() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
