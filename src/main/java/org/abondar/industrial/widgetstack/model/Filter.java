package org.abondar.industrial.widgetstack.model;

public class Filter {

    private  int xStart;

    private int yStart;

    private int xStop;

    private int yStop;

    public Filter(){}

    public int getxStart() {
        return xStart;
    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getXStop() {
        return xStop;
    }

    public void setxStop(int xStop) {
        this.xStop = xStop;
    }

    public int getYStop() {
        return yStop;
    }

    public void setyStop(int yStop) {
        this.yStop = yStop;
    }
}
