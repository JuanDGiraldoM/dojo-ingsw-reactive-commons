package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.gateways.CommandGateway;
import com.bancolombia.dojo.reactivecommons.gateways.EventGateway;
import com.bancolombia.dojo.reactivecommons.gateways.ReplyRouter;
import com.bancolombia.dojo.reactivecommons.messages.QueryTasks;
import com.bancolombia.dojo.reactivecommons.messages.SaveTask;
import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import com.bancolombia.dojo.reactivecommons.messages.Whois;
import com.bancolombia.dojo.reactivecommons.model.TaskList;
import com.bancolombia.dojo.reactivecommons.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.api.domain.Command;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.impl.config.annotations.EnableMessageListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@EnableMessageListeners
@RequiredArgsConstructor
@Configuration
public class ListenerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerConfig.class);

    private final Constants constants;
    private final TaskRepository repository;
    private final CommandGateway commandGateway;
    private final ReplyRouter replyRouter;

    @Bean
    public HandlerRegistry handlerRegistry() {
        return HandlerRegistry.register()
                .listenEvent(Whois.NAME, this::listenWhois, Whois.class)
                .handleCommand(SaveWho.NAME, this::handleSaveWho, SaveWho.class)
                .handleCommand(SaveTask.NAME, this::handleSaveTask, SaveTask.class)
                .serveQuery(QueryTasks.NAME, this::queryTask, QueryTasks.class);
    }

    public Mono<Void> listenWhois(DomainEvent<Whois> event) {
        if (event.getData().getWho().equals(constants.getNameWho())) {
            LOGGER.info("Who is for me!");
            SaveWho saveWho = SaveWho.builder()
                    .who(constants.getNameWho()).appName(constants.getAppName()).build();
            return commandGateway.saveWho(saveWho, event.getData().getReplyTo());
        }
        LOGGER.info("Whois for {} ignored", event.getData().getWho());
        return Mono.empty();
    }

    public Mono<Void> handleSaveWho(Command<SaveWho> command) {
        LOGGER.info("Resolve {} to {}",
                command.getData().getWho(), command.getData().getAppName());
        replyRouter.routeReply(command.getData().getWho(), command.getData());
        return Mono.empty();
    }

    public Mono<Void> handleSaveTask(Command<SaveTask> command) {
        LOGGER.info("Saving task {}", command.getData().getName());
        repository.saveTask(command.getData());
        return Mono.empty();
    }

    public Mono<TaskList> queryTask(QueryTasks queryTasks) {
        LOGGER.info("Sending task to {}", queryTasks.getRequester());
        return repository.getTaskList();
    }

}
