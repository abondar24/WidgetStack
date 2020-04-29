package org.abondar.industrial.widgetstack.exception;

public class RateLimitException extends Exception{


    public RateLimitException(){
        super("Exceeded the allowed number of requests");
    }
}
