//
//
//  NativeAudioAssetComplex.java
//
//  Created by Sidney Bofah on 2014-06-26.
//

package de.neofonie.cordova.plugin.nativeaudio;

import java.io.IOException;
import java.util.concurrent.Callable;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class NativeAudioAssetComplex implements OnCompletionListener {

	private MediaPlayer mp;
	private boolean isPaused;
	Callable<Void> completeCallback;

	public NativeAudioAssetComplex( AssetFileDescriptor afd, float volume)  throws IOException
	{
		isPaused = false;
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC); 
		mp.setVolume(volume, volume);
		mp.prepare();
	}

	public void play(Callable<Void> completeCb) throws IOException
	{
		completeCallback = completeCb;
		invokePlay( false );
	}

	private void invokePlay( Boolean loop )
	{
		isPaused = false;
		mp.pause();
		mp.setLooping(loop);
		mp.seekTo(0);
		mp.start();
	}

	public boolean pause()
	{
		if ( mp.isLooping() || mp.isPlaying() )
		{
			isPaused = true;
			mp.pause();
			return true;
		}
		return false;
	}

	public boolean resume()
	{
		if ( isPaused ) {
			isPaused = false;
			mp.start();
			return true;
		} else {
			return false;
		}
	}

	public void stop() throws IOException
	{
		if ( mp.isLooping() || mp.isPlaying() )
		{
			mp.pause();
			mp.seekTo(0);
		}
	}

	public void loop() throws IOException
	{
		invokePlay( true );
	}

	public void unload() throws IOException
	{
		this.stop();
		mp.release();
	}

	public void onCompletion(MediaPlayer mPlayer)
	{
		if ( !mp.isLooping() )
		{
			this.stop();
			completeCallback.call();
		}
	}
}
