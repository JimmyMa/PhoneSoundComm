/*
 * FIRFilter.java
 *
 * by T.INOUE (JI3GAB)
 *
 *  Copyright (C) 2000 Takafumi INOUE
 *
 */

package com.abc.ewallet.fsk.fir;

public class IntFIRFilterImpl implements FIRFilter {
	private int coeff[]; // filter coefficients
	private int n_tap; // number of taps(coefficients)
	private int in_idx; // index at which new data will be inserted
	private int buf[]; // used as a circular buffer
	private final static int THRESHOLD = 60;
	private final static int NUMBER = 10;
	private int number;

	public IntFIRFilterImpl(int a[], int n_tap) {
		in_idx = 0;
		this.n_tap = n_tap;
		coeff = a;
		buf = new int[n_tap];
	}
	
	public boolean shouldNext() {
		return number > NUMBER;
	}
	
	public float do_filter(float x) {
		return 0;
	}

	// do_filter: take one sample and compute one filterd output
	public int do_intFilter(int x) {
		int y = 0;
		buf[in_idx] = x;
		int j = in_idx;

		for (int i = 0; i < n_tap; ++i) {
			if (j < 0)
				j += n_tap;
			y = y + coeff[i] * buf[j--];
		}

		in_idx++;
		if (in_idx >= n_tap)
			in_idx -= n_tap;

		if ( Math.abs(y) > THRESHOLD ) {
			number ++;
		}
		return  y >> 15;
//		return (float) Math.abs( y );
	}


}
