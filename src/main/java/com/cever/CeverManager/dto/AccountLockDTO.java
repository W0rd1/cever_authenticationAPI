package com.cever.CeverManager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountLockDTO(
        @JsonProperty("username") String username,
        @JsonProperty("lock") boolean lock // true to lock, false to unlock
) {
}
