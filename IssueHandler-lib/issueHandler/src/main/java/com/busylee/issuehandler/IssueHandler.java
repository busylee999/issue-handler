package com.busylee.issuehandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;

/**
 * Created by busylee on 23.10.14.
 */
public class IssueHandler implements Thread.UncaughtExceptionHandler{

    private static final String ISSUE_BOT_PACKAGE_NAME = "com.busylee.issuebot";
    private static final String FILTER_ACTION = "com.busylee.issuebot.redmine.issue";
    public static final String EXTRA_SERVER_URL = "EXTRA_SERVER_URL";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    public static final String EXTRA_THROWABLE = "EXTRA_THROWABLE";

    private Activity mActivity;
    private String mServerUrl;
    private String mFileUrl;
    private boolean mIgnoreDebugMode = false;
    private static IssueHandler INSTANCE = new IssueHandler();

    public static void init(String serverUrl) {
        INSTANCE.mServerUrl = serverUrl;
    }

    public static void init(String serverUrl, String fileUrl) {
        init(serverUrl); INSTANCE.mFileUrl = fileUrl;
    }

    public static void setIgnoreDebugMode(boolean ignoreDebugMode) {
        INSTANCE.mIgnoreDebugMode = ignoreDebugMode;
    }

    public static void onActivityCreate(Activity activity) {
        if(isApplicationDebuggable(activity) || INSTANCE.mIgnoreDebugMode) {

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

    private static boolean isApplicationDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

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
							intent.putExtra(EXTRA_SERVER_URL, mServerUrl);
							intent.putExtra(EXTRA_THROWABLE, throwable);
							if(mFileUrl != null)
								intent.putExtra(EXTRA_FILE_PATH, mFileUrl);
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

    private static boolean isIssueBotInstalled(Activity activity) {
        return checkPackageInstalled(activity, ISSUE_BOT_PACKAGE_NAME);
    }

    private static boolean checkPackageInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        for(PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if(packageInfo.packageName.equals(packageName))
                return true;
        }
        return false;
    }

}
