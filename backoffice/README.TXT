The Poker Admin webapplication.

 * As a deployable war-file (for jetty, tomcat etc.)
 * As a sar-file (to run e.g. testwise within firebase, as a service)

BUILD
=====

When building this code for itself, use:

# clean and package
mvn clean package

# clean, package and install to local maven repo
mvn clean install 
