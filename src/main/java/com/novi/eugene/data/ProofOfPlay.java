package com.novi.eugene.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("proof_of_play")
public class ProofOfPlay {
    @Id
    private Long id;
    private Long slideshowId;
    private Long imageId;
    private LocalDateTime playedAt;
}
