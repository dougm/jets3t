@echo off

REM Include configurations directory in classpath
set CP=../configs

REM Include resources directory in classpath
set CP=%CP%;../resources

REM Include libraries in classpath
set CP=%CP%;../jars/jets3t-0.5.0.jar
set CP=%CP%;../jars/uploader-0.5.0.jar
set CP=%CP%;../libs/commons-logging/commons-logging-1.1.jar
set CP=%CP%;../libs/commons-codec/commons-codec-1.3.jar
set CP=%CP%;../libs/commons-httpclient/commons-httpclient-3.0.1.jar
set CP=%CP%;../libs/misc/BareBonesBrowserLaunch.jar
set CP=%CP%;../libs/logging-log4j/log4j-1.2.13.jar

start javaw -classpath %CP% org.jets3t.apps.uploader.Uploader
