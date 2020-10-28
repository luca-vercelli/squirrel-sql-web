# squirrel-sql-web
SQuirreL SQL Client - Web version

STATUS: Work in progress


This should be a web version of the famous web client.

Screenshots
----------
![Drivers](https://i.postimg.cc/9DfwVs8t/drivers.png)
![Aliases](https://i.postimg.cc/vchxJGzZ/aliases.png)

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

- project structure ![DONE](https://img.shields.io/badge/Status-Done-green.svg)]
- drivers CRUD: ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg)] (without properties, so far)
- aliasess CRUD: ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg)]
- SQL window: ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg)]
- SQL tables tree: ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg)]
- Tables handling: ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg)]
- Web security: ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg)]
