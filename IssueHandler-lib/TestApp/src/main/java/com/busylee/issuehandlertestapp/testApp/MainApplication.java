package com.busylee.issuehandlertestapp.testApp;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import com.busylee.issuehandler.IssueHandler;
import com.busylee.issuehandler.IssueHandlerSetup;

import java.io.*;

/**
 * Created by busylee on 05.02.15.
 */
@IssueHandlerSetup(
        serverUrl = "http://tryremember.ru:3000"
)
public class MainApplication extends Application {

	private static final String ATTACH_FILE_NAME = "/attach.txt";

	@Override
	public void onCreate() {
		super.onCreate();
		IssueHandler.init(this, getFilePath());

		modifyFileWeNeedTobeAttached();
	}

	private String getFilePath() {
		return getFilesDir().getAbsolutePath() + ATTACH_FILE_NAME;
	}

	private void modifyFileWeNeedTobeAttached() {
		PrintWriter out = null;
		try {
			File file = getFileFromPath(getFilePath());
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			out.println("test string");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				out.close();
		}
	}

	private static File getFileFromPath(String path) {
		boolean ret;
		File file = null;
		if (TextUtils.isEmpty(path)) {
			Log.e("Error", "The path of Log file is Null.");
			return file;
		}
		file = new File(path);
		if (file.exists()) {
			if (! file.canWrite())
				Log.e("Error", "The Log file can not be written.");
		} else {
			//create the log file
			try {
				ret = file.createNewFile();
				if (ret) {
					Log.i("Success", "The Log file was successfully created! -" + file.getAbsolutePath());
				} else {
					Log.i("Success", "The Log file exist! -" + file.getAbsolutePath());
				}
				if (!file.canWrite()) {
					Log.e("Error", "The Log file can not be written.");
				}
			} catch (IOException e) {
				Log.e("Error", "Failed to create The Log file.");
				e.printStackTrace();
			}
		}

		// please do not forget to make file you want to upload visible for other application
		file.setReadable(true, false);

		return file;
	}
}
