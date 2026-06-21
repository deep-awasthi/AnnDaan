package com.anndaan.app.dto;

import jakarta.validation.constraints.NotNull;

public record LocationRequest(
    @NotNull(message = "Latitude is required")
    Double latitude,

    @NotNull(message = "Longitude is required")
    Double longitude
) {}
