package org.abondar.industrial.widgetstack.controller;

import org.abondar.industrial.widgetstack.exception.NullAtrributeException;
import org.abondar.industrial.widgetstack.exception.TooManyWidgetsException;
import org.abondar.industrial.widgetstack.exception.WidgetNotFoundException;
import org.abondar.industrial.widgetstack.model.Filter;
import org.abondar.industrial.widgetstack.model.Widget;
import org.abondar.industrial.widgetstack.service.WidgetService;
import org.abondar.spring.ratelimitter.RateLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/widget")
@RateLimit
public class WidgetController {

    private final WidgetService service;

    @Autowired
    public WidgetController(WidgetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Widget> createWidget(@RequestBody Widget widget) {
        var res = service.create(widget);
        return ResponseEntity.ok(res);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Widget> updateWidget(@PathVariable String id, @RequestBody Widget widget) throws WidgetNotFoundException, NullAtrributeException {
        var res = service.update(widget, id);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Widget> findWidget(@PathVariable String id,
                                             @RequestHeader(name = "db") boolean fromDb) {
        var res = service.getById(id, fromDb);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "/many")
    @RateLimit(requests = 3, period = 5000)
    public ResponseEntity<List<Widget>> findWidgets(@RequestParam int offset,
                                                    @RequestParam(defaultValue = "10") int limit,
                                                    @RequestHeader(name = "db") boolean fromDb)
            throws TooManyWidgetsException {

        List<Widget> res = service.getWidgets(offset, limit, fromDb);

        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/filter")
    public ResponseEntity<List<Widget>> findFilteredWidgets(@RequestParam int offset,
                                                    @RequestParam(defaultValue = "10") int limit,
                                                    @RequestHeader(name = "db") boolean fromDb,
                                                    @RequestBody Filter filter) throws TooManyWidgetsException{

        List<Widget> res = service.getFilteredWidgets(offset, limit, fromDb, filter);


        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    public void delete(@RequestParam String id) throws WidgetNotFoundException {
        service.delete(id);
    }
}
