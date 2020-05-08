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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WidgetService {

    private final WidgetRepository repository;
    private final Map<String, Widget> storage;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private static final List<Function<Widget, Object>> NOT_NULL_FIELDS = List.of(
            Widget::getId,
            Widget::getHeight,
            Widget::getWidth,
            Widget::getZIndex,
            Widget::getXCoord,
            Widget::getYCoord,
            Widget::getLastModified
    );


    private final boolean dbStore;


    @Autowired
    public WidgetService(WidgetRepository repository, @Value("${db-store}") boolean dbStore) {
        this.repository = repository;
        this.dbStore = dbStore;
        this.storage = new ConcurrentHashMap<>();

    }

    public Widget create(Widget widget) {
        writeLock.lock();
        try {
            var uuid = UUID.randomUUID().toString();
            widget.setId(uuid);
            widget.setLastModified(new Date());

            if (widget.getZIndex() == null) {
                widget.setZIndex(getMaxZIndex());
            }

            fillStorage(widget);

            if (dbStore) {
                repository.save(widget);
            }

            return new Widget(widget);
        } finally {
            writeLock.unlock();
        }

    }

    private Integer getMaxZIndex() {
        return storage.values()
                .stream()
                .max(java.util.Comparator.naturalOrder())
                .map(w -> w.getZIndex() + 1)
                .orElse(Integer.MAX_VALUE);

    }

    private void fillStorage(Widget widget) {
        storage.values().stream()
                .filter(v -> v.getZIndex() >= widget.getZIndex())
                .forEach(v -> v.setZIndex(v.getZIndex() + 1));
        storage.put(widget.getId(), widget);


    }


    public Widget update(Widget widget, String id) throws WidgetNotFoundException, NullAtrributeException {
        readLock.lock();
        try {
            checkWidget(widget);

            if (storage.get(id) == null) {
                throw new WidgetNotFoundException(widget.getId());
            }
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            widget.setId(id);
            widget.setLastModified(new Date());

            storage.replace(id, widget);
            if (dbStore) {
                repository.save(widget);
            }

            return new Widget(widget);
        } finally {
            writeLock.unlock();
        }

    }


    private synchronized void checkWidget(Widget widget) throws NullAtrributeException {
        if (NOT_NULL_FIELDS.stream().anyMatch(g -> g.apply(widget) == null)) {
            throw new NullAtrributeException();
        }
    }



    public Widget getById(String id, boolean fromDb) {
        readLock.lock();
        try {
            if (dbStore && fromDb) {
                Optional<Widget> res = repository.findById(id);
                if (res.isPresent()) {
                    return res.get();
                }
            }

            return storage.get(id);
        } finally {
            readLock.unlock();
        }

    }

    public List<Widget> getWidgets(int offset, int limit, boolean fromDb) throws TooManyWidgetsException {
        readLock.lock();
        try {
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
        } finally {
            readLock.unlock();
        }

    }

    public List<Widget> getFilteredWidgets(int offset, int limit, boolean fromDb, Filter filter) throws TooManyWidgetsException {
        readLock.lock();
        try {
            return getWidgets(offset, limit, fromDb)
                    .stream()
                    .filter(wd -> matchesFilter(filter, wd))
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }


    }

    private synchronized boolean matchesFilter(Filter filter, Widget widget) {

        var fWidth = filter.getXStop() - filter.getYStart();
        var fHeight = filter.getYStop() - filter.getYStart();

        return widget.getWidth() <= fWidth && widget.getHeight() <= fHeight &&
                filter.getXStop() > widget.getXCoord() &&
                filter.getYStop() > widget.getYCoord();

    }

    private List<Widget> getWidgetsFromDb(int offset, int limit) {
        readLock.lock();
        try {
            var page = PageRequest.of(offset, limit, Sort.by("zIndex").ascending());

            return repository
                    .findAll(page)
                    .getContent();
        } finally {
            readLock.unlock();
        }
    }

    private List<Widget> getWidgetsFromStorage(int offset, int limit) {
            return storage.values()
                    .stream()
                    .sorted(Widget::compareTo)
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
    }


    public void delete(String id) throws WidgetNotFoundException {
        writeLock.lock();
        try {
            var widget = storage.remove(id);
            if (widget==null) {
                throw new WidgetNotFoundException(id);
            }

            storage.remove(id);
            if (dbStore) {
                repository.delete(widget);
            }
        } finally {
            writeLock.unlock();
        }

    }


    public Map<String, Widget> getStorage() {
        return storage;
    }
}
