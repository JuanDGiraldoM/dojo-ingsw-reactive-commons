package com.bancolombia.dojo.reactivecommons.rest;

import com.bancolombia.dojo.reactivecommons.config.Constants;
import com.bancolombia.dojo.reactivecommons.config.EventGateway;
import com.bancolombia.dojo.reactivecommons.config.TaskRepository;
import com.bancolombia.dojo.reactivecommons.messages.Whois;
import com.bancolombia.dojo.reactivecommons.model.Task;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private Constants constants;
    private TaskRepository repository;
    private EventGateway eventGateway;

    @GetMapping(path = "/tasks/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> listTasks(@PathVariable("name") String name) {
        return Mono.empty();
    }

    @PostMapping(path = "/tasks/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> saveTask(@PathVariable("name") String name, @RequestBody Task task) {
        if (name.equals(constants.getNameWho()))
            return repository.saveTask(task);
        else
            return eventGateway.emitWhoIs(Whois.builder().who(name).replyTo(constants.getAppName()).build());
        // TODO asociar respuesta desde el comando
    }

}
