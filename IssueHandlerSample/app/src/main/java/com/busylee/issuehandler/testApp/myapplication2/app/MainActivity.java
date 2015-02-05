package com.busylee.issuehandler.testApp.myapplication2.app;

import android.os.Bundle;
import android.view.View;


public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.tv_generate_exception).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int i = 1 / 0;
			}
		});
	}

}
