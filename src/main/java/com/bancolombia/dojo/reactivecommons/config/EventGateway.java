package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.messages.Whois;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@EnableDomainEventBus
public class EventGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventGateway.class);

    @Autowired
    private DomainEventBus eventBus;

    public Mono<String> emitWhoIs(Whois whois) {
        DomainEvent<Whois> event = new DomainEvent<>(Whois.NAME, UUID.randomUUID().toString(), whois);
        return Mono.from(eventBus.emit(event))
                .doOnNext(s -> LOGGER.error("Should not be called!"))
                .thenReturn("Event sent!");
    }
}
