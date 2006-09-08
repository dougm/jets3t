#!/bin/sh

# Include libraries in classpath
CP=jets3t.jar
CP=$CP:synchronize.jar
CP=$CP:libs/commons-logging/commons-logging-1.1.jar
CP=$CP:libs/commons-codec/commons-codec-1.3.jar
CP=$CP:libs/commons-httpclient/commons-httpclient-3.0.1.jar

java -classpath $CP org.jets3t.apps.synchronize.Synchronize $@
