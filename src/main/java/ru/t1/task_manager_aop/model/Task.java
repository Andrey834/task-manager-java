package ru.t1.task_manager_aop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.t1.task_manager_aop.enums.TaskStatus;

import java.util.Objects;

@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_task")
    @SequenceGenerator(name = "seq_task", allocationSize = 10, sequenceName = "SEQ_TASK")
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(userId, task.userId) &&
               Objects.equals(title, task.title) &&
               Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, title, description);
    }
}
