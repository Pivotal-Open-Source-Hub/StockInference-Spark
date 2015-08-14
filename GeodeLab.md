```
```

# Apache Geode Lab

## Requirements

* Java / JDK
* Git
* Gradle __<optional>__

## Build 

* Obtaining the source code

```
$ git clone github.com/apache/incubator-geode
```

* Building

```
$ cd incubator-geode
$ ./gradlew build -Dskip.tests=true
```


## Your first Geode cluster

* Starting a `locator` and a `server`

```
$ gfsh start locator --name=locator1
...
$ gfsh start server --name=server1 --locators=localhost[10334]
...
```

* Connecting to the cluster using `gfsh`


```
$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    v1.0.0-incubating-SNAPSHOT

Monitor and Manage GemFire
gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=192.168.1.94, port=1099] ..
gfsh>list members
  Name   | Id
-------- | ---------------------------------------
locator1 | anakin(locator1:70957:locator)<v0>:9773
server1  | anakin(server1:71106)<v1>:34411
```

* Creating a region and basic operations

```
gfsh>create region --name=myRegion --type=PARTITION
Member  | Status
------- | ---------------------------------------
server1 | Region "/myRegion" created on "server1"

gfsh>put --key=1 --value="value1" --region=/myRegion
Result      : true
Key Class   : java.lang.String
Key         : 1
Value Class : java.lang.String
Old Value   : <NULL>

gfsh>get --key=1 --region=/myRegion
Result      : true
Key Class   : java.lang.String
Key         : 1
Value Class : java.lang.String
Value       : value1
```

* Try to do another put using different values or using an existing key.
* Try to remove an entry using a key
* While still connected to `gfsh` stop the locator and the server

```
gfsh> stop server --name=server1
gfsh> stop locator --name=locator1

```

* Before moving the to next lab run the script `startGeode.sh` available at the `data` folder.



