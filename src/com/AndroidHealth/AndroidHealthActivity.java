package com.AndroidHealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.RelativeLayout;

public abstract class AndroidHealthActivity extends Activity {
	private String tag = "AndroidHealth";
	private int sceneIndex;
	MediaLoader3 mediaLoader;
	private boolean secondLoad = false;
	
	//this should hold the value of the number of Correct answers
	public static int numberAnsweredCorrect = 0;
	
	public static int numberScenesinSection = 0;
	public static int numberThisSceneinSection = 0;
	public static int indexOfFirstScene = 0;
	public static int numberInInfoQuizSection = 0;
	public static boolean infoquiz = false;
	public static final String PREFS_NAME = "MyPrefsFile";

	
	protected MediaLoader3 setMediaLoader(MediaLoader3 mediaLoader) {
		this.mediaLoader = mediaLoader;
		return mediaLoader;
	}

	protected String getTag() {
		return tag;
	}
	
	protected int getSceneIndex() {
		return sceneIndex;
	}
	
	protected int setSceneIndex(int sceneIndex) {
		this.sceneIndex = sceneIndex;
		return sceneIndex;
	}
	/* works on the assumption that the first array has the answers */
	public boolean sceneIsQuiz() {
    	TypedArray ta = getResources().obtainTypedArray(R.array.scenes);
    	int id = ta.getResourceId(sceneIndex, 0);
    	String[] sceneContents = getResources().getStringArray(id);
    	
    	if(sceneContents[0].indexOf("/") != -1) {
    		String assetsDir = sceneContents[0].substring(0, sceneContents[0].indexOf("/"));
    		if(assetsDir.compareTo("answers") == 0) {
        		return true;
        	}
    	}
    	return false;
	}
	
    
    // It's annoying when the sound keeps going after I press back button
    @Override
    public synchronized void onBackPressed() {
    	mediaLoader.finishAudio();
    	finish();
    	super.onBackPressed();
    	System.exit(0);
    }
	
    protected void onSaveInstanceState(Bundle outState) {
    	MediaPlayer mp = mediaLoader.getMediaPlayer();
    	if(!mediaLoader.audioIsFinished() && mp != null) {
    		mp.pause();
    		int audioPos = mp.getCurrentPosition();
    		outState.putInt("audioPos", audioPos);
    	}
    	outState.putInt("sceneIndex", sceneIndex);
    	outState.putBoolean("audioFinished", mediaLoader.audioIsFinished());
    }
    
    protected void setSecondLoad(boolean secondLoad) {
    	this.secondLoad = secondLoad;
    }
    protected boolean getSecondLoad() {
    	return secondLoad;
    }
    
    
    public void goToNextScene() {
    	try {

    		Intent myIntent = new Intent();
    		
    		MediaPlayer video = mediaLoader.getVideo();
    		if(video != null) {
    			video.reset();
    		}
    		
    		if(findViewById(R.id.relButtons)!=null){
        		RelativeLayout buttons = (RelativeLayout) findViewById(R.id.relButtons);
        		buttons.removeAllViewsInLayout();
        		buttons.setBackgroundColor(Color.BLACK);
    		}

    		//only go to next scene if there is another question in the quiz
    		
    		sceneIndex++;

    		myIntent.putExtra("sceneIndex", sceneIndex);

    		if(sceneIsQuiz()) { 			
    			myIntent.setClassName(getPackageName(), "com.AndroidHealth.QuizActivity");
    			finish();
    			startActivity(myIntent);
    		} else {
    			myIntent.setClassName(getPackageName(), "com.AndroidHealth.InfoActivity");
    			startActivity(myIntent);
    			finish();
    		}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }

	public void showInfoDialog() {
		if (numberScenesinSection == numberThisSceneinSection + 1){
			finalInfoDialog();
		}
		
		else{
			AlertDialog.Builder builder = new AlertDialog.Builder(AndroidHealthActivity.this);
			builder.setMessage("Would you like to continue?")
			       .setCancelable(false)
			       .setPositiveButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
//			        	   //start the scene
//					    	Intent i = new Intent(getApplicationContext(), InfoActivity.class);
//					    	//we get the sceneIndex for the start of the quiz
//					    	i.putExtra("sceneIndex", indexOfFirstScene);
//					    	AndroidHealthActivity.numberThisSceneinSection = 0;
//					    	startActivity(i);
			        	   //instead of restarting the scene, we want to go back to the menu now
			        	   Intent i = new Intent(getApplicationContext(), MyListActivity.class);
			        	   startActivity(i);
			           }
			       })
			       .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   numberThisSceneinSection++;
		    				if (numberScenesinSection > numberThisSceneinSection){
		    					goToNextScene();
		    				}
		    				else {
		    					finalInfoDialog();
		    				}
			           }
			       });
			builder.show();
		}

		
	}
	
	private void finalInfoDialog() {
		// after the quiz is over, ask the user if they would like to retake the quiz
		AlertDialog.Builder builder = new AlertDialog.Builder(AndroidHealthActivity.this);
		builder.setMessage("Would you like to take the quiz, replay all the videos, or exit this section?")
		       .setCancelable(false)
		       .setPositiveButton("Take the Quiz", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		        	 
				    	AndroidHealthActivity.numberThisSceneinSection = 0;
				    	goToNextScene();
		           }
		       })
		      .setNeutralButton("Replay all Videos", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //start the scene
				    	Intent i = new Intent(getApplicationContext(), InfoActivity.class);
				    	//we get the sceneIndex for the start of the quiz
				    	i.putExtra("sceneIndex", indexOfFirstScene);
				    	AndroidHealthActivity.numberThisSceneinSection = 0;
				    	startActivity(i);
		           }
		       })
		       .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
				    	Intent i = new Intent(getApplicationContext(), MyListActivity.class);
				    	startActivity(i);
		           }
		       });
		builder.show();
		
	}
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
    	editor.putString("fileName", mediaLoader.swfFilename);
        // Commit the edits!
        editor.commit();
        mediaLoader.releaseMediaPlayer();
        mediaLoader.doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaLoader.releaseMediaPlayer();
        mediaLoader.doCleanUp();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
	       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	       mediaLoader.swfFilename = settings.getString("fileName", "question.mp4");
	       mediaLoader.loadVideo(mediaLoader.swfFilename);
//	       mediaLoader.loadMedia(null);
	}
    
}
