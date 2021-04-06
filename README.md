# Munchtrack

## Description:
Munchtrack is a Service created with a though to allow munchkin card game to be easier to follow.

This repository contains back-end part of the service, it is made using Java and Spring Boot. <br />

## Requirements:
- Front-end side of the project that you can find here: https://github.com/Marcin-Migdal/Munchtrack-client
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=windows) 
- [Jdk 1.8 (Java SE Development Kit 8)](https://www.oracle.com/pl/java/technologies/javase/javase-jdk8-downloads.html?fbclid=IwAR3ttcmv6t_ulVmxxGrxN5x4flv4Rfvd2w64devwkAcqSCUB4Ca13sqLFUE)
- [Mysql](https://dev.mysql.com/downloads/installer/?fbclid=IwAR0r_YPX5xUgwbDNCrnqJ9jttdEZcARQ27h4bvGtrfI1XPYDuk_v01vzv60) server on port 3306
- [Python](https://www.python.org/downloads/release/python-388/?fbclid=IwAR3JBAiXazikGmOg36ua93hC8_H4VmP45j9bfi2JnunMsUAfJwBf6gSNRJ8) (Mysql requires python)

## Instalation:

### 1) Opening project in IntelliJ
- Open unpacked project in intellij, now you will have to wait for intellij to resolve project dependencies (it may take a few minutes).

- Go to `File > Project Structure`  in top left corner of intellij window, <br />
Under section Project SDK choose version 1.8 if it is not already chosen.

### 2) Adding configuration
- Click on "Add configuration" in top right corner of intellij window.

- New window will open, add new configuration and choose "Maven" now you can edit your configuration.

- "Working directory" field should be filled by a path to the project folder, if it isn't, select your project folder by yourself.

- Type `spring-boot:run` command in "Command line" field.

- Save configuration by clicking "Apply" then "Ok".

### 3) Editing `application.properties` file 
- Before starting, you will need to go to the file src/main/resources/application.properties,  <br /> and edit those two lines to match your mysql server configuration: <br />
`spring.datasource.username= root`<br />
`spring.datasource.password= Morti13@`<br /><br />
Those are credentials with which you log in to your local mysql server in mysql workbench.

### 4) Creating database
- Lastly you have to open mysql workbench.
- Start your local server and create a database for the server to work with<br />
you will do that by using this sql statement `CREATE DATABASE munchkin_app` in the query that opened after starting your mysql server.
## Now you can start munchtrack server
