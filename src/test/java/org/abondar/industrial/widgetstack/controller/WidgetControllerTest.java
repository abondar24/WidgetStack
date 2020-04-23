package org.abondar.industrial.widgetstack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        mockMvc.perform(post("/widget/create")
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

        var resp = mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);

        widget.setxCoord(7);
        body =  mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget/update")
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
        widget.setId(UUID.randomUUID().toString());
        widget.setLastModified(new Date());
        var body =  mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget/update")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNullAttr() throws Exception {
        repository.deleteAll();

        var widget = new Widget();
        var body =  mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget/update")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindWidget() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        var resp = mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);

        mockMvc.perform(get("/widget/find/{id}",widget.getId())
                .queryParam("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(widget.getId())));
    }


    @Test
    public void testFindMany() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(get("/widget/find_many")
                .queryParam("offset","0")
                .queryParam("limit","3")
                .queryParam("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    public void testFindManyDefaultLimit() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(get("/widget/find_many")
                .queryParam("offset","0")
                .queryParam("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    public void testFindManyBigLimit() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get("/widget/find_many")
                .queryParam("offset","0")
                .queryParam("limit","600")
                .queryParam("db","true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete() throws Exception {
        repository.deleteAll();

        var widget = new Widget(1, 1, 1, 1, 1);
        var body = mapper.writeValueAsString(widget);

        var resp = mockMvc.perform(post("/widget/create")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        widget = mapper.readValue(resp,Widget.class);


        mockMvc.perform(delete("/widget/delete")
                .queryParam("id",widget.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
