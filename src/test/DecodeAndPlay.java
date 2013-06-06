package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

public class DecodeAndPlay {
	
	/**
	 * The current frame number. 
	 */
	private int frame = 0;
	
	/**
	 * The MPEG audio bitstream. 
	 */
	/*final*/ private Bitstream		bitstream;
	
	/**
	 * The MPEG audio decoder. 
	 */
	private Decoder		decoder; 
	
	/**
	 * The AudioDevice the audio samples are written to. 
	 */
	private AudioDevice	audio;
	
	/**
	 * Has the player been closed?
	 */
	private boolean		closed = false;
	
	/**
	 * Has the player played back all frames from the stream?
	 */
	private boolean		complete = false;

	private int			lastPosition = 0;

	public float time = 10; // 10 seconds into the song
	public float samplingRate = 44100.0f;
	public int index = (int)(time / samplingRate);
	/**
	 * Creates a new <code>Player</code> instance. 
	 */
	
	public static void main(String args[]){
		DecodeAndPlay a = new DecodeAndPlay();
	}
	
	public DecodeAndPlay(){

		//# MP3 file
		String basefile="c:/data/";
		String filename="test2.mp3";
		Bitstream bitStream;
		
		try {
			bitStream = new Bitstream(new FileInputStream(basefile+filename));
			FactoryRegistry r = FactoryRegistry.systemRegistry();
		    Decoder decoder = new Decoder();
			audio = r.createAudioDevice();
			audio.open(decoder);
			SampleBuffer output = (SampleBuffer)decoder.decodeFrame(bitStream.readFrame(), bitStream);
		    bitStream.closeFrame();
			
			/*
			while(i<100){
				SampleBuffer output = (SampleBuffer)decoder.decodeFrame(bitStream.readFrame(), bitStream); //returns the next 2304 samples
			    bitStream.closeFrame();
				System.out.println(i+audio.toString());
				short[] a = output.getBuffer();
				audio.write(a, 0, output.getBufferLength());
			    //do whatever with your samples
				i++;
			}*/
			float[] aSound = generateSound(440.0f, samplingRate, 10);
			short[] shortOutput = new short[aSound.length];
			for(int i =0; i<aSound.length; i++){
				shortOutput[i] = (short) (aSound[i] * Short.MAX_VALUE * 0.8);
			}
			audio.write(shortOutput, 0, (int) (samplingRate * 10));
			audio.close();
			bitStream.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public float[] convertPCM( short[] leftChannel, short[] rightChannel )
	{
	   float[] result = new float[leftChannel.length];
	   for( int i = 0; i < leftChannel.length; i++ )
	      result[i] = (leftChannel[i] + rightChannel[i]) / 2 / 32768.0f;
	   return result;
	}
	
	//This generates mono sound, but the dummy song you used takes stereo. Get on it!
	public float[] generateSound( float frequency, float samplingRate, float duration )
	{
	   float[] pcm = new float[(int)(samplingRate*duration)];
	   float increment = 2 * (float)Math.PI * frequency / samplingRate;
	   float angle = 0;
	 
	   for( int i = 0; i < pcm.length; i++ )
	   {
	      pcm[i] = (float) Math.sin( angle );
	      angle += increment;
	      frequency+= frequency/pcm.length;
	      increment = 2 * (float)Math.PI * frequency / samplingRate;
	   }
	 
	   return pcm;
	}
}
