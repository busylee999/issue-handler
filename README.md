# Review
Android Library for handling uncaught exceptions and sending it to installed IssueBot program for posing to bug-trackers. The challenge of using this library it is simplify to store bug issues of your developing Android projects 

Features:

1. Handling uncaugth exceptions and posting it to preinstalled IssueBot Android Application on phone your app running for posting it to bug-tracker system IssueBot supports.
2. Installing IssueBot if it is not on the phone.
3. Automatic attaching addition files you need to store for issue in bug tracker.


## Quick start


### Adding dependency for project:

Using gradle:

Add external repository in build.gragle file in your gradle module folder:

    ...
    apply plugin: 'com.android.application'
    
    repositories {
            mavenCentral()
            maven {
              url 'http://tryremember.ru:8383/archiva/repository/mvn'
            }
    }
    
    android {
    ...
    

This is preffered approach. But it is suitable only if you have internet connection.

Using arr file:

Simple download latest version of aar file from aar-files directory of this repository and add it as dependency for your project.

### Using in project:



## Report preview:

