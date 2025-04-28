package com.github.pmviva.todo.list.api.controller;

import com.github.pmviva.todo.list.api.model.Todo;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface TodoController {

    ResponseEntity<Todo> createTodo(Todo todo);

    ResponseEntity<Page<Todo>> getTodos(Pageable pageable, Optional<Boolean> completed);

    ResponseEntity<Todo> getTodo(UUID id);

    ResponseEntity<Todo> updateTodo(UUID id, Todo todo);

    ResponseEntity<Void> deleteTodo(UUID id);
}
