package com.novi.eugene.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageNoviEventListener implements NoviEventListener<ImageEvent> {

    @Override
    @EventListener
    public void handleEvent(ImageEvent event) {
        log.info("Image Event: Action: {}, ImageID: {}, URL={}, Occured At: {}", event.getAction(), event.getImgId(), event.getImgUrl(), event.getOccuredAt());
    }
}
