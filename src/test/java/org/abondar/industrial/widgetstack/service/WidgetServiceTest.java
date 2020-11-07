package org.abondar.industrial.widgetstack.service;

import org.abondar.industrial.widgetstack.exception.NullAtrributeException;
import org.abondar.industrial.widgetstack.exception.TooManyWidgetsException;
import org.abondar.industrial.widgetstack.exception.WidgetNotFoundException;
import org.abondar.industrial.widgetstack.model.Filter;
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
        assertEquals(1, widget.getZIndex());
    }

    @Test
    public void testCreateNullZ() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        service.create(widget);

        var widget1 = new Widget(1, 1, null, 1, 1);
        widget1 = service.create(widget1);

        assertEquals(2, widget1.getZIndex());
    }

    @Test
    public void testCreateNullZEmptyStorage() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, null, 1, 1);

        widget = service.create(widget);

        assertEquals(Integer.MAX_VALUE, widget.getZIndex());
    }


    @Test
    public void testFindById() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        var res = service.getById(widget.getId(), false);
        assertEquals(widget.getZIndex(), res.getZIndex());
    }

    @Test
    public void testUpdate() throws Exception {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget = service.create(widget);

        var creationDate = widget.getLastModified();

        widget.setXCoord(3);
        widget.setYCoord(4);
        Thread.sleep(300);
        widget = service.update(widget,widget.getId());

        assertEquals(3, widget.getXCoord());
        assertEquals(4, widget.getYCoord());
        assertNotEquals(creationDate, widget.getLastModified());

    }

    @Test
    public void testUpdateNullAttr() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        var res = service.create(widget);

        res.setXCoord(null);
        assertThrows(NullAtrributeException.class, () -> service.update(res,res.getId()));
    }

    @Test
    public void testUpdateWrongId() {
        var widget = new Widget(1, 1, 1, 1, 1);
        var res = service.create(widget);

        res.setId("test");
        assertThrows(WidgetNotFoundException.class, () -> service.update(res,res.getId()));
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

        assertEquals(1, res.get(0).getZIndex());
        assertEquals(widget1.getId(), res.get(0).getId());

        assertEquals(2, res.get(1).getZIndex());
        assertEquals(widget.getId(), res.get(1).getId());

    }

    @Test
    public void testGetFiltered()throws Exception {
        service.getStorage().clear();

        var widget = new Widget(50, 50, 1, 100, 100);
        service.create(widget);

        var widget1 = new Widget(50, 100, 1, 100, 100);
        service.create(widget1);

        var widget2 = new Widget(100, 100, 1, 100, 100);
        service.create(widget2);

        var filter = new Filter();
        filter.setXStart(0);
        filter.setXStop(100);
        filter.setYStart(0);
        filter.setYStop(150);

        var res = service.getFilteredWidgets(0, 3, false,filter);

        assertEquals(2, res.size());
        assertEquals(100, res.get(0).getYCoord());
        assertEquals(50, res.get(1).getYCoord());

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

        assertEquals(2, res.get(0).getZIndex());
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
