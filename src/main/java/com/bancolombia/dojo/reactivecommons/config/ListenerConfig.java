package com.bancolombia.dojo.reactivecommons.config;


import com.bancolombia.dojo.reactivecommons.messages.Whois;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.api.HandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;



@Configuration
public class ListenerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerConfig.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.node.name}")
    private String nameWho;


    @Bean
    public HandlerRegistry handlerRegistry(){
        return HandlerRegistry.register()
                .listenEvent(Whois.NAME,this::listenWhois,Whois.class);
    }
    
    public Mono<Void> listenWhois(DomainEvent<Whois> event){
        if(event.getData().getWho().equals(nameWho)){
            LOGGER.info("Who is for me");
            //TODO: Aqui se debe enviar el comando
        }
        return Mono.empty();
    }
}
