# zoo-app-example
Sample distributed(o'rly?) application that utilizes Apache Zookeeper for coordination.
Uses trivial leader election algorithm based on Zookeeper abstractions.

## Architecture
TBD

## Build
Get executable jar:

```
mvn clean package
```

## Run
Assuming you are in project's root, bring up Zookeeper first:

```
$ docker-compose -f docker/docker-compose-zk.yml up -d
```

then run few instances of zoo-app-example application:

```
$ java -jar target/zoo-app-example-1.0-SNAPSHOT.jar 1 localhost:2181
```
