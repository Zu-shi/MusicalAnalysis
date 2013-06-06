package com.badlogic.audio.samples.part7;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.audio.analysis.SpectrumProvider;
import com.badlogic.audio.analysis.ThresholdFunction;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.PlaybackVisualizer;
import com.badlogic.audio.visualization.Plot;

public class MultiBandThreshold 
{
	public static final String FILE = "samples/btrs.mp3";
	public static final int HOP_SIZE = 512;
	public static final int HISTORY_SIZE = 50;
	public SpectrumProvider spectrumProvider;
	public static final float[] multipliers = { 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f};
	//
	//public static final float[] bands = { 80, 4000, 4000, 10000, 10000, 16000 };
	//public static final float[] bands = { 80, 2000, 2000, 4000, 4000, 8000, 8000, 16000, 16000, 32000 };
	//public static final float[] bands = { 80, 2000, 1000, 4000, 2000, 8000, 4000, 16000, 8000, 24000 };
	public static final float[] bands = { 50, 94, 94, 176, 176, 331, 331, 620, 620, 1200, 1200, 2200, 2200, 4100, 4100, 7700, 7700, 16000, 16000, 22000 };
	
	public static void main( String[] argv ) throws Exception
	{	
		MultiBandThreshold graph1 = new MultiBandThreshold();
		
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE  ) );
		SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 1024, HOP_SIZE, true );
		//List<Float> a = graph1.getSpectralFlux(spectrumProvider, 50, 94);
		Plot plot = new Plot( "Spectral Flux", 1024, 512 );	
		
		//for( int i = 0; i < bands.length / 2; i++ )
		int a = 4;
		for( int i = 0; i < bands.length; i += a)
		{
			decoder = new MP3Decoder( new FileInputStream( FILE  ) );
			List<Float> flux = graph1.getSpectralFlux(new SpectrumProvider( decoder, 1024, HOP_SIZE, true ), bands[i], bands[i+1]);
			plot.plot(flux, 1, i/2, a/1, false, Color.red, 10);
			List<Float> threshold = graph1.getThreshold(flux, 2.0f);
			plot.plot(threshold , 1, i/2, a/1, false, Color.green, 10);
			List<Float> onset = graph1.getOnset(flux, threshold);
			plot.plot(onset , 1, i/2, a/1, false, Color.yellow, 10);
		}
		
		new PlaybackVisualizer( plot, HOP_SIZE, new MP3Decoder( new FileInputStream( FILE ) ) );
	}
	
	public ArrayList<Float> getSpectralFlux(SpectrumProvider spectrumProvider, float lower, float upper){
		ArrayList<Float> spectralFlux = new ArrayList<Float>();
		float[] spectrum = spectrumProvider.nextSpectrum();
		float[] lastSpectrum = new float[spectrum.length];

		int startFreq = spectrumProvider.getFFT().freqToIndex( lower );
		int endFreq = spectrumProvider.getFFT().freqToIndex( upper );
		do
		{
			float flux = 0;
			for( int j = startFreq; j <= endFreq; j++ )
			{
				float value = (spectrum[j] - lastSpectrum[j]);
				value = (value + Math.abs(value))/2;
				flux += value;
			}
			spectralFlux.add( flux );
			System.arraycopy( spectrum, 0, lastSpectrum, 0, spectrum.length );
		}
		while( (spectrum = spectrumProvider.nextSpectrum() ) != null );
		return spectralFlux;
	}
	
	public List<Float> getThreshold(List<Float> spectralFlux, Float multiplier){
		List<Float> threshold = new ThresholdFunction( HISTORY_SIZE, multiplier ).calculate( spectralFlux );
		return threshold;
	}

	public List<Float> getOnset(List<Float> spectralFlux, List<Float> threshold){
		List<Float> onset = new ArrayList<Float>();
		int size = spectralFlux.size();
		for(int i=1;i<size;i++){
			if(spectralFlux.get(i)>threshold.get(i) && spectralFlux.get(i)<spectralFlux.get(i-1)){
				onset.add(1.0f);
			}else{
				//remember offset by i
				onset.add(0.0f);
			}
		}
		return onset;
	}
}
