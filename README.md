# Review
Android Library for handling uncaught exceptions and sending it to installed IssueBot (https://play.google.com/store/apps/details?id=com.busylee.issuebot) program for posing to bug-trackers. The challenge of using this library it is simplify to store bug issues of your developing Android projects 

Note: yet this is beta version.

Features:

1. Handling uncaugth exceptions and posting it to preinstalled IssueBot Android Application on phone your app running for posting it to bug-tracker system IssueBot supports.
2. Installing IssueBot if it is not on the phone.
3. Automatic attaching addition files you need to store for issue in bug tracker.


## Quick start

1. Add dependency for you project.
2. Add init code in Application.
3. Specify server url and file path
4. Add callback in onCreate method for your base Activity class.

You can look for sample project.

### Adding dependency for project:

Using gradle:

Add external repository in build.gragle file in your gradle module folder uses this lib or edit build.gradle file for all project:

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
    

And add dependency:
    ...
    dependency {
    ...
    compile 'com.busylee.issuehandler:issuehandler:1.0.6'
    ...
    }
    ...

This is preffered approach. But it is suitable only if you have internet connection.

Using arr file:

Simple download latest version of aar file from aar-files directory of this repository and add it as dependency for your project.

### Using in project:
In your custom application class add IssueHandler init.

It has two different variants, using filePath or not.

    IssueHandler.init("your/redmine/server/path");
    IssueHandler.init("your/redmine/server/path", "path/to/file/be/attached");

Note: If you want to attach file for issue you need to specify file permissions as readable for everybody especially if you created it in application own directory.

And in base activity class add callback method in onCreate()
    
    IssueHandler.onActivityCreate(this);

## Latest version

This is now just beta version. Latest version in mvn repo is 1.0.6.


## License

    Copyright 2013 Square, Inc.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
