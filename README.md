# squirrel-sql-web
SQuirreL SQL Client - Web version

STATUS: DRAFT!


This should be a web version of the famous web client. Just an idea, so far.

Frontend: Material Dash https://github.com/BootstrapDash/Material-Admin
Compile with

    cd src/main/webapp
    npm install
    gulp

Backend: Java EE (JAXRS, EJB, CDI). Intended to be used in a real EE container (Glassfish, TomEE, JBoss, ...) not Tomcat.
Compile with

    mvn package

In Glassfish, with default configuration, the app will run at address http://localhost:8080/squirrel-sql-web/