# StockHawk

StockHawk is an Android app built for the Udacity Androdi Nanodegree course.

##Building and Running

In order to get this project to build and run, you'll need an API key google.

###To create a Google API project:
	• Open the Google Developers Console.
	    https://cloud.google.com/console

	• Click on "Create Project".
	• Supply a project name and click "Create".
	• Once the project has been created, a page appears that displays your project ID and project number. For example, Project Number: 670330094152.

###Add a config file to your project
	• Get a config file by pressing the 'get a configuration file' button on this page.
	• Copy the config file into your App/ directory of your Android Studio project.
	• (Mac or Linux) Open the Android Studio terminal pane and enter the command: $ mv path-to-download/google-services.json app/
	• (Windows) Open the Android Studio terminal pane and enter the command: $ move path-to-download/google-services.json app/
	• Add the dependency to your project-level build.gradle file with the line: classpath 'com.google.gms:google-services:1.5.0-beta2'
	• Add the plugin to your app-level build.gradle file with the line: apply plugin: 'com.google.gms.google-services'

###To enable the GCM service:
	• In the sidebar on the left, select APIs & auth.
	• In the displayed list of APIs, turn the Google Cloud Messaging for Android toggle to ON.

###To obtain an API key:
	• In the sidebar on the left, select APIs & auth > Credentials.
	• Under Public API access, click Create new key.
	• In the Create a new key dialog, click Server key.
	• In the resulting configuration dialog, supply your server's IP address. For testing purposes, you can use 0.0.0.0/0.
	• Click Create.
	• In the refreshed page, copy the API key. You will need the API key later on to perform authentication in your app server.


To suppress a common build errors related to Google Cloud Messaging (GCM):
Add  your API key to the code by adding the following to your ~/.gradle/gradle.properties file:

GoogleAppId="[Your API key]"

In your build.gradle  (Module: app) add this line to defaultConfig to reference that key:

        resValue "string", "google_app_id", GoogleAppId
