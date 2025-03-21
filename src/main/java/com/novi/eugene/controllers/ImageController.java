package com.novi.eugene.controllers;

import com.novi.eugene.data.Image;
import com.novi.eugene.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/add")
    @Operation(
            summary = "Add a new image",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Image object with URL and duration",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "ValidImageExample",
                                    summary = "Sample Image JSON",
                                    value = """
                {
                  "url": "http://test.com/img.jpg",
                  "duration": 10
                }
                """
                            )
                    )
            )
    )
    public Mono<ResponseEntity<Image>> addImage(@RequestBody Image image) {
        return imageService.addImage(image).map(saved -> ResponseEntity.status(HttpStatus.OK).body(saved));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search images by URL fragment",
            description = "Returns a list of images whose URLs contain the given fragment.",
            parameters = {
                    @Parameter(
                            name = "urlFragment",
                            description = "Part of the image URL to search for (e.g. 'test.com/img')",
                            required = true,
                            example = "img1"
                    )
            }
    )
    public Flux<Image> findImages(@RequestParam String urlFragment) {
        return imageService.findImagesByUrl(urlFragment);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete an image by ID",
            description = "Removes the image with the specified ID from the database.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the image to delete",
                            required = true,
                            example = "1"
                    )
            }
    )
    public Mono<ResponseEntity<Void>> deleteImage(@PathVariable Long id) {
        return imageService.deleteImage(id).thenReturn(ResponseEntity.noContent().build());
    }
}
