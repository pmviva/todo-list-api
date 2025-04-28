/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Pablo Martin Viva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.pmviva.todo.list.api.controller;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.pmviva.todo.list.api.config.DataWebConfiguration;
import com.github.pmviva.todo.list.api.config.SecurityConfiguration;
import com.github.pmviva.todo.list.api.exception.NotFoundException;
import com.github.pmviva.todo.list.api.model.Todo;
import com.github.pmviva.todo.list.api.service.TodoService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import({DataWebConfiguration.class, SecurityConfiguration.class})
@WebMvcTest(DefaultTodoController.class)
public class DefaultTodoControllerTest {

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void testCreateTodo() throws Exception {
        Resource jsonResource = resourceLoader.getResource("classpath:json/controllers/todo/json-01.json");
        String json = IOUtils.toString(jsonResource.getInputStream(), StandardCharsets.UTF_8);

        Todo todo = generateTodoWithId();

        doReturn(todo).when(todoService).createTodo(any(Todo.class));

        mockMvc.perform(post("/api/v1/todos")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        header().string(
                                        HttpHeaders.LOCATION,
                                        containsString(String.format("/api/v1/todos/%s", todo.getId()))));

        verify(todoService, times(1)).createTodo(any(Todo.class));
    }

    @Test
    public void testGetTodosWithCompleted() throws Exception {
        Resource jsonResource = resourceLoader.getResource("classpath:json/controllers/todo/json-02.json");
        String json = IOUtils.toString(jsonResource.getInputStream(), StandardCharsets.UTF_8);

        PageImpl<Todo> page = new PageImpl<>(generateTodoList());

        doReturn(page).when(todoService).getTodos(any(Pageable.class), eq(Optional.of(Boolean.FALSE)));

        mockMvc.perform(get("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("completed", "false"))
                .andExpectAll(status().isOk(), content().json(json));

        verify(todoService, times(1)).getTodos(any(Pageable.class), eq(Optional.of(Boolean.FALSE)));
    }

    @Test
    public void testGetTodosWithoutCompleted() throws Exception {
        Resource jsonResource = resourceLoader.getResource("classpath:json/controllers/todo/json-02.json");
        String json = IOUtils.toString(jsonResource.getInputStream(), StandardCharsets.UTF_8);

        PageImpl<Todo> page = new PageImpl<>(generateTodoList());

        doReturn(page).when(todoService).getTodos(any(Pageable.class), eq(Optional.empty()));

        mockMvc.perform(get("/api/v1/todos").contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().json(json));

        verify(todoService, times(1)).getTodos(any(Pageable.class), eq(Optional.empty()));
    }

    @Test
    public void testGetTodo() throws Exception {
        Resource jsonResource = resourceLoader.getResource("classpath:json/controllers/todo/json-01.json");
        String json = IOUtils.toString(jsonResource.getInputStream(), StandardCharsets.UTF_8);

        doReturn(generateTodo()).when(todoService).getTodo(any(UUID.class));

        mockMvc.perform(get("/api/v1/todos/033feb09-fd25-49ff-b1af-d65ce5740eea")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().json(json));

        verify(todoService, times(1)).getTodo(any(UUID.class));
    }

    @Test
    public void testGetTodoThrowsNotFoundException() throws Exception {
        doThrow(new NotFoundException("Todo not found")).when(todoService).getTodo(any(UUID.class));

        mockMvc.perform(get("/api/v1/todos/033feb09-fd25-49ff-b1af-d65ce5740eea")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNotFound(), content().string(blankOrNullString()));

        verify(todoService, times(1)).getTodo(any(UUID.class));
    }

    @Test
    public void testUpdateTodo() throws Exception {
        Resource jsonResource = resourceLoader.getResource("classpath:json/controllers/todo/json-01.json");
        String json = IOUtils.toString(jsonResource.getInputStream(), StandardCharsets.UTF_8);

        doReturn(generateTodo()).when(todoService).updateTodo(any(UUID.class), any(Todo.class));

        mockMvc.perform(put("/api/v1/todos/033feb09-fd25-49ff-b1af-d65ce5740eea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().isOk(), content().json(json));

        verify(todoService, times(1)).updateTodo(any(UUID.class), any(Todo.class));
    }

    @Test
    public void testUpdateTodoThrowsNotFoundException() throws Exception {
        Resource jsonResource = resourceLoader.getResource("classpath:json/controllers/todo/json-01.json");
        String json = IOUtils.toString(jsonResource.getInputStream(), StandardCharsets.UTF_8);

        doThrow(new NotFoundException("Todo not found")).when(todoService).updateTodo(any(UUID.class), any(Todo.class));

        mockMvc.perform(put("/api/v1/todos/033feb09-fd25-49ff-b1af-d65ce5740eea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().isNotFound(), content().string(blankOrNullString()));

        verify(todoService, times(1)).updateTodo(any(UUID.class), any(Todo.class));
    }

    @Test
    public void testDeleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodo(any(UUID.class));

        mockMvc.perform(delete("/api/v1/todos/033feb09-fd25-49ff-b1af-d65ce5740eea")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNoContent(), content().string(blankOrNullString()));

        verify(todoService, times(1)).deleteTodo(any(UUID.class));
    }

    @Test
    public void testDeleteTodoThrowsNotFoundException() throws Exception {
        doThrow(new NotFoundException("Todo not found")).when(todoService).deleteTodo(any(UUID.class));

        mockMvc.perform(delete("/api/v1/todos/033feb09-fd25-49ff-b1af-d65ce5740eea")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNotFound(), content().string(blankOrNullString()));

        verify(todoService, times(1)).deleteTodo(any(UUID.class));
    }

    private Todo generateTodo() {
        return new Todo("DESCRIPTION", false);
    }

    private Todo generateTodoWithId() {
        Todo todo = generateTodo();
        todo.setId(UUID.randomUUID());

        return todo;
    }

    private List<Todo> generateTodoList() {
        return List.of(
                new Todo("DESCRIPTION 01", false),
                new Todo("DESCRIPTION 02", false),
                new Todo("DESCRIPTION 03", false),
                new Todo("DESCRIPTION 04", false),
                new Todo("DESCRIPTION 05", false));
    }
}
