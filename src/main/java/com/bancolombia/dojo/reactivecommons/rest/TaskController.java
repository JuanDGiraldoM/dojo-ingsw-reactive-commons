package com.bancolombia.dojo.reactivecommons.rest;

import com.bancolombia.dojo.reactivecommons.config.Constants;
import com.bancolombia.dojo.reactivecommons.gateways.CommandGateway;
import com.bancolombia.dojo.reactivecommons.gateways.EventGateway;
import com.bancolombia.dojo.reactivecommons.gateways.ReplyRouter;
import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import com.bancolombia.dojo.reactivecommons.messages.Whois;
import com.bancolombia.dojo.reactivecommons.model.Task;
import com.bancolombia.dojo.reactivecommons.model.TaskList;
import com.bancolombia.dojo.reactivecommons.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final Constants constants;
    private final TaskRepository repository;
    private final EventGateway eventGateway;
    private final ReplyRouter replyRouter;
    private final CommandGateway commandGateway;

    private final ConcurrentHashMap<String, String> routingTable = new ConcurrentHashMap<>();

    @GetMapping(path = "/tasks/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Task>> listTasks(@PathVariable("name") String name) {
        if (name.equals(constants.getNameWho())) {
            return repository.getTaskList().map(TaskList::getTaskList);
        }
        else {
            if (routingTable.containsKey(name)) {
                return commandGateway.getRemoteTasks(name, constants.getAppName())
                        .map(TaskList::getTaskList);
            }

            return eventGateway.emitWhoIs(Whois.builder().who(name).replyTo(constants.getAppName()).build())
                    .then(replyRouter.register(name)
                            .flatMap(this::saveRoute)
                            .flatMap(saveWho -> commandGateway.getRemoteTasks(saveWho.getAppName(), constants.getAppName()))
                    )
                    .map(TaskList::getTaskList);
        }
    }

    @PostMapping(path = "/tasks/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> saveTask(@PathVariable("name") String name, @RequestBody Task task) {
        if (name.equals(constants.getNameWho()))
            return repository.saveTask(task);
        else {
            if (routingTable.containsKey(name)) {
                return commandGateway.saveTask(task, routingTable.get(name))
                        .thenReturn("Task Sent with Cache");
            }

            return eventGateway.emitWhoIs(Whois.builder().who(name).replyTo(constants.getAppName()).build())
                    .then(replyRouter.register(name)
                            .flatMap(this::saveRoute)
                            .flatMap(saveWho -> commandGateway.saveTask(task, routingTable.get(name)))
                            .thenReturn("Task Sent")
                    );
        }
    }

    private Mono<SaveWho> saveRoute(SaveWho saveWho) {
        return Mono.defer(() -> {
            routingTable.put(saveWho.getWho(), saveWho.getAppName());
            return Mono.just(saveWho);
        });
    }
}
