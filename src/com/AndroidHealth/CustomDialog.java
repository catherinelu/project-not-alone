package com.AndroidHealth;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	
	public CustomDialog(Context context, int theme) {
		super(context, theme);
		
	}	
	
	
	public static class Builder {
		private TextView text;
		private Context context;
		boolean answer;
			
		public Builder(Context context, boolean answer) {
			this.context = context;
			this.answer = answer;
		}
			
		public Builder setMessage(String message) {
			text.setText(message);
			return this;
		}
		
		public Builder setMessage(int message) {
			this.text.setText((String) context.getText(message));
			return this;
		}
			
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialog);
			
			View layout = inflater.inflate(R.layout.alert_dialog_layout, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			
			text = (TextView) layout.findViewById(R.id.alertText);

			if(answer == false) {
				text.setTextColor(Color.YELLOW);
				text.setText("Try Again!!");
			} else {
				text.setTextColor(Color.GREEN);
				text.setText("Correct!!");
			}
			dialog.setContentView(layout);
			return dialog;
		}
		

	}
	
}

	