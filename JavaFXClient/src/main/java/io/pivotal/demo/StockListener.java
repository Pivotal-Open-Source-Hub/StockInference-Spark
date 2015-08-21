package io.pivotal.demo;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.gemstone.gemfire.pdx.internal.PdxInstanceImpl;

/**
 * @author wmarkito
 */
public class StockListener<K,V> extends CacheListenerAdapter<K, V> implements Declarable {
	
    Logger logger = Logger.getAnonymousLogger();
    
    
    
    
    @Override
	public void afterUpdate(EntryEvent<K, V> event) {
    	addToQueue((PdxInstanceImpl) event.getNewValue());
    }
    
    @Override
	public void afterCreate(EntryEvent<K, V> event) {
    	addToQueue((PdxInstanceImpl) event.getNewValue());
    }
    
	public void addToQueue(PdxInstanceImpl instance) {
        try {
    
        	List<String> names = instance.getFieldNames();
            // reading fields from the event
            Double close = Double.parseDouble(instance.readField("LastTradePriceOnly").toString());
            

            if (close>FinanceUI.maxY) FinanceUI.maxY = close;
            if (close<FinanceUI.minY) FinanceUI.minY = close;
          
            
            //Double close = (double) instance.readField("ema");
            //Double prediction = (double)instance.readField("predictedPeak");;

            
            if (FinanceUI.getInstance() != null) {
                FinanceUI.getInstance().getStockDataQueue().add((Number) close);
                FinanceUI.yAxis.setUpperBound(FinanceUI.maxY);
                FinanceUI.yAxis.setLowerBound(FinanceUI.minY);
                
              //  FinanceUI.getInstance().getPredictionDataQueue().add((Number) prediction);
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
            logger.severe("Problems parsing event for chart update:" + ex.getMessage());
        }

        logger.fine(String.format("Received afterCreate event for entry:  %s", instance.toString()));
    }

    @Override
    public void init(Properties props) {
        // do nothing
    }
}