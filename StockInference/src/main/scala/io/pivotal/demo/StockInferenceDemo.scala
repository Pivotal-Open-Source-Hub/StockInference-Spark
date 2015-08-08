package io.pivotal.demo

import org.apache.spark.mllib.regression.StreamingLinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkContext
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.DataFrame
import io.pivotal.gemfire.spark.connector._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating, MatrixFactorizationModel}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.classification.{LogisticRegressionWithLBFGS, LogisticRegressionModel}
import org.apache.spark.mllib.evaluation.MulticlassMetrics


/**
 * @author fmelo
 */
object StockInferenceDemo {

  def main(args: Array[String]) {

    
    val conf = new SparkConf().setMaster("local").setAppName("StreamingLinearRegression")
    
    conf.set("spark.gemfire.locators", "localhost[10334]");
    
    //val ssc = new StreamingContext(conf, Seconds(args(2).toLong))

    val sc = new SparkContext(conf);
    val sqlContext = new SQLContext(sc);
    
    val df = sqlContext.gemfireOQL("SELECT s.Change, s.DaysHigh FROM /Stocks s")
    val rdd = df.rdd;
    
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
