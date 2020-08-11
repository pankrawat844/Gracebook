package com.grace.book.crashreport;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CrashActivity extends AppCompatActivity {
	private String STACKTRACE= "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crash_activity);
		STACKTRACE=getIntent().getStringExtra("STACKTRACE");


		final TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText("Sorry, Something went wrong. \nPlease send error logs to developer.");



		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				sendErrorMail(getApplicationContext(),STACKTRACE);
				//checkFileUploadPermissions();
				// so it will first save the error trace in vm folder of parent directory of SD card

			}
		});

	}
	final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
	List<String> permissions = new ArrayList<String>();

	public void checkFileUploadPermissions() {
		permissions.clear();
		if (Build.VERSION.SDK_INT > 22) {
			String storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
			String storagePermission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;

			int hasstoragePermission = checkSelfPermission(storagePermission);
			int hasstoragePermission2 = checkSelfPermission(storagePermission2);

			if (hasstoragePermission != PackageManager.PERMISSION_GRANTED) {
				permissions.add(storagePermission);
			}

			if (hasstoragePermission2 != PackageManager.PERMISSION_GRANTED) {
				permissions.add(storagePermission2);
			}

			if (!permissions.isEmpty()) {
				String[] params = permissions.toArray(new String[permissions.size()]);
				requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS_AGENT);
			} else {
				loginVerification();
			}
		} else
			loginVerification();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_PERMISSIONS_AGENT:
				if (grantResults.length > 0) {
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						loginVerification();
					}
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void loginVerification() {
		String filePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/vm/" + ".errorTrace.txt";
		sendErrorMail(CrashActivity.this, filePath);
		finish();
	}

	/**
	 * This list a set of application which can send email. 
	 * Here user have to pick one apps via email will be send to developer email id.
	 * @param
	 * @param filePath
	 */
	private void sendErrorMail(Context mContext, String filePath) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		String subject = "Error Description"; // here subject
		String body = "Sorry for your inconvenience .\nWe assure you that we will solve this problem as soon possible."
				+ "\n\nThanks for using app."; // here email body

		sendIntent.setType("plain/text");
		sendIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "prosanto.mbstu@gmail.com" }); // your developer email id
		sendIntent.putExtra(Intent.EXTRA_TEXT, filePath);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//		sendIntent.putExtra(Intent.EXTRA_STREAM,
//				Uri.fromFile(new File(filePath)));
		//sendIntent.setType("message/rfc822");
		mContext.startActivity(Intent.createChooser(sendIntent, "Complete action using"));
	}
}
