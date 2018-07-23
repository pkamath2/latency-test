package org.pk.latencytest.generator;

/**
 * Author: purnimakamath
 */
public class Latency {

    private int requestId;
    private long timeTaken;
    private long startTime;

    public Latency(int requestId, long timeTaken, long startTime) {
        this.requestId = requestId;
        this.timeTaken = timeTaken;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return  requestId +
                "," + timeTaken +
                "," + startTime;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
