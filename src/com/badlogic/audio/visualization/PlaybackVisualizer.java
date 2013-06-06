package com.badlogic.audio.visualization;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.Decoder;

/**
 * Takes a plot and a decoder and plays back the audio
 * form the decoder as well as setting the marker in the
 * plot accordingly.
 * 
 * @author mzechner
 *
 */
public class PlaybackVisualizer 
{
	/**
	 * Consturctor, plays back the audio form the decoder and
	 * sets the marker of the plot accordingly. This will return
	 * when the playback is done.
	 * 
	 * @param plot The plot.
	 * @param samplesPerPixel the numbe of samples per pixel.
	 * @param decoder The decoder.
	 * @throws Exception 
	 */
	public PlaybackVisualizer( final Plot plot, final int samplesPerPixel, Decoder decoder ) throws Exception
	{
		AudioDevice device = new AudioDevice( );
		float[] samples = new float[1024];
		
		long startTime = 0;
		/*
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
			if( startTime == 0 )
				startTime = System.nanoTime();
			float elapsedTime = (System.nanoTime()-startTime)/1000000000.0f;
			int position = (int)(elapsedTime * (44100/samplesPerPixel)); 
			plot.setMarker( position, Color.white );
			Thread.sleep(20); // this is needed or else swing has no chance repainting the plot!
		}
		
		boolean done = false;
		System.out.println();
		while( !done )
		{
			//System.out.println( "length is " + decoder.readSamples( samples ));
			if(decoder.readSamples( samples )<samples.length){done = true;}
			//device.writeSamples( samples );
			
			if( startTime == 0 )
				startTime = System.nanoTime();
			float elapsedTime = (System.nanoTime()-startTime)/1000000000.0f;
			int position = (int)(elapsedTime * (44100/samplesPerPixel)); 
			plot.setMarker( position, Color.white );
			Thread.sleep(20); // this is needed or else swing has no chance repainting the plot!
		}

		*/
		
		(new Thread(new AudioWriter(device, decoder))).start();
		System.out.print("DONE");
		final float startTime2 = System.nanoTime();
		Timer m = new Timer(16, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//System.out.print("aaa");
				float elapsedTime = (System.nanoTime()-startTime2)/1000000000.0f;
				int position = (int)(elapsedTime * (44100/samplesPerPixel)); 
				plot.setMarker( position, Color.white );
			}
		});
		m.start();
	}
}


class AudioWriter implements Runnable {

	float[] samples = new float[1024];
	AudioDevice device;
	Decoder decoder;
	
	public AudioWriter(AudioDevice device, Decoder decoder){
		this.device = device;
		this.decoder = decoder;
	}
	
	@Override
	public void run() {
		while( decoder.readSamples( samples ) == samples.length )
		{
			device.writeSamples( samples );
		}
    }

}
