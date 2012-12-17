package com.AndroidHealth;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class MediaLoader3 implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

    private static final String TAG = "MediaLoader2";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
	private boolean audioFinished;
	private MediaPlayer media;
	String swfFilename;
	private String[] mp3Filenames;
	private AndroidHealthActivity currentActivity;
	private int currentAudioIndex;
	private boolean isQuiz;
	private AssetFileDescriptor afd;
    
	public MediaLoader3(String swfFilename, String[] mp3Filenames, boolean isQuiz, AndroidHealthActivity currentActivity) {
		currentAudioIndex = 0;
		this.swfFilename = swfFilename;
		this.mp3Filenames = mp3Filenames;
		this.currentActivity = currentActivity;
		this.isQuiz = isQuiz;
	}
	
	public void loadMedia(Bundle savedInstanceState) {
		loadVideo(swfFilename);
		if(savedInstanceState == null || !(Boolean)savedInstanceState.get("audioFinished")) {
			loadAudio(mp3Filenames[currentAudioIndex], savedInstanceState);
		} else {
			audioFinished = true;
		}
	}
	
	public boolean audioIsFinished() {
		return audioFinished;
	}
	
	public MediaPlayer getVideo() {
		return mMediaPlayer;
	}
	
	public MediaPlayer getMediaPlayer() {
		return media;
	}
	
	/*
	 * Removes suffix if filename has a suffix
	 */
	private String removeSuffix(String filename) {
		int index = filename.lastIndexOf('.');
		if(index != -1) {
			return filename.substring(0, index);
		}
		return filename;
	}
	
	void loadVideo(String videoFilename) {
		videoFilename = removeSuffix(videoFilename);
		
		if(currentActivity.sceneIsQuiz()) {
			mSurfaceView = (SurfaceView) currentActivity.findViewById(R.id.quizVideo);
		} else {
			mSurfaceView = (SurfaceView) currentActivity.findViewById(R.id.infoSurfaceView);
		}
		initSurfaceView();
		try {
//			int resId = (R.raw.question_video);
			int resId = currentActivity.getResources().getIdentifier(videoFilename, "raw", currentActivity.getPackageName());
	        afd = currentActivity.getResources().openRawResourceFd(resId);

		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}


	} 
	
	void initSurfaceView () {
        
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mSurfaceView.setFocusable(true);
		mSurfaceView.setFocusableInTouchMode(true);
		mSurfaceView.requestFocus();
	}
    
    private void playVideo() {
        doCleanUp();
        try {	        
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        playVideo();
    }


    void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    void doCleanUp() {
    	releaseMediaPlayer();
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }
    
    public void loadAudio(String mp3Filename, Bundle savedInstanceState) {
    	try {
    		media = new MediaPlayer();
    		AssetFileDescriptor descriptor;
    		descriptor = currentActivity.getAssets().openFd(mp3Filename);
    		media.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());	
    		descriptor.close();
            media.prepare();
            media.setOnCompletionListener(new AudioCompletionListener());
            if(savedInstanceState != null && savedInstanceState.containsKey("audioPos")) {
            	int pos = savedInstanceState.getInt("audioPos");
            	media.seekTo(pos);
            }
            media.start();
            
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    
    public void finishAudio() {
    	releasePlayer(media);
    }
    
    /* synchronized in case the user answers correctly at same time audio finishes */
	   private synchronized void releasePlayer(MediaPlayer mp) {
		   if(mp!= null) {
			   audioFinished = true;
			   mp.release();
			   media = null;
		   }
	   }
    
    private class AudioCompletionListener implements MediaPlayer.OnCompletionListener {

		public void onCompletion(MediaPlayer mp) {
    		   try {
    			   //pause the video
    			   mMediaPlayer.pause();
//    			   mMediaPlayer.release();
    			   //releases audio
    			   releasePlayer(mp);
    			   if(!isQuiz) {
    				   currentActivity.showInfoDialog();
    			   }
//    			   if(!isQuiz) {
//    				   currentActivity.showInfoDialog();
//    				   currentAudioIndex++;
//    				   if(currentAudioIndex >= mp3Filenames.length) {
//    					   currentActivity.goToNextScene();
//    				   } else {
//    					   loadAudio(mp3Filenames[currentAudioIndex], null);
//    				   }
//    			   }
    		 } catch (Exception ex) {
    			 ex.printStackTrace();
    		 }
		}
    }
}
