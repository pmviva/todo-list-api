package com.github.pmviva.todo.list.api.service;

import com.github.pmviva.todo.list.api.model.Todo;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoService {

    Todo createTodo(@Nonnull Todo todo);

    Page<Todo> getTodos(@Nonnull Pageable pageable, @Nonnull Optional<Boolean> completed);

    Todo getTodo(@Nonnull UUID id);

    Todo updateTodo(@Nonnull UUID id, @Nonnull Todo todo);

    void deleteTodo(@Nonnull UUID id);
}
