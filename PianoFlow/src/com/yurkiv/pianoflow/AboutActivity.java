package com.yurkiv.pianoflow;

import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {

	private TextView siteText;
	private Button feedbackButton;
	private FeedbackDialog feedBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);		
		initFeddback();
		siteText=(TextView) findViewById(R.id.siteText);
		feedbackButton=(Button) findViewById(R.id.feedbackButton);
		
		String siteString="pianoflow.tk";
		siteText.setText(Html.fromHtml(siteString));
		
		siteText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToSite("http://pianoflow.tk/");
			}
		});
		
		feedbackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				feedBack.show();
			}
		});
	}
	
	private void initFeddback(){		
		FeedbackSettings feedbackSettings = new FeedbackSettings();
		//SUBMIT-CANCEL BUTTONS
		feedbackSettings.setCancelButtonText("Cancel");
		feedbackSettings.setSendButtonText("Send");
		//DIALOG TEXT
		feedbackSettings.setText("Write about ideas or mistakes:");
		feedbackSettings.setYourComments("Type your message here...");
		feedbackSettings.setTitle("Feedback");
		//TOAST MESSAGE
		feedbackSettings.setToast("Thank you so much!");
		//RADIO BUTTONS
		feedbackSettings.setRadioButtons(true); // Disables radio buttons
		feedbackSettings.setBugLabel("Bug");
		feedbackSettings.setIdeaLabel("Idea");
		feedbackSettings.setQuestionLabel("Question");
		feedBack = new FeedbackDialog(this, "AF-7BD2AB3FC0F0-C0", feedbackSettings);		
	}
	
	private void goToSite(String url){
		//String url = "http://www.example.com";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);		
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    feedBack.dismiss();
	}
}
