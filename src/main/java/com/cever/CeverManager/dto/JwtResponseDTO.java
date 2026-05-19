package com.cever.CeverManager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtResponseDTO(
        @JsonProperty("token") String token
)
{}