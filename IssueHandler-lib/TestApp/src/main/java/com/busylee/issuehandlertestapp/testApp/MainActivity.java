package com.busylee.issuehandlertestapp.testApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.a_main_activity);

		inflateViews();
	}

	private void inflateViews() {
		findViewById(R.id.btn_test_exception).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int i = 1 / 0;
			}
		});

		findViewById(R.id.btn_start_another_activity).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, MainActivity.class));
			}
		});
	}
}
