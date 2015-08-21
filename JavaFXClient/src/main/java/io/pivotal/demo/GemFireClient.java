package io.pivotal.demo;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;

import java.io.IOException;

/**
 * @author wmarkito
 *         2015
 */
public class GemFireClient {

    private static String regionName = "Predictions";

    public static void main(String[] args) throws IOException, InterruptedException {
        Region stocksRegion = cache.getRegion(regionName);

        stocksRegion.registerInterest("ALL_KEYS");

        while (true) {
            System.out.println("Size:"+stocksRegion.size());
            Thread.sleep(5000);
        }

//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        bufferedReader.readLine();

    }

    private static ClientCache cache = new ClientCacheFactory()
            .set("name", "GemFireClient")
            .set("cache-xml-file", "client.xml")
            .create();

}
