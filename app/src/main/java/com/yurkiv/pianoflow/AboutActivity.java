package com.yurkiv.pianoflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

	@InjectView(R.id.about_toolbar)Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		ButterKnife.inject(this);

		if (toolbar!=null){
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@OnClick(R.id.tv_site)
	protected void goToSite(){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("http://pianoflow.tk/"));
		startActivity(i);		
	}

	@OnClick(R.id.tv_email)
	protected void sendEmail(){
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto","pianoflowproject@gmail.com", null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}
}
