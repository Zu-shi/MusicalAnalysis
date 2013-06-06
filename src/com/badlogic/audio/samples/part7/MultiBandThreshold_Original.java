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

public class MultiBandThreshold_Original 
{
	public static final String FILE = "samples/hitme.mp3";
	public static final int HOP_SIZE = 512;
	public static final int HISTORY_SIZE = 50;
	//
	public static final float[] multipliers = { 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f};
	public static final float[] bands = { 50, 94 , 94, 176, 176, 331, 331, 620, 620, 1200, 1200, 2200, 2200, 4100, 4100, 7700, 7700, 16000, 16000, 22000 };
	
	public static void main( String[] argv ) throws Exception
	{		
		MultiBandThreshold_Original graph1 = new MultiBandThreshold_Original();
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE  ) );
		SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 1024, HOP_SIZE, true );			
		float[] spectrum = spectrumProvider.nextSpectrum();
		float[] lastSpectrum = new float[spectrum.length];		
		List<List<Float>> spectralFlux = new ArrayList<List<Float>>( );
		for( int i = 0; i < bands.length / 2; i++ )
			spectralFlux.add( new ArrayList<Float>( ) );
				
		do
		{						
			for( int i = 0; i < bands.length; i+=2 )
			{
			//int i=0;
				int startFreq = spectrumProvider.getFFT().freqToIndex( bands[i] );
				int endFreq = spectrumProvider.getFFT().freqToIndex( bands[i+1] );
				float flux = 0;
				for( int j = startFreq; j <= endFreq; j++ )
				{
					float value = (spectrum[j] - lastSpectrum[j]);
					value = (value + Math.abs(value))/2;
					flux += value;
				}
				spectralFlux.get(i/2).add( flux );
			}
					
			System.arraycopy( spectrum, 0, lastSpectrum, 0, spectrum.length );
		}
		while( (spectrum = spectrumProvider.nextSpectrum() ) != null );				

		System.out.println(spectralFlux.get(0).size());
		System.out.println(spectralFlux.get(0).get(100));
		System.out.println(spectralFlux.get(0).get(101));
		System.out.print("a");
		//Calculate Thresholds
		List<List<Float>> thresholds = new ArrayList<List<Float>>( );
		for( int i = 0; i < bands.length / 2; i++ )
		{
			List<Float> threshold = new ThresholdFunction( HISTORY_SIZE, 2.0f ).calculate( spectralFlux.get(i) );
			thresholds.add( threshold );
		}
		
		//peak picking.
		//Try update to draw just range, instead of all that's over?
		//Maybe you can use this to detect change in sections.
		//Remember logaritmic scarle incrementaiton.
		
		Plot plot = new Plot( "Spectral Flux", 1024, 512 );
		for( int i = 0; i < bands.length / 2; i++ )
		{
			//plot.plot( spectralFlux.get(i), 1, i, 1, false, Color.gray, bands.length/2);
			//plot.plot( thresholds.get(i), 1, i, 1, true, Color.cyan,bands.length/2 );
			List<Float> onset = graph1.getOnset(spectralFlux.get(i), thresholds.get(i));
			//plot.plot(onset , 1, i, 1, false, Color.red, 10);
		}
		int size = spectralFlux.size();
		
		List<Float> totalFlux = new ArrayList<Float>();
		List<Float> totalThreshold2 = new ArrayList<Float>();
		for(int i2=0;i2<spectralFlux.get(0).size();i2++){
			float result = 0.0f;
			float result2 = 0.0f;
			for(int i=0;i<spectralFlux.size();i++){
				/*
				if(thresholds.get(i).get(i2)!=0){
					result+=spectralFlux.get(i).get(i2)/thresholds.get(i).get(i2);
				}else{
					result+=spectralFlux.get(i).get(i2);
				}*/
				//result+=Math.sqrt(spectralFlux.get(i).get(i2));
				result+=Math.pow(spectralFlux.get(i).get(i2), 2);
				result2+=thresholds.get(i).get(i2);
			}
			totalFlux.add(result);
			totalThreshold2.add(result2);
		}

		
		plot.plot( totalFlux, 1, 0, 10, false, Color.green, 10);
		//setting for sqrt
		//List<Float> totalThreshold = graph1.getThreshold(totalFlux, 1.4f);
		//setting for sq
		List<Float> totalThreshold = graph1.getThreshold(totalFlux, 2.5f);
		graph1.getStats(totalFlux);
		graph1.getStats(totalThreshold);
		//plot.plot( totalThreshold, 1, 0, 10, false, Color.yellow, 10);
		List<Float> onset = graph1.getOnset(totalFlux, totalThreshold);
		plot.plot(onset , 1, 0, 10, false, Color.green, 10);
		plot.setOnset(onset);
		
		//Onset 2, for more serious onsets
		List<Float> totalThreshold3 = graph1.getThreshold(totalFlux, 5f);
		onset = graph1.getOnset(totalFlux, totalThreshold3);
		plot.plot(onset , 1, 0, 10, false, Color.yellow, 10);
		

		List<Float> totalThreshold4 = graph1.getThreshold(totalFlux, 10f);
		onset = graph1.getOnset(totalFlux, totalThreshold3);
		plot.plot(onset , 1, 0, 10, false, Color.red, 10);
		
		new PlaybackVisualizer( plot, HOP_SIZE, new MP3Decoder( new FileInputStream( FILE ) ) );
	}

	public void getStats(List<Float> samples){
		float min = 0;
		float max = 0;
		float total = 0.0f;
		for(int i2=0;i2<samples.size();i2++){
			total+=samples.get(i2);
			min = Math.min( samples.get(i2), min );
			max = Math.max( samples.get(i2), max );
		}
		System.out.println("samples min is "+min+".");
		System.out.println("samples max is "+max+".");
		System.out.println("samples average is "+total/samples.size()+".");
	}
	
	public List<Float> getThreshold(List<Float> spectralFlux, Float multiplier){
		List<Float> threshold = new ThresholdFunction( HISTORY_SIZE, multiplier ).calculate( spectralFlux );
		return threshold;
	}
	
	public List<Float> getOnset(List<Float> spectralFlux, List<Float> threshold){
		List<Float> onset = new ArrayList<Float>();
		int size = spectralFlux.size();
		int counter = 0;
		for(int i=1;i<size-1;i++){
			if(spectralFlux.get(i)>threshold.get(i) && spectralFlux.get(i+1)<spectralFlux.get(i)){
				if(counter<=0){
					onset.add(1.0f);}
				else{
					onset.add(0.0f);
				}
				counter = 10;
			}else{
				//remember offset by i
				onset.add(0.0f);
			}
			counter--;
		}
		return onset;
	}
}
