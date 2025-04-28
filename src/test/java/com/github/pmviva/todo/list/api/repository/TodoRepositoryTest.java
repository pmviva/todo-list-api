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

package com.github.pmviva.todo.list.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.pmviva.todo.list.api.config.TestcontainersConfiguration;
import com.github.pmviva.todo.list.api.model.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@Transactional
@DataJpaTest
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Sql("classpath:sql/repositories/todo/script-01.sql")
    @Rollback
    @Test
    public void testFindByCompleted() {
        Page<Todo> result;

        result = todoRepository.findByCompleted(PageRequest.of(1, 5), Boolean.FALSE);

        assertThat(result).isNotEmpty();
        assertThat(result.getNumberOfElements()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(12);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result).allMatch(todo -> Boolean.FALSE.equals(todo.getCompleted()));

        result = todoRepository.findByCompleted(PageRequest.of(1, 5), Boolean.TRUE);

        assertThat(result).isNotEmpty();
        assertThat(result.getNumberOfElements()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(12);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result).allMatch(todo -> Boolean.TRUE.equals(todo.getCompleted()));
    }
}
