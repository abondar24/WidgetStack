package org.abondar.industrial.widgetstack.exception;

import org.abondar.spring.ratelimitter.RateLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class WidgetExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({WidgetNotFoundException.class})
    public void handleNotFound(Exception ex,HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler({TooManyWidgetsException.class})
    public void handleBadRequest(Exception ex,HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler({NullAtrributeException.class})
    public void handleBadRequestAttrs(Exception ex,HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler({RateLimitException.class})
    public void handleTooManyRequests(Exception ex,HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage());
    }

}
