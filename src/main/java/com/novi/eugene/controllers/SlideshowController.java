package com.novi.eugene.controllers;

import com.novi.eugene.data.Image;
import com.novi.eugene.data.ProofOfPlay;
import com.novi.eugene.dto.SlideshowRequest;
import com.novi.eugene.services.PopService;
import com.novi.eugene.services.SlideshowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/slideshow")
@RequiredArgsConstructor
public class SlideshowController {
    private final SlideshowService slideshowService;
    private final PopService popService;

    @PostMapping("/add")
    @Operation(
            summary = "Add a new slideshow",
            description = "Creates a new slideshow with a list of image IDs and their display durations.",
            requestBody = @RequestBody(
                    required = true,
                    description = "SlideshowRequest JSON with a name and image list",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SlideshowExample",
                                    summary = "Example slideshow request",
                                    value = """
                {
                  "name": "Test Slideshow",
                  "images": [
                    { "imageId": 1, "duration": 5 },
                    { "imageId": 2, "duration": 7 }
                  ]
                }
                """
                            )
                    )
            )
    )
    public Mono<ResponseEntity> addSlideshow(@RequestBody SlideshowRequest slideshowRequest) {
        return slideshowService.addSlideshow(slideshowRequest)
                .map(savedSShow -> ResponseEntity.status(HttpStatus.CREATED).body(savedSShow));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete a slideshow by ID",
            description = "Deletes the slideshow with the specified ID from the system.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the slideshow to delete",
                            required = true,
                            example = "1001"
                    )
            }
    )
    public Mono<ResponseEntity<Void>> deleteSlideShow(@PathVariable Long id) {
        return slideshowService.deleteSlideshow(id).thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}/images")
    @Operation(
            summary = "Get images in a slideshow",
            description = "Retrieves all images associated with the given slideshow ID, ordered by their addition date.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the slideshow",
                            required = true,
                            example = "42"
                    )
            }
    )
    public Flux<Image> getSlideShowImages(@PathVariable Long id) {
        return slideshowService.getImages(id);
    }

    @PostMapping("/{id}/proof-of-play/{imageId}")
    @Operation(
            summary = "Register a proof-of-play event",
            description = "Records an event indicating that a specific image has been shown in a slideshow.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the slideshow",
                            required = true,
                            example = "1"
                    ),
                    @Parameter(
                            name = "imageId",
                            description = "ID of the image that was played",
                            required = true,
                            example = "10"
                    )
            }
    )
    public Mono<ResponseEntity<ProofOfPlay>> registerProofOfPlay(@PathVariable Long id, @PathVariable Long imageId) {
        return popService.registerPop(ProofOfPlay.builder().slideshowId(id).imageId(imageId).playedAt(LocalDateTime.now()).build())
                .map(pop -> ResponseEntity.status(HttpStatus.CREATED).body(pop));
    }
}
