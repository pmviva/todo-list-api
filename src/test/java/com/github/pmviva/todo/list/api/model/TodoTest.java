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

package com.github.pmviva.todo.list.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TodoTest {

    private static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidateDescriptionNotBlank() {
        Set<ConstraintViolation<Todo>> result;

        Todo todo = new Todo();

        todo.setCompleted(Boolean.FALSE);

        todo.setDescription(null);

        result = validator.validate(todo);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).anyMatch(constraint -> "description"
                .equals(constraint.getPropertyPath().toString()));

        todo.setDescription("");

        result = validator.validate(todo);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).anyMatch(constraint -> "description"
                .equals(constraint.getPropertyPath().toString()));

        todo.setDescription(" ");

        result = validator.validate(todo);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).anyMatch(constraint -> "description"
                .equals(constraint.getPropertyPath().toString()));

        todo.setDescription("DESCRIPTION");

        result = validator.validate(todo);
        assertThat(result).isEmpty();
    }

    @Test
    public void testValidateCompletedNotNull() {
        Set<ConstraintViolation<Todo>> result;

        Todo todo = new Todo();

        todo.setDescription("DESCRIPTION");

        todo.setCompleted(null);

        result = validator.validate(todo);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).anyMatch(constraint -> "completed"
                .equals(constraint.getPropertyPath().toString()));

        todo.setCompleted(Boolean.FALSE);

        result = validator.validate(todo);
        assertThat(result).isEmpty();
    }

    @Test
    public void testEquals() {
        Todo todo1 = new Todo("DESCRIPTION", Boolean.FALSE);
        Todo todo2 = new Todo("DESCRIPTION", Boolean.FALSE);

        assertThat(todo1).isEqualTo(todo2);

        Todo todo3 = new Todo("DESCRIPTION OTHER", Boolean.FALSE);

        assertThat(todo1).isNotEqualTo(todo3);
        assertThat(todo2).isNotEqualTo(todo3);

        Todo todo4 = new Todo("DESCRIPTION", Boolean.TRUE);

        assertThat(todo1).isNotEqualTo(todo4);
        assertThat(todo2).isNotEqualTo(todo4);
    }

    @Test
    public void testHashCode() {
        Todo todo1 = new Todo("DESCRIPTION", Boolean.FALSE);
        Todo todo2 = new Todo("DESCRIPTION", Boolean.FALSE);

        assertThat(todo1.hashCode()).isEqualTo(todo2.hashCode());

        Todo todo3 = new Todo("DESCRIPTION OTHER", Boolean.FALSE);

        assertThat(todo1.hashCode()).isNotEqualTo(todo3.hashCode());
        assertThat(todo2.hashCode()).isNotEqualTo(todo3.hashCode());

        Todo todo4 = new Todo("DESCRIPTION", Boolean.TRUE);

        assertThat(todo1.hashCode()).isNotEqualTo(todo4.hashCode());
        assertThat(todo2.hashCode()).isNotEqualTo(todo4.hashCode());
    }
}
