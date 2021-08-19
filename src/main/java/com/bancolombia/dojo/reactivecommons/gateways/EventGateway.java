package com.bancolombia.dojo.reactivecommons.gateways;

import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import com.bancolombia.dojo.reactivecommons.messages.Whois;
import lombok.AllArgsConstructor;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.util.concurrent.Queues;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableDomainEventBus
@AllArgsConstructor
public class EventGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventGateway.class);

    private final ConcurrentHashMap<String, UnicastProcessor<SaveWho>> processors = new ConcurrentHashMap<>();
    private final DomainEventBus eventBus;

    public Mono<String> emitWhoIs(Whois whois) {
        DomainEvent<Whois> event = new DomainEvent<>(Whois.NAME, UUID.randomUUID().toString(), whois);
        return Mono.from(eventBus.emit(event))
                .doOnNext(s -> LOGGER.error("Should not be called!"))
                .thenReturn("Event sent!");
    }

    public Mono<SaveWho> register(String correlationID) {
        final UnicastProcessor<SaveWho> processor = UnicastProcessor.create(Queues.<SaveWho>one().get());
        processors.put(correlationID, processor);
        return processor.singleOrEmpty();
    }

    public void routeReply(String correlationID, SaveWho data) {
        final UnicastProcessor<SaveWho> processor = processors.remove(correlationID);
        if (processor != null) {
            processor.onNext(data);
            processor.onComplete();
        }
    }
}
