package org.abondar.industrial.widgetstack.service;

import org.abondar.industrial.widgetstack.model.Widget;
import org.abondar.industrial.widgetstack.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class})
@ActiveProfiles("db")
public class WidgetServiceDbTest {

    @MockBean
    private WidgetRepository repository;

    @Autowired
    private WidgetService service;

    @Test
    public void testCreate() {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        when(repository.save(widget)).thenReturn(widget);

        service.create(widget);

        verify(repository, times(1)).save(widget);
    }

    @Test
    public void testUpdate() throws Exception {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        when(repository.save(widget)).thenReturn(widget);

        var res = service.create(widget);
        when(repository.save(res)).thenReturn(res);

        res.setxCoord(3);
        res.setyCoord(4);
        service.update(res);

        verify(repository, times(1)).save(widget);
        verify(repository, times(1)).save(res);
    }

    @Test
    public void testFindById() {
        when(repository.findById("test")).thenReturn(Optional.of(new Widget()));

        service.getById("test", true);
        verify(repository, times(1)).findById("test");
    }

    @Test
    public void testFindByIdFromStorage() {
        var widget = new Widget(1, 1, 1, 1, 1);
        when(repository.save(widget)).thenReturn(widget);
        when(repository.findById(widget.getId())).thenReturn(Optional.empty());

        widget = service.create(widget);
        var res = service.getById(widget.getId(), true);

        assertEquals(widget.getId(), res.getId());
    }

    @Test
    public void testGetWidgets() {
        var widget = new Widget(1, 1, 1, 1, 1);
        var widget1 = new Widget(1, 1, 2, 1, 1);

        var widgets = List.of(widget, widget1);
        var pageRes = new PageImpl<>(widgets);
        when(repository.findAll(any(PageRequest.class))).thenReturn(pageRes);

        assertEquals(widget.getzIndex(), pageRes
                .get()
                .collect(Collectors.toList())
                .get(0)
                .getzIndex());

    }

    @Test
    public void testDelete() throws Exception {
        service.getStorage().clear();

        var widget = new Widget(1, 1, 1, 1, 1);
        when(repository.save(widget)).thenReturn(widget);

        var res = service.create(widget);

        service.delete(res.getId());

    }
}
