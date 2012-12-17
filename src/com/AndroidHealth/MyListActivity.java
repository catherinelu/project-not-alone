package com.AndroidHealth;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyListActivity extends ListActivity{
	
	
	  /** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  
		  //you can add to or edit the scene names in the scene_structure.xml file
		  String[] sceneNames = getResources().getStringArray(R.array.title_names);
//		  String[] sceneNames = {"Introductory Quiz", "Symptoms", "Diagnosis", "Causes", 
//				  "Treatment", "End of Module Quiz"};
		  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, sceneNames));
		  ListView lv = getListView();
		  lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			    	final int positionFinal;
			    	final String title;
			    	final String positive;
			    	final String negative;
			    	final boolean info;
			    	final String secondTitle;
			    	switch (position) {
			    	// needs to open the Introductory Quiz
		            case 0:
//		            	each of these should be the (scene_#) -1 so to start on scene_1, enter 0
		            	positionFinal = 0;
		            	title = "Would you like to take the Introductory Quiz?";
		            	positive = "Yes";
		            	negative = "No";
		            	info = false;
		            	secondTitle = null;
		            	setDialog(title, positive, negative, positionFinal, info, secondTitle);
		                break;
			    	// needs to open the Symptoms Info
		            case 1:
//		            	 to start on scene_6
		            	positionFinal = 5;
		            	title = "Would you like to learn about the Symptoms of Depression?";
		            	positive = "Yes";
		            	negative = "No";
		            	info = true;
		            	secondTitle = setSecondTitle("symptoms");
		            	setDialog(title, positive, negative, positionFinal, info, secondTitle);
		                break;
				    // needs to open the Diagnosis Info
		            case 2:  
//		            	to start on scene_15
		            	positionFinal = 14;
		            	title = "Would you like to learn about the Diagnosis of Depression?";
		            	positive = "Yes";
		            	negative = "No";
		            	info = true;
		            	secondTitle = setSecondTitle("diagnosis");
		            	setDialog(title, positive, negative, positionFinal, info, secondTitle);
		                break;
					// needs to open the Causes Info
		            case 3:  
//		            	to start on scene_19
		            	positionFinal = 18;
		            	title = "Would you like to learn about the Causes of Depression?";
		            	positive = "Yes";
		            	negative = "No";
		            	info = true;
		            	secondTitle = setSecondTitle("causes");
		            	setDialog(title, positive, negative, positionFinal, info, secondTitle);
		                break;
					// needs to open the Treatment Info
		            case 4:  
//		            	to start on scene_28
		            	positionFinal = 27;
		            	title = "Would you like to learn about the Treatment of Depression?";
		            	positive = "Yes";
		            	negative = "No";
		            	info = true;
		            	secondTitle = setSecondTitle("treatment");
		            	setDialog(title, positive, negative, positionFinal, info, secondTitle);
		                break; 
				    // needs to open the Final Quiz
		            case 5:  
//		            	to start on scene_36
		            	positionFinal = 35;
		            	title = "Would you like to take the Final Quiz?";
		            	positive = "Yes";
		            	negative = "No";
		            	info = false;
		            	secondTitle = null;
		            	setDialog(title, positive, negative, positionFinal, info, secondTitle);
		                break;
			    	}


			    }


			  });
		  
		}
		
		protected String setSecondTitle(String string) {
			return "A series of videos will be played which explain different aspects" +
					" of the " + string + " of depression, and at the end a short quiz will test what" +
					" you have learned.";
	}

		private void setDialog(String title, String positive,
				String negative, final int positionFinal, final boolean info, 
				final String secondTitle) {
			if (info){
				AndroidHealthActivity.infoquiz = true;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(MyListActivity.this);			
			builder.setMessage(title)
				.setPositiveButton(positive, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   if (info){
			        		   AlertDialog.Builder builder1 = new AlertDialog.Builder(MyListActivity.this);
			        		   builder1.setMessage(secondTitle)
			        		          .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        		              public void onClick(DialogInterface dialog, int id) {
										    	Intent i = new Intent(getApplicationContext(), InfoActivity.class);
										    	//this will send the positionFinal as the scene to open
										    	i.putExtra("sceneIndex", positionFinal);
										    	AndroidHealthActivity.numberScenesinSection = 0;
										    	AndroidHealthActivity.numberThisSceneinSection = 0;
										    	AndroidHealthActivity.indexOfFirstScene = positionFinal;
										    	startActivity(i);
			        		              }
			        		          });
			        		   builder1.show();
			        	   }
			        	   else{
			        		   //start the quiz
			        		   Intent i = new Intent(getApplicationContext(), QuizActivity.class);
			        		   //this will send the positionFinal as the scene to open
			        		   i.putExtra("sceneIndex", positionFinal);
			        		   AndroidHealthActivity.numberScenesinSection = 0;
			        		   AndroidHealthActivity.numberThisSceneinSection = 0;
			        		   AndroidHealthActivity.indexOfFirstScene = positionFinal;
			        		   startActivity(i);


			        	   }

			           }
			       })
			       .setNegativeButton(negative, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			builder.show();
			
		}

}
