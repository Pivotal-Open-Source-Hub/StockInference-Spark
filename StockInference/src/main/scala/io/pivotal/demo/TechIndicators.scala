package io.pivotal.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

/**
 * @author fmelo
 */
object TechIndicators {
  

  def calculateAvg(rdd: RDD[Row], columnNumber: Integer, numPeriods: Integer) : Double = {
       rdd.take(numPeriods)                      
                        .map(s => 
                          s.getString(columnNumber).toDouble
                        )
                        .reduce((a, b) => a + b) / numPeriods;
                      
  }
    
  
}