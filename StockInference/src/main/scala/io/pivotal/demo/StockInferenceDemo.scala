package io.pivotal.demo

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.regression.StreamingLinearRegressionWithSGD
import org.apache.spark.rdd._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.SQLContext

import io.pivotal.gemfire.spark.connector._


/**
 * @author fmelo
 */
object StockInferenceDemo {

  
  
  
  def getTrainingData(sqlContext: SQLContext) {
    
    val result = sqlContext.sql("select * from stocks s order by s.entryTimestamp desc limit 100000")
    val rdd = result.rdd
    
    case class NameValueWithLag(name: String, value: Int, lag: Int)
    val cnt = rdd.count() - 1
  
    // CREATE THE RDD TO TRAIN THE ML ALGORITHM
    // EMA_LAG, CLOSE, EMA
    // http://stackoverflow.com/questions/31538007/is-there-an-rdd-transform-function-that-looks-at-neighboring-elements
    rdd.
      zipWithIndex.
      flatMap{case (x, i) => (0 to 1).map(lag => (i - lag, (i, x)))}.
      groupByKey.
      filter{ case (k, v) => k != cnt}.
      values.
      map(vals => {
          val sorted = vals.toArray.sortBy(_._1).map(_._2)
          if (sorted.length == 1) {
              NameValueWithLag(sorted(0).name, sorted(0).value, sorted(0).value)
          } else {
              NameValueWithLag(
                 sorted(1).name, sorted(1).value,
                 sorted(1).value - sorted(0).value
              )
          }
      })        
    
    
    
    
    val dataset = rdd.map { line => 
      LabeledPoint(line.getString(0).toDouble, Vectors.dense(line.getString(1).toDouble))
    }.cache()
      
    
    
  }
  
  
  
  def main(args: Array[String]) {

    
    val conf = new SparkConf().setMaster("local[*]").setAppName("StreamingLinearRegression")
    
    conf.set("spark.gemfire.locators", "localhost[10334]");
    
    //val ssc = new StreamingContext(conf, Seconds(args(2).toLong))

    val sc = new SparkContext(conf);
    val sqlContext = new SQLContext(sc);
    
    val df = sqlContext
            .gemfireOQL("SELECT s.Change, s.DaysHigh, s.entryTimestamp FROM /Stocks s ");   
    
    df.registerTempTable("stocks");
    val result = sqlContext.sql("select * from stocks s order by s.entryTimestamp desc limit 10")
       
    

            
    val rdd = result.rdd
    
    val numPeriods = 10
    

    val averageChange = TechIndicators.calculateAvg(rdd, 0, numPeriods);
                      
    
    
    val dataset = rdd.map { line => 
      LabeledPoint(line.getString(0).toDouble, Vectors.dense(line.getString(1).toDouble))
     }.cache()
    
    val splits = dataset.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)     
         
     
    val numValues = dataset.count    
    println("Got " + numValues + " values from Gem")
     
    val numIterations = 1000
    val model = LinearRegressionWithSGD.train(training, numIterations)
     
     
    /*
    
val res = rdd.map(t => (t._1, (t._2.foo, 1))).reduceByKey((x,y) => (x._1+x._2, y._1+y._2)).collect    


input
  .map{ case (k, v) => (k, (1, v, v*v)) }
  .reduceByKey { case ((c1, s1, ss1), (c2, s2, ss2)) => (c1+c2, s1+s2, ss1+ss2) }
  .map { case (k, (count, sum, sumsq)) => (k, sumsq/count - (sum/count * sum/count)) }
  
  
        val grouped = rdd.groupByKey().mapValues { mcs => 
          val values = mcs.map(_.foo.toDouble) 
          val n = values.count(x => true) 
          val sum = values.sum 
          val sumSquares = values.map(x => x * x).sum 
          val stddev = math.sqrt(n * sumSquares - sum * sum) / n 
          print("stddev: " + stddev) 
          stddev 
        } 

        
import org.apache.spark.util.StatCounter 

val a = ordersRDD.join(ordersRDD).map{case((partnerid, itemid),((matchedida, pricea), (matchedidb, priceb))) => ((matchedida, matchedidb), (if(priceb > 0) (pricea/priceb).toDouble else 0.toDouble))} 
        .groupByKey 
        .mapValues( value => org.apache.spark.util.StatCounter(value)) 
        .take(5) 
        .foreach(println) 

output: 

((2383,2465),(count: 4, mean: 0.883642, stdev: 0.086068, max: 0.933333, min: 0.734568)) 
((2600,6786),(count: 4, mean: 2.388889, stdev: 0.559094, max: 3.148148, min: 1.574074)) 
((2375,2606),(count: 6, mean: 0.693981, stdev: 0.305744, max: 1.125000, min: 0.453704)) 
((6780,2475),(count: 2, mean: 0.827549, stdev: 0.150991, max: 0.978541, min: 0.676558)) 
((2475,2606),(count: 7, mean: 3.975737, stdev: 3.356274, max: 9.628572, min: 0.472222))


  
scala> data.mapValues((_, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).mapValues{ case (sum, count) => (1.0 * sum)/count}.collectAsMap()
  

*/
     /*
      *  ONLY VALUES FROM 0 to 9 POSSIBLE  
      *
    val model = new LogisticRegressionWithLBFGS()
        .setNumClasses(10)
        .run(training)
 
 * 
 *     
 */
    
 
/*    
    
// Compute raw scores on the test set.
val scoreAndLabels = test.map { point =>
  val score = model.predict(point.features)
  (score, point.label)
}

// Get evaluation metrics.
val metrics = new BinaryClassificationMetrics(scoreAndLabels)
val auROC = metrics.areaUnderROC()

println("Area under ROC = " + auROC)

// Save and load model
model.save(sc, "myModelPath")
val sameModel = SVMModel.load(sc, "myModelPath")    
    
    
  */   
//    LinearRegressionWithSGD.train(training, 100);
    //val model = ALS.train(training, 8, 10, 5);
  /*
   * 
   * 
   * 
   * val ranks = List(8, 12)
    val lambdas = List(1.0, 10.0)
    val numIters = List(10, 20)
    var bestModel: Option[MatrixFactorizationModel] = None
    var bestValidationRmse = Double.MaxValue
    var bestRank = 0
    var bestLambda = -1.0
    var bestNumIter = -1
    for (rank <- ranks; lambda <- lambdas; numIter <- numIters) {
      val model = ALS.train(training, rank, numIter, lambda)
      val validationRmse = computeRmse(model, validation, numValidation)
      println("RMSE (validation) = " + validationRmse + " for the model trained with rank = "
        + rank + ", lambda = " + lambda + ", and numIter = " + numIter + ".")
      if (validationRmse < bestValidationRmse) {
        bestModel = Some(model)
        bestValidationRmse = validationRmse
        bestRank = rank
        bestLambda = lambda
        bestNumIter = numIter
      }
    }

    val testRmse = computeRmse(bestModel.get, test, numTest)

    println("The best model was trained with rank = " + bestRank + " and lambda = " + bestLambda
      + ", and numIter = " + bestNumIter + ", and its RMSE on the test set is " + testRmse + ".")
   * 
   * 
   * 
   * 
   * 
   */
    
    /*
     * val input = dataFrame.map { line => val fields = line.split(",")
     * ( "(" fields(0) ")" )}
     */
    
    //sc.
    
    //val trainingData = ssc.textFileStream(args(0)).map(LabeledPoint.parse)
    //val testData = ssc.textFileStream(args(1)).map(LabeledPoint.parse)

    /*
    val model = new StreamingLinearRegressionWithSGD()
      .setInitialWeights(Vectors.zeros(args(3).toInt))

    model.trainOn(trainingData)
    model.predictOnValues(testData.map(lp => (lp.label, lp.features))).print()

    ssc.start()
    ssc.awaitTermination()
*/
  }
  
}
