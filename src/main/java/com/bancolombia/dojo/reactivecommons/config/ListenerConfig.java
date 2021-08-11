package com.bancolombia.dojo.reactivecommons.config;

import com.bancolombia.dojo.reactivecommons.messages.SaveWho;
import com.bancolombia.dojo.reactivecommons.messages.Whois;
import com.rabbitmq.client.ConnectionFactory;
import lombok.AllArgsConstructor;
import org.reactivecommons.api.domain.Command;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.impl.config.ConnectionFactoryProvider;
import org.reactivecommons.async.impl.config.annotations.EnableMessageListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@EnableMessageListeners
@AllArgsConstructor
@Configuration
public class ListenerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerConfig.class);

    private Constants constants;
    private CommandGateway commandGateway;

    private void configureSsl(ConnectionFactory connectionFactory) {
        try {
            connectionFactory.useSslProtocol();
        } catch (NoSuchAlgorithmException | KeyManagementException exception) {
            LOGGER.error(exception.getMessage());
        }
    }

    @Bean
    @Primary
    public ConnectionFactoryProvider connection(RabbitProperties rabbitProperties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        configureSsl(connectionFactory);
        return () -> connectionFactory;
    }

    @Bean
    public HandlerRegistry handlerRegistry() {
        return HandlerRegistry.register()
                .listenEvent(Whois.NAME, this::listenWhois, Whois.class)
                .handleCommand(SaveWho.NAME, this::handleSaveWho, SaveWho.class);
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
        LOGGER.info("Command Received from {}...", command.getData().getWho());
        return Mono.empty();
    }
}
