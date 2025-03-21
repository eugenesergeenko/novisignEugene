package com.novi.eugene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlideshowRequest {
    private String name;
    private List<Long> images;
}
