package com.busylee.issuehandler.testApp.myapplication2.app;

import android.app.Application;
import com.busylee.issuehandler.IssueHandler;

/**
 * Created by busylee on 05.02.15.
 */
public class CustomApplication extends Application {

	private static final String YOUR_REDMINE_SERVER = "";

	@Override
	public void onCreate() {
		super.onCreate();

		IssueHandler.init(YOUR_REDMINE_SERVER);
	}
}
