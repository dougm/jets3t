Welcome to the jets3t toolkit and application suite.


* Running Applications

Each application can be run using a script in the "bin" directory.
To run an application, such as Cockpit, run the appropriate script from
the directory containing this README.txt file.

Windows:
cd jets3t-0.5.0
bin\cockpit.bat

Unixy:
cd jets3t-0.5.0
bash bin/cockpit.sh


* Servlets

The jets3t application suite now includes a Gatekeeper servlet implementation.
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

Jar files are available in the "jars" directory.

The class files in these jars are compiled with Sun's JDK version 1.4.2
and have debugging turned on to provide more information if errors occur. 

To use jets3t in high-performance scenarios, the classes should be 
recompiled using the latest practicable version of Java, and with 
debugging turned off.