package org.abondar.industrial.widgetstack.service;

import org.abondar.industrial.widgetstack.exception.NullAtrributeException;
import org.abondar.industrial.widgetstack.exception.TooManyWidgetsException;
import org.abondar.industrial.widgetstack.exception.WidgetNotFoundException;
import org.abondar.industrial.widgetstack.model.Filter;
import org.abondar.industrial.widgetstack.model.Widget;
import org.abondar.industrial.widgetstack.repository.WidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WidgetService {

    private final WidgetRepository repository;
    private final Map<String, Widget> storage;
    @Value("${db-store}")
    private boolean dbStore;


    @Autowired
    public WidgetService(WidgetRepository repository) {
        this.repository = repository;
        this.storage = Collections
                .synchronizedMap(new LinkedHashMap<>(500, .75f, true));

    }

    public synchronized Widget create(Widget widget) {
        var uuid = UUID.randomUUID().toString();
        widget.setId(uuid);
        widget.setLastModified(new Date());

        if (widget.getzIndex() == null) {
            widget.setzIndex(getMaxZindex());
        }

        fillStorage(widget);

        if (dbStore) {
            repository.save(widget);
        }


        return new Widget(widget);
    }

    private Integer getMaxZindex() {
        return storage.isEmpty() ? Integer.MAX_VALUE : storage.values()
                .stream()
                .min(java.util.Comparator.naturalOrder())
                .get()
                .getzIndex() + 1;
    }

    private void fillStorage(Widget widget) {
        storage.put(widget.getId(), widget);
        if (storage.size()>1) {
            storage.forEach((k,v)->{
                var oldZIndex = v.getzIndex();
                if (oldZIndex>=widget.getzIndex() && !v.getId().equals(widget.getId())){
                    v.setzIndex(oldZIndex + 1);
                }
            });
        }
    }


    public synchronized Widget update(Widget widget, String id) throws WidgetNotFoundException, NullAtrributeException {
        checkWidget(widget);

        if (storage.get(id) == null) {
            throw new WidgetNotFoundException(widget.getId());
        }

        widget.setId(id);
        widget.setLastModified(new Date());

        storage.replace(id, widget);
        if (dbStore) {
            repository.save(widget);
        }

        return new Widget(widget);
    }

    private void checkWidget(Widget widget) throws NullAtrributeException {
        if (widget.getId() == null) {
            throw new NullAtrributeException();
        }

        if (widget.getHeight() == null) {
            throw new NullAtrributeException();
        }

        if (widget.getWidth() == null) {
            throw new NullAtrributeException();
        }

        if (widget.getLastModified() == null) {
            throw new NullAtrributeException();
        }

        if (widget.getxCoord() == null) {
            throw new NullAtrributeException();
        }

        if (widget.getyCoord() == null) {
            throw new NullAtrributeException();
        }

        if (widget.getzIndex() == null) {
            throw new NullAtrributeException();
        }

    }

    public Widget getById(String id, boolean fromDb) {

        if (dbStore && fromDb) {
            Optional<Widget> res = repository.findById(id);
            if (res.isPresent()) {
                return res.get();
            }
        }

        return storage.get(id);
    }

    public List<Widget> getWidgets(int offset, int limit, boolean fromDb) throws TooManyWidgetsException {
        int maxLimit = 500;
        if (limit > maxLimit) {
            throw new TooManyWidgetsException();
        }

        if (dbStore && fromDb) {
            var res = getWidgetsFromDb(offset, limit);
            if (!res.isEmpty()) {
                return res;
            }

        }
        return getWidgetsFromStorage(offset, limit);
    }

    public List<Widget> getFilteredWidgets(int offset, int limit, boolean fromDb, Filter filter) throws TooManyWidgetsException {
        return getWidgets(offset, limit, fromDb)
                .stream()
                .filter(wd -> matchesFilter(filter, wd))
                .collect(Collectors.toList());

    }

    private boolean matchesFilter(Filter filter, Widget widget) {

        var fWidth = filter.getxStop() - filter.getyStart();
        var fHeight = filter.getyStop() - filter.getyStart();

        return widget.getWidth() <= fWidth && widget.getHeight() <= fHeight &&
                filter.getxStop() > widget.getxCoord() &&
                filter.getyStop() > widget.getyCoord();

    }

    private List<Widget> getWidgetsFromDb(int offset, int limit) {
        var page = PageRequest.of(offset, limit, Sort.by("zIndex").ascending());

        return repository
                .findAll(page)
                .get()
                .collect(Collectors.toList());

    }

    private List<Widget> getWidgetsFromStorage(int offset, int limit) {

        return storage.values()
                .stream()
                .sorted(Widget::compareTo)
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }


    public synchronized void delete(String id) throws WidgetNotFoundException {
        var widget = storage.get(id);
        if (widget == null) {
            throw new WidgetNotFoundException(id);
        }

        storage.remove(id);
        if (dbStore) {
            repository.delete(widget);
        }
    }


    public Map<String, Widget> getStorage() {
        return storage;
    }
}
