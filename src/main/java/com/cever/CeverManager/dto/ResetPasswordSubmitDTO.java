package com.cever.CeverManager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetPasswordSubmitDTO(
        @JsonProperty("token") String token,
        @JsonProperty("newPassword") String newPassword
) {
}
