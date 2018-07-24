package org.pk.latencytest.generator;


import org.HdrHistogram.Histogram;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Author: purnimakamath
 */
public class LoadGenerator {

    static int requestId = -1;
    static int totalThreads = 100;
    static int iterations = totalThreads * 20;
    static long[] measurements = new long[iterations];

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        Histogram hdrHistogram = new Histogram(TimeUnit.MINUTES.toNanos(2),3);

        System.out.println("**Starting load generation**");
        for (int i=0;i<20;i++) {
            List<Callable<Latency>> tasks = new ArrayList<>();
            IntStream.range(0, totalThreads).forEach(j -> tasks.add(() -> callService(++requestId)));
            try {
                List<Future<Latency>> results = executorService.invokeAll(tasks);
                for (int t=0;t<results.size();t++) {
                    Latency latency = results.get(t).get();
                    measurements[latency.getRequestId()]=latency.getTimeTaken();
                    hdrHistogram.recordValue(latency.getTimeTaken());
                    //System.out.println(latency);
                }
                System.out.println("Iteration "+i+" complete");
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println(e.getMessage() +":" + requestId);
            }
        }
        System.out.println("**Completed load generation**");
        executorService.shutdown();

        Arrays.sort(measurements);

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

        hdrHistogram.outputPercentileDistribution(System.out, 1000.0);

        System.out.println("Latency Measurements with arrays of raw data- ");
        System.out.println("Minimum = "+ measurements[0]);
        System.out.println("50%'ile = "+ measurements[iterations /2]);
        System.out.println("90%'ile = "+ measurements[(int)(90* iterations /100)]);
        System.out.println("95%'ile = "+ measurements[(int)(95* iterations /100)]);
        System.out.println("99%'ile = "+ measurements[(int)(99* iterations /100)]);
        System.out.println("99.9%'ile = "+ measurements[(int)(999* iterations /1000)]);
        System.out.println("99.99%'ile = "+ measurements[(int)(9999* iterations /10000)]);
        System.out.println("Maximum = "+ measurements[iterations -1]);

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
            connection.setReadTimeout(50000);
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
