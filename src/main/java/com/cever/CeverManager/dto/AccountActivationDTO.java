package com.cever.CeverManager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountActivationDTO(
        @JsonProperty("token") String token
) {
}
