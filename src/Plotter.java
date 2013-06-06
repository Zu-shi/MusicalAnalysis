import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.audio.io.WaveDecoder;
import com.badlogic.audio.visualization.Plot;


public class Plotter {

	public static void main(String args[]){
		try {
			Plotter a = new Plotter();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Plotter() throws FileNotFoundException, Exception{
	      WaveDecoder decoder = new WaveDecoder( new FileInputStream( "samples/sample.wav" ) );
	      ArrayList<Float> allSamples = new ArrayList<Float>( );
	      float[] samples = new float[1024];
	 
	      while( decoder.readSamples( samples ) > 0 )
	      {
	         for( int i = 0; i < samples.length; i++ )
	            allSamples.add( samples[i] );
	      }
	 
	      samples = new float[allSamples.size()];
	      for( int i = 0; i < samples.length; i++ )
	         samples[i] = allSamples.get(i);
	 
	      Plot plot = new Plot( "Wave Plot", 512, 512 );
	      plot.plot( samples, 44100 / 1000, Color.red );
	}
}
