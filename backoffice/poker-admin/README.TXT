The Poker Admin Webapplication, used to manage Cubeia Poker

To build: 
mvn clean package

To run:
mvn jetty:run

Access at localhost:8088/poker-admin/

For more installation instructions, see the root INSTALL.TXT



AUTHENTICATION
==============

Default logins are admin/admin and user/user. To override with more/other accounts create a file in 
classpath named admin-user-list.xml which contains users and roles as in the example below.

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<users>
    <user userName="admin" password="admin">
        <role>ROLE_ADMIN</role>
    </user>
    
    <user userName="user" password="user">
        <role>ROLE_USER</role>
    </user>
</users>

CURRENCIES AND ACCOUNTS
=======================

If you create more currencies later, you have to create those 3
accounts for the currencies, too.

The type is SYSTEM_ACCOUNT and enable to option "negative balance allowed".

 * SYSTEM_ACCOUNT | EUR | OPEN | UserID -1000 | Rake
 * SYSTEM_ACCOUNT | EUR | OPEN | UserID -2000 | Promotions
 * SYSTEM_ACCOUNT | EUR | OPEN | UserID -3000 | General 
