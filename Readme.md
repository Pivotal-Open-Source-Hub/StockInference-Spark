# Stock Prediction with Machine Learning 

![Architecture](StockInference-arch.png)


## Summary

Stock Inference engine using Spring XD, Apache Geode / GemFire and Spark ML Lib.

## Requirements

* [Apache Geode (Incubating)](http://geode.incubator.apache.org/) or [Pivotal GemFire](http://pivotal.io/big-data/pivotal-gemfire)
* [Spring XD 1.2+](http://projects.spring.io/spring-xd/)
* [Apache Spark 1.3.1](http://spark.apache.org/downloads.html)
* [Apache Zeppelin (Incubating)](http://zeppelin.incubator.apache.org/)
* 8GB+ RAM (recommended)
* Linux or OSX (Windows should be OK but instructions assume *nix shell)


ps: If you are were given a pre-packaged Vagrant VM for this lab you'll only need:

* [VirtualBox 4.3+ or 5.0](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant](https://www.vagrantup.com/downloads.html)

if you prefer to create a demo VM yourself, the instructions are [here](VM.md)

## First steps with each product

If you're not familiar with Geode/GemFire, Spring XD, Spark ML and Apache Zeppelin, please first follow the product specific labs below:

* [First steps with Apache Geode](Geode.md)
* [First steps with SpringXD Lab](SpringXD.md)
* [First steps with Spark ML](SparkML.md)
* [First steps with Apache Zeppelin](Zeppelin.md)



### Starting the demo environment

If you have received a pre-built Virtual Machine, start the VM and access its console using ssh as below:

```
$ vagrant up

Bringing machine 'default' up with 'virtualbox' provider...
==> default: Box 'package.box' could not be found. Attempting to find and install...
    default: Box Provider: virtualbox
    default: Box Version: >= 0
==> default: Adding box 'package.box' (v0) for provider: virtualbox
    default: Downloading: file:///Users/fmelo/qcon/package.box
==> default: Successfully added box 'package.box' (v0) for 'virtualbox'!
(...)
    default: Running: inline script
==> default: stdin: is not a tty
==> default: QCon Rio 2015 - Pivotal Lab

$ vagrant ssh
Welcome to Ubuntu 15.04 (GNU/Linux 3.19.0-23-generic x86_64)

 * Documentation:  https://help.ubuntu.com/

  System information as of Mon Aug 24 01:13:06 UTC 2015

  System load:  0.0                Processes:           96
  Usage of /:   17.6% of 38.81GB   Users logged in:     0
  Memory usage: 2%                 IP address for eth0: 10.0.2.15
  Swap usage:   0%                 IP address for eth1: 192.168.56.10

  Graph this data and manage this system at:
    https://landscape.canonical.com/

  Get cloud support with Ubuntu Advantage Cloud Guest:
    http://www.ubuntu.com/business/services/cloud

47 packages can be updated.
27 updates are security updates.


Last login: Sun Aug 23 23:16:30 2015 from 10.0.2.2
_____________________________________________________________________________

Lab variables:
GEODE_HOME=/home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode
SPRINGXD_HOME=/home/vagrant/spring-xd-2.0.0.BUILD-SNAPSHOT
ZEPPELIN_HOME=/home/vagrant/incubator-zeppelin
SPARK_HOME=/home/vagrant/spark-1.3.1-bin-hadoop2.6
PROJECT=/home/vagrant/project/StockInference-Spark

_____________________________________________________________________________

[vagrant@stocks-vm, load: 0.00] (Mon Aug 24 - 01:13:10)
~ $
```


### Creating the Geode / GemFire regions to store 

The demo uses three different regions for storing data:
* /Stocks - Stores raw stock trading data, as acquired from [Yahoo Finance YQL](finance.yahoo.com) or using the simulator (which randomly replays data previously ingested from the same Yahoo Finance YQL)
* /TechIndicators - Stores technical indicators, which will be used as inputs / features to the Machine Learning model. The indicators are calculated by the [R script](streaming/tech_indicators.R), which is involked by Spring XD.
* /Predictions - Stores the predicted data, as it gets calculated by the [Spark MLLib model](StockInference/src/main/scala/io/pivotal/demo/StockInferenceDemo.scala).

To create the regions, execute the script `startGeode.sh` under the `data` folder of this project root path as below:

```
$ cd project/StockInference-Spark/data
$ ./startGeode

1. Executing - start locator --name=locator1 --J=-Dgemfire.http-service-port=7575

............................................
Locator in /home/vagrant/project/StockInference-Spark/data/locator1 on 192.168.56.10[10334] as locator1 is currently online.
Process ID: 4374
Uptime: 23 seconds
GemFire Version: 1.0.0-incubating-SNAPSHOT
Java Version: 1.8.0_51
Log File: /home/vagrant/project/StockInference-Spark/data/locator1/locator1.log
JVM Arguments: -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.http-service-port=7575 -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode/lib/gemfire-core-1.0.0-incubating-SNAPSHOT.jar:/home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode/lib/gemfire-core-dependencies.jar

Successfully connected to: [host=192.168.56.10, port=1099]

Cluster configuration service is up and running.

2. Executing - start server --name=server1 --J=-Dgemfire.start-dev-rest-api=true --J=-Dgemfire.http-service-port=8888

..............
Server in /home/vagrant/project/StockInference-Spark/data/server1 on 192.168.56.10[40404] as server1 is currently online.
Process ID: 4546
Uptime: 7 seconds
GemFire Version: 1.0.0-incubating-SNAPSHOT
Java Version: 1.8.0_51
Log File: /home/vagrant/project/StockInference-Spark/data/server1/server1.log
JVM Arguments: -Dgemfire.default.locators=192.168.56.10[10334] -Dgemfire.use-cluster-configuration=true -Dgemfire.start-dev-rest-api=true -Dgemfire.http-service-port=8888 -XX:OnOutOfMemoryError=kill -KILL %p -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode/lib/gemfire-core-1.0.0-incubating-SNAPSHOT.jar:/home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode/lib/gemfire-core-dependencies.jar

3. Executing - create region --name=/Stocks --type=PARTITION

Member  | Status
------- | -------------------------------------
server1 | Region "/Stocks" created on "server1"

4. Executing - create region --name=/TechIndicators --type=PARTITION

Member  | Status
------- | ---------------------------------------------
server1 | Region "/TechIndicators" created on "server1"

5. Executing - create region --name=/Predictions --type=PARTITION

Member  | Status
------- | ------------------------------------------
server1 | Region "/Predictions" created on "server1"

6. Executing - import data --region=/Stocks --file=../Stocks.gfd --member=server1

Data imported from file : /home/vagrant/project/StockInference-Spark/data/Stocks.gfd on host : 192.168.56.10 to region : /Stocks

7. Executing - import data --region=/TechIndicators --file=../TechIndicators.gfd --member=server1

Data imported from file : /home/vagrant/project/StockInference-Spark/data/TechIndicators.gfd on host : 192.168.56.10 to region : /TechIndicators

8. Executing - describe region --name=/Stocks

..........................................................
Name            : Stocks
Data Policy     : partition
Hosting Members : server1

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 41540
       | data-policy | PARTITION


9. Executing - describe region --name=/TechIndicators

..........................................................
Name            : TechIndicators
Data Policy     : partition
Hosting Members : server1

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 36486
       | data-policy | PARTITION


10. Executing - describe region --name=/Predictions

..........................................................
Name            : Predictions
Data Policy     : partition
Hosting Members : server1

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 0
       | data-policy | PARTITION
```
As you can verify, the script not only created the regions but also imported some data we had captured earlier, in order to train our model. We'll keep re-training it later as we ingest data.

Next, we should deploy the Geode/GemFire functions for the Spark Connector:

```
~/project/StockInference-Spark/data $ ./deployFunctionVM.sh

(1) Executing - connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=192.168.56.10, port=1099] ..
Successfully connected to: [host=192.168.56.10, port=1099]


(2) Executing - deploy --jar=/home/vagrant/incubator-geode/gemfire-spark-connector/gemfire-functions/target/scala-2.10/gemfire-functions_2.10-0.5.0.jar

Member  |           Deployed JAR           | Deployed JAR Location
------- | -------------------------------- | ------------------------------------------------------------------------------------------------
server1 | gemfire-functions_2.10-0.5.0.jar | /home/vagrant/project/StockInference-Spark/data/server1/vf.gf#gemfire-functions_2.10-0.5.0.jar#1

```


### Training the Machine Learning model

Before executing the Machine Learning model on Spark, we need to train it with existing data.
For doing that, run the script below, which involkes the [scala class](StockInference/src/main/scala/io/pivotal/demo/StockInferenceDemo.scala) 

From the streaming folder, run ``train.sh`` . It will take a while for it to run.
```
~/project/StockInference-Spark $ cd streaming

~/project/StockInference-Spark/streaming $ ./train.sh
15/08/24 03:07:41 INFO SparkContext: Running Spark version 1.3.1
15/08/24 03:07:41 WARN Utils: Your hostname, stocks-vm resolves to a loopback address: 127.0.1.1; using 10.0.2.15 instead (on interface eth0)
(...)
15/08/24 03:09:33 INFO DAGScheduler: Stage 2012 (mean at StockInferenceDemo.scala:127) finished in 0.072 s
15/08/24 03:09:33 INFO DAGScheduler: Job 2012 finished: mean at StockInferenceDemo.scala:127, took 0.084467 s
training Mean Squared Error = 4.356799628392633E-4
[info 2015/08/24 03:09:33.493 UTC <Distributed system shutdown hook> tid=0x40] VM is exiting - shutting down distributed system

[info 2015/08/24 03:09:33.498 UTC <Distributed system shutdown hook> tid=0x40] GemFireCache[id = 1756587746; isClosing = true; isShutDownAll = false; created = Mon Aug 24 03:07:48 UTC 2015; server = false; copyOnRead = false; lockLease = 120; lockTimeout = 60]: Now closing.

[info 2015/08/24 03:09:33.555 UTC <Distributed system shutdown hook> tid=0x40] Destroying connection pool DEFAULT
```


### Creating the Spring XD streams

Now we'll create the streams in SpringXD that orchestrate all the data flow.

Let's first start Spring XD. From the ``streaming`` directory, run the script `startSpringXD.sh`. This will start the server in background, outputing the logs to the file ``nohup.out``

```
~/project/StockInference-Spark/streaming $ ./startSpringXD.sh
Starting...
nohup: appending output to ‘nohup.out’
```

Next, take a look at the file ``stream-create.xd``, containing the streams we'll create on Spring XD. Remember the  [architecture image](StockInference-arch.png)

```
~/project/StockInference-Spark/streaming $ more stream-create.xd
admin config server http://localhost:9393

stream create process_sink --definition "queue:stocks >  transform --script='file:./transform.groovy' | object-to-json | gemfire-json-server --useLocator=true --host=localhost --port
=10334 --regionName=Stocks --keyExpression=payload.getField('entryTimestamp')" --deploy

stream create yahoo_finance_source --definition "trigger --cron='* * 7-13 * * MON-FRI' | http-client --url='''https://query.yahooapis.com/v1/public/yql?q=select Symbol, LastTradeDate
, LastTradeTime, LastTradePriceOnly, DaysHigh, DaysLow, Open from yahoo.finance.quotes where symbol in (\"TSLA\")&format=json&env=store://datatables.org/alltableswithkeys''' --httpMe
thod=GET | splitter --expression=#jsonPath(payload,'$.query.results.quote')  > queue:stocks" --deploy

stream create http_source --definition "http --port=9000 | splitter --expression=#jsonPath(payload,'$') > queue:stocks" --deploy

stream create --name r_process --definition "tap:stream:process_sink.transform > r-parsing: object-to-json | shell --command='Rscript ./tech_indicators.R' | formatting: splitter --ex
pression=#jsonPath(payload,'$') | filter --expression=#jsonPath(payload,'$.rsi').indexOf('NaN')==-1 | object-to-json | gemfire-json-server --useLocator=true --host=locator --port=103
34 --regionName=TechIndicators --keyExpression=payload.getField('entryTimestamp')" --deploy

stream create --name prediction --definition "tap:stream:r_process.object-to-json > shell --command='../evaluate.sh' | gemfire-json-server --regionName=Predictions --host=localhost -
-port=10334 --useLocator=true --keyExpression=payload.getField('entryTimestamp')" --deploy

# stream create --name training --definition "trigger --fixedDelay=300 | shell --command='../train.sh'" --deploy
```

Please note the ``yahoo_finance_source`` stream has a cron based on PDT timezone (7-13 MON-FRI = normal Wall Street operating times). If your environment has another timezone set, you'll need to adjust it accordingly.

Execute the script ``stream-create.sh`` to deploy all streams to Spring XD:

```
~/project/StockInference-Spark/streaming $ ./stream-create.sh
Aug 24, 2015 3:19:48 AM org.springframework.shell.core.AbstractShell handleExecutionResult
INFO: Successfully targeted http://localhost:9393
Aug 24, 2015 3:19:53 AM org.springframework.shell.core.AbstractShell handleExecutionResult
INFO: Created and deployed new stream 'process_sink'
Aug 24, 2015 3:19:54 AM org.springframework.shell.core.AbstractShell handleExecutionResult
INFO: Created and deployed new stream 'yahoo_finance_source'
Aug 24, 2015 3:19:55 AM org.springframework.shell.core.AbstractShell handleExecutionResult
INFO: Created and deployed new stream 'http_source'
Aug 24, 2015 3:20:05 AM org.springframework.shell.core.AbstractShell handleExecutionResult
INFO: Created and deployed new stream 'r_process'
Aug 24, 2015 3:20:08 AM org.springframework.shell.core.AbstractShell handleExecutionResult
INFO: Created and deployed new stream 'prediction'
```

At this point, Spring XD is already querying Yahoo Finance for the latest quotes(at the normal market hours), and also listening for possible quotes at port 9000. 

### Using the Simulator

When you're not on US stock market open hours, we can use the simulator to randomly replay some of the old quotes stored on Geode / GemFire. As the order is random, we can expect a much higher volatility.

Start the simulator from the ``FinanceStreamSimulator`` folder:

