package io.pivotal.demo

import java.io.InputStream
import org.apache.spark.SparkConf
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.feature.StandardScaler
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
import java.io.BufferedReader
import java.io.InputStreamReader
import scala.util.parsing.json.JSON
import org.apache.spark.mllib.feature.StandardScalerModel
import java.io.File




/**
 * @author fmelo
 */
object StockInferenceDemo {

  val MODEL_PATH = "SparkModel"
  
  val SCALER_FILE = MODEL_PATH+"/scaler.obj"
  
  val conf = new SparkConf().setMaster("local[*]").setAppName("StockInferenceMLDemo")  
  conf.set("spark.gemfire.locators", "localhost[10334]");
  conf.set("spark.hadoop.validateOutputSpecs", "false")
  conf.set("spark.files.overwrite","true")
    
  val sc = new SparkContext(conf);
  
  val sqlContext = new SQLContext(sc);
  
  val numIterations = 2000  
  val stepSize = 0.2

  val algorithm = new LinearRegressionWithSGD()
  
  
  algorithm.setIntercept(true)
  algorithm.optimizer
    .setNumIterations(numIterations)
    .setStepSize(stepSize)
    
      
  def train() = {
    
    val df = sqlContext.gemfireOQL("SELECT t.entryTimestamp, t.close, t.ema, t.future_ema, t.rsi, t.ema_diff, t.low_diff, t.high_diff FROM /TechIndicators t");       
    df.registerTempTable("tech_indicators");
    
    val result = sqlContext.sql("select entryTimestamp, close, ema, future_ema, rsi, ema_diff, low_diff, high_diff  from tech_indicators t order by entryTimestamp desc LIMIT 1000")
    val rdd = result.rdd.cache()
        
    val dataset = rdd.map { line =>
      // tech indicators 
      val entryTimestamp = line.getLong(0)
      val close = line.getString(1).toDouble
      val ema = line.getString(2).toDouble
      val future_ema = line.getString(3).toDouble
      val rsi = line.getString(4).toDouble
      val ema_diff = line.getString(5).toDouble
      val low_diff = line.getString(6).toDouble
      val high_diff = line.getString(7).toDouble      
      
      LabeledPoint(future_ema, Vectors.dense(close, ema, rsi))
    }.cache()      
    
                 
    val scaler = new StandardScaler(withMean = true, withStd = true)
                     .fit(dataset.map(x => x.features))    
                     
                          
                     
    val scaledData = dataset
                    .map(x => 
                    LabeledPoint(x.label, 
                       scaler.transform(Vectors.dense(x.features.toArray)))).cache()
      
    val splits = scaledData.randomSplit(Array(0.8, 0.2), seed = 11L)
    val trainingData = splits(0).cache()
    val testingData = splits(1).cache()     

                       
    println("\nGot " + dataset.count() + " values from Gem. Using " + trainingData.count() + " for training and "+ testingData.count() + " for testing\n")                       
   
    
     val model = algorithm.run(trainingData)    
     val modelFileDirectory = new File(MODEL_PATH)
     
     if (modelFileDirectory.exists()) Utils.deleteRecursive(modelFileDirectory)
    
     // save the trained model
     model.save(sc, MODEL_PATH)
     // save the scaler
     sc.parallelize(Seq(scaler), 1).saveAsObjectFile(SCALER_FILE) 
     
     // Test model on training examples
      val valuesAndPreds = testingData.map { point =>
        val prediction = model.predict(point.features)
        (point.label, point.features, prediction)
      }
      // Print out features, actual and predicted values...
      valuesAndPreds.foreach({case (v, f, p) => 
          println(s"Features: ${f}, Predicted: ${p}, Actual: ${v}")})   
        
      val MSE = valuesAndPreds.map{case(v, f, p) => math.pow((v - p), 2)}.mean()
      println("training Mean Squared Error = " + MSE)    
    
    
  }
  
  
  def evaluate() ={

    // load the model
    val model = LinearRegressionModel.load(sc, MODEL_PATH)
    
    // load the scaler
    val scaler = sc.objectFile[StandardScalerModel](SCALER_FILE).first()
    
    val br = new BufferedReader(new InputStreamReader(System in))

    while (true){
      val line = br.readLine()
      val json = JSON.parseFull(line)
      
      val keyValueProps: Map[String,Any] = json.asInstanceOf[Map[String,Any]]
      
      val close = keyValueProps.get("close").toString().toDouble
      val ema = keyValueProps.get("ema").toString().toDouble
      val rsi = keyValueProps.get("rsi").toString().toDouble
      val ema_diff = keyValueProps.get("ema_diff").toString().toDouble
      val low_diff = keyValueProps.get("low_diff").toString().toDouble
      val high_diff = keyValueProps.get("high_diff").toString().toDouble      
          
      
      val input =  scaler.transform(Vectors.dense(close, ema, rsi, ema_diff, low_diff, high_diff))
      val prediction = model.predict(input)

      println(prediction)
      
    }
    
   
    
  }
  
  def printUsage()={
    // spark-submit --class io.pivotal.demo.StockInferenceDemo --driver-memory 4G --executor-memory 2G --master local[*] StockInference-1.0.jar
    println("Usage:  StockInferenceDemo <train|evaluate>")
  }
  
  def main(args: Array[String]) {

    if (args.length!=1){
      printUsage()
    }
    else if (args(0).equalsIgnoreCase("train")){
      train()
    }
    else if (args(0).equalsIgnoreCase("evaluate")){
      evaluate()
    }



  }
 
  
}
