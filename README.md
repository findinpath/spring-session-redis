Spring Session with Redis persistence
=====================================

This project acts as a simple proof of concept on how to do Spring Session 
persistence based on Redis Master-Replica configuration

## Introduction

The main advantage of persisting the session data in [redis](https://redis.io/)
is that there's no need to work with sticky session cookies on the load balancer side.
When restarting the spring boot application the session information will not be looked up
locally, but instead in redis.

The code of the project is based on the project
[spring-session-sample-boot-redis-simple](https://github.com/spring-projects/spring-session/tree/master/spring-session-samples/spring-session-sample-boot-redis-simple)
present in the sample project from the [spring-session](https://github.com/spring-projects/spring-session)
Github repository.

This project spawns a Redis Master-Replica environment based on the docker
image [bitnami/redis](https://hub.docker.com/r/bitnami/redis/).

The Redis Master-Replica topic is explained more in detail in the page:

https://redis.io/topics/replication

Redis has one master and several following replicas. This means that the **WRITE** operations
are made **only** on the **master** redis node, but for **READ** operations are preferred on the replica
redis nodes.  

Just for heads-up information, probably [Redis Sentinel](https://redis.io/topics/sentinel) is also a viable alternative to
[Redis Static Master-Replica](https://redis.io/topics/replication) configuration. 

## Run the project

Start the redis master-replica docker-compose environment

```bash
docker-compose -f docker/docker-compose.yml up -d --scale redis-master=1 --scale redis-replica=3
```

Check the ports exposed by the redis replica servers:

```
docker-compose -f docker/docker-compose.yml ps
```

and modify accordingly the [application.yml](src/main/resources/application.yml)
file to point the slaves to the correct addresses.


Start the spring boot application

```bash
mvn spring-boot:run
```


Visit the page on the browser:

http://localhost:8080/


and login by using the following credentials:

- username: `user`
- password: `password`

When restarting the spring boot application and reloading the browser page  or 
opening the browser again, the login page shouldn't be shown again and the user
should see the secured page.


In case that the content on the redis side needs to be checked
use [redis-cli](https://redis.io/topics/rediscli) or any other Redis browser


```bash
redis-cli -p 6379 -a my_redis_password
```

Some useful redis commands for the purpose of this proof of concept project:

```
127.0.0.1:6379> keys *
1) "spring:session:sessions:5c5c2f89-912c-4537-bdef-61b0d37bb155"
127.0.0.1:6379> HGETALL spring:session:sessions:5c5c2f89-912c-4537-bdef-61b0d37bb155
 1) "creationTime"
 2) "\xac\xed\x00\x05sr\x00\x0ejava.lang.Long;\x8b\xe4\x90\xcc\x8f#\xdf\x02\x00\x01J\x00\x05valuexr\x00\
...
```


Stop the redis master-replica docker-compose environment

```bash
docker-compose -f docker/docker-compose.yml down
```


## Miscellaneous


### Login page
The spring security provides by default a login page this is why this project doesn't include one.
To see where the login page is generated, consult:

https://stackoverflow.com/questions/35557484/where-is-spring-security-default-login-page-code-located/35557596#35557596

### Bitnami Redis docker images

Check out more details about how to setup replication on the `bitnami/redis` docker image on the page:

https://hub.docker.com/r/bitnami/redis/

**NOTE** that working with a volume to store the data for the redis master would allow restarting the master
and would provide an almost seamless session experience even in case when a restart of the redis master is happening. 
