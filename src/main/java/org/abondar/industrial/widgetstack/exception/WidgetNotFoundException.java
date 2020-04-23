package org.abondar.industrial.widgetstack.exception;

public class WidgetNotFoundException extends Exception{

    public WidgetNotFoundException(String id){
        super("Widget not found with id: "+id);
    }
}
