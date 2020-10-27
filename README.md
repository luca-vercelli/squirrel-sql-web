# squirrel-sql-web
SQuirreL SQL Client - Web version

STATUS: Work in progress


This should be a web version of the famous web client.

Screenshots
----------
(TODO)

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

- project structure: DONE
- drivers CRUD: Work in progress (without properties, so far)
- aliasess CRUD: Work in progress
- SQL window: Work in progress
- SQL tables tree: TODO
- Tables handling: TODO
- Web security: TODO
