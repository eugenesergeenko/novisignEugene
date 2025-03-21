package com.novi.eugene.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageEvent extends NoviEvent {
    private Long imgId;
    private String imgUrl;

    public ImageEvent(Action action, LocalDateTime occuredAt, Long imgId, String imgUrl) {
        super(action, occuredAt);
        this.imgId = imgId;
        this.imgUrl = imgUrl;
    }
}
