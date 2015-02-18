package com.busylee.issuehandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by busylee on 11.02.15.
 */
public class IssueHandlerActivity extends Activity {

	private static final Uri ISSUE_BOT_PLAY_URI = Uri.parse("market://details?id=" + IssueHandler.ISSUE_BOT_PACKAGE_NAME);
    private static final String FILTER_ACTION = "com.busylee.issuebot.redmine.issue";

    public static final String EXTRA_SERVER_URL = "EXTRA_SERVER_URL";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    public static final String EXTRA_THROWABLE = "EXTRA_THROWABLE";

	public static final String ACTION_ISSUE = "ACTION_ISSUE";
	public static final String ACTION_BOT_APPLICATION = "ACTION_BOT_APPLICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getWindow().addFlags(Window.FEATURE_NO_TITLE);

		final String action = getIntent().getAction();

		final boolean isIssueBotInstalled = IssueHandler.isIssueBotInstalled(this);

		if(ACTION_ISSUE.equals(action) && isIssueBotInstalled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.issue_dialog_title);
			builder.setMessage(R.string.issue_dialog_message);
			builder.create();
			builder.setPositiveButton(R.string.issue_dialog_positive_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					Intent intent = new Intent(FILTER_ACTION);
					intent.putExtra(EXTRA_SERVER_URL, getIntent().getStringExtra(EXTRA_SERVER_URL));
					intent.putExtra(EXTRA_THROWABLE, getIntent().getSerializableExtra(EXTRA_THROWABLE));
					intent.putExtra(EXTRA_FILE_PATH, getIntent().getStringExtra(EXTRA_FILE_PATH));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();

				}
			});
			builder.setNegativeButton(R.string.issue_dialog_negative_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					finish();
				}
			});
			builder.show();
		} else if(ACTION_BOT_APPLICATION.equals(action) || !isIssueBotInstalled) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setMessage(R.string.issuebot_not_installed_dialog_message);

			alertDialogBuilder.setPositiveButton(R.string.issuebot_not_installed_dialog_positive_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(ISSUE_BOT_PLAY_URI);
					startActivity(intent);
					finish();
				}
			});

			alertDialogBuilder.setNegativeButton(R.string.issuebot_not_installed_dialog_negative_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					finish();
				}
			});

			alertDialogBuilder.show();
		} else
			finish();

    }


}
