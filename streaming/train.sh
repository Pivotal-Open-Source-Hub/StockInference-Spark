spark-submit --class io.pivotal.demo.StockInferenceDemo --driver-memory 1G \
--executor-memory 1G \
--jars ~/.m2/repository/io/pivotal/gemfire/spark/gemfire-spark-connector_2.10/0.5.0/gemfire-spark-connector_2.10-0.5.0.jar,$GEODE_HOME/lib/gemfire-core-dependencies.jar \
--master local[*] $PROJECT/StockInference/build/libs/StockInference-1.0.jar train
