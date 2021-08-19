package com.bancolombia.dojo.reactivecommons.gateways;

import com.bancolombia.dojo.reactivecommons.messages.QueryTasks;
import com.bancolombia.dojo.reactivecommons.messages.SaveTask;
import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import com.bancolombia.dojo.reactivecommons.model.Task;
import com.bancolombia.dojo.reactivecommons.model.TaskList;
import lombok.AllArgsConstructor;
import org.reactivecommons.api.domain.Command;
import org.reactivecommons.async.api.AsyncQuery;
import org.reactivecommons.async.api.DirectAsyncGateway;
import org.reactivecommons.async.impl.config.annotations.EnableDirectAsyncGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@EnableDirectAsyncGateway
@AllArgsConstructor
public class CommandGateway {

    private DirectAsyncGateway directAsyncGateway;

    public Mono<Void> saveWho(SaveWho saveWho, String target) {
        Command<SaveWho> command = new Command<>(SaveWho.NAME, UUID.randomUUID().toString(), saveWho);
        return directAsyncGateway.sendCommand(command, target);
    }

    public Mono<Void> saveTask(Task task, String target) {
        Command<Task> command = new Command<>(SaveTask.NAME, UUID.randomUUID().toString(), task);
        return directAsyncGateway.sendCommand(command, target);
    }

    public Mono<TaskList> getRemoteTasks(String target, String appName) {
        QueryTasks query = QueryTasks.builder().personName(target).requester(appName).build();
        AsyncQuery<QueryTasks> asyncQuery = new AsyncQuery<>(QueryTasks.NAME, query);
        return directAsyncGateway.requestReply(asyncQuery, target, TaskList.class);
    }
}
