package com.novi.eugene.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class NoviEvent {
    private Action action;
    private LocalDateTime occuredAt;
}
