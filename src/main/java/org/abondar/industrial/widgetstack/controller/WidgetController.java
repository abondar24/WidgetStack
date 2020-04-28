package org.abondar.industrial.widgetstack.controller;

import org.abondar.industrial.widgetstack.exception.NullAtrributeException;
import org.abondar.industrial.widgetstack.exception.TooManyWidgetsException;
import org.abondar.industrial.widgetstack.exception.WidgetNotFoundException;
import org.abondar.industrial.widgetstack.model.Filter;
import org.abondar.industrial.widgetstack.model.Widget;
import org.abondar.industrial.widgetstack.service.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/widget")
public class WidgetController {

    private final WidgetService service;

    @Autowired
    public WidgetController(WidgetService service) {
        this.service = service;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Widget> createWidget(@RequestBody Widget widget) {
        var res = service.create(widget);
        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<Widget> updateWidget(@RequestBody Widget widget) throws WidgetNotFoundException, NullAtrributeException {
        var res = service.update(widget);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "/find/{id}")

    public ResponseEntity<Widget> findWidget(@PathVariable String id, @RequestParam(name = "db") boolean fromDb) {
        var res = service.getById(id, fromDb);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "/find_many")
    public ResponseEntity<List<Widget>> findWidgets(@RequestParam int offset,
                                                    @RequestParam(defaultValue = "10") int limit,
                                                    @RequestParam(name = "db") boolean fromDb,
                                                    @RequestBody(required = false) Filter fp)
            throws TooManyWidgetsException {

        List<Widget> res;
        if (fp!=null){
            res = service.getFilteredWidgets(offset, limit,fromDb,fp);
        } else {
            res = service.getWidgets(offset, limit,fromDb);
        }

        return ResponseEntity.ok(res);
    }

    @DeleteMapping(path = "/delete")
    public void delete(@RequestParam String id) throws WidgetNotFoundException {
        service.delete(id);
    }
}
