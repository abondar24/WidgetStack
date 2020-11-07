package org.abondar.industrial.widgetstack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.industrial.widgetstack.model.Filter;
import org.abondar.industrial.widgetstack.model.Widget;
import org.abondar.industrial.widgetstack.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Date;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WidgetRepository repository;

    @Test
    public void testCreate() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(any(String.class))))
                .andExpect(jsonPath("$.z", is(any(Integer.class))))
                .andExpect(jsonPath("$.lastModified",is(notNullValue())));
    }

    @Test
    public void testUpdate() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        var resp = mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);

        widget.setXCoord(7);
        body =  mapper.writeValueAsString(widget);

        mockMvc.perform(put("/widget/{id}",widget.getId())
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x", is(7)))
                .andExpect(jsonPath("$.lastModified",is(notNullValue())));
    }

    @Test
    public void testUpdateIdNotFound() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        widget.setId("test");
        widget.setLastModified(new Date());

        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(put("/widget/{id}","test")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNullAttr() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        var resp = mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);
        widget.setHeight(null);

        mockMvc.perform(put("/widget/{id}",widget.getId())
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindWidget() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        var resp = mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);

        mockMvc.perform(get("/widget/{id}",widget.getId())
                .header("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(widget.getId())));
    }


    @Test
    public void testFindMany() throws Exception {
        repository.deleteAll();
        Thread.sleep(5000);

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(get("/widget/many")
                .queryParam("offset","0")
                .queryParam("limit","3")
                .header("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    public void testFindManyDefaultLimit() throws Exception {
        repository.deleteAll();
        Thread.sleep(5000);

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(get("/widget/many")
                .queryParam("offset","0")
                .header("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }


    @Test
    public void testFindFiltered() throws Exception {
        repository.deleteAll();
        Thread.sleep(5000);

        var widget = new Widget(50, 50, 1, 100, 100);
        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        var filter = new Filter();
        filter.setXStart(0);
        filter.setXStop(100);
        filter.setYStart(0);
        filter.setYStop(150);
        var filterBody = mapper.writeValueAsString(filter);


        mockMvc.perform(get("/widget/filter")
                .queryParam("offset","0")
                .header("db","true")
                .content(filterBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)));
    }

    @Test
    public void testFindManyBigLimit() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get("/widget/many")
                .queryParam("offset","0")
                .queryParam("limit","600")
                .header("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        var resp = mockMvc.perform(post("/widget")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);


        mockMvc.perform(delete("/widget")
                .queryParam("id",widget.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
