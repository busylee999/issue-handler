package com.busylee.issuehandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by busylee on 23.10.14.
 */
public class IssueHandler implements Thread.UncaughtExceptionHandler{

    private static final String LOG_TAG = "IssueHandler";

    private static final String ISSUE_BOT_PACKAGE_NAME = "com.busylee.issuebot";
    private static final String FILTER_ACTION = "com.busylee.issuebot.redmine.issue";
    public static final String EXTRA_SERVER_URL = "EXTRA_SERVER_URL";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    public static final String EXTRA_THROWABLE = "EXTRA_THROWABLE";

    private static IssueHandler INSTANCE = new IssueHandler();
    private static IssueHandlerConfiguration CONFIGURATION;

    private Activity mActivity;

    @Deprecated
    public static void init(String serverUrl) {
        CONFIGURATION.mServerUrl = serverUrl;
    }

    @Deprecated
    public static void init(String serverUrl, String fileUrl) {
        init(serverUrl); CONFIGURATION.mFileUrl = fileUrl;
    }

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
        init(new IssueHandlerConfiguration(issueHandlerSetup, filePath));
    }

    /**
     * Storing parsed configuration parameters
     * @param issueHandlerConfiguration
     */
    private static void init(IssueHandlerConfiguration issueHandlerConfiguration) {
        CONFIGURATION = issueHandlerConfiguration;
    }

    /**
     * Callback for handling new activity appearing. Dialog require Activity context for showing.
     * @param activity
     */
    public static void onActivityCreate(Activity activity) {
        if(isApplicationDebuggable(activity) || CONFIGURATION.ignoreMode) {

            INSTANCE.mActivity = activity;

            if (Thread.getDefaultUncaughtExceptionHandler() instanceof IssueHandler)
                return;

            Thread.setDefaultUncaughtExceptionHandler(INSTANCE);

			if(!isIssueBotInstalled(activity)) {
				showIssueBotInstallDialog(activity);
				return;
			}
        }
    }

    /**
     * Check is application debuggable
     * @param context
     * @return
     */
    private static boolean isApplicationDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    /**
     * Catching exception and showing alert dialog to ask user if he wants to create issue.
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {


        (new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

				if(!isIssueBotInstalled(mActivity)) {
					showIssueBotInstallDialog(mActivity);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
					builder.setTitle(R.string.issue_dialog_title);
					builder.setMessage(R.string.issue_dialog_message);
					builder.create();
					builder.setPositiveButton(R.string.issue_dialog_positive_button, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							Intent intent = new Intent(FILTER_ACTION);
							intent.putExtra(EXTRA_SERVER_URL, CONFIGURATION.mServerUrl);
							intent.putExtra(EXTRA_THROWABLE, throwable);
							if(!TextUtils.isEmpty(CONFIGURATION.mFileUrl))
								intent.putExtra(EXTRA_FILE_PATH, CONFIGURATION.mFileUrl);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							mActivity.startActivity(intent);
							mActivity.moveTaskToBack(true);
							System.exit(0);
						}
					});
					builder.setNegativeButton(R.string.issue_dialog_negative_button, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mActivity.moveTaskToBack(true);
							System.exit(0);
						}
					});
					builder.show();
				}

                Looper.loop();
            }
        })).start();
    }

    /**
     * Showing information dialog, that this application uses Issue Bot application for posting
     * issues to bug tracker. Asking user if he wants to install it from google play.
     * @param activity
     */
    private static void showIssueBotInstallDialog(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(R.string.issuebot_not_installed_dialog_message);

        alertDialogBuilder.setPositiveButton(R.string.issuebot_not_installed_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + ISSUE_BOT_PACKAGE_NAME));
                activity.startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.issuebot_not_installed_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.show();
    }

    /**
     * Checking for Issue Bot application is installed
     * @param activity
     * @return
     */
    private static boolean isIssueBotInstalled(Activity activity) {
        return checkPackageInstalled(activity, ISSUE_BOT_PACKAGE_NAME);
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
        private boolean ignoreMode = false;

        IssueHandlerConfiguration(IssueHandlerSetup issueHandlerSetup) {
            mServerUrl = issueHandlerSetup.serverUrl();
            mFileUrl = issueHandlerSetup.filePath();
            ignoreMode = issueHandlerSetup.ignoreMode();
        }

        IssueHandlerConfiguration(IssueHandlerSetup issueHandlerSetup, String filePath) {
            this(issueHandlerSetup);

            if(!TextUtils.isEmpty(filePath))
                mFileUrl = filePath;
        }
    }

}
