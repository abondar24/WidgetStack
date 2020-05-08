package org.abondar.industrial.widgetstack;

import org.abondar.spring.ratelimitter.RateConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RateConfig.class})
public class WidgetStackApplication {

    public static void main(String[] args) {
        SpringApplication.run(WidgetStackApplication.class, args);
    }
}
