#!/bin/sh

# Include configurations directory in classpath
CP=../configs

# Include libraries in classpath
CP=$CP:../jars/jets3t-0.5.0.jar
CP=$CP:../jars/uploader-0.5.0.jar
CP=$CP:../libs/commons-logging/commons-logging-1.1.jar
CP=$CP:../libs/commons-codec/commons-codec-1.3.jar
CP=$CP:../libs/commons-httpclient/commons-httpclient-3.0.1.jar
CP=$CP:../libs/misc/BareBonesBrowserLaunch.jar
CP=$CP:../libs/logging-log4j/log4j-1.2.13.jar

java -classpath $CP org.jets3t.apps.uploader.Uploader
