# SpringXD

Spring XD is a unified, distributed, and extensible system for data ingestion, real time analytics, batch processing, and data export. The project's goal is to simplify the development of big data applications.

Installation directory: spring-xd-2.0.0.BUILD-SNAPSHOT/

## Starting an single-node instance

```
$ xd-singlenode
```

*Make the process run in the background (nohup  &) or open a new terminal for the next commands.*

## Using `xd-shell` (SpringXD CLI)

* Start `xd-shell`

```
$ xd-shell
```

* List streams

```
xd:> stream list
```

* Create a simple log stream

```
xd:> stream create --name ticktock --definition "time | log" --deploy
```

*Check the output on SpringXD single-node log file*

* List current modules

```
xd:>module list
```

* Destry simple log stream

```
xd:>stream destroy --name ticktock
```

### Simple Geode stream

* Using `gfsh` create a region *REPLICATED* named `myRegion`

Check [Geode](Geode.md) instructions if you don't remember the steps.

* Create a time stream using a **gemfire-server** sink

```
xd:>stream create --name geode-simple --definition "time | gemfire-server --regionName=myRegion --port=10334 --useLocator=true --host=localhost --keyExpression='1' " --deploy
```

* Return to `gfsh` and use the command `list clients`

```
gfsh>list clients

ClientList

                    Client Name / ID                      | Server Name / ID
--------------------------------------------------------- | -------------------------
192.168.56.10(6303:loner):57534:9d03a82f(version:GFE 8.0) | member=server1,port=40404
```

* Read the value using `gfsh`

```
gfsh>get --region=/myRegion --key=1 --key-class=java.lang.Integer
Result      : true
Key Class   : java.lang.Integer
Key         : 1
Value Class : java.lang.String
Value       : 2015-08-15 04:46:59
```

*Repeat the command and verify that value has changed*

* Destroy all streams on `xd-shell`

```
xd:>stream all destroy
```

## Final step

* Before moving the to next lab run the script `stream-create.sh` from `$PROJECT/streaming/`
* After the script execute, access the following from a browser:
  * http://192.168.56.10:9393/admin-ui/#/streams/definitions
  * http://192.168.56.10:9393/admin-ui/#/streams/create
* Remember `gfsh`? Can you check if there is a client (SpringXD) connected on Geode?

*If you don't have started SpringXD use  `startSpringXD.sh` from `$PROJECT/streaming` folder*

## References

* [Website](http://projects.spring.io/spring-xd/)
* [Documentation](http://docs.spring.io/spring-xd/docs/2.0.0.BUILD-SNAPSHOT/reference/html/)
* [Examples](https://github.com/spring-projects/spring-xd-samples)
