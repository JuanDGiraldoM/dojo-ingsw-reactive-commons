package com.bancolombia.dojo.reactivecommons.gateways;

import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.util.concurrent.Queues;

import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class ReplyRouter {
    private final ConcurrentHashMap<String, UnicastProcessor<SaveWho>> processors = new ConcurrentHashMap<>();

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
