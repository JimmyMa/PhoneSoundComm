/*
 * FIRFilter.java
 *
 * by T.INOUE (JI3GAB)
 *
 *  Copyright (C) 2000 Takafumi INOUE
 *
 */

package com.abc.ewallet.fsk.fir;

public class FIRFilterImpl implements FIRFilter {
	private double coeff[]; // filter coefficients
	private int n_tap; // number of taps(coefficients)
	private int in_idx; // index at which new data will be inserted
	private float buf[]; // used as a circular buffer
	private final static int THRESHOLD = 60;
	private final static int NUMBER = 10;
	private int number;

	public FIRFilterImpl(double a[], int n_tap) {
		in_idx = 0;
		this.n_tap = n_tap;
		coeff = a;
		buf = new float[n_tap];
	}
	
	public boolean shouldNext() {
		return number > NUMBER;
	}
	public int do_intFilter(int x) {
		return 0;
	}
	// do_filter: take one sample and compute one filterd output
	public float do_filter(float x) {
		double y = 0.0;
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
		return (float) y;
//		return (float) Math.abs( y );
	}

	// do_filter: take an array of samples and compute
	// the same number of filterd output.
	// The contents of the array will be overriden
	// by filtered output.
	public void do_filter(float x[], int len) {

		for (int k = 0; k < len; ++k) {
			double y = 0.0;
			int j = in_idx;
			buf[in_idx] = x[k];

			for (int i = 0; i < n_tap; ++i) {
				if (j < 0)
					j += n_tap;
				y = y + coeff[i] * buf[j--];
			}
			in_idx++;
			if (in_idx >= n_tap) {
				in_idx -= n_tap;
			}
			x[k] = (float) y;
		}
	}
}
