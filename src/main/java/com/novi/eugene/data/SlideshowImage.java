package com.novi.eugene.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("slideshow_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlideshowImage {
    @Id
    private Long id;
    private Long slideshowId;
    private Long imageId;
}
