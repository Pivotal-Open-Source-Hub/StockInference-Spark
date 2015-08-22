package io.pivotal.demo;
/**
 * @author wmarkito
 *         2015
 */

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FinanceUI extends Application {

    private static final int MAX_DATA_POINTS = 200;
    private int xSeriesDataStock = 0;
    private int xSeriesDataPrediction = 0;
    private XYChart.Series stockPriceSeries;
    private XYChart.Series emaPriceSeries;
    private XYChart.Series predictionSeries;
    private ConcurrentLinkedQueue<Number> stockDataQueue = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> emaDataQueue = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> predictionDataQueue = new ConcurrentLinkedQueue<Number>();
    private static FinanceUI instance;
    private final static String stocksRegionName = "Stocks";
    private final static String predictionsRegionName = "Predictions";
    private static Region stocksRegion;
    private static Region predictionsRegion;

    static double minY = Double.MAX_VALUE;
    static double maxY = Double.MIN_VALUE;
    
    public ConcurrentLinkedQueue<Number> getPredictionDataQueue() {
        return predictionDataQueue;
    }

    public ConcurrentLinkedQueue<Number> getStockDataQueue() {
        return stockDataQueue;
    }

    public ConcurrentLinkedQueue<Number> getEmaDataQueue() {
        return emaDataQueue;
    }

    public static FinanceUI getInstance() {
        return instance;
    }

    public static Region getStocksRegion() {
        return stocksRegion;
    }

    private static final String fxTitle = "Stock Inference Demo - SpringXD + Geode + R + Spark MLLib";

    private static ClientCache cache = new ClientCacheFactory()
            .set("name", "GemFireClient"+ LocalDateTime.now())
            .set("cache-xml-file", "client.xml")
            .create();

    static NumberAxis xAxis;
    static NumberAxis yAxis;

    public static void main(String[] args) {
        stocksRegion = cache.getRegion(stocksRegionName);
        stocksRegion.registerInterest("ALL_KEYS");

        predictionsRegion = cache.getRegion(predictionsRegionName);
        predictionsRegion.registerInterest("ALL_KEYS");
        launch(args);
    }

    private void init(Stage primaryStage) {
        instance = this;

        xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(true);
        xAxis.setLabel("Time");

        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(true);
        xAxis.setMinorTickVisible(false);

        yAxis = new NumberAxis();        
        yAxis.setAutoRanging(false);
        yAxis.setForceZeroInRange(false);
        //yAxis.setLowerBound(210.4);
        //yAxis.setUpperBound(212);
        
        yAxis.setLabel("Stock Price ($)");

        //-- Chart
        final LineChart<Number, Number> sc = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {

            }
        };
        sc.setCursor(Cursor.CROSSHAIR);
        sc.setAnimated(false);
        sc.setId("stockChart");
//        sc.setTitle("Stock Price");


        //-- Chart Series
        stockPriceSeries = new XYChart.Series<Number, Number>();
        stockPriceSeries.setName("Last Close");

        emaPriceSeries = new XYChart.Series<Number, Number>();
        emaPriceSeries.setName("Med Avg");

        predictionSeries = new XYChart.Series<Number, Number>();
        predictionSeries.setName("Predicted Med Avg.");


        sc.getData().addAll(stockPriceSeries, emaPriceSeries, predictionSeries);
        sc.getStylesheets().add("style.css");
        sc.applyCss();

        primaryStage.setScene(new Scene(sc));
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle(fxTitle);
        init(stage);
//        stage.setMinHeight(600);
        stage.setMinWidth(850);
        stage.show();

        //-- Prepare Timeline
        prepareTimeline();
    }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 50; i++) {
            if (stockDataQueue.isEmpty()) break;
            stockPriceSeries.getData().add(new AreaChart.Data(xSeriesDataStock++, stockDataQueue.remove()));
                        
            if (predictionDataQueue.isEmpty() || emaDataQueue.isEmpty()) break;
            predictionSeries.getData().add(new AreaChart.Data(xSeriesDataPrediction, predictionDataQueue.remove()));
            emaPriceSeries.getData().add(new AreaChart.Data(xSeriesDataPrediction, emaDataQueue.remove()));
            xSeriesDataPrediction++;
            
//            series3.getData().add(new AreaChart.Data(xSeriesData++, dataQ3.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        
        if (stockPriceSeries.getData().size() > MAX_DATA_POINTS) {
            stockPriceSeries.getData().remove(0, stockPriceSeries.getData().size() - MAX_DATA_POINTS);
        }
        if (predictionSeries.getData().size() > MAX_DATA_POINTS) {
            predictionSeries.getData().remove(0, predictionSeries.getData().size() - MAX_DATA_POINTS);
        }
        
        if (emaPriceSeries.getData().size() > MAX_DATA_POINTS) {
            emaPriceSeries.getData().remove(0, emaPriceSeries.getData().size() - MAX_DATA_POINTS);
        }

        xAxis.setLowerBound(xSeriesDataPrediction - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesDataStock - 1);
           
        
    }
}
