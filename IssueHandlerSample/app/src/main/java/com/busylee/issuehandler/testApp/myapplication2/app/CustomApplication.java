package com.busylee.issuehandler.testApp.myapplication2.app;

import android.app.Application;
import com.busylee.issuehandler.IssueHandler;
import com.busylee.issuehandler.IssueHandlerSetup;

/**
 * Created by busylee on 05.02.15.
 */

@IssueHandlerSetup(
        serverUrl = "/your/redmine/server/url"
)
public class CustomApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		IssueHandler.init(this);
	}
}
