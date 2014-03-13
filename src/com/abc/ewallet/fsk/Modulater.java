package com.abc.ewallet.fsk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import com.abc.ewallet.fsk.fir.FIRFactory;
import com.abc.ewallet.fsk.fir.FIRFilter;
import com.abc.ewallet.utils.Constants;

public class Modulater {
//	private static final int BITS_PER_SAMPLE = 16;
//	private static final int NUMBER_OF_CHANNELS = 1;

	private static final int SAMPLES_PER_BIT = 55;
	private static final int BITS_PER_WORD = 8;
	private static final int SAMPLES_PER_FRAME = SAMPLES_PER_BIT * BITS_PER_WORD;
//	private static final int BITS_PER_FRAME = 1024;
//	private static final int SAMPLE_RATE = 8000; // 44100 if you want a really nice, clean sin wave, but then you must change FFT_SIZE to at least 16384 too
	public static final int SAMPLE_RATE = 44100; // 44100 if you want a really nice, clean sin wave, but then you must change FFT_SIZE to at least 16384 too
//	private static final int BASE_FREQUENCY = 500;
//	private static final int SAMPLES_PER_CHARACTER = SAMPLE_PER_BIT * BITS_PER_WORD;
	private static final int BASE_AMPLITUDE = (int)(Short.MAX_VALUE *0.9);
	private static int time = 0;
	FIRFilter fir;
	private double prePhase = 0;
	private boolean inEnvelope = false;

	public byte[] modulate(String string) throws IOException {

		fir = FIRFactory.createHPF_16800_32order();
		byte[] buffer = new byte[SAMPLES_PER_FRAME * 3 * (string.length() + 14)];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
		
		begin(shortBuffer);

		int i = 0;
		for (i = 0; i < string.length() && i < SAMPLES_PER_FRAME; i++)
		{
			byte byteToModulate = (byte) string.charAt(i);

			modulateByte(shortBuffer, byteToModulate, i % 2 == 0);
		}
		end(shortBuffer);

		File saveFile=new File( Constants.PCM_OUT_FILE );
        FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(saveFile);
	        outStream.write(buffer);
	        outStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return buffer;
	}
	
	private void begin(ShortBuffer shortBuffer) {
		inEnvelope = true;
//		for ( int i = 0; i < 10; i ++ ) {
//			modulateByte(shortBuffer, (byte)0xFF,  true );
//		}
		modulateByte(shortBuffer, (byte)0x55,  true );
		inEnvelope = false;
	}
	
	private void end(ShortBuffer shortBuffer) {
		inEnvelope = true;
		modulateByte(shortBuffer, (byte)0xFF,  true );
		inEnvelope = false;
	}

	private void modulateByte(ShortBuffer shortBuffer, byte byteToModulate, boolean even) {
		for (int bitNumber = 0; bitNumber < BITS_PER_WORD; bitNumber++) {
			boolean bitSet = ((byteToModulate & (byte) (Math.pow(2, bitNumber))) != 0);
			modulateBit( shortBuffer, bitSet );
		}
	}
	
	private void modulateBit(ShortBuffer shortBuffer, boolean bitSet) {
		if (bitSet)	{
			modulateOne( shortBuffer );
			if ( !inEnvelope ) {
				modulateOne( shortBuffer );
			}
		} else {
			modulateZero( shortBuffer );
			if ( !inEnvelope ) {
				modulateOne( shortBuffer );
			}
		}
	}


	private void modulateOne(ShortBuffer shortBuffer) {
		time = 1;
		double prePhase = getPrePhase();
		double phase = 0;
		for ( int i = 0; i < SAMPLES_PER_BIT; i ++ ) {
			phase = ((double) 19600  * (time++)) / SAMPLE_RATE + prePhase;
			int sampleValue = (int)(Math.sin(2 * Math.PI * phase ) * BASE_AMPLITUDE/5 ) ;
			if ( fir != null ) {
				sampleValue = (int)fir.do_filter( sampleValue );
			}
			shortBuffer.put( (short)( sampleValue << 8 & 0xFF00 | sampleValue >> 8 & 0xFF ) );
//			shortBuffer.put( (short)sampleValue );
		}
		setPrePhase( phase );
//		System.out.println( "1" );
//		modulateZero( shortBuffer );
		
	}
	
	private void modulateZero(ShortBuffer shortBuffer) {
		time = 1;
		double prePhase = getPrePhase();
		double phase = 0;
		for ( int i = 0; i < SAMPLES_PER_BIT; i ++ ) {
			phase = ((double) 18400  * (time++)) / SAMPLE_RATE + prePhase;
			int sampleValue = (int)(Math.sin(2 * Math.PI * phase ) * BASE_AMPLITUDE) ;
			if ( fir != null ) {
				sampleValue = (int)fir.do_filter( sampleValue );
			}
//			shortBuffer.put( (short)sampleValue );
			shortBuffer.put( (short)( sampleValue << 8 & 0xFF00 | sampleValue >> 8 & 0xFF ) );
//			System.out.println( i + " = " + sampleValue );
//			shortBuffer.put( (short)sampleValue );
		}
		setPrePhase( phase );
//		System.out.println( "0" );

	}
	
	
	private double getPrePhase() {
		return prePhase;
	}
	
	private void setPrePhase( double prePhase ) {
		this.prePhase = prePhase;
	}
		

}
