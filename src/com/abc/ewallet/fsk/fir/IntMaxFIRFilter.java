package com.abc.ewallet.fsk.fir;

public class IntMaxFIRFilter implements FIRFilter {
	private int preValueIndex;
	private int len;
	private int[] values;

	public IntMaxFIRFilter(int len) {
		this.len = len;
		values = new int[len];
	}
	
	public boolean shouldNext() {
		return true;
	}
	
	public int do_intFilter(int x) {
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
		
		int max1 = (int)Math.pow( max , 2 ) ;
		return max1;	
	}

	// do_filter: take one sample and compute one filterd output
	public float do_filter(float x) {
		return 0;	
	}
}
