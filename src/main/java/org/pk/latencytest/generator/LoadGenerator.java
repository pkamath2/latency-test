package org.pk.latencytest.generator;


import org.HdrHistogram.Histogram;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Author: purnimakamath
 */
public class LoadGenerator {


    static int TOTAL_THREADS = 800;
    static int requestId = -1;
    static int ITERATIONS = TOTAL_THREADS * 35;
    static long[] MEASUREMENTS = new long[ITERATIONS];

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_THREADS);

        Histogram hdrHistogram = new Histogram(3);

        for (int i=0;i<35;i++) {
            List<Callable<Latency>> tasks = new ArrayList<>();
            IntStream.range(0,TOTAL_THREADS).forEach(j -> tasks.add(() -> callService(++requestId)));
            try {
                List<Future<Latency>> results = executorService.invokeAll(tasks);
                for (int t=0;t<results.size();t++) {
                    Latency latency = results.get(t).get();
                    MEASUREMENTS[latency.getRequestId()]=latency.getTimeTaken();
                    hdrHistogram.recordValue(latency.getTimeTaken());
                    System.out.println(latency);
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e.getMessage() +":" + requestId);
            }
        }
        executorService.shutdown();

        Arrays.sort(MEASUREMENTS);

        System.out.println("Latency Measurements with HDR Histogram- ");
        System.out.println("Minimum = "+hdrHistogram.getMinValue());
        System.out.println("Mean = "+hdrHistogram.getMean());
        System.out.println("50%'ile = "+hdrHistogram.getValueAtPercentile(50));
        System.out.println("90%'ile = "+hdrHistogram.getValueAtPercentile(90));
        System.out.println("95%'ile = "+hdrHistogram.getValueAtPercentile(95));
        System.out.println("99%'ile = "+hdrHistogram.getValueAtPercentile(99));
        System.out.println("99.9%'ile = "+hdrHistogram.getValueAtPercentile(99.9));
        System.out.println("99.99%'ile = "+hdrHistogram.getValueAtPercentile(99.99));
        System.out.println("Maximum = "+ hdrHistogram.getMaxValue());


        System.out.println("Latency Measurements with arrays of raw data- ");
        System.out.println("Minimum = "+MEASUREMENTS[0]);
        System.out.println("50%'ile = "+MEASUREMENTS[ITERATIONS/2]);
        System.out.println("90%'ile = "+MEASUREMENTS[(int)(90*ITERATIONS/100)]);
        System.out.println("95%'ile = "+MEASUREMENTS[(int)(95*ITERATIONS/100)]);
        System.out.println("99%'ile = "+MEASUREMENTS[(int)(99*ITERATIONS/100)]);
        System.out.println("99.9%'ile = "+MEASUREMENTS[(int)(999*ITERATIONS/1000)]);
        System.out.println("99.99%'ile = "+MEASUREMENTS[(int)(9999*ITERATIONS/10000)]);
        System.out.println("Maximum = "+ MEASUREMENTS[ITERATIONS-1]);

    }

    private static Latency callService(int requestId) {
        Latency latency = null;
        URL loadUrl;
        HttpURLConnection connection = null;
        try {
            long startTime = System.currentTimeMillis();
            loadUrl = new URL("http://localhost:9090/pk/test?id=" + (++requestId));
            connection = (HttpURLConnection) loadUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(20000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Error :" + requestId);
            }
            latency = new Latency(requestId, (System.currentTimeMillis() - startTime), startTime);
        } catch (Exception e) {
            System.out.println(e.getMessage() +":" + requestId);
        } finally {
            if (connection != null) connection.disconnect();
        }
        return latency;
    }
}
