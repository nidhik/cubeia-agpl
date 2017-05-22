================================================================================
               CUBEIA POKER - OPEN SOURCE STAND-ALONE SYSTEM
================================================================================


IMPORTANT
=========

The main scope of this stand-alone system is to simplify the source-code-level
evaluation of Cubeia Poker, Network and Firebase for any interested parties and
of course for potential Cubeia customers. You don't need any special knowledge
to install this system.

Still, anyone can evolve this Open Source Poker System towards a production 
system. Further Development, maintenance and live operation require of course
some deep knowledge of the involved technologies, like java, SOA/WOA and
web-servers in general.


PREREQUISITES
=============

* Java SDK 1.7
* Maven 3.0.4 or higher
* Mercurial 

Check the versions with
  hg --version
  mvn -v

Ensure that maven has enough memory reserved, on linux just do:
  export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=512m";


GET & BUILD
===========

mkdir cubeia
cd cubeia
hg clone https://bitbucket.org/cubeia/cubeia-poker poker
cd poker
mvn clean install
cd  system/uar
mvn firebase:run -Dhibernate.hbm2ddl.auto=create

http://localhost:8181 - you should see a page with further information.

Note:
the -Dhibernate.hbm2ddl.auto=create creates the database schema and adds most
of the initial data. Please do not use it when restarting, as this will 
overwrite any existing data.



================================================================================
ADVANCED STEPS
================================================================================

UPDATE AND REBUILD
==================

After source code updates via

cd cubeia/poker
hg pull -u
mvn clean package install -U

The "-U" flag ensures that dependencies are updated (important when sources are
under development).


HISTORIES (HAND & TOURNAMENT)
=============================

Mandatory: for hand histories and tournament histories, install mongodb on your
system.

http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/
http://www.mongodb.org/downloads

Ensure to have mongod running. The database will be created by the software, 
default configuration is: db: poker, user: poker, pass: poker

cd system/uar
mvn clean package -Phistory
mvn firebase:run


FIREBASE
========

Firebase (Cubeia's Game Application Server which drives Cubeia Poker) was
fetched during the build. You can start the poker-server directly by starting 
firebase from within

./system/uar/target/firebase-run/firebase-<version>-CE

This location (the "target" folder) is overwritten by a clean build (mvn clean),
or when calling firebase:run.

Once your poker-system runs fine, you can copy the firebase folder to another
location. If your poker directory resides in e.g. cubeia/poker, you could copy
firebase to cubeia/poker001 (where 001 is just a running version number).

In this case, you either have to copy new versions of the poker-uar.uar into the
firebase/game/deploy directory, or you have to make a symbolic link. Remember to
verify the configuration files (new files or new configuration data).

Alternatively, you copy the next stable version to cubeia/poker002.


MYSQL DATABASE
==============

The simple system uses an embedded H2 database, enabling this way a simplified
setup and simplified updates. You may want to change to a mysql database,
especially if you want to keep the data once you start using the poker-system.

A simple way is to use the "initdb.sql" script file. Just copy&paste it to the
mysql command-line, or execute it, giving the full path, e.g. 

mysql> source ./backoffice/poker-admin/src/sql/initdb.sql;

This will create 4 mysql databases (poker, accounting, user, operator).

You have to change the deployed datasources within:

./system/uar/src/test/resources/firebase/conf/game/deploy



FURTHER WEBSITE INTEGRATION
=========================== 

Edit the OPERATOR 1 to setup further integrations links, e.g. edit profile or
add credits.

If you want to integrate an existent website with user-registration:
There are several supporting modules available, like e.g. a simple jdbc login
to an existent database (of your website, e.g. wordpress based):

www.cubeia.org/wiki/index.php/Tutorials/howto_jdbc_login
