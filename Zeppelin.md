# Zeppelin

Zeppelin, a web-based notebook that enables interactive data analytics. You can make beautiful data-driven, interactive and collaborative documents with SQL, Scala and more.

## Building

1. Obtaining the source code

```
$ git clone https://github.com/apache/incubator-zeppelin
```

1. Building

```
$ cd incubator-zeppelin
$ mvn clean package -Pspark-1.3 -Dhadoop.version=2.2.0 -Phadoop-2.2 -DskipTests
```

*Requires Apache Maven 3*
*Requires nodejs and npm*

## Starting Zeppeling

```
$ cd $ZEPPELIN_HOME
$ bin/zeppelin-deamon.sh start
```

Using a web browser access http://localhost:8080 OR if you're running the pre-packaged VM http://192.168.56.10:8080

## Enabling Geode OQL Interpreter

* Go to the Interpreter page and click `+Create`
* Fill the form using the following information:
  * Name: **Geode**
  * Interpreter: **geode**
* Leave default values and click on `Save`

If needed adjust the hostname and port for the locator by clicking on `edit` on the upper right corner of the Interpreter.

## Executing Geode OQL queries

* Click on `Notebook` and create a new Note
* Type the following text and click on Play icon on the right.

```
%geode
select * from /Stocks
```
* Select different visualizations
* Use common SQL instructions such as `order` or `group by` and run different queries

**Note**: Make sure you have a Geode cluster up and running and data loaded in the /Stocks region.

## Enabling Geode Spark Connector


## Executing SparkSQL with Geode data



## References

* [Website](http://zeppelin.incubator.apache.org)
* [Documentation](https://zeppelin.incubator.apache.org/docs/index.html)
