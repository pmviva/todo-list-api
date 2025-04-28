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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.pmviva.todo.list.api.exception.NotFoundException;
import com.github.pmviva.todo.list.api.model.Todo;
import com.github.pmviva.todo.list.api.repository.TodoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class DefaultTodoServiceTest {

    @InjectMocks
    private DefaultTodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    @Test
    public void testCreateTodo() {
        Todo todo = generateTodo();

        doReturn(todo).when(todoRepository).save(any(Todo.class));

        Todo result = todoService.createTodo(todo);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(todo);

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    public void testGetTodosWithCompleted() {
        List<Todo> todoList = generateTodoList();

        doReturn(new PageImpl<>(todoList)).when(todoRepository).findByCompleted(any(Pageable.class), anyBoolean());

        Page<Todo> result = todoService.getTodos(PageRequest.of(1, 10), Optional.of(Boolean.TRUE));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull().isEqualTo(todoList);

        verify(todoRepository, times(1)).findByCompleted(any(Pageable.class), anyBoolean());
    }

    @Test
    public void testGetTodosWithoutCompleted() {
        List<Todo> todoList = generateTodoList();

        doReturn(new PageImpl<>(todoList)).when(todoRepository).findAll(any(Pageable.class));

        Page<Todo> result = todoService.getTodos(PageRequest.of(1, 10), Optional.empty());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull().isEqualTo(todoList);

        verify(todoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetTodo() {
        Todo todo = generateTodo();
        doReturn(Optional.of(todo)).when(todoRepository).findById(any(UUID.class));

        assertDoesNotThrow(() -> {
            Todo result = todoService.getTodo(UUID.randomUUID());

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(todo);
        });

        verify(todoRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testGetTodoThrowsNotFoundException() {
        doReturn(Optional.empty()).when(todoRepository).findById(any(UUID.class));

        assertThrows(NotFoundException.class, () -> todoService.getTodo(UUID.randomUUID()));

        verify(todoRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testUpdateTodo() {
        Todo todo = generateTodo();

        doReturn(Optional.of(todo)).when(todoRepository).findById(any(UUID.class));
        doReturn(todo).when(todoRepository).save(any(Todo.class));

        assertDoesNotThrow(() -> {
            Todo result = todoService.updateTodo(UUID.randomUUID(), todo);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(todo);
        });

        verify(todoRepository, times(1)).findById(any(UUID.class));
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    public void testUpdateTodoThrowsNotFoundException() {
        doReturn(Optional.empty()).when(todoRepository).findById(any(UUID.class));

        assertThrows(NotFoundException.class, () -> todoService.updateTodo(UUID.randomUUID(), generateTodo()));

        verify(todoRepository, times(1)).findById(any(UUID.class));
        verify(todoRepository, times(0)).save(any(Todo.class));
    }

    @Test
    public void testDeleteTodo() {
        doReturn(Optional.of(generateTodo())).when(todoRepository).findById(any(UUID.class));
        doNothing().when(todoRepository).deleteById(any(UUID.class));

        assertDoesNotThrow(() -> todoService.deleteTodo(UUID.randomUUID()));

        verify(todoRepository, times(1)).findById(any(UUID.class));
        verify(todoRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void testDeleteTodoThrowsNotFoundException() {
        doReturn(Optional.empty()).when(todoRepository).findById(any(UUID.class));

        assertThrows(NotFoundException.class, () -> todoService.deleteTodo(UUID.randomUUID()));

        verify(todoRepository, times(1)).findById(any(UUID.class));
        verify(todoRepository, times(0)).deleteById(any(UUID.class));
    }

    private Todo generateTodo() {
        return new Todo("DESCRIPTION", false);
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
