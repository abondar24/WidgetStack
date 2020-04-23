package org.abondar.industrial.widgetstack.exception;

public class TooManyWidgetsException extends Exception {

    public TooManyWidgetsException(){
        super("Can't read more than 500 widgets per request");
    }
}
