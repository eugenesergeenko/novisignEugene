package com.novi.eugene.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SlideshowEvent extends NoviEvent {
    private Long slideshowId;
    private String slideshowName;

    public SlideshowEvent(Action action, LocalDateTime occuredAt, Long slideshowId, String slideshowName) {
        super(action, occuredAt);
        this.slideshowId = slideshowId;
        this.slideshowName = slideshowName;
    }
}
