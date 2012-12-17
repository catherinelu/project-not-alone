package com.AndroidHealth;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AndroidHealthActivity {

    int sceneIndex;

	/** Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.info);

        Bundle passedData = getIntent().getExtras();
        
        if(passedData == null) {
        	sceneIndex = setSceneIndex(0);
        } else {
        	sceneIndex = setSceneIndex((Integer) passedData.get("sceneIndex"));
        }
        
    	TypedArray ta = getResources().obtainTypedArray(R.array.scenes);
    	int id = ta.getResourceId(sceneIndex, 0);
    	String[] sceneContents = getResources().getStringArray(id);
    	
    	String swfFilename = sceneContents[0];
    	String[] mp3Filenames = {sceneContents[1]};
    	String textId = sceneContents[2];
    	
    	numberScenesinSection = Integer.valueOf(sceneContents[3]);
    	
    	MediaLoader3 mediaLoader = setMediaLoader(new MediaLoader3 (swfFilename, mp3Filenames, false, this));
    	mediaLoader.loadMedia(savedInstanceState);
    	
    	TextView tv = (TextView) findViewById(R.id.infoTextView);
    	tv.setText(getResources().getText(getResources().getIdentifier(textId, "string", getPackageName())));
    	tv.setTextSize(18);
    }

}
