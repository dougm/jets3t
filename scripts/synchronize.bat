@echo off

REM Include configurations directory in classpath
set CP=configs

REM Include libraries in classpath
set CP=%CP%;jars/jets3t-0.5.0.jar
set CP=%CP%;jars/synchronize-0.5.0.jar
set CP=%CP%;libs/commons-logging/commons-logging-1.1.jar
set CP=%CP%;libs/commons-codec/commons-codec-1.3.jar
set CP=%CP%;libs/commons-httpclient/commons-httpclient-3.0.1.jar
set CP=%CP%:libs/logging-log4j/log4j-1.2.13.jar

java -classpath %CP% org.jets3t.apps.synchronize.Synchronize %*
