package com.busylee.issuehandler;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by busylee on 23.10.14.
 */
public class IssueHandler implements Thread.UncaughtExceptionHandler{

	public static final String ISSUE_BOT_PACKAGE_NAME = "com.busylee.issuebot";

    private static final String LOG_TAG = "IssueHandler";
	private static final int ALARM_DELAY = 500; //500ms

    private static IssueHandler INSTANCE = new IssueHandler();
    private static IssueHandlerConfiguration CONFIGURATION;

    private Application mContext;

    /**
     * Initialization of IssueHandler. It if Application class has no @IssueHandlerSetup
     * annotation, it logs appropriate error.
     * If file path is specified in @IssueHandlerSetup annotation, assumed that file located in
     * application private directory. This file must be marked as readable for another application.
     * @param application
     */
    public static void init(Application application) {
        init(application, null);
    }

    /**
     * Initialization of IssueHandler. It if Application class has no @IssueHandlerSetup
     * annotation, it logs appropriate error.
     * It stores file location, which will be attached to issue, if exception will occur.
     * Param filePath passed into function overrides filePath specified in @IssueHandlerSetup
     * annotation
	 * Check if IssueBot application is installed then set instance of handler as
	 * default uncaught exception handler, otherwise show dialog for install IssueBot app.
     * @param application
     * @param filePath file path to file will be attached to issue
     */
    public static void init(Application application, String filePath) {
        final IssueHandlerSetup issueHandlerSetup = application.getClass().getAnnotation(IssueHandlerSetup.class);
        if (issueHandlerSetup == null) {
            Log.e(LOG_TAG,
                    "Issue handler missed annotation @IssueHandlerSetup");
            return;
        }

        if(isApplicationDebuggable(application) || CONFIGURATION.mIgnoreMode) {

            if (Thread.getDefaultUncaughtExceptionHandler() instanceof IssueHandler)
                return;

            if(!isIssueBotInstalled(application))
                showIssueBotInstallDialog(application);

			INSTANCE.mContext = application;

			final String serverUrl = issueHandlerSetup.serverUrl();
			final boolean ignoreMode = issueHandlerSetup.ignoreMode();

			String resultFilePath = null;
			if(!TextUtils.isEmpty(filePath)) {
				resultFilePath = filePath;
			} else {
				String filePathSetup = issueHandlerSetup.filePath();
				if(!TextUtils.isEmpty(filePathSetup))
					resultFilePath = application.getFilesDir().getAbsolutePath() + filePathSetup;
			}

			init(new IssueHandlerConfiguration(serverUrl, ignoreMode, resultFilePath));

			Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
        } else {
			Log.i(LOG_TAG,
					"Application not in debug mode");
		}

    }

    /**
     * Storing parsed configuration parameters
     * @param issueHandlerConfiguration
     */
    private static void init(IssueHandlerConfiguration issueHandlerConfiguration) {
        CONFIGURATION = issueHandlerConfiguration;
    }

    /**
     * Check is application debuggable
     * @param context
     * @return
     */
    private static boolean isApplicationDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

	private Context getContext() {
		return mContext;
	}
    /**
     * Catching exception and showing alert dialog to ask user if he wants to create issue.
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {

		Intent crashedIntent = new Intent(getContext(), IssueHandlerActivity.class);
		crashedIntent.setAction(IssueHandlerActivity.ACTION_ISSUE);
		crashedIntent.putExtra(IssueHandlerActivity.EXTRA_SERVER_URL, CONFIGURATION.mServerUrl);
		crashedIntent.putExtra(IssueHandlerActivity.EXTRA_THROWABLE, throwable);
		if(!TextUtils.isEmpty(CONFIGURATION.mFileUrl))
			crashedIntent.putExtra(IssueHandlerActivity.EXTRA_FILE_PATH, CONFIGURATION.mFileUrl);

		crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		crashedIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		PendingIntent intent = PendingIntent.getActivity(getContext(), 0, crashedIntent, crashedIntent.getFlags());
		AlarmManager mgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + ALARM_DELAY, intent);

		System.exit(0);
    }

    /**
     * Showing information dialog, that this application uses Issue Bot application for posting
     * issues to bug tracker. Asking user if he wants to install it from google play.
     * @param context
     */
    private static void showIssueBotInstallDialog(final Context context) {
		Intent issueBotInstallIntent = new Intent(context, IssueHandlerActivity.class);
		issueBotInstallIntent.setAction(IssueHandlerActivity.ACTION_BOT_APPLICATION);

		PendingIntent intent = PendingIntent.getActivity(context, 0, issueBotInstallIntent, issueBotInstallIntent.getFlags());
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + ALARM_DELAY, intent);
    }

    /**
     * Checking for Issue Bot application is installed
     * @param context
     * @return
     */
    static boolean isIssueBotInstalled(Context context) {
        return checkPackageInstalled(context, ISSUE_BOT_PACKAGE_NAME);
    }

    /**
     * CHeck package is installed
     * @param context
     * @param packageName
     * @return
     */
    private static boolean checkPackageInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        for(PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if(packageInfo.packageName.equals(packageName))
                return true;
        }
        return false;
    }

    /**
     * Class for storing information about IssueHandler configuration
     */
    private static class IssueHandlerConfiguration {

        private String mServerUrl;
        private String mFileUrl;
        private boolean mIgnoreMode = false;

        IssueHandlerConfiguration(String serverUrl, boolean ignoreMode, String filePath) {
            mServerUrl = serverUrl;
            mIgnoreMode = ignoreMode;
            mFileUrl = filePath;
        }
    }

}
