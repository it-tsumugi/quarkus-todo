package com.example.todo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "todos")
public class Todo extends PanacheEntity {

    // PanacheEntity が id フィールド（Long）を自動生成する

    @Column(nullable = false)
    public String title;

    public boolean completed = false;

    public LocalDateTime createdAt = LocalDateTime.now();

    public static List<Todo> listAllOrdered() {
        return listAll(Sort.ascending("createdAt"));
    }
}
