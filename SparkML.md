# Spark ML

## Building the StocksSpark project

StocksSpark is an sbt project. Just execute `sbt package` from project folder and the package will be generated under `target`.

## Submitting Spark jobs

There are two convenient scripts that submit spark jobs for **training** and **evaluating** the model, respectively `train.sh` and `evaluate.sh`.

![Spark Cluster Overview](http://spark.apache.org/docs/latest/img/cluster-overview.png)

### Training the model
```
spark-submit --class io.pivotal.demo.StockInferenceDemo --driver-memory 1G \
  --executor-memory 1G \
  --jars ~/.m2/repository/io/pivotal/gemfire/spark/gemfire-spark-connector_2.10/0.5.0/gemfire-spark-connector_2.10-0.5.0.jar,$GEODE_HOME/lib/gemfire-core-dependencies.jar \
  --master local[*] $PROJECT/StocksSpark/target/scala-2.10/stocksspark_2.10-1.0.jar train
```

### Evaluating

```
spark-submit --class io.pivotal.demo.StockInferenceDemo --driver-memory 1G \
  --executor-memory 1G \
  --jars ~/.m2/repository/io/pivotal/gemfire/spark/gemfire-spark-connector_2.10/0.5.0/gemfire-spark-connector_2.10-0.5.0.jar,$GEODE_HOME/lib/gemfire-core-dependencies.jar \
  --master local[*] $PROJECT/StocksSpark/target/scala-2.10/stocksspark_2.10-1.0.jar evaluate
```

## Automation through SpringXD

```
stream create --name training --definition "trigger --fixedDelay=300 | shell --command='./train.sh'" --deploy
```
## Querying results through Zeppelin

### Using Geode Interpreter

On Zeppelin UI:
```
%geode.oql
select * from /Predictions order by entryTimestamp
```
### Using Spark SQL Interpreter

```
%sql
PENDING
```

## References

* [SBT - Scala building tool](http://www.scala-sbt.org/)
* [Apache Spark ML Programming Guide](http://spark.apache.org/docs/latest/ml-guide.html)
* [Apache Spark Cluster Overview](http://spark.apache.org/docs/latest/cluster-overview.html)
* [Apache Geode Spark Connector](https://github.com/apache/incubator-geode/tree/develop/gemfire-spark-connector)
