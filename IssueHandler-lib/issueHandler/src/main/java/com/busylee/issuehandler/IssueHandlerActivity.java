package com.busylee.issuehandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by busylee on 11.02.15.
 */
public class IssueHandlerActivity extends Activity {

    private static final String FILTER_ACTION = "com.busylee.issuebot.redmine.issue";
    public static final String EXTRA_SERVER_URL = "EXTRA_SERVER_URL";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    public static final String EXTRA_THROWABLE = "EXTRA_THROWABLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("213", "sfbfb");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.issue_dialog_title);
        builder.setMessage(R.string.issue_dialog_message);
        builder.create();
        builder.setPositiveButton(R.string.issue_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FILTER_ACTION);
//                intent.putExtra(EXTRA_SERVER_URL, CONFIGURATION.mServerUrl);
//                intent.putExtra(EXTRA_THROWABLE, throwable);
//                if(!TextUtils.isEmpty(CONFIGURATION.mFileUrl))
//                    intent.putExtra(EXTRA_FILE_PATH, CONFIGURATION.mFileUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                mActivity.moveTaskToBack(true);
//                System.exit(0);
            }
        });
        builder.setNegativeButton(R.string.issue_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                mActivity.moveTaskToBack(true);
//                System.exit(0);
            }
        });
        builder.show();

    }


}
