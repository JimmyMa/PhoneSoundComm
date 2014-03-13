package com.abc.ewallet.fsk.fir;

public class MaxFIRFilter implements FIRFilter {
	private int preValueIndex;
	private int len;
	private float[] values;

	public MaxFIRFilter(int len) {
		this.len = len;
		values = new float[len];
	}
	
	public boolean shouldNext() {
		return true;
	}
	
	public int do_intFilter(int x) {
		return 0;
	}

	// do_filter: take one sample and compute one filterd output
	public float do_filter(float x) {
//		return x;
		values[preValueIndex] = Math.abs( x );
		preValueIndex ++;
		if ( preValueIndex >= len ) {
			preValueIndex = 0;
		}
		float max = 0;
		for ( int i = 0; i < values.length; i ++ ) {
			if ( values[i] > max ) {
				max = values[i];
			}
		}
//		return max;
		
		float max1 = (float)Math.pow( max , 2 ) ;
		return max1;	}
}
