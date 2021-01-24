# squirrel-sql-web
SQuirreL SQL Client - Web version

![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg)

This is a web version of the famous web client.

Frontend: Vuetify Material Dashboard https://www.creative-tim.com/product/vuetify-material-dashboard

Backend: Java EE (JAXRS, EJB, CDI).



Screenshots
----------
![Drivers](screenshots/01-drivers-list.png)
![Driver](screenshots/02-driver.png)
![Aliases](screenshots/03-alias-list.png)
![Alias](screenshots/04-alias.png)
![Objects](screenshots/05-objects-tab.png)
![Query](screenshots/06-sql-tab.png)
![Tables](screenshots/07-table-tab.png)
![Preferences](screenshots/08-preferences.png)

Build
-----
You need at least Java (>=8), Maven, nodejs. Compile all with

    mvn package

Run
---
The application is intended to be run in a real EE container (Glassfish, TomEE, JBoss, ...) not Tomcat.

In Glassfish, with default configuration, the app will run at address http://localhost:8080/squirrel-sql-web/

We are using Glassfish 4.1.2 with MOXy patch described [here](https://github.com/eclipse-ee4j/glassfish/issues/21440#issuecomment-422056135)

Project roadmap
---------------

- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Project structure
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Vue.js frontend
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Drivers CRUD
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Driver JAR's selection box
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Aliases CRUD
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Alias properties window
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) SQL tab
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Objects tree tab
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Database tab
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Table tab
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Table "WHERE" and "ORDERBY" clause panels
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Procedure tab
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) UDT tab
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Handling with large tables
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Editing table contents
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Table import/export
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Table DDL, Script SQL
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Procedure source code: Mysql, Oracle, PostGreSQL, ...
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) New session properties window
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Global preferences window
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) SQuirreL logs window
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Highlight syntax
- ![DONE](https://img.shields.io/badge/Status-Done-green.svg) Web security: authentication (well, we still need to encrypt passwords)
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Web security: authorizations
- ![WIP](https://img.shields.io/badge/Status-WorkInProgress-yellow.svg) Users CRUD
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) i18n
- ![TODO](https://img.shields.io/badge/Status-ToDo-red.svg) Github CI (continuous integration)


Authentication
--------------
Default username is `admin` with password `admin`. Users can be configured inside `~/.squirrel-sql/Users.xml`.

Run mock
--------
For frontend development, we can run in mock mode with NodeJS instead of Glassfish:

1. Set `VUE_APP_MOCK=true` in file `.env` or `.env.local`
2. `cd src/main/javascript`
3. `npm start`
