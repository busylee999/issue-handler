package com.busylee.issuehandler;

/**
 * Created by busylee on 09.02.15.
 */
public @interface IssueHandlerSetup {

    enum IssueTracker {
        Redmine,
    }

    String serverUrl();
    String filePath() default "";
    boolean ignoreMode() default false;

    IssueTracker issueTraker() default IssueTracker.Redmine;
}
