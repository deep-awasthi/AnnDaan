package com.anndaan.app.dto;

import com.anndaan.app.entity.Role;
import java.util.UUID;

public record AuthResponse(
    String token,
    UUID id,
    String username,
    Role role
) {}
