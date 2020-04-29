package org.abondar.industrial.widgetstack.controller.rate;

import org.abondar.industrial.widgetstack.controller.WidgetController;
import org.abondar.industrial.widgetstack.exception.RateLimitException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateInterceptor implements HandlerInterceptor {

    private final AtomicInteger requests = new AtomicInteger();
    private final ConcurrentMap<String, RateObj> methodLimits = new ConcurrentHashMap<>();
    private RateObj overallLimit;

    private long startBlock;

    private static final String START_PATH = "/widget";

    private void readRateLimit() {
        var ctrl = WidgetController.class;
        var annotation = ctrl.getAnnotation(RateLimit.class);
        if (annotation != null) {
            overallLimit = new RateObj(annotation.requests(), annotation.period());
        }

        var methods = ctrl.getMethods();
        for (Method method : methods) {

            var rateAnn = method.getAnnotation(RateLimit.class);
            if (rateAnn != null) {
                var methodRate = new RateObj(rateAnn.requests(), rateAnn.period());


                if (method.getAnnotation(GetMapping.class) != null) {
                    var path = START_PATH + method.getAnnotation(GetMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(PostMapping.class) != null) {
                    var path = START_PATH + method.getAnnotation(PostMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(PutMapping.class) != null) {
                    var path = START_PATH + method.getAnnotation(PutMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(DeleteMapping.class) != null) {
                    var path = START_PATH + method.getAnnotation(DeleteMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(PatchMapping.class) != null) {
                    var path = method.getAnnotation(PatchMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(RequestMapping.class) != null) {
                    var path = START_PATH + method.getAnnotation(RequestMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }
            }

        }

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requests.getAndIncrement();

        if (requests.get()==1){
            startBlock = System.currentTimeMillis();
        }

        checkLimit(requests.get(), request.getRequestURI());

        return true;
    }

    @Async
    public void checkLimit(int incomingRequests, String methodURI) throws Exception {
        readRateLimit();


        RateObj activeLimit;
        if (methodLimits.containsKey(methodURI)) {
            activeLimit = methodLimits.get(methodURI);
        } else {
            activeLimit = overallLimit;
        }

        if (incomingRequests > activeLimit.getLimit()) {
            if (System.currentTimeMillis()-startBlock>activeLimit.getPeriod()){
               requests.set(0);
            } else {
                throw new RateLimitException();
            }

        }

    }


}
