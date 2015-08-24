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

To create the regions, execute the script <SCRIPT_PATH> as below:

```
$ BLA BLA BLA

```

Import some existing data in order to pre-train our model. We'll re-train it later with the data being ingested.

```
$ gfsh import bla bla bla bla bla

```

### Training the Machine Learning model

Before executing the Machine Learning model on Spark, we need to train it with existing data.
For doing that, run the script below, which involkes the [scala class](StockInference/src/main/scala/io/pivotal/demo/StockInferenceDemo.scala) 

```
$ ./train.sh
(...)
```




### Creating the Spring XD streams

Now we'll create the streams (...)
