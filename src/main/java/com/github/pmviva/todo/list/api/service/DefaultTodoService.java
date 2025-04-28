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

package com.github.pmviva.todo.list.api.service;

import com.github.pmviva.todo.list.api.exception.NotFoundException;
import com.github.pmviva.todo.list.api.model.Todo;
import com.github.pmviva.todo.list.api.repository.TodoRepository;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DefaultTodoService implements TodoService {

    private static final String TODO_NOT_FOUND = "Todo not found";

    private final TodoRepository todoRepository;

    @Autowired
    public DefaultTodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Todo createTodo(@Nonnull Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public Page<Todo> getTodos(@Nonnull Pageable pageable, @Nonnull Optional<Boolean> completed) {
        if (completed.isPresent()) {
            return todoRepository.findByCompleted(pageable, completed.get());
        } else {
            return todoRepository.findAll(pageable);
        }
    }

    @Override
    public Todo getTodo(@Nonnull UUID id) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isPresent()) {
            return optionalTodo.get();
        } else {
            throw new NotFoundException(TODO_NOT_FOUND);
        }
    }

    @Override
    public Todo updateTodo(@Nonnull UUID id, @Nonnull Todo todo) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isPresent()) {
            return todoRepository.save(todo);
        } else {
            throw new NotFoundException(TODO_NOT_FOUND);
        }
    }

    @Override
    public void deleteTodo(@Nonnull UUID id) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isPresent()) {
            todoRepository.deleteById(id);
        } else {
            throw new NotFoundException(TODO_NOT_FOUND);
        }
    }
}
