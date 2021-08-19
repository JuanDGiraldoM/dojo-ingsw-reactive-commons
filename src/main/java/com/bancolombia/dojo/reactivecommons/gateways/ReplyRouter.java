package com.bancolombia.dojo.reactivecommons.gateways;

import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class ReplyRouter {

    private final ConcurrentHashMap<String, Sinks.One<SaveWho>> processors = new ConcurrentHashMap<>();

    public Mono<SaveWho> register(String correlationId) {
        final Sinks.One<SaveWho> processor = Sinks.one();
        processors.put(correlationId, processor);

        return processor.asMono();
    }

    public void routeReply(String correlationId, SaveWho saveWho) {
        final Sinks.One<SaveWho> processor = processors.remove(correlationId);
        if (processor != null) {
            processor.tryEmitValue(saveWho);
        }
    }
}
