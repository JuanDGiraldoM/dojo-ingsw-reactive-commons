package com.bancolombia.dojo.reactivecommons.rest;

import com.bancolombia.dojo.reactivecommons.config.EventGateway;
import com.bancolombia.dojo.reactivecommons.config.TaskRepository;
import com.bancolombia.dojo.reactivecommons.model.Task;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
public class TaskController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.node.name}")
    private String nameWho;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private EventGateway eventGateway;

    @GetMapping(path = "/tasks/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Task>> listTasks(@PathVariable("name") String name) {
        return Mono.just(Collections.emptyList());
    }


    @PostMapping(path = "/tasks/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> saveTask(@PathVariable("name") String name, @RequestBody Task task) {

        if (name.equals(nameWho)) {
            return repository.saveTask(task);
        }
        else {
            eventGateway.emitWhoIs(name, appName);
            // TODO asociar respuesta desde el comando
        }

        return Mono.empty();
    }

}
