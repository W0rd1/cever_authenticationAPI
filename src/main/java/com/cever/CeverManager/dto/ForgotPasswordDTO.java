package com.cever.CeverManager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ForgotPasswordDTO (
        @JsonProperty("email") String email
) {
}
