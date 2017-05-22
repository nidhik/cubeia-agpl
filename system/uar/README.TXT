A very simple Poker System, which can be used as a quick-start, demo or even for
simple installations.

It is packaged as a uar, and embeds all services, coming this way close to a
"normal" application, without the need to start multiple subsystems.

The embedded services are:

client, bots, admin, network (wallet, user, operator)

To build, change to the project root and execute

mvn clean package

run with

mvn firebase:run
