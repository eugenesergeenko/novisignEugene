package com.novi.eugene.controllers;

import com.novi.eugene.Application;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Application.class)
@WebFluxTest
public abstract class NoviControllerTest {
    @Autowired
    protected WebTestClient webTestClient; // Shared across all controllers
}
