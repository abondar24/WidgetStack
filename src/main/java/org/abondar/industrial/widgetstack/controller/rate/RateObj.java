package org.abondar.industrial.widgetstack.controller.rate;

import java.util.concurrent.TimeUnit;

public class RateObj {
    private int limit;
    private long period;

    public RateObj(int limit, long period) {
        this.limit = limit;
        this.period = period;
    }

    public int getLimit() {
        return limit;
    }

    public long getPeriod() {
        return period;
    }

}
