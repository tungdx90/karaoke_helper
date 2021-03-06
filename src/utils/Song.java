/**
 * 
 */
package utils;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import utils.Debug;

/**
 * @author W. Mooncai
 * 
 * @since 0.0
 * 
 * Class for use with Karaoke Helper app.
 * 
 * Stores song, and provides methods for karaoking the song.
 *
 */

public class Song {
	
	String[] mSong;
	
	public boolean mAutoScroll = false;
	public boolean mAutoScrollByLength = false;
	
	TextView mSongTV;
	TextView mVerseTV;
	
	private int mScrollMin;
	private int mScrollMax;
	public int mAutoScrollDelay = 1;
	
	public int mVerseLine = 0;
	
	Verse mVerse = new Verse("VerseThread");
	private Handler mHandler = new Handler();
	
	private Debug d = new Debug(true, Debug.DEBUG_LEVEL_DEBUG);
	
    // ------------------------------------------------------------------------

	public Song(TextView songView, TextView verseView,
			int scrollDelay, int scrollMin, int scrollMax) {
		
		mSongTV = songView;
		mVerseTV = verseView;
		
		mScrollMin = scrollMin;
		mScrollMax = scrollMax;
		
		setAutoScrollDelay(scrollDelay);
	}

    // ------------------------------------------------------------------------

	public void setSong(String[] song) {
		if (song.length > 0) {
			mSong = song;
		} else d.toLog(Debug.DEBUG_LEVEL_DEBUG, "ERROR setSong() - song.length: "
				+ song.length);
	}
	
    // ------------------------------------------------------------------------

	public void setAutoScrollDelay(int delay) {
		
		if ( (delay >= mScrollMin) && (delay <= mScrollMax) ) 
			mAutoScrollDelay = delay;
		else d.toLog(Debug.DEBUG_LEVEL_DEBUG, "ERROR setAutoScrollDelay() - delay: "
				+ delay + " / mScrollMin: " + mScrollMin + " / mScrollMax: "
				+ mScrollMax);
	}
	
    // ========================================================================

	public void singNextVerse() {
		mSongTV.setText(mSong[mVerseLine]);
		mVerseTV.setText("Verse: " + (mVerseLine + 1) + " / " + mSong.length);
		
		// Wrap the song or increment it
		if (mVerseLine < (mSong.length - 1) ) {
			mVerseLine++;
		} else {
			mVerseLine = 0;
		}
	}
	
    // ------------------------------------------------------------------------

	public void singSong() {
		
		for (int line = 0; line < mSong.length; line++) {
            mHandler.removeCallbacks(mVerse);
            mHandler.postDelayed(mVerse, 100);
		}
	}

	// ========================================================================
	
	private class Verse implements Runnable {

		/*
		 *  This won't work because all UI I/O must occur in the UI thread.
		 *  
		 *  This is a potential reference solution:
		 *  
		 *  http://android-developers.blogspot.com/2007/11/stitch-in-time.html
		 *  
		 */
		
		
		String mThreadName;
		
		Verse(String name) { mThreadName = name; }
		
		@Override
		public void run() {
			
			try {
				singNextVerse();
				wait(mAutoScrollDelay * 1000);
			}
			catch(InterruptedException exc) {
				d.toLog(Debug.DEBUG_LEVEL_DEBUG, "ERROR singSong() - InterruptedException: "
			+ exc.toString());
			}
			d.toLog(Debug.DEBUG_LEVEL_INFORMATIONAL, "Verse.run() - Thread"
					+ mThreadName + " terminating\n");
		}
		
	}
	
	
	private Runnable Verse2 = new Runnable() {
		   public void run() {
		       final long start = System.currentTimeMillis();
		       long millis = SystemClock.uptimeMillis() - start;
		       int seconds = (int) (millis / 1000);
		       int minutes = seconds / 60;
		       seconds     = seconds % 60;
	     /*
		       mHandler.postAtTime(this,
		               singNextVerse());
	      */
		   }
		};
		
} // Class Song
    
