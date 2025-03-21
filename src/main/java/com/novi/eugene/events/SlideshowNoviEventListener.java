package com.novi.eugene.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SlideshowNoviEventListener implements NoviEventListener<SlideshowEvent> {

    @Override
    @EventListener
    public void handleEvent(SlideshowEvent event) {
        log.info("Slideshow Event: Action: {}, SlideshowID: {}, Name: {}, Occured At: {}",
                event.getAction(), event.getSlideshowId(), event.getSlideshowName(), event.getOccuredAt());
    }
}
