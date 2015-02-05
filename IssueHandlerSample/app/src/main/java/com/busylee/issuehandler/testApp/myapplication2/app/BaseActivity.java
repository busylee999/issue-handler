package com.busylee.issuehandler.testApp.myapplication2.app;

import android.app.Activity;
import android.os.Bundle;
import com.busylee.issuehandler.IssueHandler;

/**
 * Created by busylee on 05.02.15.
 */
public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IssueHandler.onActivityCreate(this);

	}
}
