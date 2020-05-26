EMMA master
===========

This repository contains the EMMA master component reponsible for orchestrating an EMMA deployment.

Build
-----

To build and create the application binaries, run

    mvn clean install

You can then find the following application binary:

* `emma-controller/target/emma-controller-<version>.jar`

Running the EMMA master
-----------------------

### Prerequisites

We use [Redis](https://redis.io) and its [keyspace notification](https://redis.io/topics/notifications)
mechanism to distribute subscription tables.
An EMMA deployment requires a running Redis instance with keyspace notifications activated.
To activate them for a running Redis instance use the `redis-cli` command

    redis-cli config set notify-keyspace-events KEA

### Starting the controller

The controller is a [Spring Boot](https://projects.spring.io/spring-boot)
application and can be run with

    java -jar emma-controller/target/emma-controller-<version>-SNAPSHOT.jar

The controller requires four available ports (which can be parameterized via `application.yml`,
but the first two are also encoded in the properties files of the broker and gateway components):
* `60042` for the broker heartbeats
* `60043` for the monitoring protocol
* `50042` for the controller shell

The Spring Boot application starts a web server on port `8080`.
The controller also starts an [Orvell](https://git.dsg.tuwien.ac.at/trausch/orvell)
shell that you can connect to via TCP with `nc` or `telnet` by running

    nc localhost 50042
