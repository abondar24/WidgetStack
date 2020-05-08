package org.abondar.industrial.widgetstack.service;

import org.abondar.industrial.widgetstack.model.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StorageTest {

    private  Map<String,Widget> storage = Collections.synchronizedMap(new LinkedHashMap<>());


    @BeforeEach
    public void setUp() {
        storage.clear();
        var widget1 = new Widget();
        widget1.setId(UUID.randomUUID().toString());
        widget1.setZIndex(6);

        var widget2 = new Widget();
        widget2.setId(UUID.randomUUID().toString());
        widget2.setZIndex(3);

        storage.put(widget1.getId(),widget1);
        storage.put(widget2.getId(),widget2);

    }

    @Test
    public void findMaxTest() {
        var res =  storage.isEmpty() ? Integer.MAX_VALUE : storage.values()
                .stream()
                .max(java.util.Comparator.naturalOrder())
                .get()
                .getZIndex() + 1;

        assertEquals(7, res);

    }

    @Test
    public void addStorageTest() {
        var widget = new Widget();
        widget.setId("test");
        widget.setZIndex(3);

        storage.put(widget.getId(), widget);

        storage.forEach((k,v)->{
            var oldZindex = v.getZIndex();
            if (oldZindex>=widget.getZIndex()){
                v.setZIndex(oldZindex + 1);
            }
        });
        assertEquals(3, storage.size());
        assertEquals(4,storage.get(widget.getId()).getZIndex());
    }


}
