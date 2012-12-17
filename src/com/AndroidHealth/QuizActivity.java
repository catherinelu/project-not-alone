package com.AndroidHealth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class QuizActivity extends AndroidHealthActivity {
	
	private static final int ANSWER_TEXT_SIZE = 30;
	int sceneIndex;
    
    /** Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	setContentView(R.layout.quiz);
        Bundle passedData = getIntent().getExtras();
        
        if(passedData == null) {
        	sceneIndex = setSceneIndex(0);
        } else {
    		sceneIndex = setSceneIndex((Integer) passedData.get("sceneIndex"));
        }
        
    	TypedArray ta = getResources().obtainTypedArray(R.array.scenes);
    	int id = ta.getResourceId(sceneIndex, 0);
    	String[] sceneContents = getResources().getStringArray(id);

    	String answerFilename = sceneContents[0];
    	String swfFilename = sceneContents[1];
    	String[] mp3Filenames = {sceneContents[2]};
    	numberScenesinSection = Integer.valueOf(sceneContents[3]);
    	
    	MediaLoader3 mediaLoader = setMediaLoader(new MediaLoader3(swfFilename, mp3Filenames, true, this));
    	mediaLoader.loadMedia(savedInstanceState);
    	loadAnsButtons(answerFilename);
    }

	private void addFirstButton(int buttonId, Stack<String> buttonTextStack, RelativeLayout frame, 
    							int correctAnswNum, int answerFromStack) {
    	
    	Button button = new Button(this);
    	button.setText(buttonTextStack.pop());
    	button.setId(buttonId);
    	button.setTextSize(ANSWER_TEXT_SIZE);
    	if (answerFromStack == correctAnswNum) {
    		button.setOnClickListener(new CorrectAnswerListener());
    	} else {
    		button.setOnClickListener(new WrongAnswerListener());
    	}
    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    	button.setLayoutParams(params);
    	frame.addView(button);
    }
    
    
    public void loadAnsButtons(String answFile) {
    	int correctAnswNum;
    	Stack<String> buttonTextStack = new Stack<String>();
    	RelativeLayout frame = (RelativeLayout) findViewById(R.id.relButtons);
    	try {
    		AssetManager assetMan = getAssets();
    		InputStream is = assetMan.open(answFile);
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		String line = br.readLine();
    		if(line != null) {
    			correctAnswNum = Integer.parseInt(line);
    			line = br.readLine();
    		} else {
    			return;
    		}
    		while(line != null) {
    			buttonTextStack.push(line);
    			line = br.readLine();
    		}
    	} catch(Exception ex) {
    		Log.e(getTag(), "Did not set up this answer file correctly: " + answFile);
    		return;
    	}
    	int answerFromStack = buttonTextStack.size();
    	
    	int buttonId = 1;
    	if(!buttonTextStack.empty()) {
    		addFirstButton(buttonId, buttonTextStack, frame, correctAnswNum, answerFromStack);
    		answerFromStack--;
    	}
    	while(!buttonTextStack.empty()) {
    		Button button = new Button(this);
        	button.setText(buttonTextStack.pop());
        	button.setTextSize(ANSWER_TEXT_SIZE);
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        	params.addRule(RelativeLayout.ABOVE, buttonId);
        	buttonId++;
        	
        	button.setId(buttonId);
        	if (answerFromStack == correctAnswNum) {
        		button.setOnClickListener(new CorrectAnswerListener());
        	} else {
        		button.setOnClickListener(new WrongAnswerListener());
        	}
        	button.setLayoutParams(params);
        	frame.addView(button);
        	answerFromStack--;
    	}
    }
    
    public class CorrectAnswerListener implements OnClickListener {
    	@Override
    	public void onClick(View v) {
    		motivationalToasts();
    		numberThisSceneinSection++;
    		CustomDialog.Builder builder = new CustomDialog.Builder(QuizActivity.this, true);
    		final Dialog dialog;
    		dialog = builder.create();
    		dialog.show();

    		
    		Handler handler = new Handler();
    		handler.postDelayed(new Runnable() {
    			public void run() {
    				dialog.dismiss();
    				mediaLoader.finishAudio();
    				if (numberScenesinSection > numberThisSceneinSection){
    					goToNextScene();
    				}
    				else {
    					takeAgainDialog();
    				}
    			}
    		}, 1000);
    	}
    	
    	private void takeAgainDialog() {
    		//we want to display a different dialog if it's an info quiz
    		if (infoquiz){
    			AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
    			builder.setMessage("Would you like to retake the quiz, restart this section, or exit?")
    			       .setCancelable(false)
    			       .setPositiveButton("Retake Quiz", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			        	   //start the scene
    					    	Intent i = new Intent(getApplicationContext(), QuizActivity.class);
    					    	numberInInfoQuizSection = numberScenesinSection;
    					    	//we get the sceneIndex for the start of the quiz
    					    	//we get the number of scenes in the previous info section
    					    	i.putExtra("sceneIndex", (indexOfFirstScene + numberInInfoQuizSection));
    					    	AndroidHealthActivity.numberThisSceneinSection = 0;
    					    	startActivity(i);
    			           }
    			       })
    			      .setNeutralButton("Restart Section", new DialogInterface.OnClickListener() {
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
    		else{
    			// after the quiz is over, ask the user if they would like to retake the quiz
    			AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
    			builder.setMessage("Would you like to retake this quiz?")
    			       .setCancelable(false)
    			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			        	   //start the scene
    					    	Intent i = new Intent(getApplicationContext(), QuizActivity.class);
    					    	//we get the sceneIndex for the start of the quiz
    					    	i.putExtra("sceneIndex", indexOfFirstScene);
    					    	AndroidHealthActivity.numberThisSceneinSection = 0;
    					    	startActivity(i);
    			           }
    			       })
    			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    					    	Intent i = new Intent(getApplicationContext(), MyListActivity.class);
    					    	startActivity(i);
    			           }
    			       });
    			builder.show();
    		}

			
		}

		private void motivationalToasts (){
//    		increment the number of correctly answered questions
    		numberAnsweredCorrect++;
    		if (numberAnsweredCorrect == 2) {
    			Toast.makeText(getApplicationContext(), "You've gotten 2 correct in a row!" +
    					System.getProperty("line.separator") + "Keep it up!", Toast.LENGTH_SHORT).show();
    		}
    		if (numberAnsweredCorrect == 5) {
    			Toast.makeText(getApplicationContext(), "Wow, you've gotten 5 correct in a row!", Toast.LENGTH_SHORT).show();
    		}
    		if (numberAnsweredCorrect == 10) {
    			Toast.makeText(getApplicationContext(), "You've gotten 10 correct in a row! " +
    					System.getProperty("line.separator") + "That's Amazing!", 
    					Toast.LENGTH_SHORT).show();
    		}
    	}
    }
    
    public class WrongAnswerListener implements OnClickListener { 	
    	@Override
    	public void onClick(View v) {
//    		if a question is answered incorectly the numberAnsweredCorrect variable is set to 0
    		numberAnsweredCorrect = 0;
    		CustomDialog.Builder builder = new CustomDialog.Builder(QuizActivity.this, false);
    		final Dialog dialog;
    		dialog = builder.create();
    		dialog.show();
    		Handler handler = new Handler();
    		handler.postDelayed(new Runnable() {
    			public void run() {
    				dialog.dismiss();
    			}
    		}, 1000);
    	}
    }
  
 



    
}