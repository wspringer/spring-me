
This sample application demonstrates the use of Spring ME in GWT environment.

To run the application in hosted mode use this command:

  mvn com.totsp.gwt:maven-googlewebtoolkit2-plugin:gwt

To run the application in web browser use:

  mvn jetty:run-exploded

To debug the application in Eclipse developement environment, use

  mvn eclipse:clean eclipse:eclipse

and then use the supplied "woj.launch" configuration to run/debug the application in hosted mode from within the ide. To build a war archive use

  mvn war:war

Have fun,

Wojciech 
