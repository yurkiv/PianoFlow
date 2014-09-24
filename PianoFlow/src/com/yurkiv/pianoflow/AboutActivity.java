package com.yurkiv.pianoflow;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

	private TextView siteText;
	private TextView mailtoText;
	private TextView sourcesText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		siteText=(TextView) findViewById(R.id.siteText);
		mailtoText=(TextView) findViewById(R.id.mailtoText);
		sourcesText=(TextView) findViewById(R.id.sourcesText);
		
		String siteString="<u>http://pianoflow.tk/</u>";
		siteText.setText(Html.fromHtml(siteString));
		
		String mailtoString="<u>jurkiw.misha@gmail.com</u>";
		mailtoText.setText(Html.fromHtml(mailtoString));
		
		String sourcesString="<u>www.github.com/zloysalat</u>";
		sourcesText.setText(Html.fromHtml(sourcesString));
		
		siteText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToSite("http://pianoflow.tk/");
			}
		});
		
		mailtoText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendFeddback();
			}
		});
		
		sourcesText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToSite("https://github.com/zloysalat/PianoFlow");
			}
		});
	}
	
	private void sendFeddback(){
		String report="Enter Feedback..";
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jurkiw.misha@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
		i.putExtra(Intent.EXTRA_TEXT   , report);
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(AboutActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void goToSite(String url){
		//String url = "http://www.example.com";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);		
	}
}
