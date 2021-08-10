package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import lombok.AllArgsConstructor;
import org.reactivecommons.api.domain.Command;
import org.reactivecommons.async.api.DirectAsyncGateway;
import org.reactivecommons.async.impl.config.annotations.EnableDirectAsyncGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@EnableDirectAsyncGateway
public class CommandGateway {

    @Autowired
    private DirectAsyncGateway directAsyncGateway;

    public Mono<Void> saveWho(SaveWho saveWho,String target){
        Command<SaveWho> command = new Command<>(SaveWho.NAME, UUID.randomUUID().toString(),saveWho);
        return directAsyncGateway.sendCommand(command,target);
    }


}
