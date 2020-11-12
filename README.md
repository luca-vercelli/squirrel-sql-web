# squirrel-sql-web
SQuirreL SQL Client - Web version

![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg)

This is a web version of the famous web client.


Screenshots
----------
[![Drivers](https://i.postimg.cc/9DfwVs8t/drivers.png)](https://i.postimg.cc/8zRRdSL3/drivers.png)
[![Aliases](https://i.postimg.cc/vchxJGzZ/aliases.png)](https://i.postimg.cc/Z5tPyRvp/aliases.png)

Build
-----
You need at least Java (>=8), Maven, nodejs, gulp. Compile all with

    mvn package

Frontend: Material Dash https://github.com/BootstrapDash/Material-Admin

Backend: Java EE (JAXRS, EJB, CDI).


Run
---
The application is intended to be run in a real EE container (Glassfish, TomEE, JBoss, ...) not Tomcat.

In Glassfish, with default configuration, the app will run at address http://localhost:8080/squirrel-sql-web/


Project roadmap
---------------

- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Project structure 
- ![WIP](https://img.shields.io/badge/Status-Done-green.svg) Drivers CRUD
- ![WIP](https://img.shields.io/badge/Status-Done-green.svg) Aliasess CRUD
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Aliases properties window
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) SQL window
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Tables tree
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Global preferences window
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) New session properties window
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) View SQuirreL logs window
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Web security


Authentication
--------------
Default username is `admin` with password `admin`. Users can be configured inside `~/.squirrel-sql/Users.xml`.

Run mock
--------
For frontend development, we can run in mock mode with NodeJS instead of Glassfish:

1. Set `enable_mock = true` in file `login.js`
2. `cd src/main/javascript`
3. `gulp serve`
