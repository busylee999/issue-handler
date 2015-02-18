package com.busylee.issuehandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by busylee on 09.02.15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IssueHandlerSetup {

    enum IssueTracker {
        Redmine,
    }

    String serverUrl();
    String filePath() default "";
    boolean ignoreMode() default false;

    IssueTracker issueTraker() default IssueTracker.Redmine;
}
