package com.novi.eugene.events;

public interface NoviEventListener<T extends NoviEvent> {
    void handleEvent(T event);
}
