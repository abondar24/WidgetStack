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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WidgetService {

    private final int MAX_LIMIT = 500;
    private final WidgetRepository repository;
    private final List<Widget> storage;
    @Value("${db-store}")
    private boolean dbStore;


    @Autowired
    public WidgetService(WidgetRepository repository) {
        this.repository = repository;
        this.storage = new ArrayList<>();

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

        return storage.isEmpty() ? Integer.MAX_VALUE : storage.stream()
                .max(Collections.reverseOrder())
                .get()
                .getzIndex() + 1;
    }

    private void fillStorage(Widget widget) {
        storage.sort(Collections.reverseOrder());


        if (!storage.isEmpty()) {

            var insertIndex = storage.indexOf(
                    storage
                            .stream()
                            .filter(wd -> wd.getzIndex().equals(widget.getzIndex()))
                            .findFirst()
                            .orElseGet(() -> storage.get(storage.size() - 1)));

            storage.add(insertIndex, widget);

            for (int i = insertIndex + 1; i < storage.size(); i++) {
                var elem = storage.get(i);
                var zIndex = elem.getzIndex();
                if (zIndex >= widget.getzIndex()) {
                    elem.setzIndex(zIndex + 1);
                }
            }
        } else {
            storage.add(widget);
        }

    }


    public synchronized Widget update(Widget widget,String id) throws WidgetNotFoundException, NullAtrributeException {
        checkWidget(widget);

        var found = find(id);
        if (found.isEmpty()) {
            throw new WidgetNotFoundException(widget.getId());
        }

        widget.setId(id);
        widget.setLastModified(new Date());

        var replaceIndex = storage.indexOf(
                storage.stream()
                        .filter(wd -> wd.getId().equals(widget.getId()))
                        .findFirst()
                        .get()
        );

        storage.set(replaceIndex, widget);

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

        Optional<Widget> res;
        if (dbStore && fromDb) {
            res = repository.findById(id);
            if (res.isPresent()) {
                return res.get();
            }
        }

        res = find(id);

        return res.orElse(null);
    }

    public List<Widget> getWidgets(int offset, int limit, boolean fromDb) throws TooManyWidgetsException {
        if (limit > MAX_LIMIT) {
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
        storage.sort(Collections.reverseOrder());

        return storage.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }


    public synchronized void delete(String id) throws WidgetNotFoundException {
        var widget = find(id);
        if (widget.isEmpty()) {
            throw new WidgetNotFoundException(id);
        }

        storage.remove(widget.get());
        if (dbStore) {
            repository.delete(widget.get());
        }
    }


    private Optional<Widget> find(String id) {
        return storage
                .stream()
                .filter(wd -> wd.getId().equals(id))
                .findFirst();
    }

    public List<Widget> getStorage() {
        return storage;
    }
}
