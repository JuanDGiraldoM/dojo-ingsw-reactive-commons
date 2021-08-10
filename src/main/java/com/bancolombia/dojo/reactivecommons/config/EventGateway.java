package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.messages.Whois;
import lombok.AllArgsConstructor;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@EnableDomainEventBus
public class EventGateway {

    @Autowired
    private DomainEventBus eventBus;

    public Mono<Void> emitWhoIs(String who, String replyTo) {

        Whois whois = Whois.builder().who(who).replyTo(replyTo).build();
        DomainEvent<Whois> event = new DomainEvent<>(Whois.NAME, UUID.randomUUID().toString(), whois);

        return Mono.from(eventBus.emit(event));
    }
}
