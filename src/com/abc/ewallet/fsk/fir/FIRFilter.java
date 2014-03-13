package com.abc.ewallet.fsk.fir;

public interface FIRFilter {

	public float do_filter(float x);
	public int do_intFilter(int x);
	public boolean shouldNext();
}
