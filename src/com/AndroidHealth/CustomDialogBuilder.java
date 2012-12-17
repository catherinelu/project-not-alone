package com.AndroidHealth;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class CustomDialogBuilder extends AlertDialog.Builder {
	private TextView text;
	

	public CustomDialogBuilder(Context context, boolean answer) {
		super(context);
		
		View layout = View.inflate(context, R.layout.alert_dialog_layout, null);
		text = (TextView) layout.findViewById(R.id.alertText);
		if(answer == false) {
			text.setTextColor(Color.YELLOW);
			text.setText("Try Again!!");
		} else {
			text.setTextColor(Color.GREEN);
			text.setText("Correct!!");
		}
		setView(layout);
	}
}
	