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

import com.github.pmviva.todo.list.api.model.Todo;
import com.github.pmviva.todo.list.api.service.TodoService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/todos")
public class DefaultTodoController implements TodoController {

    private final TodoService todoService;

    @Autowired
    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public DefaultTodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> createTodo(@RequestBody @Valid Todo todo) {
        Todo result = todoService.createTodo(todo);

        URI uri = MvcUriComponentsBuilder.fromMethodName(DefaultTodoController.class, "getTodo", result.getId())
                .build()
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Todo>> getTodos(
            @PageableDefault Pageable pageable, @RequestParam Optional<Boolean> completed) {
        return ResponseEntity.ok(todoService.getTodos(pageable, completed));
    }

    @GetMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> getTodo(@PathVariable UUID id) {
        return ResponseEntity.ok(todoService.getTodo(id));
    }

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> updateTodo(@PathVariable UUID id, @RequestBody @Valid Todo todo) {
        return ResponseEntity.ok(todoService.updateTodo(id, todo));
    }

    @DeleteMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
