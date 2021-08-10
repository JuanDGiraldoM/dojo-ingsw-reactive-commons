package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.model.Task;
import com.bancolombia.dojo.reactivecommons.model.TaskList;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@NoArgsConstructor
public class TaskRepository {
    private static final List<Task> taskList = new CopyOnWriteArrayList<>();

    public Mono<Void> saveTask(Task task) {
        taskList.add(task);
        return Mono.empty();
    }

    public Mono<TaskList> getTaskList() {
        TaskList list = TaskList.builder().taskList(taskList).build();
        return Mono.just(list);
    }
}
