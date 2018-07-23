package org.pk.latencytest.generator;

import org.HdrHistogram.Histogram;

import java.util.Arrays;

/**
 * Author: purnimakamath
 */
public class MockLoadGenerator {

    public static void main(String[] args) {

        Histogram hdrHistogram = new Histogram(3);

        // Mock latencies, where all requests are recorded to respond in 5 ms. Just one outlier of 20 secs.
        int[] mockLatencies = Arrays.copyOf(new int[]{5},10000);
        mockLatencies[500]=20000;

        for (int t=0;t<mockLatencies.length;t++) {
            hdrHistogram.recordValue(mockLatencies[t]);
        }

        System.out.println("Latency Measurements with HDR Histogram- ");
        System.out.println("Minimum = "+hdrHistogram.getMinValue());
        System.out.println("Mean = "+hdrHistogram.getMean());
        System.out.println("50%'ile = "+hdrHistogram.getValueAtPercentile(50));
        System.out.println("90%'ile = "+hdrHistogram.getValueAtPercentile(90));
        System.out.println("95%'ile = "+hdrHistogram.getValueAtPercentile(95));
        System.out.println("99%'ile = "+hdrHistogram.getValueAtPercentile(99)); //99%'ile will be less than mean
        System.out.println("99.9%'ile = "+hdrHistogram.getValueAtPercentile(99.9));
        System.out.println("99.99%'ile = "+hdrHistogram.getValueAtPercentile(99.99));
        System.out.println("Maximum = "+ hdrHistogram.getMaxValue());

    }
}
