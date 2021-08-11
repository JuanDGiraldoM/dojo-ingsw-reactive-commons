package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.model.Task;
import com.bancolombia.dojo.reactivecommons.model.TaskList;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@NoArgsConstructor
public class TaskRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepository.class);
    private static final List<Task> taskList = new CopyOnWriteArrayList<>();

    public Mono<String> saveTask(Task task) {
        return Mono.just(taskList.add(task))
                .doOnSuccess(s -> LOGGER.info("Task '{}' saved", task.getName()))
                .thenReturn("Task saved!");
    }

    public Mono<TaskList> getTaskList() {
        return Mono.just(TaskList.builder().taskList(taskList).build());
    }
}
