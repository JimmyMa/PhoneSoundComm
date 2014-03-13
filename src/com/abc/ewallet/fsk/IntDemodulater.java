package com.abc.ewallet.fsk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.abc.ewallet.utils.Constants;



public class IntDemodulater {

	int phase = 60;
	byte[] data = new byte[30];
	int byteIndex = 0;
	int bitIndex = 0;
	int bufferSize = 0;
	int frameCounter = 0;
	int status = 0;
	boolean inZero = false;
	boolean inOne = false;


	PeakPoint[] peaks;
	int peakIndex = 0;
	int nextPoint = 0;
	int threshold = 0;
	int initThreshold = 0;
	int angleThreshold = 100;
	boolean foundHeader = false;
	
	private FileOutputStream logos;

	public void init(int bufferSize) {
		this.bufferSize = bufferSize;
		foundHeader = false;
		angleThreshold = 100;
		status = 0;
		inZero = false;
		inOne = false;
		byteIndex = 0;
		bitIndex = 0;
		clearPeaks();
		frameCounter = 0;
		if ( Constants.debug ) {
			try {
				if ( logos == null ) {
					logos = new FileOutputStream( new File( Constants.LOG_IN_FILE ) );
				}
				write( "Init Buffer Size: " + bufferSize );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void reinit() {
		init( this.bufferSize );
	}
	
	public void done() {
		if ( !Constants.debug ) {
			return;
		}
		try {
			logos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void setMax( float max ) {
		initThreshold = (int)(max * 0.1);
	}
	
	private void write( String msg ) {
		if ( !Constants.debug ) {
			return;
		}
		try {
			logos.write( (msg + "\n").getBytes() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearPeaks() {
		peaks = new PeakPoint[3];
		peaks[0] = new PeakPoint();
		peaks[1] = new PeakPoint();
		peaks[2] = new PeakPoint();
		peakIndex = 0;
	}
	
	public int getStatus() {
		return status;
	}

	public byte[] getData() {
		byte[] result = new byte[byteIndex];
		System.arraycopy(data, 0, result, 0, byteIndex);
		return result;
	}

	public boolean demodulateFMData(int[] buffer) {
		write( "demodulateFMData: Buffer Size: " + buffer.length + ":" + foundHeader);
		if (!foundHeader) {
			foundHeader = demodulateFMDataFindHeader(buffer);
		}

		if (foundHeader) {
			return demodulateFMData2(buffer);
		}
		return true;
	}

	public boolean demodulateFMDataFindHeader(int[] buffer) {
		try {

			int start = 0;
			int end = buffer.length;

			for (int i = start; i < end - phase * 9; i++) {
				if ( buffer[i] < initThreshold ) {
					continue;
				}
				int b = (int) (buffer[i + phase + 30] - buffer[i]) / phase;
				if (b > angleThreshold) {
					write( "" + (frameCounter * bufferSize + i ) );
					int j = i;
					for ( j = i; j < i + phase * 9; j ++ ) {
						if ( buffer[ j ] > peaks[peakIndex].value ) {
							 peaks[peakIndex].point = frameCounter * bufferSize + j + 1;
							 peaks[peakIndex].value = buffer[ j ];
						}
						if (( frameCounter * bufferSize + j + 1 ) - peaks[peakIndex].point > 10 && isNear( peaks[0].value, peaks[1].value, peaks[2].value ) ) {
//							phase =  ( peaks[2].point - peaks[0].point )/4;
							nextPoint = ( peaks[0].point + peaks[1].point +  peaks[2].point ) / 3 + phase * 5;
							threshold = (int)(( peaks[0].value + peaks[1].value +  peaks[2].value ) / 3 * 0.5);
							write( "T:" + threshold );
							return true;
						}
						if ( ( frameCounter * bufferSize + j + 1 ) - peaks[peakIndex].point > phase ) {
							peakIndex ++;
						
						}
						if ( peakIndex == 3 ) {
							peakIndex = 0;
						} 
					}

					i += j;

				}
				clearPeaks();
			}

			frameCounter++;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean demodulateFMData2(int[] buffer) {
		write( "Demo: " );
		for (int i = (nextPoint - frameCounter * bufferSize - 1); i < bufferSize; i += phase) {
			int max = Math.max(buffer[i-1], buffer[i]);
			if ( max > threshold ) {
				threshold = (int)(threshold + max * 0.5 ) / 2;

				if ( !inputData3(false) ) {
					return false;
				}
				i = getNearMaxIndex( buffer, i, 10 );
				write("Data: 0 P:" + (frameCounter * bufferSize + i + 1) );
			} else {
				if ( !inputData3(true) ) {
					return false;
				}
				write("Data: 1 P:" + (frameCounter * bufferSize + i + 1) );
			}
			nextPoint = frameCounter * bufferSize + i + 1 + phase;
		}

		if (byteIndex == data.length) {
			return false;
		}

		frameCounter++;

		return true;
		// System.out.println( new String( data ) );
	}

	private int getNearMaxIndex( int[] buffer, int i, int len ) {
		int max = 0;
		int index = 0;
		for ( int j = (i - len > 0 ? i - len : 0 ); j < buffer.length && j < i + len; j ++ ) {
			if ( buffer[j] > max ) {
				max = buffer[j];
				index = j;
			}
		}
		return index;
	}
	
	private boolean isNear(float f1, float f2, float f3) {
		if (f1 == 0 || f2 == 0 || f3 == 0) {
			return false;
		}
		double d1 = f1 / f2;
		double d2 = f2 / f3;
		if (0.8 < d1 && d1 < 1.2 && d2 > 0.8 && d2 < 1.2) {
			return true;
		}
		return false;
	}

	private boolean inputData3(boolean bitSet) {
		if (bitSet) {
			if ( inZero ) {
				inZero = false;
				return true;
			}
			if ( inOne ) {
				inOne = false;
				return true;
			}
//			System.out.println("Data:" + byteIndex + ":" + bitIndex
//					+ "  ---------- 1");
			data[byteIndex] = (byte) (data[byteIndex] | (byte) (Math.pow(2,
					bitIndex)));
			bitIndex++;
			inOne = true;
		} else {
			inZero = true;
//			System.out.println("Data:" + byteIndex + ":" + bitIndex
//					+ "  ---------- 0");
			bitIndex++;
		}
	
		if (bitIndex == 8) {
			if ( data[byteIndex] == -1 || byteIndex == data.length - 1 ) {
				data[byteIndex] = '\n';
				status = 1;
				return false;
			}
			write("Byte ----------:" + (char) data[byteIndex]);
			bitIndex = 0;
			byteIndex++;
		}
		return true;
	}
}

