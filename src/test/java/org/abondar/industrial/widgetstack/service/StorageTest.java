package org.abondar.industrial.widgetstack.service;

import org.abondar.industrial.widgetstack.model.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StorageTest {

    private List<Widget> storage = new ArrayList<>();


    @BeforeEach
    public void setUp() {
        storage.clear();
        var widget1 = new Widget();
        widget1.setzIndex(6);

        var widget2 = new Widget();
        widget2.setzIndex(3);

        storage.add(widget1);
        storage.add(widget2);
        storage.sort(Collections.reverseOrder());

    }

    @Test
    public void findMaxTest() {
        var res = storage.stream()
                .max(Collections.reverseOrder())
                .get()
                .getzIndex() + 1;

        assertEquals(7, res);

    }

    @Test
    public void addStorageTest() {
        var widget = new Widget();
        widget.setId("test");
        widget.setzIndex(3);


        var insertIndex = storage.indexOf(
                storage
                        .stream()
                        .filter(wd -> wd.getzIndex().equals(widget.getzIndex()))
                        .findFirst()
                        .get()
        );

        storage.add(insertIndex, widget);

        for (int i = insertIndex + 1; i < storage.size(); i++) {
            var elem = storage.get(i);
            var zIndex = elem.getzIndex();
            if (zIndex >= widget.getzIndex()) {
                elem.setzIndex(zIndex + 1);
            }
        }

        assertEquals(3, storage.size());
        assertEquals(3,storage.get(0).getzIndex());
        assertEquals(4,storage.get(1).getzIndex());
        assertEquals(7,storage.get(2).getzIndex());
    }


}
