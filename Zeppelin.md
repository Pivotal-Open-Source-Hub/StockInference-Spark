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

*This feature is experimental* 

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

* Access the Zeppelin interface at http://192.168.56.10:8080
* Click on **Interpreter** and select **Edit** under the `spark` interpreter. Add parameters at the end of the list:

|  Param | Value |
|-------------------|-------------|
| spark.gemfire.locators | localhost[10334] |
| spark.kryo.registrator | io.pivotal.gemfire.spark.connector.GemFireKryoRegistrator |

* Click on the notebook and load the libraries for the connector:
```
%dep
z.reset()
z.load("/home/vagrant/incubator-geode/gemfire-spark-connector/gemfire-spark-connector/target/scala-2.10/gemfire-spark-connector_2.10-0.5.0.jar")
z.load("/home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode/lib/gemfire-core-dependencies.jar")
z.load("/home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode/lib/gemfire-core-1.0.0-incubating-SNAPSHOT.jar")
```
*Click on the play icon (upper right of the note) to execute.*
*PS: The directories here assume you've created the VM as described in the [VM](VM.md) instructions. Update the paths properly if needed*

## Executing SparkSQL with Geode data

* Insert a new note in the notebook and copy the following content:

  ```
  %spark
  import io.pivotal.gemfire.spark.connector._
  import sqlContext.implicits._

  val stocks = sqlContext.gemfireOQL("SELECT Symbol, LastTradeDate, LastTradeTime, tradeTimestamp, Open, LastTradePriceOnly, DaysHigh, DaysLow, entryTimestamp FROM /Stocks s")
  stocks.registerTempTable("stocks")

  val predictions = sqlContext.gemfireOQL("SELECT entryTimestamp, close, ema, rsi, ema_diff, low_diff, high_diff, predicted FROM /Predictions s")

  predictions.registerTempTable("predictions")
  stocks.registerTempTable("stocks")

  val test = sqlContext.sql("select * from stocks s order by s.entryTimestamp desc")

  test.printSchema()
  ```

*Click on the play icon (upper right of the note) to execute.*

* Execute queries and play with different visualizations

Add another note in the notebook and use the `%sql` interpreter to query the tables we just registered.

```
%sql
select * from stocks
```

## References

* [Website](http://zeppelin.incubator.apache.org)
* [Documentation](https://zeppelin.incubator.apache.org/docs/index.html)
