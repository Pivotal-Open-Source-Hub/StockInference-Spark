package io.pivotal.demo;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.gemstone.gemfire.pdx.internal.PdxInstanceImpl;

import java.util.Properties;
import java.util.logging.Logger;

import sun.swing.UIAction;

/**
 * @author fmelo
 */
public class PredictionListener<K,V> extends CacheListenerAdapter<K, V> implements Declarable {
	
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
    
        	Double prediction = (double)instance.readField("predicted");
            Double ema = (double)instance.readField("ema");

            if (prediction>FinanceUI.maxY) FinanceUI.maxY = prediction;
            if (prediction<FinanceUI.minY) FinanceUI.minY = prediction;

            if (ema>FinanceUI.maxY) FinanceUI.maxY = ema;
            if (ema<FinanceUI.minY) FinanceUI.minY = ema;
            
            if (FinanceUI.getInstance() != null) {
            	FinanceUI.getInstance().getPredictionDataQueue().add((Number) prediction);
            	FinanceUI.getInstance().getEmaDataQueue().add((Number) ema);
                
                FinanceUI.yAxis.setUpperBound(FinanceUI.maxY);
                FinanceUI.yAxis.setLowerBound(FinanceUI.minY);
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