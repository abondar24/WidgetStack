package org.abondar.industrial.widgetstack.service;

import org.abondar.industrial.widgetstack.exception.NullAtrributeException;
import org.abondar.industrial.widgetstack.exception.TooManyWidgetsException;
import org.abondar.industrial.widgetstack.exception.WidgetNotFoundException;
import org.abondar.industrial.widgetstack.model.Widget;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith({SpringExtension.class})
public class WidgetServiceTest {

    @Autowired
    private WidgetService service;

    @Test
    public void testCreate() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);

        widget = service.create(widget);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(1, widget.getzIndex());
    }

    @Test
    public void testCreateNullZ() {
        var widget = new Widget(1, 1, 1, 1, 1);
        service.create(widget);

        var widget1 = new Widget(1, 1, null, 1, 1);
        widget1 = service.create(widget1);

        assertEquals(2, widget1.getzIndex());
    }

    @Test
    public void testCreateNullZEmptyStorage() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, null, 1, 1);

        widget = service.create(widget);

        assertEquals(Integer.MAX_VALUE, widget.getzIndex());
    }


    @Test
    public void testFindById() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        var res = service.getById(widget.getId(), false);
        assertEquals(widget.getzIndex(), res.getzIndex());
    }

    @Test
    public void testUpdate() throws Exception {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        var creationDate = widget.getLastModified();

        widget.setxCoord(3);
        widget.setyCoord(4);
        Thread.sleep(300);
        widget = service.update(widget);

        assertEquals(3, widget.getxCoord());
        assertEquals(4, widget.getyCoord());
        assertNotEquals(creationDate, widget.getLastModified());

    }

    @Test
    public void testUpdateNullAttr() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        var res = service.create(widget);

        res.setxCoord(null);
        assertThrows(NullAtrributeException.class, () -> service.update(res));
    }

    @Test
    public void testUpdateWrongId() {
        var widget = new Widget(1, 1, 1, 1, 1);
        var res = service.create(widget);

        res.setId("test");
        assertThrows(WidgetNotFoundException.class, () -> service.update(res));
    }

    @Test
    public void testGetWidgets() throws Exception {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        var widget1 = new Widget(1, 1, 1, 1, 1);
        widget1 = service.create(widget1);

        var res = service.getWidgets(0, 3, false);

        assertEquals(2, res.size());

        assertEquals(1, res.get(0).getzIndex());
        assertEquals(widget1.getId(), res.get(0).getId());

        assertEquals(2, res.get(1).getzIndex());
        assertEquals(widget.getId(), res.get(1).getId());

    }

    @Test
    public void testGetWidgetsOffset() throws Exception {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        var widget1 = new Widget(1, 1, 1, 1, 1);
        service.create(widget1);

        var res = service.getWidgets(1, 3, false);

        assertEquals(1, res.size());

        assertEquals(2, res.get(0).getzIndex());
        assertEquals(widget.getId(), res.get(0).getId());

    }

    @Test
    public void testGetWidgetsMaxLimit() {
        assertThrows(TooManyWidgetsException.class, () -> service.getWidgets(0, 600, false));
    }

    @Test
    public void testDelete() throws Exception{
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        service.delete(widget.getId());
        assertTrue(service.getStorage().isEmpty());

    }

    @Test
    public void testDeleteNotFound() {

        assertThrows(WidgetNotFoundException.class,()-> service.delete("test"));

    }

}
