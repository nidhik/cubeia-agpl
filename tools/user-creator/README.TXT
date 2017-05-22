A simple utility for creating new poker users with Cubeia Network. Usually used
for scripting.

BUILDING
========
Run this command from the module root:

  `mvn clean package`

This will create a JAR file in "target/" which is runnable
and contains all dependencies. 

RUNNING
=======
To create a user with user name "test" and password "pass" for operator 1
and default values copy the compiled JAR file above to the 
machine running you Cubeia Network installations, and run:

  java -jar user-creator.jar -u test -p pass -ο 1

In order to see all options, run:

  java -jar user-creator.jar

To create 20 users in one pass:

  java -jar user-creator.jar -u Tester -p 1 -o 1 -r 20

Tester_1 with password 1
Tester_2 with password 2 and so on

To create 100 bots in one pass, setting operator to 0:

  java -jar user-creator.jar -u Bot -p 1 -o 0 -r 100

To create 50 further bots in one pass:

  java -jar user-creator.jar -u Bot -p 1 -o 0 -r 50 -s 101

you can use the "-d t" parameter, to switch easily to the ports used in 
development mode services (9090/9091).