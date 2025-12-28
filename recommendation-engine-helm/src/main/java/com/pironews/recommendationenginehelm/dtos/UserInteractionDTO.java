package com.pironews.recommendationenginehelm.dtos;


import com.pironews.recommendationenginehelm.models.UserInteraction.InteractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInteractionDTO {

    private String userId;

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "News ID is required")
    private String newsId;

    @NotNull(message = "Interaction type is required")
    private InteractionType interactionType;

    private Integer readDurationSeconds;

    private String referrerUrl;

    private String deviceType;
}
