package com.novi.eugene.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoviEventPublisherImpl implements NoviEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishEvent(NoviEvent event) {
        publisher.publishEvent(event);
    }
}
