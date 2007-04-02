Welcome to the JetS3t toolkit and application suite.

Website: http://jets3t.s3.amazonaws.com/index.html


* Running Applications

Each application can be run using a script in the "bin" directory.
To run an application, such as Cockpit, run the appropriate script from
the bin directory.

Windows:
cd jets3t-0.5.1\bin
cockpit.bat

Unixy:
cd jets3t-0.5.1/bin
bash cockpit.sh


* Servlets

The JetS3t application suite now includes a Gatekeeper servlet implementation.
The deployable WAR file for this servlet is located in the "servlets/gatekeeper"
directory.


* Configuration files

Some applications or library components read text configuration files.
Generally, these configuration files must be available in the classpath.

Example configuration files are located in the "configs" directory. The 
run scripts in the "bin" directory automatically include this "configs" 
directory in the classpath.

The configuration files include:

- synchronize.properties: Properties for the Synchronize application
- uploader.properties: Properties for the Uploader application
- jets3t.properties: Low-level toolkit configuration.
- mime.types: Maps file extensions to the appropriate mime/content type.
  For example, the "txt" extension maps to "text/plain".
- commons-logging.properties: Which logging implementation to use.
- log4j.properties: When Log4J is the chosen logging implementation, these
  settings control how much logging information is displayed, and the way 
  it is displayed.


* JAR files

The compiled JetS3t code jar files are available in the "jars" directory,
and include the following:

jets3t-0.5.1.jar      : The JetS3t toolkit. The toolkit including the JetS3t
                      : service implementions of interest developers and 
                      : which underlies all the other JetS3t applications.
                      : http://jets3t.s3.amazonaws.com/toolkit/toolkit.html
cockpit-0.5.1.jar     : Cockpit, a GUI application/applet for viewing and
                      : managing the contents of an S3 account.
                      : http://jets3t.s3.amazonaws.com/applications/cockpit.html
synchronize-0.5.1.jar : Synchronize, a console application for synchronizing 
                      : directories on a computer with an Amazon S3 account.
                      : http://jets3t.s3.amazonaws.com/applications/synchronize.html
uploader-0.5.1.jar    : a wizard-based GUI application/applet that S3 account 
                      : holders (Service Providers) may provide to clients to 
                      : allow them to upload files to S3 without requiring 
                      : access to the Service Provider's S3 credentials
                      : http://jets3t.s3.amazonaws.com/applications/uploader.html

The class files in these jars are compiled with Sun's JDK version 1.4.2
and have debugging turned on to provide more information if errors occur. 

To use JetS3t in high-performance scenarios, the classes should be 
recompiled using the latest practicable version of Java, and with 
debugging turned off.