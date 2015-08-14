/**
 * Created by wmarkito on 8/13/15.
 */

import io.pivotal.gemfire.spark.connector._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.rdd.RDDFunctions._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

object HelloWorld {

  def main(args: Array[String]) {
    print("Hello world - Thank you guys!")
// Create the context with a 1 second batch size
//    val sparkConf = new SparkConf()().setAppName("NetworkWordCount").set(GemFireLocatorPropKey, args(2))
//    val ssc = new StreamingContext()(sparkConf, Seconds(1))
//    ssc.checkpoint(".")

//    val stocks = sqlContext.gemfireOQL("SELECT s.Change, s.DaysHigh, s.entryTimestamp FROM /Stocks s")
//    stocks.registerTempTable("stocks")
  }

}
